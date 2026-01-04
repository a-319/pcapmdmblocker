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

public class MainActivity extends Activity { 
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);
      //  new Thread(){public void run(){
     //           Main.main();
       // }}.start();
     //   startActivity(new Intent(this,listactivity.class));
        startActivity(new Intent(this,com.mdm.activities.storeActivity.class));
        finish();
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

          /*  // 2. Aptoide (מחליף את aptoideService.getAppMetaByPackageName)
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
            
          /*  // 3. F-Droid (מחליף את fdroidService.getPackageInfo)
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
