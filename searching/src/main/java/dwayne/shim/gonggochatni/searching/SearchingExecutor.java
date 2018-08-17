package dwayne.shim.gonggochatni.searching;

import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import dwayne.shim.gonggochatni.common.searching.LuceneResultField;
import lombok.extern.log4j.Log4j2;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LatLonDocValuesField;
import org.apache.lucene.document.LatLonPoint;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.*;

@Log4j2
public class SearchingExecutor {

    private SearcherManager manager;
    private IndexWriter indexWriter;

    private final Analyzer analyzer;
    private final double bufferSize;

    private final Object lock = new Object();
    private final Object indexWriterLock = new Object();

    private final Map<String, JobDataIndexField> indexFieldMap;
    public SearchingExecutor(String indexDirectoryLocation) {
        this(indexDirectoryLocation, 1024.0);
    }

    public SearchingExecutor(String indexDirectoryLocation,
                             double bufferSize) {

        // 1. Initialize ...
        this.bufferSize = bufferSize;
        this.analyzer = new CJKAnalyzer();

        try {
            init(indexDirectoryLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 2. init index-field-type map
        indexFieldMap = JobDataIndexField.map();
    }

    private void init(String indexDirectoryLocation) throws Exception {

        // 1. configration for indexWriter ...
        Directory directory = FSDirectory.open(Paths.get(indexDirectoryLocation));
        IndexWriterConfig config = new IndexWriterConfig(new CJKAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setRAMBufferSizeMB(bufferSize);

        synchronized (indexWriterLock) {
            if(indexWriter != null) indexWriter.close();
            indexWriter = new IndexWriter(directory, config);
        }

        // 2. new manager and trackingIndexWriter ...
        if(manager != null) manager.close();
        manager = new SearcherManager(indexWriter, new SearcherFactory());
    }


    //******************************************************************************************************************
    // Etc
    //******************************************************************************************************************
    public void switchIndexLocation(String indexDirectoryLocation) throws Exception {
        synchronized (lock) {
            init(indexDirectoryLocation);
        }
    }

    private IndexSearcher getSearcher() throws Exception {
        synchronized (lock) {
            manager.maybeRefresh();
            return manager.acquire();
        }
    }

    private BooleanQuery.Builder buildBoolQuery(String[] fieldsToSearch,
                                                String keywords) throws Exception {
        QueryParser parser = new MultiFieldQueryParser(fieldsToSearch, analyzer);
        return buildBoolQuery(parser, keywords);
    }

    private BooleanQuery.Builder buildBoolQuery(QueryParser parser, String keywords) throws Exception {
        Query query = parser.parse(parser.escape(keywords));

        BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
        boolQuery.add(query, BooleanClause.Occur.MUST);
        return boolQuery;
    }

    //******************************************************************************************************************
    // Read all indices
    //******************************************************************************************************************
    public Map<String, String> getLocationIdAndAreaCodePair(String locationIdField,
                                                            String areaCodeField) throws Exception {
        // 1. initialize ...
        IndexSearcher searcher = getSearcher();
        IndexReader indexReader = searcher.getIndexReader();

        try {
            Map<String, String> pairMap = new HashMap<>();
            int maxDocNum = indexReader.maxDoc();
            for (int i = 0; i < maxDocNum; i++) {
                Document doc = indexReader.document(i);
                String locationId = doc.get(locationIdField);
                String areaCode = doc.get(areaCodeField);

                if (locationId == null || areaCode == null) continue;
                locationId = locationId.trim();
                areaCode = areaCode.trim();
                if (locationId.isEmpty() || areaCode.isEmpty()) continue;

                pairMap.put(locationId, areaCode);
            }
            return Collections.unmodifiableMap(pairMap);
        } finally {
            manager.release(searcher);
        }
    }

    //******************************************************************************************************************
    // Search and get specific fields
    //******************************************************************************************************************

    public SearchResult searchAllSorted(String[] fieldsToGet,
                                        String fieldToSort,
                                        int resultLimit) throws Exception {
        Sort sort = new Sort(new SortedNumericSortField(fieldToSort, SortField.Type.LONG, true));
        return search(fieldsToGet, new MatchAllDocsQuery(), sort, resultLimit);
    }

    public SearchResult search(String[] fieldsToGet,
                               String[] fieldsToSearch,
                               String keywords,
                               int resultLimit) throws Exception {

        return search(fieldsToGet, buildBoolQuery(fieldsToSearch, keywords).build(), resultLimit);
    }

    public SearchResult searchSimilar(String[] fieldsToGet,
                                       String[] fieldsToSearch,
                                       String keywords,
                                       String exclusiveId,
                                       int resultLimit) throws Exception {

        TermQuery excQuery = new TermQuery(new Term(JobDataIndexField.ID.label(), exclusiveId));
        BooleanQuery.Builder builder = buildBoolQuery(fieldsToSearch, keywords);
        builder.add(excQuery, BooleanClause.Occur.MUST_NOT);
        return search(fieldsToGet, builder.build(), resultLimit);
    }

    public SearchResult search(String[] fieldsToGet,
                               String[] fieldsToSearch,
                               Map<String, Float> boostMap,
                               String keywords,
                               int resultLimit) throws Exception {
        QueryParser parser = new MultiFieldQueryParser(fieldsToSearch, analyzer, boostMap);
        return search(fieldsToGet, buildBoolQuery(parser, keywords).build(), resultLimit);
    }

    private SearchResult search(String[] fieldsToGet,
                                Query query,
                                int resultLimit) throws Exception {
        return search(fieldsToGet, query, null, resultLimit);
    }

    private SearchResult search(String[] fieldsToGet,
                                Query query,
                                Sort sort,
                                int resultLimit) throws Exception {
        // 1. initialize ...
        IndexSearcher searcher = getSearcher();

        // 2. instantiate searchResult to return ...
        SearchResult result = new SearchResult();
        try {
            log.info("QUERY : {} ", query.toString());

            // 3. querying and getting the result from lucene ...
            long startTime =System.currentTimeMillis();
            TopDocs results = (sort == null) ? searcher.search(query, resultLimit) : searcher.search(query, resultLimit, sort);
            log.info("Duration(search) : {} (s)", (System.currentTimeMillis() - startTime)/1000.0);

            ScoreDoc[] hits = results.scoreDocs;
            long numTotalHits = results.totalHits;
            result.setTotalHits(numTotalHits);

            // 4. iteration for searched docs ...
            for(ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                Map<String, String> docMap = new HashMap<>();

                docMap.put(LuceneResultField.SCORE.label(), String.valueOf(hit.score));

                result.addDocMap(docMap);

                toMap(doc, fieldsToGet, docMap);
            }
        } finally {
            manager.release(searcher);
        }

        return result;
    }

    private void toMap(Document doc,
                       String[] fields,
                       Map<String, String> map) {

        if(fields == null || fields.length == 0) {
            for (IndexableField field : doc.getFields())
                map.put(field.name(), field.stringValue());
        } else {
            for(String fieldName : fields)
                map.put(fieldName, doc.get(fieldName));
        }
    }

    public static void main(String[] args) throws Exception {
        SearchingExecutor se = new SearchingExecutor("D:/JobIndexData1");

        List<String> fieldsToGet = new ArrayList<>();
        for(JobDataIndexField field : JobDataIndexField.values())
            fieldsToGet.add(field.label());

        String[] fieldsToSearch = {
                JobDataIndexField.COMPANY_NAME.label(),
                JobDataIndexField.POSITION_TITLE.label(),
                JobDataIndexField.COMPANY_NAME_KEYWORDS.label(),
                JobDataIndexField.POSITION_TITLE_KEYWORDS.label()
        };

        Sort sort = new Sort(new SortedNumericSortField(JobDataIndexField.MODIFICATION_TIMESTAMP_SORT.label(), SortField.Type.LONG, true));
        SearchResult result = se.search(null, new MatchAllDocsQuery(), sort, 100);
        System.out.println(result);
    }
}
