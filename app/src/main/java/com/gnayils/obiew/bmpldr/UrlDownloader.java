package com.gnayils.obiew.bmpldr;

import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

/**
 * Created by Gnayils on 9/3/16.
 */
public class URLDownloader {

    public static final int CONNECTION_TIMEOUT = 5 * 1000;
    public static final int READ_TIMEOUT = 20 * 1000;
    public static final int MAX_REDIRECT_COUNT = 5;

    public static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";


    private int connectionTimeout;
    private int readTimeout;

    private URLDownloader(int connectionTimeout, int readTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public InputStream getInputStream(String url) throws IOException {
        HttpURLConnection conn = createConnection(url);
        int redirectCount = 0;
        while(conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            conn = createConnection(conn.getHeaderField("Location"));
            redirectCount ++;
        }

        InputStream inputStream;
        try {
            inputStream = conn.getInputStream();
        } catch (IOException e) {
            if(conn.getErrorStream() != null) {
                conn.getErrorStream().close();
            }
            throw e;
        }

        if(conn.getResponseCode() != 200) {
            if(inputStream != null) {
                inputStream.close();
            }
            throw new IOException("request url [" + url + "] failed with response code: " + conn.getResponseCode());
        }

        return new ContentLengthInputStream(new BufferedInputStream(inputStream), conn.getContentLength());
    }

    public InputStream getInputStream(File file) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        return new ContentLengthInputStream(inputStream, (int)file.length());
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        return conn;
    }


    public static URLDownloader create() {
        return new URLDownloader(CONNECTION_TIMEOUT, READ_TIMEOUT);
    }
}
