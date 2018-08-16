package dwayne.shim.gonggochatni.allinone.data.service.service;

import dwayne.shim.gonggochatni.common.data.JobData;
import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import dwayne.shim.gonggochatni.searching.SearchResult;
import dwayne.shim.gonggochatni.searching.SearchingExecutor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.LRUMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
public class JobDataService {

    @Value("${job.new.size}")
    private int jobNewSize;

    private final SearchingExecutor searchingExecutor;

    private final LRUMap<Object, List<JobData>> cache;

    public JobDataService(SearchingExecutor searchingExecutor) {
        this.searchingExecutor = searchingExecutor;
        this.cache = new LRUMap<>(2);
    }

    private long timeKey() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public List<JobData> getNewJobs() throws Exception {
        long key = timeKey();
        synchronized (cache) {
            List<JobData> oldJobDataList = cache.get(key);
            if(oldJobDataList != null) return oldJobDataList;

            SearchResult result = searchingExecutor.searchAllSorted(null, JobDataIndexField.MODIFICATION_TIMESTAMP_SORT.label(), jobNewSize);
            oldJobDataList = asJobDataList(result);
            cache.put(key, oldJobDataList);
            return oldJobDataList;
        }
    }

    private List<JobData> asJobDataList(SearchResult result) {
        List<JobData> jobDataList = new ArrayList<>();
        for(Map<String, String> jobMap : result.getDocMapList()) {
            jobDataList.add(new JobData(jobMap));
        }
        return jobDataList;
    }

}
