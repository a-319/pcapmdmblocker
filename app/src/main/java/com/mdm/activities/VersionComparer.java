package com.mdm.activities;

/**
 * כלי עזר להשוואת גרסאות סמנטית (כגון 1.5.0 מול 1.0.10).
 */
public class VersionComparer {

    /**
     * משווה שתי מחרוזות גרסה.
     * @param v1 גרסה ראשונה
     * @param v2 גרסה שנייה
     * @return 1 אם v1 חדשה יותר, -1 אם v2 חדשה יותר, 0 אם שוות.
     */
    public static int compareVersions(String v1, String v2) {
        if (v1 == null || v2 == null) {
            return 0;
        }

        // מנקים מחרוזות
        v1 = v1.replaceAll("[^0-9.]", "");
        v2 = v2.replaceAll("[^0-9.]", "");

        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = 0;
            int num2 = 0;

            try {
                num1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
            } catch (NumberFormatException e) { /* Ignore non-numeric parts */ }

            try {
                num2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;
            } catch (NumberFormatException e) { /* Ignore non-numeric parts */ }

            if (num1 < num2) {
                return -1;
            }
            if (num1 > num2) {
                return 1;
            }
        }
        return 0;
    }

    public static boolean isNewer(String newVersion, String currentVersion) {
        return compareVersions(newVersion, currentVersion) > 0;
    }
}
