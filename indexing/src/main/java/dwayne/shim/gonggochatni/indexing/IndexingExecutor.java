package dwayne.shim.gonggochatni.indexing;

import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import dwayne.shim.gonggochatni.core.keyword.KeywordExtractor;
import lombok.extern.log4j.Log4j2;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.util.*;

@Log4j2
public class IndexingExecutor {

    private final DocProcessing docProcessing;
    public IndexingExecutor(KeywordExtractor keywordExtractor) {
        this.docProcessing = new DocProcessing(keywordExtractor);
    }

    public void execute(String inDirLocation,
                        int docSizeLimit,
                        String outDirLocation) throws Exception {

        // 1. get ready ...
        new File(outDirLocation).mkdirs();
        BatchIndexer batchIndexer = new BatchIndexer(outDirLocation, 1024.0);

        // 2. ** start calculate distance ...
        ObjectMapper objectMapper = new ObjectMapper();
        int docCount = 0;
        File[] docFiles = new File(inDirLocation).listFiles();
        // 3. ** start indexing ...
        List<Map<String, String>> docList = new ArrayList<>();
        docCount = 0;
        int allowedDocCount = 0;
        for(File docFile : docFiles) {
            Map<String, String> docMap = objectMapper.readValue(docFile, Map.class);

            docList.add(docMap);
            ++allowedDocCount;

            // 3-1. replace http to https ...
            docProcessing.replaceHttpToHttps(docMap, JobDataIndexField.URL.label());
            docProcessing.replaceHttpToHttps(docMap, JobDataIndexField.COMPANY_NAME_HREF.label());

            // 3-3. extract keywords and put it into docMap as new fields
            docProcessing.extractKeywords(docMap, JobDataIndexField.COMPANY_NAME.label(), JobDataIndexField.COMPANY_NAME_KEYWORDS.label());
            docProcessing.extractKeywords(docMap, JobDataIndexField.POSITION_TITLE.label(), JobDataIndexField.POSITION_TITLE_KEYWORDS.label());

            // 3-4. shorten contents
            docProcessing.shortenContent(docMap, JobDataIndexField.COMPANY_NAME.label(), JobDataIndexField.COMPANY_NAME_SHORT.label(), 20);
            docProcessing.shortenContent(docMap, JobDataIndexField.POSITION_TITLE.label(), JobDataIndexField.POSITION_TITLE_SHORT.label(), 50);

            // 3-5. duplicate fields
            docProcessing.duplicate(docMap, JobDataIndexField.POSTING_TIMESTAMP.label(), JobDataIndexField.POSTING_TIMESTAMP_SORT.label());
            docProcessing.duplicate(docMap, JobDataIndexField.MODIFICATION_TIMESTAMP.label(), JobDataIndexField.MODIFICATION_TIMESTAMP_SORT.label());
            docProcessing.duplicate(docMap, JobDataIndexField.EXPIRATION_TIMESTAMP.label(), JobDataIndexField.EXPIRATION_TIMESTAMP_POINT.label());

            if(++docCount % docSizeLimit != 0) continue;

            batchIndexer.index(docList);
            log.info("indexing {}", docList.size());
            docList.clear();
        }

        // 4. do indexing last ones ...
        if(!docList.isEmpty()) {
            batchIndexer.index(docList);
            log.info("indexing {}", docList.size());
            docList.clear();
        }

        log.info("allowed doc count : {}", allowedDocCount);
        batchIndexer.forceMerge(1);
        batchIndexer.close();
    }



    public static void main(String[] args) throws Exception {

        final String keyExtConfigLocation = "D:/korean-analyzer/configurations/main.conf";
        final String inLocation = "D:/JobData";
        final String outLocation = "D:/JobDataIndex1";
        final int docSizeLimit = 10000;

        new IndexingExecutor(new KeywordExtractor(keyExtConfigLocation)).execute(inLocation, docSizeLimit, outLocation);
    }
}
