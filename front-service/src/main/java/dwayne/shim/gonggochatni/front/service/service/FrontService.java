package dwayne.shim.gonggochatni.front.service.service;

import dwayne.shim.gonggochatni.common.data.JobData;
import dwayne.shim.gonggochatni.front.service.model.Job2DepthInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FrontService {

    @Value("${rest.new}")
    private String restNew;

    @Autowired
    private RestTemplate restTemplate;

    public List<Job2DepthInfo> getNewJobs() {
        JobData[] result = restTemplate.getForObject(restNew, JobData[].class);
        return null;
    }

    private List<Job2DepthInfo> asCategorized2DepthJobInfo(JobData[] jobDatas) {
        return null;
    }
}
