package util;

import java.util.HashMap;
import java.util.Map;

public class PageSourceCache {
    private static Map<String, String> pageSourceCache = new HashMap<>();

    public static String getPageSource(String activity) {
        if (pageSourceCache.containsKey(activity))
            return pageSourceCache.get(activity);
        else {
            String pageSource = Driver.getPageSource();
            pageSourceCache.put(activity, pageSource);
            return pageSource;
        }
    }
}
