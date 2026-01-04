package com.mdm.store;

//package io.github.kdroidfilter.storekit.apkpure.core.model;

/**
 * מחלקה שמייצגת מידע בסיסי על יישום באתר APKPure.
 * מחליף את ה-data class של קוטלין.
 */
public class ApkPureApplicationInfo {
    public final String title;
    public final String version;
    public final String versionCode;
    public final String signature;
    public final String downloadLink;
    public final String appId;
    public final String url;

    public ApkPureApplicationInfo(String title, String version, String versionCode, String signature, String downloadLink, String appId, String url) {
        this.title = title != null ? title : "";
        this.version = version != null ? version : "";
        this.versionCode = versionCode != null ? versionCode : "";
        this.signature = signature != null ? signature : "";
        this.downloadLink = downloadLink != null ? downloadLink : "";
        this.appId = appId != null ? appId : "";
        this.url = url != null ? url : "";
    }
}
