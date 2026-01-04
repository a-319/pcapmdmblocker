package com.mdm.store;

//package io.github.kdroidfilter.storekit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * כלי עזר ללא תלות לניתוח ערכים מ-JSON באמצעות ביטויים רגולריים.
 * מיועד להחלפת kotlinx.serialization בג'אווה טהורה.
 */
public class JsonParserUtils {

    /**
     * מחלץ ערך String מ-JSON עבור מפתח נתון.
     */
    public static String getString(String json, String key) {
        // מחפש "key": "value" - תופס את כל התוכן בין הגרשיים של הערך.
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*?)\"");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    /**
     * מחלץ ערך Long מ-JSON עבור מפתח נתון.
     */
    public static long getLong(String json, String key) {
        // מחפש "key": number
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            try {
                return Long.parseLong(m.group(1));
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }

    /**
     * מחלץ ערך Boolean מ-JSON עבור מפתח נתון.
     */
    public static boolean getBoolean(String json, String key) {
        // מחפש "key": true/false
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(true|false)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return "true".equalsIgnoreCase(m.group(1));
        }
        return false;
    }

    /**
     * מחלץ בלוק JSON מקונן עבור מפתח נתון (למשל, בלוק "file" או "data").
     */
    public static String extractBlock(String json, String key) {
        // מחפש "key":{...}
        // DOTALL כדי שהנקודה תתאים גם למעברי שורה.
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\{.*?\\})", Pattern.DOTALL);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "{}";
    }
}
