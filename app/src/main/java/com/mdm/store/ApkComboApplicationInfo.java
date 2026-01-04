package com.mdm.store;

//package io.github.kdroidfilter.storekit.apkcombo.core.model;

/**
 * מחלקה שמייצגת מידע מפורט על יישום ב-APKCombo.
 * מחליף את ה-data class של קוטלין.
 */
public class ApkComboApplicationInfo {
    public final String title;
    public final String version;
    public final String versionCode;
    public final String downloadLink;
    public final String appId;
    public final String url;

    public ApkComboApplicationInfo(String title, String version, String versionCode, String downloadLink, String appId, String url) {
        this.title = title != null ? title : "";
        this.version = version != null ? version : "";
        this.versionCode = versionCode != null ? versionCode : "";
        this.downloadLink = downloadLink != null ? downloadLink : "";
        this.appId = appId != null ? appId : "";
        this.url = url != null ? url : "";
    }
}
