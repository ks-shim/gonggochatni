package dwayne.shim.gonggochatni.indexing;

import dwayne.shim.gonggochatni.core.keyword.KeywordExtractor;

import java.util.List;
import java.util.Map;

public class DocProcessing {

    private final KeywordExtractor keywordExtractor;
    public DocProcessing(KeywordExtractor keywordExtractor) {
        this.keywordExtractor = keywordExtractor;
    }

    public void replaceHttpToHttps(Map<String, String> docMap,
                                    String fieldName) throws Exception {
        String content = docMap.get(fieldName);
        if(content == null) return;

        content = content.replaceAll("http:", "https:");
        docMap.put(fieldName, content);
    }

    public void extractKeywords(Map<String, String> docMap,
                                 String sourceFieldName,
                                 String targetFieldName) throws Exception {
        String content = docMap.get(sourceFieldName);
        if(content == null) return;
        docMap.put(targetFieldName, extractKeywords(content));
    }

    public void shortenContent(Map<String, String> docMap,
                                String sourceFieldName,
                                String targetFieldName,
                                int contentLengthLimit) throws Exception {
        String content = docMap.get(sourceFieldName);
        if(content == null) return;
        if(content.length() > contentLengthLimit) content = content.substring(0, contentLengthLimit-3) + " ...";
        docMap.put(targetFieldName, content);
    }

    public void duplicate(Map<String, String> docMap,
                           String sourceFieldName,
                           String targetFieldName) throws Exception {
        String content = docMap.get(sourceFieldName);
        if(content == null) return;
        docMap.put(targetFieldName, content);
    }

    public String extractKeywords(String content) {
        if(content == null) return "";
        List<String> keywords = keywordExtractor.extract(content);
        return asString(keywords);
    }

    public String asString(List<String> keywords) {
        StringBuilder sb = new StringBuilder();
        for(String keyword : keywords)
            sb.append(keyword).append(' ');

        return sb.toString().trim();
    }
}
