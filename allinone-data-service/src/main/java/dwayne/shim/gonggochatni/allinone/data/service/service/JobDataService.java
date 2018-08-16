package dwayne.shim.gonggochatni.allinone.data.service.service;

import dwayne.shim.gonggochatni.common.data.JobData;
import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import dwayne.shim.gonggochatni.searching.SearchResult;
import dwayne.shim.gonggochatni.searching.SearchingExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class JobDataService {

    @Value("${job.new.size}")
    private int jobNewSize;

    private final SearchingExecutor searchingExecutor;

    public JobDataService(SearchingExecutor searchingExecutor) {
        this.searchingExecutor = searchingExecutor;
    }

    public List<JobData> getNewJobs() throws Exception {
        SearchResult result = searchingExecutor.searchAllSorted(null, JobDataIndexField.MODIFICATION_TIMESTAMP_SORT.label(), jobNewSize);
        return asJobDataList(result);
    }

    private List<JobData> asJobDataList(SearchResult result) {
        List<JobData> jobDataList = new ArrayList<>();
        for(Map<String, String> jobMap : result.getDocMapList()) {
            jobDataList.add(new JobData(jobMap));
        }
        return jobDataList;
    }

}
