package dwayne.shim.gonggochatni.front.service.service;

import dwayne.shim.gonggochatni.common.data.statistics.SessionCountData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FrontStatisticsService {

    @Value("${rest.stat-session-count}")
    private String restStatSessionCount;

    @Autowired
    private RestTemplate restTemplate;

    //**********************************************************************************
    // count statistics ...
    //**********************************************************************************
    public void getSessionCount(List<String> labelList,
                                List<Integer> valueList) throws Exception {
        SessionCountData[] datas = restTemplate.getForObject(restStatSessionCount, SessionCountData[].class);
        for(int i=datas.length-1; i>=0; i--) {
            SessionCountData data = datas[i];
            if(data.hasError()) continue;

            labelList.add(data.getDate());
            valueList.add(data.getCount());
        }
    }
}
