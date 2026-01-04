package com.mdm.store;

//package io.github.kdroidfilter.storekit.authenticity.core.model;

/**
 * מודל נתונים עבור מידע חתימה ואימות.
 */
public class ApkAuthenticityInfo {
    public final String source;
    public final String packageName;
    public final String signature;
    public final boolean isVerified;

    public ApkAuthenticityInfo(String source, String packageName, String signature, boolean isVerified) {
        this.source = source != null ? source : "";
        this.packageName = packageName != null ? packageName : "";
        this.signature = signature != null ? signature : "";
        this.isVerified = isVerified;
    }
}
