package dwayne.shim.gonggochatni.front.service.service;

import dwayne.shim.gonggochatni.common.data.JobData;
import dwayne.shim.gonggochatni.front.service.constants.AreaCode;
import dwayne.shim.gonggochatni.front.service.constants.JobCategoryCode;
import dwayne.shim.gonggochatni.front.service.constants.JobInfoField;
import dwayne.shim.gonggochatni.front.service.model.Job2DepthInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FrontService {

    @Value("${rest.new}")
    private String restNew;

    @Autowired
    private RestTemplate restTemplate;

    public List<Job2DepthInfo> getNewJobs() {
        JobData[] result = restTemplate.getForObject(restNew, JobData[].class);
        return asCategorized2DepthJobInfo(result);
    }

    private List<Job2DepthInfo> asCategorized2DepthJobInfo(JobData[] jobDatas) {

        Map<String, Job2DepthInfo> keyJobMap = new HashMap<>();

        AtomicInteger seq = new AtomicInteger(0);
        for(JobData jd : jobDatas) {
            Map<String, String> docMap = jd.getInfoMap();

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
