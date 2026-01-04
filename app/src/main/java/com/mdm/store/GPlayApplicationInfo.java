package com.mdm.store;

//package io.github.kdroidfilter.storekit.gplay.core.model;

/**
 * מודל נתונים עבור מידע ציבורי של Google Play.
 */
public class GPlayApplicationInfo {
    public final String packageName;
    public final String title;
    public final String version;
    public final String url;
    public final String sizeText; // גודל כטקסט (קשה לחלץ מספר מדויק ללא API)

    public GPlayApplicationInfo(String packageName, String title, String version, String url, String sizeText) {
        this.packageName = packageName != null ? packageName : "";
        this.title = title != null ? title : "";
        this.version = version != null ? version : "";
        this.url = url != null ? url : "";
        this.sizeText = sizeText != null ? sizeText : "";
    }
}
