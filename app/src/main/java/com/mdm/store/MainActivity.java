package com.mdm.store;

import android.app.Activity;
import android.os.Bundle;

/*
import io.github.kdroidfilter.storekit.apkcombo.core.model.ApkComboApplicationInfo;
import io.github.kdroidfilter.storekit.apkcombo.scraper.services.ApkComboService;
import io.github.kdroidfilter.storekit.apkpure.core.model.ApkPureApplicationInfo;
import io.github.kdroidfilter.storekit.apkpure.scraper.services.ApkPureService;
import io.github.kdroidfilter.storekit.aptoide.core.model.AptoideApplicationInfo;
import io.github.kdroidfilter.storekit.aptoide.api.services.AptoideService;
import io.github.kdroidfilter.storekit.fdroid.core.model.FdroidApplicationInfo;
import io.github.kdroidfilter.storekit.fdroid.api.services.FdroidService;
import io.github.kdroidfilter.storekit.gplay.core.model.GPlayApplicationInfo;
import io.github.kdroidfilter.storekit.gplay.scraper.services.GPlayService;
import io.github.kdroidfilter.storekit.apkresolver.core.ApkSourcePriority;
import io.github.kdroidfilter.storekit.apkresolver.core.model.ApkDownloadInfo;
import io.github.kdroidfilter.storekit.apkresolver.services.ApkLinkResolverService;
*/

import java.util.Arrays;
import android.content.Intent;
import com.mdm.activities.storeActivity;
import com.emanuelef.remote_capture.activities.LogUtil;

// ✅ ייבוא Dhizuku
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.widget.Toast;
import com.rosan.dhizuku.api.Dhizuku;
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // ✅ אתחול Dhizuku - נוסף לפני הפעולות הקיימות
        initializeDhizuku();
        
        // setContentView(R.layout.activity_main);
        // new Thread(){public void run(){
        //     Main.main();
        // }}.start();
        // startActivity(new Intent(this,listactivity.class));
        startActivity(new Intent(this, com.mdm.activities.storeActivity.class));
        finish();
    }

    // ✅ פונקציות חדשות של Dhizuku
    
    /**
     * אתחול Dhizuku ובדיקת הרשאות
     */
    private void initializeDhizuku() {
        // בדיקה אם Dhizuku זמין במכשיר
        if (!Dhizuku.init(this)) {
            LogUtil.logToFile("Dhizuku לא זמין במכשיר");
            Toast.makeText(this, "Dhizuku לא מותקן או לא פעיל", Toast.LENGTH_SHORT).show();
            return;
        }
        
        LogUtil.logToFile("Dhizuku אותחל בהצלחה");
        
        // בדיקה אם כבר יש הרשאות
        if (Dhizuku.isPermissionGranted()) {
            LogUtil.logToFile("הרשאות Dhizuku כבר קיימות");
            useDhizukuFeatures();
        } else {
            LogUtil.logToFile("מבקש הרשאות Dhizuku");
            requestDhizukuPermission();
        }
    }
    
    /**
     * בקשת הרשאות מ-Dhizuku
     */
    private void requestDhizukuPermission() {
        Dhizuku.requestPermission(new DhizukuRequestPermissionListener() {
            @Override
            public void onRequestPermission(int grantResult) throws RemoteException {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    LogUtil.logToFile("הרשאות Dhizuku אושרו!");
                    Toast.makeText(MainActivity.this, "הרשאות Dhizuku אושרו", Toast.LENGTH_SHORT).show();
                    useDhizukuFeatures();
                } else {
                    LogUtil.logToFile("הרשאות Dhizuku נדחו");
                    Toast.makeText(MainActivity.this, "הרשאות Dhizuku נדחו", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    /**
     * שימוש ביכולות Device Owner דרך Dhizuku
     */
    private void useDhizukuFeatures() {
        try {
            DevicePolicyManager dpm = Dhizuku.getDpm();
            ComponentName admin = Dhizuku.getAdmin();
            
            if (dpm != null && admin != null) {
                LogUtil.logToFile("גישה ל-DevicePolicyManager דרך Dhizuku הצליחה");
                
                // דוגמה: בדיקת סטטוס מצלמה
                boolean cameraDisabled = dpm.getCameraDisabled(admin);
                LogUtil.logToFile("מצלמה: " + (cameraDisabled ? "חסומה" : "פעילה"));
                
                // כאן תוכל להוסיף פונקציות Device Owner נוספות:
                // dpm.setCameraDisabled(admin, true);
                // dpm.setPasswordQuality(admin, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);
                // dpm.setKeyguardDisabledFeatures(admin, DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
                // וכו'...
            } else {
                LogUtil.logToFile("לא הצלחנו לקבל DevicePolicyManager מ-Dhizuku");
            }
        } catch (Exception e) {
            LogUtil.logToFile("שגיאה בשימוש ב-Dhizuku: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * מחלקה לדוגמה המדגימה את השימוש בכל השירותים שהומרו.
     * מחליף את ה-Main.kt של קוטלין ומבצע קריאות סינכרוניות (Blocking).
     */
    public static class Main {
        // תחליף ל-json.encodeToString(obj) מכיוון שאסורות תלויות חיצוניות ל-JSON.
        private static String objectToJsonRepresentation(Object obj) {
            return JsonEncoderUtils.encodeObject(obj);
        }

        public static void main() {
            // הגדרת קדימות מותאמת אישית (מחליף את ApkSourcePriority.setPriorityOrder)
            ApkSourcePriority.setCustomPriority(Arrays.asList(
                    /* "APKPure",
                    "APKCombo",
                    "FDroid",*/
                    "Aptoide"
            ));

            // --- דוגמאות לשירותים יחידים ---

            // 1. Google Play (מחליף את getGooglePlayApplicationInfo)
            /* LogUtil.logToFile("=== Google Play Example ===");
            try {
                GPlayApplicationInfo gplayApp = GPlayService.getGPlayApplicationInfo("com.waze");
                LogUtil.logToFile(objectToJsonRepresentation(gplayApp));
            } catch (Exception e) {
                LogUtil.logToFile("Error retrieving Google Play info: " + e.toString());
            }*/

            /* // 2. Aptoide (מחליף את aptoideService.getAppMetaByPackageName)
            LogUtil.logToFile("=== Aptoide Example ===");
            try {
                // יש להעביר שפה, משתמשים ב-"en" כברירת מחדל
                AptoideService.AptoideApplicationInfo aptoideApp = AptoideService.getAppMetaByPackageName("com.waze", "en");
                LogUtil.logToFile(objectToJsonRepresentation(aptoideApp));
            } catch (Exception e) {
                LogUtil.logToFile("Error retrieving Aptoide info: " + e.toString());
            }*/

            /* LogUtil.logToFile("=== ApkComboService Example ===");
            try {
                // יש להעביר שפה, משתמשים ב-"en" כברירת מחדל
                ApkComboApplicationInfo aptoideApp = ApkComboService.getApkComboApplicationInfo("com.waze");
                LogUtil.logToFile(objectToJsonRepresentation(aptoideApp));
            } catch (Exception e) {
                LogUtil.logToFile("Error retrieving ApkComboService info: " + e.toString());
            }*/

            /* // 3. F-Droid (מחליף את fdroidService.getPackageInfo)
            LogUtil.logToFile("=== FDroid Example ===");
            try {
                //FdroidApplicationInfo fdroidPackage = FdroidService.getFdroidApplicationInfo("net.thunderbird.android");
                FdroidApplicationInfo fdroidPackage = FdroidService.getFdroidApplicationInfo("com.gokadzev.musify.fdroid");
                LogUtil.logToFile(objectToJsonRepresentation(fdroidPackage));
            } catch (Exception e) {
                LogUtil.logToFile("Error retrieving FDroid info: " + e.toString());
            }*/

            // 4. APKPure (מחליף את getApkPureApplicationInfo)
            /*LogUtil.logToFile("=== APKPure Example ===");
            try {
                ApkPureApplicationInfo apkpureApp = ApkPureService.getApkPureApplicationInfo("com.waze");
                LogUtil.logToFile(objectToJsonRepresentation(apkpureApp));
            } catch (Exception e) {
                LogUtil.logToFile("Error retrieving APKPure info: " + e.toString());
            }*/

            // --- דוגמה לשירות המרכזי ---

            /*
            // 5. APK Downloader (ApkLinkResolverService)
            LogUtil.logToFile("=== APK Downloader Example ===");
            // יצירת מופע חדש של השירות (מחליף את val)
            ApkLinkResolverService apkLinkResolverService = new ApkLinkResolverService();
            try {
                // קבלת קישור הורדה לחבילה (מחליף את getApkDownloadLink)
                //ApkDownloadInfo downloadInfo = apkLinkResolverService.getApkDownloadLink("com.unicell.pangoandroid");
                ApkDownloadInfo downloadInfo = apkLinkResolverService.getApkDownloadLink("com.waze");
                LogUtil.logToFile("Download info for com.apple.bnd:");
                // מדפיסים את האובייקט כ-toString
                LogUtil.logToFile(objectToJsonRepresentation(downloadInfo));
                // הדפסה מפורטת של השדות (מחליף את ה-String Interpolation)
                LogUtil.logToFile("Source: " + downloadInfo.source);
                LogUtil.logToFile("Title: " + downloadInfo.title);
                LogUtil.logToFile("Version: " + downloadInfo.version);
                LogUtil.logToFile("Version Code: " + downloadInfo.versionCode);
                LogUtil.logToFile("Download Link: " + downloadInfo.downloadLink);
                LogUtil.logToFile("File Size: " + downloadInfo.fileSize + " bytes");
            } catch (Exception e) {
                LogUtil.logToFile("Error retrieving download link: " + e.toString());
            } finally {
                // איפוס לברירת המחדל (מחליף את finally)
                ApkSourcePriority.resetToDefault();
            }
            */

            LogUtil.logToFile("end sample");
        }
    }
}
