package dwayne.shim.gonggochatni.front.service.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Job1DepthInfo {

    private String key;
    private int seq;
    private List<Map<String, String>> jobMapList;
    private int totalSize;

    public Job1DepthInfo(String key,
                         int seq) {
        this.key = key;
        this.seq = seq;
        this.jobMapList = new ArrayList<>();
    }

    public void add(Map<String, String> destMap) {
        totalSize++;
        jobMapList.add(destMap);
    }
}
