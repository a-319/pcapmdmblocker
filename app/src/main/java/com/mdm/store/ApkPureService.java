package com.mdm.store;

//package io.github.kdroidfilter.storekit.apkpure.scraper.services;
/*
import io.github.kdroidfilter.storekit.apkpure.core.model.ApkPureApplicationInfo;
import io.github.kdroidfilter.storekit.utils.HttpService;
*/
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.URL;


public class ApkPureService {

    private static final String BASE_APKPURE_URL = "https://apkpure.com";
    private static final String APP_PATH = "/app";
    private static final String DOWNLOAD_SUFFIX = "/download";
    private static final String DIRECT_DL_BASE = "https://d.apkpure.com/b/XAPK";

    // מחלקת עזר לשמירת הנתונים המחולצים במהלך החילוץ
    private static class SimpleAppInfo {
        public String title = "";
        public String version = "";
        public String versionCode = "";
        public String signature = "";
        public String downloadLink = "";

        // בנאים ושיטות העתקה
        public SimpleAppInfo(String title, String version, String versionCode, String signature, String downloadLink) {
            this.title = title;
            this.version = version;
            this.versionCode = versionCode;
            this.signature = signature;
            this.downloadLink = downloadLink;
        }
        public SimpleAppInfo() { this("", "", "", "", ""); }
        public SimpleAppInfo copyWithTitle(String newTitle) { return new SimpleAppInfo(newTitle, version, versionCode, signature, downloadLink); }
        public SimpleAppInfo copyWithVersion(String newVersion) { return new SimpleAppInfo(title, newVersion, versionCode, signature, downloadLink); }
        public SimpleAppInfo copyWithVersionCode(String newVersionCode) { return new SimpleAppInfo(title, version, newVersionCode, signature, downloadLink); }
        public SimpleAppInfo copyWithSignature(String newSignature) { return new SimpleAppInfo(title, version, versionCode, newSignature, downloadLink); }
        public SimpleAppInfo copyWithDownloadLink(String newDownloadLink) { return new SimpleAppInfo(title, version, versionCode, signature, newDownloadLink); }
    }


    public static String buildApkPureDownloadUrl(String packageName, String version) {
        String versionParam = (version == null || version.isEmpty()) ? "latest" : version;
        return DIRECT_DL_BASE + "/" + packageName + "?version=" + versionParam;
    }

    public static String buildApkPureInfoUrl(String packageName) {
        return BASE_APKPURE_URL + APP_PATH + "/" + packageName + DOWNLOAD_SUFFIX;
    }

    // --- שיטות חילוץ נתונים (תורגמו מ-Kotlin Regex ל-Java Pattern) ---

    private static String extractTitle(String html) {
        Pattern[] patterns = new Pattern[] {
            Pattern.compile("<h1[^>]*class=\"[^\"]*name[^\"]*\"[^>]*>(.*?)</h1>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<title>([^<]+)</title>", Pattern.CASE_INSENSITIVE)
        };
        for (Pattern p : patterns) {
            Matcher m = p.matcher(html);
            if (m.find()) {
                return m.group(1).trim()
                    .replaceAll("&amp;", "&")
                    .replaceAll(" - APK Download", "");
            }
        }
        return "";
    }

    private static String extractVersion(String html) {
        Pattern[] patterns = new Pattern[] {
            Pattern.compile("<div[^>]*class=\"[^\"]*version( name)?[^\"]*\"[^>]*>\\s*<span[^>]*>([^<]+)</span>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<span[^>]*class=\"[^\"]*vername[^\"]*\"[^>]*>([^<]+)</span>", Pattern.CASE_INSENSITIVE)
        };
        for (Pattern p : patterns) {
            Matcher m = p.matcher(html);
            if (m.find()) {
                // ב-Java קבוצות מתחילות מ-1
                int idx = m.groupCount() >= 2 ? 2 : 1;
                return m.group(idx).trim();
            }
        }
        return "";
    }

    private static String extractVersionCode(String html) {
        Pattern p = Pattern.compile("<span[^>]*class=\"[^\"]*vercode[^\"]*\"[^>]*>\\(([^)]+)\\)</span>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        return m.find() ? m.group(1).trim() : "";
    }

    private static String extractVersionCodeFallback(String html) {
        Pattern[] patterns = new Pattern[] {
            // Variant info-top line
            Pattern.compile("<span[^>]*class=\"[^\"]*code[^\"]*\"[^>]*>\\(([^)]+)\\)</span>", Pattern.CASE_INSENSITIVE),
            // Download button href
            Pattern.compile("href=\"[^\"]*versionCode=([0-9]+)[^\"]*\""),
            // Data attribute variant code, sometimes plain number inside span
            Pattern.compile("\\(\\s*([0-9]{3,})\\s*\\)")
        };
        for (Pattern p : patterns) {
            Matcher m = p.matcher(html);
            if (m.find()) return m.group(1).trim();
        }
        return "";
    }

    private static String extractSignature(String html) {
        Pattern[] patterns = new Pattern[] {
            // More App Info list item
            Pattern.compile("Signature</div>\\s*<div[^>]*class=\"value[^\"]*\"[^>]*>([a-fA-F0-9]{8,})</div>", Pattern.CASE_INSENSITIVE),
            // Variant dialog line
            Pattern.compile("<span[^>]*class=\"label\"[^>]*>\\s*Signature\\s*</span>\\s*<span[^>]*class=\"value\"[^>]*>\\s*([a-fA-F0-9]{8,})\\s*</span>", Pattern.CASE_INSENSITIVE)
        };
        for (Pattern p : patterns) {
            Matcher m = p.matcher(html);
            if (m.find()) return m.group(1).trim();
        }
        return "";
    }

    // ** הפונקציה המתוקנת לחילוץ קישור ההורדה **
    private static String extractDownloadLink(String html) {
        // 1. חיפוש קישור בכפתור ההורדה הראשי (id="download_link").
        Pattern mainButtonPattern = Pattern.compile("<a[^>]*id=[\"']download_link[\"'][^>]*href=[\"']([^\"']+)[\"']");
        Matcher m = mainButtonPattern.matcher(html);

        if (m.find()) {
            String link = m.group(1).trim();

            // ודא שהקישור הוא מוחלט אם הוא יחסי.
            if (link.startsWith("/")) {
                return BASE_APKPURE_URL + link;
            }

            return link;
        }

        return "";
    }

    
    /**
     * מחלץ את הקישור הישיר הסופי מתוך גוף HTML (כנראה של דף הפניה זמני).
     */
    private static String extractFinalDownloadLink(String html) {
        // 1. חיפוש קישור ישיר שמכיל את הסיומת .apk או .xapk ואינו קישור של XAPK Installer
        Pattern finalLinkPattern = Pattern.compile("href=[\"']([^\"']+\\.(apk|xapk)[^\"']+)[\"']");
        Matcher m = finalLinkPattern.matcher(html);

        if (m.find()) {
            String link = m.group(1).trim();
            // ודא שזה לא קישור ל-XAPK installer
            if (!link.contains("xapk-installer")) {
                return link;
            }
        }

        // 2. חיפוש redirect ב-Javascript (לדוגמה: location.href)
        Pattern jsRedirectPattern = Pattern.compile("location\\.href\\s*=\\s*[\"']([^\"']+)[\"']");
        Matcher m2 = jsRedirectPattern.matcher(html);

        if (m2.find()) {
            return m2.group(1).trim();
        }

        return "";
    }
    
    public static String getRedirectLocation(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            // 1. חובה: כיבוי מעקב אוטומטי אחר הפניות
            connection.setInstanceFollowRedirects(false); 

            connection.setRequestMethod("GET");
            connection.connect();

            int status = connection.getResponseCode();

            // 2. בדיקה אם הסטטוס הוא הפניה (301, 302, 303, 307, 308)
            if (status == HttpURLConnection.HTTP_MOVED_PERM || 
                status == HttpURLConnection.HTTP_MOVED_TEMP ||
                status == 303 || status == 307 || status == 308) {

                // 3. חילוץ הקישור הישיר מכותרת Location
                String newLocation = connection.getHeaderField("Location");
                if (newLocation != null && !newLocation.isEmpty()) {
                    return newLocation;
                }
            }
        } catch (Exception e) {
            // כשל בחיבור
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "";
    }
    
    /**
     * Fetches application metadata from the APKPure website using the package name.
     */
    public static ApkPureApplicationInfo getApkPureApplicationInfo(String packageName) throws Exception {
        String url = buildApkPureInfoUrl(packageName);

        // 1. שליפת עמוד ההורדה
        HttpService.HttpResponse response = HttpService.executeRequest(url, "GET", null);

        if (!response.isSuccess()) {
            throw new IllegalArgumentException("Application with packageName: " + packageName + 
                                               " does not exist or is not accessible. HTTP status: " + response.status);
        }

        String html = response.body;

        String title = extractTitle(html);
        String version = extractVersion(html);
        String versionCode = extractVersionCode(html);

        if (versionCode.isEmpty()) {
            versionCode = extractVersionCodeFallback(html);
        }

        String signature = extractSignature(html);

        // 2. חילוץ הקישור
        // חילוץ קישור ביניים
        String downloadLink_intermediate = buildApkPureDownloadUrl(packageName, "latest");
        

        // 3. שלב שלישי: ביצוע בקשה לקישור הביניים וחילוץ הקישור הישיר הסופי
        String downloadLink_final = getRedirectLocation(downloadLink_intermediate);

        // 4. Fallback: אם הקישור הסופי עדיין ריק, נשתמש בקישור הביניים שבנינו
        if (downloadLink_final.isEmpty()) {
            downloadLink_final = downloadLink_intermediate;
        }

        return new ApkPureApplicationInfo(
            title.isEmpty() ? "Unknown App" : title,
            version,
            versionCode,
            signature,
            downloadLink_final,
            packageName,
            url
        );
    }
}
