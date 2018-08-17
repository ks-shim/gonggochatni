package dwayne.shim.gonggochatni.allinone.data.service.service;

import dwayne.shim.gonggochatni.allinone.data.service.service.data.UserKeywordsData;
import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Log4j2
@Service
public class UserPreferenceDataService {

    @Value("${user.keywords.size}")
    private int userKeywordsSize;

    @Value("${user.keywords.ttl}")
    private long userKeywordsTTL;

    @Value("${user.keywords.entry.limit}")
    private long userEntryLimit;

    @Resource
    private StatisticsDataService statisticsDataService;

    private final ConcurrentMap<String, UserKeywordsData> userKeywordsMap;
    public UserPreferenceDataService() {
        userKeywordsMap = new ConcurrentHashMap<>(5000);
    }

    public String getUserKeywords(String userId) {
        UserKeywordsData data = userKeywordsMap.get(userId);
        if(data == null) return null;

        return data.getKeywords();
    }

    public void addUserKeywords(String userId,
                                Map<String, String> docMap,
                                JobDataIndexField... sourcefields) {

        if(userKeywordsMap.size() >= userEntryLimit) {
            log.info("Too many User-Keywords entries exists, so skip this request !!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(JobDataIndexField field : sourcefields) {
            String value = docMap.get(field.label());
            if(value == null || value.isEmpty()) continue;

            if(sb.length() > 0) sb.append(' ');
            sb.append(value);
        }

        addUserKeywords(userId, sb.toString());
    }

    private void addUserKeywords(String userId,
                                 String keywords) {
        UserKeywordsData data = userKeywordsMap.get(userId);
        if(data == null) {
            // This means 'new session'
            data = new UserKeywordsData(userId, userKeywordsSize);
            userKeywordsMap.putIfAbsent(userId, data);
            statisticsDataService.incrementSessionCount(new Date());
        }

        data.appendKeywords(keywords);
    }

    public void removeOldUserData() {
        log.info("Before removing => User-Keywords entry size : {}", userKeywordsMap.size());
        Iterator<UserKeywordsData> iterator = userKeywordsMap.values().iterator();
        while(iterator.hasNext()) {
            UserKeywordsData data = iterator.next();
            if(!data.notAccessedFor(userKeywordsTTL)) continue;
            iterator.remove();
        }
        log.info("After removing => User-Keywords entry size : {}", userKeywordsMap.size());
    }
}
