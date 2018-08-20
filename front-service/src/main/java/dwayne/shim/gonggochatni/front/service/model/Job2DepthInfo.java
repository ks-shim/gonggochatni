package dwayne.shim.gonggochatni.front.service.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class Job2DepthInfo {

    private String key;
    private int seq;
    private Map<String, List<Map<String, String>>> jobListMap;
    private int totalSize;

    public Job2DepthInfo() {}
    public Job2DepthInfo(String key,
                         int seq) {
        this.key = key;
        this.seq = seq;
        this.jobListMap = new TreeMap<>();
    }

    public void add(String subKey,
                    Map<String, String> destMap) {
        List<Map<String, String>> destMapList = jobListMap.get(subKey);
        if(destMapList == null) {
            destMapList = new ArrayList<>();
            jobListMap.put(subKey, destMapList);
        }

        totalSize++;
        destMapList.add(destMap);
    }
}


