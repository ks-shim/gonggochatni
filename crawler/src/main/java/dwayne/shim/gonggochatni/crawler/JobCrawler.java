package dwayne.shim.gonggochatni.crawler;

import dwayne.shim.gonggochatni.crawler.apicaller.ApiCaller;
import dwayne.shim.gonggochatni.crawler.apicaller.DefaultApiCaller;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileDeleteStrategy;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class JobCrawler {

    enum ParameterKey {
        START("start"),
        COUNT("count");

        private final String label;
        private ParameterKey(String _label) {
            label = _label;
        }

        public String labe() {
            return label;
        }
    }

    enum XMLNode {

        JOB("job"),
        ID("id");

        private final String label;
        private XMLNode(String _label) {
            label = _label;
        }

        public String labe() {
            return label;
        }
    }

    public void execute(File outDir) throws Exception {

        // 1. init
        log.info("Initiating ...");
        outDir.mkdirs();
        File[] oldFiles = outDir.listFiles();
        for(File oldFile : oldFiles) {
            FileDeleteStrategy.FORCE.delete(oldFile);
            log.info("Deleted " + oldFile.getName());
        }

        // 2. xml parser
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        // 3. etc
        ObjectMapper objectMapper = new ObjectMapper();
        Map<ParameterKey, String> parameters = new HashMap<>();

        // 4. api info
        RestApiInfo apiInfo = buildJobApiInfo();

        Map<String, Map<String, String>> jobDataMap = new HashMap<>();
        boolean keepCrawling = true;
        int pageNo = 0;
        try (ApiCaller apiCaller = new DefaultApiCaller()) {
            while (true) {
                keepCrawling = readJobData(jobDataMap, apiCaller, apiInfo, parameters, pageNo++, dBuilder);
                if(!keepCrawling) break;

                writeToFile(jobDataMap, objectMapper, outDir);

                jobDataMap.clear();
            }
        }
    }

    private boolean readJobData(Map<String, Map<String, String>> jobDataMap,
                                ApiCaller apiCaller,
                                RestApiInfo apiInfo,
                                Map<ParameterKey, String> parameters,
                                int pageNo,
                                DocumentBuilder dBuilder) throws Exception {
        parameters.clear();
        parameters.put(ParameterKey.START, String.valueOf(pageNo));
        String url = apiInfo.asUrlStringWith(parameters);
        log.info(url);
        String xml = apiCaller.callAsGet(url);

        return putDataInfoInto(jobDataMap, dBuilder.parse(new InputSource(new StringReader(xml))));
    }

    private void putDataInfo(String prefix,
                             Element element,
                             Map<String, String> dataMap) {

        String tagName = (prefix != null ? prefix + '-' : "") + element.getTagName();
        String value = null;
        try {
            value = element.getFirstChild().getNodeValue();
        }catch (Exception e) {
            value = null;
        }

        // 1. store tag-name : value
        if(value != null && !value.trim().isEmpty())
            dataMap.put(tagName, value);

        // 2. store tag-name : attributes
        NamedNodeMap attrMap = element.getAttributes();
        for(int i=0; i<attrMap.getLength(); i++) {
            Node attr = attrMap.item(i);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            dataMap.put(tagName + '-' + attrName, attrValue);
        }

        NodeList children = element.getChildNodes();
        for(int c=0; c<children.getLength(); c++) {
            Node child = children.item(c);
            if(child.getNodeType() != Node.ELEMENT_NODE) continue;
            putDataInfo(tagName, (Element)child, dataMap);
        }
    }

    private boolean putDataInfoInto(Map<String, Map<String, String>> jobDataMap,
                                    Document doc) {

        // 1. get all item list ...
        NodeList nodeList = doc.getElementsByTagName(XMLNode.JOB.label);
        int nodeLen = nodeList.getLength();
        if(nodeLen <= 0) return false;

        // 2. traverse all item nodes ...
        boolean hasValidOnes = false;
        for(int i=0; i<nodeLen; i++) {
            Element element = (Element)nodeList.item(i);

            // 2-1. read all data for a item ...
            Map<String, String> newItemValueMap = new HashMap<>();
            NodeList children = element.getChildNodes();
            for(int c=0; c<children.getLength(); c++) {
                Node child = children.item(c);
                if(child.getNodeType() != Node.ELEMENT_NODE) continue;
                putDataInfo(null, (Element)child, newItemValueMap);
            }

            String id = newItemValueMap.get(XMLNode.ID.label);
            if(id == null) continue;

            hasValidOnes = true;

            jobDataMap.put(id, newItemValueMap);
        }

        return hasValidOnes;
    }

    //******************************************************************************************
    // Data writing methods ...
    //******************************************************************************************
    private void writeToFile(Map<String, Map<String, String>> jobDataMap,
                             ObjectMapper objectMapper,
                             File outDir) throws Exception {

        for(String contentId : jobDataMap.keySet()) {
            Map<String, String> contentValues = jobDataMap.get(contentId);
            if(contentValues == null) continue;

            objectMapper.writeValue(new File(outDir, contentId), contentValues);
        }
    }

    private RestApiInfo buildJobApiInfo() {

        // make area-based-list restapi info ...
        Map<ParameterKey, String> parameters = new HashMap<>();
        parameters.put(ParameterKey.START, "0");
        parameters.put(ParameterKey.COUNT, "110");

        RestApiInfo apiInfo = new RestApiInfo(
                "http://api.saramin.co.kr/job-search", Collections.unmodifiableMap(parameters));

        return apiInfo;
    }

    public static void main(String[] args) throws Exception {
        new JobCrawler().execute(new File("D:/JobData"));
    }

    private static class RestApiInfo {

        private final String baseUrl;
        private final Map<ParameterKey, String> parameters;

        public RestApiInfo(String baseUrl,
                           Map<ParameterKey, String> parameters) {
            this.baseUrl = baseUrl;
            this.parameters = parameters;
        }

        public String asUrlStringWith(Map<ParameterKey, String> newParams) {

            // 1. put pre-existing parameters into newParams ...
            for(ParameterKey key : parameters.keySet())
                newParams.putIfAbsent(key, parameters.get(key));

            // 2. make url (baseUrl + parameters)
            StringBuilder sb = new StringBuilder();
            sb.append(baseUrl).append('?');

            boolean isFirst = true;
            for(ParameterKey key : newParams.keySet()) {
                if(!isFirst) sb.append('&');
                else isFirst = false;

                sb.append(key.label).append('=').append(newParams.get(key));
            }

            return sb.toString();
        }
    }
}
