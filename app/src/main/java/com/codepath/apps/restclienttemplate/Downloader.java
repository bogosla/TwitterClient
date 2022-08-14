package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.List;

public class Downloader {
    private final static String TAG = "Downloader";
    Context context;
    FileCache fileCache;
    VideoDownloadListener videoDownloadListener;

    public Downloader(Context ctx) {
        this.context = ctx;
        this.fileCache = new FileCache(ctx);
    }

    interface VideoDownloadListener {
        void onVideoDownloaded(String url, String path);
        void onVideoFailed(String url);
    }

    public void setVideoDownloadListener(VideoDownloadListener listener) {
        this.videoDownloadListener = listener;
    }

    public void clear() {
        Utils.clear(context);
        fileCache.clear();
    }

    public void start(final List<String> urls) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < urls.size(); i++) {
                String url = urls.get(i);
                String alreadySave = Utils.readPref(context, url, "false");
                boolean isAvailable = Boolean.valueOf(alreadySave);

                Log.i(TAG, String.valueOf(isAvailable));

                if (!isAvailable) {
                    Activity activity = (Activity) context;
                    String downloadedPath = downloadVideo(url);
                    if (downloadedPath != null) {
                        activity.runOnUiThread(() -> {
                            Utils.savePref(context, url, "true");
                            videoDownloadListener.onVideoDownloaded(url, downloadedPath);
                        });
                    } else {
                        activity.runOnUiThread(() -> {
                            Utils.savePref(context, url, "true");
                            videoDownloadListener.onVideoFailed(url);
                        });
                    }
                }

            }

        });
        Log.i(TAG, "START");
        thread.start();
        Log.i(TAG, "STOP");

    }

    private String downloadVideo(String urlStr) {
        Log.i(TAG, urlStr.toString() + " OK");
        URL url;
        File file;
        try {
            file = fileCache.getFile(urlStr);
            url = new URL(urlStr);
            // long startTime = System.currentTimeMillis();
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 2);
            FileOutputStream outStream = new FileOutputStream(file);
            byte[] buff = new byte[2 * 1024];
            int len;

            while((len = inStream.read(buff)) != -1) {
                outStream.write(buff, 0, len);
            }

            outStream.flush();
            outStream.close();
            inStream.close();
            Log.i(TAG, "Clap clap!!!");
            return file.getAbsolutePath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Oh no no!!!");
        return null;

    }
}
