package com.mdm.store;

//package io.github.kdroidfilter.storekit.fdroid.core.model;

/**
 * מודל נתונים עבור יישום FDroid.
 */
public class FdroidApplicationInfo {
    public final String packageName;
    public final String name;
    public final String summary;
    public final String version;
    public final long versionCode;
    public final long fileSize;
    public final String downloadLink;
    public final String added;
    public final String sig;

    public FdroidApplicationInfo(String packageName, String name, String summary, String version, long versionCode, long fileSize, String downloadLink, String added, String sig) {
        this.packageName = packageName != null ? packageName : "";
        this.name = name != null ? name : "";
        this.summary = summary != null ? summary : "";
        this.version = version != null ? version : "";
        this.versionCode = versionCode;
        this.fileSize = fileSize;
        this.downloadLink = downloadLink != null ? downloadLink : "";
        this.added = added != null ? added : "";
        this.sig = sig != null ? sig : "";
    }
}
