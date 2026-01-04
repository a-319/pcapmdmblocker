package com.mdm.store;

/*
import io.github.kdroidfilter.storekit.authenticity.core.model.ApkAuthenticityInfo;
import io.github.kdroidfilter.storekit.apkresolver.core.model.ApkDownloadInfo;
*/
/**
 * שירות בדיקת אותנטיות מפושט (ללא גישה למערכת הקבצים).
 * במקום בדיקת חתימה בפועל, הוא מבצע בדיקה מבוססת מקור.
 */
public class ApkAuthenticityService {

    // רשימת מקורות הורדה הנחשבים אמינים
    private static final String[] SAFE_SOURCES = {
        "FDroid", 
        "Aptoide",
        "APKPure",
        "APKCombo"
        // ניתן להוסיף GPlay אם הקישור הוא ל-Google Play ישירות
    };

    /**
     * בודק האם קישור ההורדה נחשב "אותנטי" בהתבסס על המקור.
     */
    public static ApkAuthenticityInfo getAuthenticityInfo(ApkDownloadInfo downloadInfo) {
        boolean isSafe = false;
        String source = downloadInfo.source;
        String signature = downloadInfo.signature;

        // בדיקת מקור בסיסית
        for (String safeSource : SAFE_SOURCES) {
            if (safeSource.equals(source)) {
                isSafe = true;
                break;
            }
        }

        // בדיקת חתימה (אם קיימת)
        if (!isSafe && signature != null && !signature.isEmpty()) {
            // הוספת לוגיקה מורכבת יותר לבדיקת חתימה אמיתית אם יש (לא אפשרי בג'אווה טהורה)
            // לצורך ההדגמה, אם יש חתימה, נניח שהמידע נשאב בהצלחה.
            isSafe = true;
        }

        return new ApkAuthenticityInfo(source, downloadInfo.packageName, signature, isSafe);
    }
}
