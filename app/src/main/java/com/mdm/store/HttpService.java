package com.mdm.store;

//package io.github.kdroidfilter.storekit.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

public class HttpService {

    /**
     * מחלקה פנימית שמחזיקה את קוד הסטטוס ואת גוף התגובה, כתחליף ל-Ktor HttpResponse.
     */
    public static class HttpResponse {
        public final int status;
        public final String body;

        public HttpResponse(int status, String body) {
            this.status = status;
            this.body = (body != null) ? body : "";
        }

        public boolean isSuccess() {
            return status >= 200 && status < 300;
        }
    }

    /**
     * מבצע בקשת HTTP חוסמת באמצעות HttpURLConnection (ללא java.net.http.HttpClient).
     */
    public static HttpResponse executeRequest(String urlString, String method, String postData) throws Exception {
        
        //owerride netfree crt
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                @Override public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {}
                @Override public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {}
            }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslSocketFactory);
        // הגדרות בסיסיות
        connection.setRequestMethod(method);
        connection.setConnectTimeout(10000); // 10 שניות
        connection.setReadTimeout(30000); // 30 שניות
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 16) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.7151.116 Mobile");

        if ("POST".equalsIgnoreCase(method) && postData != null && !postData.isEmpty()) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // שליחת נתוני POST
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(postData);
                wr.flush();
            }
        }

        int status = connection.getResponseCode();

        StringBuilder responseBody = new StringBuilder();
        // קריאת התגובה - InputStream עבור הצלחה, ErrorStream עבור שגיאות
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                                                        status < 400 ? connection.getInputStream() : connection.getErrorStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine).append("\n"); 
            }
        } finally {
            connection.disconnect();
        }

        return new HttpResponse(status, responseBody.toString());
    }
}
