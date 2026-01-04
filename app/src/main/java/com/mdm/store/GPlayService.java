package com.mdm.store;

//package io.github.kdroidfilter.storekit.gplay.scraper.services;
/*
import io.github.kdroidfilter.storekit.gplay.core.model.GPlayApplicationInfo;
import io.github.kdroidfilter.storekit.utils.HttpService;
*/
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GPlayService {

    private static final String BASE_GPLAY_URL = "https://play.google.com/store/apps/details";

    private static String buildGPlayUrl(String packageName) {
        return BASE_GPLAY_URL + "?id=" + packageName + "&hl=en&gl=US";
    }

    /**
     * מחלץ מידע על אפליקציית Google Play באמצעות סקראפינג (מוגבל).
     * הערה: שירות זה מבוסס על סקראפינג של HTML ציבורי, מה שהופך אותו לשביר.
     */
    public static GPlayApplicationInfo getGPlayApplicationInfo(String packageName) throws Exception {
        String url = buildGPlayUrl(packageName);

        HttpService.HttpResponse response = HttpService.executeRequest(url, "GET", null);

        if (response.status == 404 || response.body.contains("Page not found")) {
            throw new IllegalArgumentException("Application not found on Google Play: " + packageName);
        }
        if (!response.isSuccess()) {
            throw new Exception("Failed to fetch Google Play page. HTTP status: " + response.status);
        }

        String html = response.body;

        String title = extractTitle(html);
        String version = extractVersion(html);
        String sizeText = extractSize(html);

        return new GPlayApplicationInfo(
            packageName, title, version, url, sizeText
        );
    }

    private static String extractTitle(String html) {
        // מחפש את הכותרת ב-Meta tag או h1
        Pattern p = Pattern.compile("<h1[^>]*>\\s*<span[^>]*>(.*?)</span>\\s*</h1>", Pattern.DOTALL);
        Matcher m = p.matcher(html);
        if (m.find()) return m.group(1).trim();

        p = Pattern.compile("<meta\\s*itemprop=\"name\"\\s*content=\"(.*?)\"");
        m = p.matcher(html);
        if (m.find()) return m.group(1).trim();

        return "";
    }

    private static String extractVersion(String html) {
        // מחפש את "Current Version" ואת הערך שלו
        Pattern p = Pattern.compile("Current Version</div><span[^>]*>([^<]*)</span>");
        Matcher m = p.matcher(html);
        if (m.find()) return m.group(1).trim();

        return "";
    }

    private static String extractSize(String html) {
        // מחפש את "Size" ואת הערך שלו
        Pattern p = Pattern.compile("Size</div><span[^>]*>([^<]*)</span>");
        Matcher m = p.matcher(html);
        if (m.find()) return m.group(1).trim();

        return "";
    }
}
