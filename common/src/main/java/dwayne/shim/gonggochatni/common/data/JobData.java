package dwayne.shim.gonggochatni.common.data;

import lombok.Data;

import java.io.Serializable;
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

}
