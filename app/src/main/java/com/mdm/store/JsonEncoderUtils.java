package com.mdm.store;

//package io.github.kdroidfilter.storekit.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * כלי עזר לקידוד אובייקטים ל-JSON באמצעות Reflection של ג'אווה טהורה.
 * מחליף את kotlinx.serialization.Json.encodeToString() ללא תלויות חיצוניות.
 */
public class JsonEncoderUtils {

    /**
     * מקודד אובייקט נתונים לפורמט JSON (מפורמט יפה).
     * מטפל בשדות פאבליק סופיים (final public) בלבד, כפי שהוגדרו במודלים.
     */
    public static String encodeObject(Object obj) {
        if (obj == null) {
            return "null";
        }

        Class objClass = obj.getClass();
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        Field[] fields = objClass.getDeclaredFields();
        boolean firstField = true;

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            // בודק שהשדה הוא פאבליק ופיינל (כפי שהומרנו)
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isFinal(modifiers)) {

                if (!firstField) {
                    json.append(",\n");
                }

                try {
                    String name = field.getName();
                    Object value = field.get(obj);

                    json.append("  \"").append(name).append("\": ");
                    json.append(encodeValue(value));

                    firstField = false;
                } catch (IllegalAccessException e) {
                    // ממשיכים במקרה של שגיאה (לא אמור לקרות)
                }
            }
        }

        json.append("\n}");
        return json.toString();
    }

    /**
     * מקודד ערך יחיד.
     */
    private static String encodeValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String) {
            String s = (String) value;
            // טיפול בתווי בריחה בסיסיים והוספת מרכאות
            s = s.replaceAll("\"", "\\\\\"");
            s = s.replaceAll("\n", "\\\\n");
            return "\"" + s + "\"";
        } 

        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }

        // טיפול חסר רמה: אם אובייקט, נחזיר מחרוזת המייצגת אותו במקום רקורסיה מורכבת
        return "\"" + value.getClass().getName() + " (Object)\"";
    }
}
