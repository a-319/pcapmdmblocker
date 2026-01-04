package com.mdm.store;
/*
import io.github.kdroidfilter.storekit.apkcombo.scraper.services.ApkComboService;
import io.github.kdroidfilter.storekit.apkcombo.core.model.ApkComboApplicationInfo;
import io.github.kdroidfilter.storekit.apkpure.scraper.services.ApkPureService;
import io.github.kdroidfilter.storekit.apkpure.core.model.ApkPureApplicationInfo;
import io.github.kdroidfilter.storekit.aptoide.api.services.AptoideService;
import io.github.kdroidfilter.storekit.aptoide.core.model.AptoideApplicationInfo;
import io.github.kdroidfilter.storekit.fdroid.api.services.FdroidService;
import io.github.kdroidfilter.storekit.fdroid.core.model.FdroidApplicationInfo;
import io.github.kdroidfilter.storekit.apkresolver.core.ApkSourcePriority;
import io.github.kdroidfilter.storekit.apkresolver.core.model.ApkDownloadInfo;
*/
import java.util.List;
import com.emanuelef.remote_capture.activities.LogUtil;

/**
 * השירות המרכזי שמחפש קישור הורדה לפי רשימת קדימויות.
 * מחליף את ה-Flow/suspend logic של קוטלין בלולאה סינכרונית.
 */
public class ApkLinkResolverService {

    /**
     * מחפש את קישור ההורדה הראשון העובד לפי סדר הקדימויות.
     * @throws Exception אם לא נמצא קישור הורדה זמין.
     */
    public ApkDownloadInfo getApkDownloadLink(String packageName) throws Exception {
        List priority = ApkSourcePriority.getCurrentPriority();
        Exception lastException = null;

        for (int i = 0; i < priority.size(); i++) {
            String source = (String) priority.get(i);

            try {
                if ("APKPure".equals(source)) {
                    ApkPureApplicationInfo info = ApkPureService.getApkPureApplicationInfo(packageName);
                    if (!info.downloadLink.isEmpty()) {
                        return new ApkDownloadInfo(
                            info.appId, source, info.title, info.version, info.versionCode, 
                            info.signature, info.downloadLink, 0L // fileSize לא נגיש
                        );
                    }
                } 
                else if ("APKCombo".equals(source)) {
                    ApkComboApplicationInfo info = ApkComboService.getApkComboApplicationInfo(packageName);
                    if (!info.downloadLink.isEmpty()) {
                        return new ApkDownloadInfo(
                            info.appId, source, info.title, info.version, info.versionCode, 
                            "", info.downloadLink, 0L // signature ו-fileSize לא נגישים
                        );
                    }
                } 
                else if ("Aptoide".equals(source)) {
                    AptoideService.AptoideApplicationInfo info = AptoideService.getAppMetaByPackageName(packageName, "en");
                    if (!info.file.path.isEmpty()) {
                        return new ApkDownloadInfo(
                            info.packageName, source, info.name, info.file.vername, 
                            String.valueOf(info.file.vercode), info.file.signature.sha1, info.file.path, info.size
                        );
                    }
                }
                else if ("FDroid".equals(source)) {
                    FdroidApplicationInfo info = FdroidService.getFdroidApplicationInfo(packageName);
                    if (!info.downloadLink.isEmpty()) {
                        return new ApkDownloadInfo(
                            info.packageName, source, info.name, info.version, 
                            String.valueOf(info.versionCode), info.sig, info.downloadLink, info.fileSize
                        );
                    }
                }
                // GPlay לא נכלל כיוון שאינו מספק קישור הורדה ישיר
            } catch (Exception e) {
                // שמירת השגיאה האחרונה לדיווח אם אף מקור לא הצליח
                lastException = e; 
                LogUtil.logToFile("Error processing source " + source + ": " + e.toString());
            }
        }

        // אם הלולאה הסתיימה ללא הצלחה, זורק את השגיאה האחרונה או שגיאה כללית
        if (lastException != null) {
            throw new Exception("Could not find a download link for " + packageName + ". Last error: " + lastException.getMessage());
        } else {
            throw new Exception("Could not find a download link for " + packageName + " using any source.");
        }
    }
}
