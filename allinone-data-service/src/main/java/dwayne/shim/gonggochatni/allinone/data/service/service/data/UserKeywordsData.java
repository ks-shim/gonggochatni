package dwayne.shim.gonggochatni.allinone.data.service.service.data;

import org.apache.commons.collections4.map.LRUMap;

public class UserKeywordsData {

    private final String userId;
    private final LRUMap<String, Integer> keywordMap;
    private volatile long lastAccessTime;

    public UserKeywordsData(String userId,
                            int keywordSize) {
        this.userId = userId;
        this.keywordMap = new LRUMap<>(keywordSize);
        this.lastAccessTime = System.currentTimeMillis();
    }

    private void updateLastAccessTime() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    public synchronized boolean notAccessedFor(long duration) {
        return System.currentTimeMillis() - lastAccessTime > duration;
    }

    public synchronized String getKeywords() {
        updateLastAccessTime();

        StringBuilder sb = new StringBuilder();
        for(String key : keywordMap.keySet()) {
            if(sb.length() > 0) sb.append(' ');
            sb.append(key);
        }
        return sb.toString();
    }

    public synchronized void appendKeywords(String newKeywordStr) {

        updateLastAccessTime();

        String[] newKeywords = newKeywordStr.split("\\s+");

        for(String keyword : newKeywords) {
            if("&gt;".equals(keyword) || "&lt;".equals(keyword)) continue;

            Integer count = keywordMap.get(keyword);
            if(count == null)
                count = 0;

            keywordMap.put(keyword, count + 1);
        }
    }
}
