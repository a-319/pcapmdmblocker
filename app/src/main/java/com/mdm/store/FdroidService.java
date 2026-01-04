package com.mdm.store;

//package io.github.kdroidfilter.storekit.fdroid.api.services;
/*
import io.github.kdroidfilter.storekit.fdroid.core.model.FdroidApplicationInfo;
import io.github.kdroidfilter.storekit.utils.HttpService;
import io.github.kdroidfilter.storekit.utils.JsonParserUtils;
*/
import java.net.URLEncoder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FdroidService {

    // *** שונה: שימוש בנתיבי ה-API הייעודיים כפי שהיו בקוטלין ***
    private static final String BASE_FDROID_API_URL = "https://f-droid.org/api/v1";
    private static final String PACKAGES_PATH = "/packages"; 

    /**
     * מחלץ מידע על אפליקציית F-Droid באמצעות API ייעודי.
     * @throws IllegalArgumentException אם האפליקציה לא קיימת ב-API.
     */
     /*
    public static FdroidApplicationInfo getFdroidApplicationInfo(String packageName) throws Exception {
        String url = BASE_FDROID_API_URL + PACKAGES_PATH + "/" + packageName;

        // 1. קבלת התגובה מה-API
        HttpService.HttpResponse response = HttpService.executeRequest(url, "GET", null);

        if (!response.isSuccess()) {
            throw new IllegalArgumentException("Package with package name: " + packageName + " does not exist or is not accessible. HTTP status: " + response.status);
        }

        String jsonText = response.body;

        // 2. חילוץ הנתונים מהמודל (FDroidPackageInfo)
        String pkgName = JsonParserUtils.getString(jsonText, "packageName");
        long suggestedVersionCode = JsonParserUtils.getLong(jsonText, "suggestedVersionCode");

        // 3. חילוץ מערך הגרסאות (Packages)
        String packagesBlock = JsonParserUtils.extractBlock(jsonText, "packages");
        String versionName = "";
        LogUtil.logToFile(jsonText);
        LogUtil.logToFile(packagesBlock);
        // 4. חיפוש ה-versionName התואם ל-suggestedVersionCode בתוך מערך ה-JSON
        if (!packagesBlock.isEmpty() && !packagesBlock.equals("{}") && suggestedVersionCode != 0) {
            LogUtil.logToFile("over..suc");
            // Regex מורכב למציאת האובייקט בתוך המערך שבו versionCode תואם.
            // מניח ששם הגרסה מופיע לפני קוד הגרסה באובייקט: {"versionName": "X.X", "versionCode": YYY}
            String versionRegex = "\\{\"versionName\"\\s*:\\s*\"([^\"]+)\"[^\\}]*\"versionCode\"\\s*:\\s*" + suggestedVersionCode;
            Pattern p = Pattern.compile(versionRegex, Pattern.DOTALL);
            Matcher m = p.matcher(packagesBlock);

            if (m.find()) {
                versionName = m.group(1);
            }
        }

        // 5. בניית קישור ההורדה
        String downloadLink = buildDownloadLink(pkgName, suggestedVersionCode);

        // 6. יצירת האובייקט (שדות חסרים יוגדרו כריקים או 0)
        return new FdroidApplicationInfo(
            pkgName, 
            "", // name - לא זמין ב-API זה
            "", // summary - לא זמין ב-API זה
            versionName, // version
            suggestedVersionCode, // versionCode
            0, // fileSize - לא זמין ב-API זה
            downloadLink, 
            "", // added - לא זמין ב-API זה
            ""  // sig - לא זמין ב-API זה
        );
    }*/
    /*
    public static FdroidApplicationInfo getFdroidApplicationInfo(String packageName) throws Exception {
        String url = BASE_FDROID_API_URL + PACKAGES_PATH + "/" + packageName;
        HttpService.HttpResponse response = HttpService.executeRequest(url, "GET", null);

        if (!response.isSuccess()) {
            throw new IllegalArgumentException("Package not accessible. Status: " + response.status);
        }

        String jsonText = response.body;
        String pkgName = JsonParserUtils.getString(jsonText, "packageName");
        long suggestedVersionCode = JsonParserUtils.getLong(jsonText, "suggestedVersionCode");
        String packagesBlock = JsonParserUtils.extractBlock(jsonText, "packages");

        String versionName = "";
        LogUtil.logToFile(jsonText);
        LogUtil.logToFile(suggestedVersionCode+"");
        
        // 1. חיפוש הבלוק הספציפי שכולל את ה-VersionCode // 2. חילוץ ה-VersionName מתוך אותו בלוק בלבד
        if (packagesBlock != null && !packagesBlock.isEmpty() && suggestedVersionCode != 0) {

            // Regex 1: מחפש את השם שמופיע לפני הקוד (כמו ב-JSON שלך)
            // הסבר: מחפש versionName, אז גרשיים, אז את התוכן, ואז כל תו עד לקוד הגרסה
            String patternStr = "\"versionName\"\\s*:\\s*\"([^\"]+)\"[^\\{]*?\"versionCode\"\\s*:\\s*" + suggestedVersionCode;

            // Regex 2: מחפש את השם שמופיע אחרי הקוד (ליתר ביטחון)
            String reversePatternStr = "\"versionCode\"\\s*:\\s*" + suggestedVersionCode + "[^\\}]*?\"versionName\"\\s*:\\s*\"([^\"]+)\"";

            Matcher m = Pattern.compile(patternStr, Pattern.DOTALL).matcher(packagesBlock);
            if (m.find()) {
                versionName = m.group(1);
            } else {
                m = Pattern.compile(reversePatternStr, Pattern.DOTALL).matcher(packagesBlock);
                if (m.find()) {
                    versionName = m.group(1);
                }
            }
        }

        // 3. מנגנון Fallback: אם ה-Regex נכשל, ננסה חילוץ פשוט של ה-versionName הראשון (בד"כ הכי חדש) // 4. תיעוד התוצאה
        if (versionName.isEmpty()) {
            versionName = JsonParserUtils.getString(packagesBlock.split("\\}")[0], "versionName");
            LogUtil.logToFile("Used fallback for versionName");
        }

        LogUtil.logToFile("Package: " + pkgName + " | Final VersionName: " + versionName);

        String downloadLink = buildDownloadLink(pkgName, suggestedVersionCode);
        return new FdroidApplicationInfo(
            pkgName, "", "", versionName, suggestedVersionCode, 0, downloadLink, "", ""
        );
    }*/
    public static FdroidApplicationInfo getFdroidApplicationInfo(String packageName) throws Exception {
        String url = BASE_FDROID_API_URL + PACKAGES_PATH + "/" + packageName;
        HttpService.HttpResponse response = HttpService.executeRequest(url, "GET", null);

        if (!response.isSuccess()) {
            throw new IllegalArgumentException("API error: " + response.status);
        }

        String jsonText = response.body;
        String pkgName = JsonParserUtils.getString(jsonText, "packageName");
        long suggestedVersionCode = JsonParserUtils.getLong(jsonText, "suggestedVersionCode");

        // 1. עבודה על ה-jsonText המלא כדי למנוע טעויות חיתוך // 2. שימוש ב-Regex שתופס את הגרסה ללא קשר למיקום במערך
        String versionName = "";

        if (suggestedVersionCode != 0) {
            // Regex סופר-גמיש: מחפש את המילה versionName, ואז את הערך שלה, בתנאי שבהמשך (באותו אובייקט) מופיע ה-code הנכון
            // הסבר: (?=[^\{]*\"versionCode\"\s*:\s*64) מבטיח שאנחנו באובייקט הנכון
            String searchPattern = "\"versionName\"\\s*:\\s*\"([^\"]+)\"(?=[^\\{]*\"versionCode\"\\s*:\\s*" + suggestedVersionCode + ")";

            Matcher m = Pattern.compile(searchPattern, Pattern.DOTALL).matcher(jsonText);
            if (m.find()) {
                versionName = m.group(1);
            } else {
                // 3. ניסיון אחרון למקרה שהסדר הפוך (הקוד לפני השם)
                String reversePattern = "\"versionCode\"\\s*:\\s*" + suggestedVersionCode + "[^\\}]*\"versionName\"\\s*:\\s*\"([^\"]+)\"";
                Matcher m2 = Pattern.compile(reversePattern, Pattern.DOTALL).matcher(jsonText);
                if (m2.find()) {
                    versionName = m2.group(1);
                }
            }
        }

        // 4. בדיקת חירום: אם עדיין ריק, נבצע חילוץ ידני גס על ידי פיצול מחרוזות // 5. לוג סופי של התוצאה
        if (versionName.isEmpty() && jsonText.contains("\"versionCode\":" + suggestedVersionCode)) {
            try {
                String[] parts = jsonText.split("\"versionCode\":" + suggestedVersionCode);
                String prefix = parts[0]; // החלק שלפני הקוד
                int lastIndex = prefix.lastIndexOf("\"versionName\":\"");
                if (lastIndex != -1) {
                    int start = lastIndex + 15;
                    versionName = prefix.substring(start, prefix.indexOf("\"", start));
                }
            } catch (Exception e) {
               // LogUtil.logToFile("Manual split failed: " + e.getMessage());
            }
        }

       // LogUtil.logToFile("Package: " + pkgName + " | Extracted Version: " + (versionName.isEmpty() ? "STILL_EMPTY" : versionName));

        String downloadLink = buildDownloadLink(pkgName, suggestedVersionCode);
        return new FdroidApplicationInfo(
            pkgName, "", "", versionName, suggestedVersionCode, 0, downloadLink, "", ""
        );
    }
    /**
     * שיטה סטטית לבניית קישור ההורדה, כפי שהייתה במודל הקוטלין.
     */
    private static String buildDownloadLink(String packageName, long versionCode) {
        if (packageName.isEmpty() || versionCode == 0) return "";
        try {
            String fileName = packageName + "_" + versionCode + ".apk";
            return "https://f-droid.org/repo/" + URLEncoder.encode(fileName, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
}
