package com.mdm.store;


public class AptoideExtensions {
    /**
     * Converts the SHA1 signature format from "XX:XX:XX..." to a continuous lowercase hex string.
     */
    public static String toFormattedSha1(AptoideService. AptoideSignature signature) {
        if (signature == null || signature.sha1 == null) {
            return "";
        }
        // Kotlin's toLowercase() is equivalent to Java's toLowerCase()
        return signature.sha1.replace(":", "").toLowerCase();
    }
}
