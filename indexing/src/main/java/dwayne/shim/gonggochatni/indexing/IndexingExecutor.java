package dwayne.shim.gonggochatni.indexing;

import com.lyncode.jtwig.functions.util.HtmlUtils;
import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import dwayne.shim.gonggochatni.core.keyword.KeywordExtractor;
import lombok.extern.log4j.Log4j2;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.util.*;

@Log4j2
public class IndexingExecutor {

    private final KeywordExtractor keywordExtractor;
    public IndexingExecutor(KeywordExtractor keywordExtractor) {
        this.keywordExtractor = keywordExtractor;
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
            replaceHttpToHttps(docMap, JobDataIndexField.URL.label());
            replaceHttpToHttps(docMap, JobDataIndexField.COMPANY_NAME_HREF.label());

            // 3-3. extract keywords and put it into docMap as new fields
            extractKeywords(docMap, JobDataIndexField.COMPANY_NAME.label(), JobDataIndexField.COMPANY_NAME_KEYWORDS.label());
            extractKeywords(docMap, JobDataIndexField.POSITION_TITLE.label(), JobDataIndexField.POSITION_TITLE_KEYWORDS.label());

            // 3-4. shorten contents (title and overview)
            shortenContent(docMap, JobDataIndexField.COMPANY_NAME.label(), JobDataIndexField.COMPANY_NAME_SHORT.label(), 20);
            shortenContent(docMap, JobDataIndexField.POSITION_TITLE.label(), JobDataIndexField.POSITION_TITLE_SHORT.label(), 50);

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

    private void replaceHttpToHttps(Map<String, String> docMap,
                                    String fieldName) throws Exception {
        String content = docMap.get(fieldName);
        if(content == null) return;

        content = content.replaceAll("http:", "https:");
        docMap.put(fieldName, content);
    }

    private void extractKeywords(Map<String, String> docMap,
                                 String sourceFieldName,
                                 String targetFieldName) throws Exception {
        String content = docMap.get(sourceFieldName);
        if(content == null) return;
        docMap.put(targetFieldName, extractKeywords(content));
    }

    private void shortenContent(Map<String, String> docMap,
                                String sourceFieldName,
                                String targetFieldName,
                                int contentLengthLimit) throws Exception {
        String content = docMap.get(sourceFieldName);
        if(content == null) return;
        if(content.length() > contentLengthLimit) content = content.substring(0, contentLengthLimit-3) + " ...";
        docMap.put(targetFieldName, content);
    }

    private String extractKeywords(String content) {
        if(content == null) return "";
        List<String> keywords = keywordExtractor.extract(content);
        return asString(keywords);
    }

    private String asString(List<String> keywords) {
        StringBuilder sb = new StringBuilder();
        for(String keyword : keywords)
            sb.append(keyword).append(' ');

        return sb.toString().trim();
    }

    public static void main(String[] args) throws Exception {

        final String keyExtConfigLocation = "D:/korean-analyzer/configurations/main.conf";
        final String inLocation = "D:/JobData";
        final String outLocation = "D:/JobIndexData1";
        final int docSizeLimit = 10000;

        new IndexingExecutor(new KeywordExtractor(keyExtConfigLocation)).execute(inLocation, docSizeLimit, outLocation);
    }
}
