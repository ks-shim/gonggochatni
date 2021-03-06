package dwayne.shim.gonggochatni.front.service.service;

import dwayne.shim.gonggochatni.common.data.JobData;
import dwayne.shim.gonggochatni.front.service.constants.AreaCode;
import dwayne.shim.gonggochatni.front.service.constants.JobCategoryCode;
import dwayne.shim.gonggochatni.front.service.constants.JobInfoField;
import dwayne.shim.gonggochatni.front.service.model.Job2DepthInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
public class FrontService {

    @Value("${rest.new}")
    private String restNew;

    @Value("${rest.detail}")
    private String restDetail;

    @Value("${rest.search}")
    private String restSearch;

    @Value("${rest.similar}")
    private String restSimilar;

    @Value("${rest.interest}")
    private String restInterest;

    @Autowired
    private RestTemplate restTemplate;

    public List<Job2DepthInfo> getNewJobs() {
        JobData[] result = restTemplate.getForObject(restNew, JobData[].class);
        return asCategorized2DepthJobInfo(result);
    }

    public Map<String, String> getJobDetail(String jobId,
                                            String userId,
                                            boolean ignoreUser) {
        String url = restDetail + '/' + jobId + ((userId == null || userId.isEmpty()) ? "" : "?userId=" + userId + "&ignoreUser=" + ignoreUser);
        JobData result = restTemplate.getForObject(url, JobData.class);
        try {
            formatTimestamp(result.getInfoMap(), JobInfoField.EXPIRATION_TIMESTAMP.label(), JobInfoField.EXPIRATION_TIMESTAMP_FORMATTED.label());
        } catch (Exception e) {
            log.error(e);
        }
        return result.getInfoMap();
    }

    public List<Job2DepthInfo> getSimilarJobs(String jobId) {
        JobData[] result = restTemplate.getForObject(restSimilar + '/' + jobId, JobData[].class);
        return asCategorized2DepthJobInfo(result);
    }

    public List<Job2DepthInfo> getInterestingJobs(String userId) {
        if(userId == null || userId.trim().isEmpty()) return new ArrayList<>();

        JobData[] result = restTemplate.getForObject(restInterest + "/" + userId, JobData[].class);
        return asCategorized2DepthJobInfo(result);
    }

    public List<Job2DepthInfo> searchJobs(String keywords) {
        JobData[] result = restTemplate.getForObject(restSearch + "?keywords=" + keywords, JobData[].class);
        return asCategorized2DepthJobInfo(result);
    }

    private List<Job2DepthInfo> asCategorized2DepthJobInfo(JobData[] jobDatas) {

        Map<String, Job2DepthInfo> keyJobMap = new TreeMap<>();

        StringBuilder sb = new StringBuilder();
        AtomicInteger seq = new AtomicInteger(0);
        for(JobData jd : jobDatas) {
            Map<String, String> docMap = jd.getInfoMap();

            // make additional fields ...
            try {
                shortenContent(docMap, JobInfoField.POSITION_JOB_TYPE.label(), JobInfoField.POSITION_JOB_TYPE_SHORT.label(), 6);
                shortenContentByComma(docMap, JobInfoField.POSITION_LOCATION.label(), JobInfoField.POSITION_LOCATION_SHORT.label(), 2, sb);
                formatTimestamp(docMap, JobInfoField.EXPIRATION_TIMESTAMP.label(), JobInfoField.EXPIRATION_TIMESTAMP_FORMATTED.label());
            } catch (Exception e) {
                continue;
            }

            // do something ...
            String jobCategoryCodeStr = docMap.get(JobInfoField.POSITION_JOB_CATEGORY_CODE.label());
            String areaCodeStr = docMap.get(JobInfoField.POSITION_LOCATION_CODE.label());

            Set<String> categoryKeySet = asKeySet(jobCategoryCodeStr, id -> JobCategoryCode.asLabel(id));
            Set<String> areaKeySet = asKeySet(areaCodeStr, id -> AreaCode.asLabel(id));

            put2DepthData(categoryKeySet, areaKeySet, docMap, keyJobMap, seq);
        }

        return new ArrayList<>(keyJobMap.values());
    }

    private void put2DepthData(Set<String> key1Set,
                               Set<String> key2Set,
                               Map<String, String> valueMap,
                               Map<String, Job2DepthInfo> jobInfoMap,
                               AtomicInteger seq) {

        for(String key1 : key1Set) {
            Job2DepthInfo jobInfo =  jobInfoMap.get(key1);
            if(jobInfo == null) {
                jobInfo = new Job2DepthInfo(key1, seq.incrementAndGet());
                jobInfoMap.put(key1, jobInfo);
            }

            for(String key2 : key2Set) {
                jobInfo.add(key2, valueMap);
            }
        }
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

    private void shortenContentByComma(Map<String, String> docMap,
                                       String sourceFieldName,
                                       String targetFieldName,
                                       int contentLengthLimit,
                                       StringBuilder sb) throws Exception {
        String content = docMap.get(sourceFieldName);
        if(content == null) return;
        String[] contents = content.split(",");

        sb.setLength(0);
        for(int i=0; i<contentLengthLimit; i++) {
            if(contents.length <= i) break;

            if(sb.length() > 0) sb.append(',');
            sb.append(contents[i]);
        }

        if(contents.length > contentLengthLimit) sb.append(" ...");
        docMap.put(targetFieldName, sb.toString());
    }

    private void formatTimestamp(Map<String, String> docMap,
                                 String sourceFieldName,
                                 String targetFieldName) throws Exception {
        String content = docMap.get(sourceFieldName);
        if(content == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(content + "000"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        docMap.put(targetFieldName, formatter.format(cal.getTime()));
    }

    private Set<String> asKeySet(String keyStr, LabelFinder labelFinder) {
        Set<String> keySet = new HashSet<>();
        if(keyStr == null) return keySet;
        keyStr = keyStr.trim();
        if(keyStr.isEmpty()) return keySet;

        String[] keys = keyStr.split(",");
        for(String key : keys)
            keySet.add(labelFinder.asLabel(key.trim()));

        return keySet;
    }

    @FunctionalInterface
    public interface LabelFinder {
        String asLabel(String id);
    }
}
