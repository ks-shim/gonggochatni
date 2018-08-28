package dwayne.shim.gonggochatni.common.data;

import lombok.Data;
import sun.util.resources.cldr.zh.CalendarData_zh_Hans_HK;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class JobData implements Serializable {

    private Map<String, String> infoMap;

    public JobData() {}

    public JobData(Map<String, String> infoMap) {
        this.infoMap = infoMap;
    }

    private final static JobData dummyJobData = new JobData();
    public static JobData dummyJobData() {
        return dummyJobData;
    }

    public Map<String, String> getInfoMap() {
        return infoMap == null ? new HashMap<>(0) : infoMap;
    }

}
