package com.mdm.store;

//package io.github.kdroidfilter.storekit.apkcombo.scraper.services;
/*
import io.github.kdroidfilter.storekit.apkcombo.core.model.ApkComboApplicationInfo;
import io.github.kdroidfilter.storekit.utils.HttpService;
*/
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//package io.github.kdroidfilter.storekit.apkcombo.scraper.services;
/*
import io.github.kdroidfilter.storekit.apkcombo.core.model.ApkComboApplicationInfo;
import io.github.kdroidfilter.storekit.utils.HttpService;
*/
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ApkComboService {

    private static final String BASE_APKCOMBO_URL = "https://apkcombo.com";
    private static final String APP_PATH = "/app";
    private static final String DOWNLOAD_PATH = "/download/apk";

    // --- עזרי Utility ---

    private static String cleanDownloadLink(String link) {
        if (link == null) return "";
        if (link.startsWith("/r2?u=")) {
            String encodedUrl = link.substring(link.indexOf("u=") + 2);
            try {
                return URLDecoder.decode(encodedUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return link;
            }
        } else if (link.startsWith("/")) {
            return BASE_APKCOMBO_URL + link;
        } else {
            return link;
        }
    }

    private static class SimpleAppInfo {
        //... (SimpleAppInfo class content remains the same) ...
        public String title = "";
        public String version = "";
        public String versionCode = "";
        public String downloadLink = "";

        public SimpleAppInfo(String title, String version, String versionCode, String downloadLink) {
            this.title = title;
            this.version = version;
            this.versionCode = versionCode;
            this.downloadLink = downloadLink;
        }

        public SimpleAppInfo() {
            this("", "", "", "");
        }

        public SimpleAppInfo copyWithTitle(String newTitle) { return new SimpleAppInfo(newTitle, version, versionCode, downloadLink); }
        public SimpleAppInfo copyWithVersion(String newVersion) { return new SimpleAppInfo(title, newVersion, versionCode, downloadLink); }
        public SimpleAppInfo copyWithVersionCode(String newVersionCode) { return new SimpleAppInfo(title, version, newVersionCode, downloadLink); }
        public SimpleAppInfo copyWithDownloadLink(String newDownloadLink) { return new SimpleAppInfo(title, version, versionCode, newDownloadLink); }
    }

    /**
     * מחלץ נתוני אפליקציה באמצעות Regex.
     */
    private static SimpleAppInfo extractAppInfoWithRegex(String html) {
        SimpleAppInfo appInfo = new SimpleAppInfo();

        // 1. גרסה (Version)
        Pattern versionPattern = Pattern.compile("<span[^>]*class=\"[^\"]*vername[^\"]*\"[^>]*>([^<]+)</span>"); 
        Matcher versionMatch = versionPattern.matcher(html);
        if (versionMatch.find()) {
            appInfo = appInfo.copyWithVersion(versionMatch.group(1).trim()); 
        }

        // 2. קוד גרסה (Version Code)
        Pattern versionCodePattern = Pattern.compile("<span[^>]*class=\"[^\"]*vercode[^\"]*\"[^>]*>\\(([^)]+)\\)</span>"); 
        Matcher versionCodeMatch = versionCodePattern.matcher(html);
        if (versionCodeMatch.find()) {
            appInfo = appInfo.copyWithVersionCode(versionCodeMatch.group(1).trim()); 
        }

        // 3. קישור הורדה (Download Link) - תיקון למיקוד סמנטי
        Pattern[] downloadPatterns = new Pattern[] {
            // ** דפוס 1: חיפוש קישור בתוך כפתור הורדה (הפתרון לבעיית הקישור השגוי) **
            Pattern.compile("a[^>]*class=\"[^\"]*(variant|download-btn)[^\"]*\"[^>]*href=\"([^\"]*(\\.apk|\\.xapk|/r2\\?u=)[^\"]*)\""),

            // דפוס 2: קישור ישיר המכיל key (אחרי בקשת POST)
            Pattern.compile("href=\"([^\"]+\\?key=[^\"]+)\""),

            // דפוס 3: קישור r2?u= כלשהו (כ-Fallback אחרון)
            Pattern.compile("href=\"(/r2\\?u=[^\"]+)\""),
        };

        for (Pattern pattern : downloadPatterns) {
            Matcher match = pattern.matcher(html);
            if (match.find()) {
                // לוכד את הקבוצה ה-2 בדפוס החדש (ה-href)
                String link = pattern.pattern().startsWith("a") ? match.group(2) : match.group(1);
                appInfo = appInfo.copyWithDownloadLink(link);
                break;
            }
        }

        // 4. כותרת (Title) - (נשאר ללא שינוי, משתמש ב- [^\"]*)
        if (appInfo.title.isEmpty()) {
            Pattern[] titlePatterns = new Pattern[] {
                Pattern.compile("<title>([^<]*APK[^<]*)</title>", Pattern.CASE_INSENSITIVE),
                Pattern.compile("<h1[^>]*class=\"[^\"]*title[^\"]*\"[^>]*>([^<]+)</h1>", Pattern.CASE_INSENSITIVE),
                Pattern.compile("Download ([^-]+) APK", Pattern.CASE_INSENSITIVE),
            };

            for (Pattern pattern : titlePatterns) {
                Matcher match = pattern.matcher(html);
                if (match.find()) {
                    String title = match.group(1).trim()
                        .replaceAll(" APK", "")
                        .replaceAll(" - Latest Version", "")
                        .replaceAll(" - Download", "")
                        .replaceAll("Download ", "")
                        .replaceAll("&amp;", "&");

                    if (!title.trim().isEmpty() && title.length() > 1) {
                        appInfo = appInfo.copyWithTitle(title);
                        break;
                    }
                }
            }
        }

        return appInfo;
    }

    /**
     * מחלץ את נקודת הקצה (Endpoint) עבור בקשת ה-POST הדינאמית. (ללא שינוי משמעותי)
     */
    private static String extractDownloadEndpoint(String html, String packageName) {
        // 1. חיפוש var xid
        Pattern xidPattern = Pattern.compile("var xid = \"([^\"]+)\"");
        Matcher xidMatch = xidPattern.matcher(html);

        if (xidMatch.find()) {
            String xid = xidMatch.group(1);

            // 2. חיפוש fetchData("/app-name/package.name/" + xid + "/dl")
            Pattern appPathPattern = Pattern.compile("fetchData\\(\"([^\"]+/" + Pattern.quote(packageName) + "/)\" \\+ xid \\+ \"/dl\"\\)");
            Matcher appPathMatch = appPathPattern.matcher(html);

            if (appPathMatch.find()) {
                String appPath = appPathMatch.group(1);
                return BASE_APKCOMBO_URL + appPath + xid + "/dl";
            }

            // 3. Fallback: חיפוש נתיב בסיס (basePath) ואז הוספת xid/dl
            Pattern pathPattern = Pattern.compile("\"/([^\"]+/" + Pattern.quote(packageName) + "/)\"");
            Matcher pathMatch = pathPattern.matcher(html);

            if (pathMatch.find()) {
                String basePath = pathMatch.group(1);
                return BASE_APKCOMBO_URL + "/" + basePath + xid + "/dl"; 
            }
        }

        // 4. Look for direct fetchData patterns
        Pattern fetchDataPattern = Pattern.compile("fetchData\\(\"([^\"]+/dl)\""); 
        Matcher fetchDataMatch = fetchDataPattern.matcher(html);

        if (fetchDataMatch.find()) {
            String endpoint = fetchDataMatch.group(1);
            if (endpoint.contains(packageName) || endpoint.endsWith("/dl")) {
                if (endpoint.startsWith("/")) {
                    return BASE_APKCOMBO_URL + endpoint;
                } else {
                    return BASE_APKCOMBO_URL + "/" + endpoint;
                }
            }
        }

        return "";
    }

    // --- שירות רשת ---
    private static HttpService.HttpResponse fetchAppDownloadPage(String packageName) throws Exception {
        //... (fetchAppDownloadPage method content remains the same) ...
        String downloadUrl = BASE_APKCOMBO_URL + APP_PATH + "/" + packageName + DOWNLOAD_PATH;

        HttpService.HttpResponse initialResponse = HttpService.executeRequest(downloadUrl, "GET", null);
        String initialHtml = initialResponse.body;

        boolean hasDirectDownloadLinks = initialHtml.contains("class=\"variant\"") &&
            (initialHtml.contains("/r2?u=") ||
            initialHtml.contains(".apk") || initialHtml.contains(".xapk"));

        if (!hasDirectDownloadLinks && (initialHtml.contains("fetchData") || initialHtml.contains("app-details"))) {
            String downloadEndpoint = extractDownloadEndpoint(initialHtml, packageName); 

            if (!downloadEndpoint.isEmpty()) {
                String postData = "package_name=" + packageName + "&version="; 
                HttpService.HttpResponse postResponse = HttpService.executeRequest(downloadEndpoint, "POST", postData);

                if (postResponse.isSuccess()) {
                    return postResponse;
                } else {
                    return initialResponse;
                }
            } else {
                return initialResponse;
            }
        } else {
            return initialResponse;
        }
    }


    /**
     * מחלץ ומחזיר מידע מפורט על יישום ב-APKCombo.
     */
    public static ApkComboApplicationInfo getApkComboApplicationInfo(String packageName) throws Exception {
        HttpService.HttpResponse response = fetchAppDownloadPage(packageName);

        if (!response.isSuccess()) {
            throw new IllegalArgumentException("Application with packageName: " + packageName + 
                                               " does not exist or is not accessible. HTTP status: " + response.status);
        }

        String html = response.body;

        SimpleAppInfo appInfo = extractAppInfoWithRegex(html); 

        String downloadLink = cleanDownloadLink(appInfo.downloadLink);

        String url = BASE_APKCOMBO_URL + APP_PATH + "/" + packageName + DOWNLOAD_PATH;

        return new ApkComboApplicationInfo(
            appInfo.title.isEmpty() ? "Unknown App" : appInfo.title,
            appInfo.version,
            appInfo.versionCode,
            downloadLink,
            packageName,
            url
        );
    }
}
