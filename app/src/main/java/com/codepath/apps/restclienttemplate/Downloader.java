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
        void onAlready(String path);
    }

    public void setVideoDownloadListener(VideoDownloadListener listener) {
        this.videoDownloadListener = listener;
    }

    public void clear() {
        Utils.clear(context);
        fileCache.clear();
    }


    public void start(final String url, MyVideo v) {
        Thread thread = new Thread(() -> {
            String alreadySave = Utils.readPref(context, url, "false");
            Activity activity = (Activity) context;

            if ("false".equals(alreadySave)) {
                String downloadedPath;
                synchronized (v) {
                    downloadedPath = downloadVideo(url);

                }
                if (downloadedPath != null) {
                    activity.runOnUiThread(() -> {
                        Utils.savePref(context, url, downloadedPath);
                        videoDownloadListener.onVideoDownloaded(url, downloadedPath);
                    });
                } else {
                    activity.runOnUiThread(() -> videoDownloadListener.onVideoFailed(url));
                }
            } else {
                activity.runOnUiThread(() -> videoDownloadListener.onAlready(alreadySave));
            }
        });
        thread.start();
    }

    private String downloadVideo(String urlStr) {

        // int total = 0;



        try {
            URL url;
            File file;
            file = fileCache.getFile(urlStr);
            url = new URL(urlStr);
            // long startTime = System.currentTimeMillis();
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 2);
            FileOutputStream outStream = new FileOutputStream(file);
            byte[] buff = new byte[2 * 1024];
            // int fileLength = connection.getContentLength();
            int len;
            Log.i(TAG, urlStr + "start downloading...");
            while((len = inStream.read(buff)) != -1) {
                // total += len;
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
        Log.i(TAG, "Downloading failed !");
        return null;

    }
}
//else {
//        MyVideo media = itemRow.vidEmb;
//        itemRow.download.setVisibility(TextView.VISIBLE);
//        media.setVisibility(MyVideo.VISIBLE);
//        Downloader downloader = new Downloader(context);
//
//        itemRow.download.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        itemRow.downloadtext.setVisibility(TextView.VISIBLE);
//        itemRow.downloadtext.setText("Downloading...");
//        itemRow.download.setVisibility(TextView.GONE);
//        downloader.start(m.get(2), media);
//        }
//        });
//        media.setOnPreparedListener(new MyVideo.VideoOK() {
//@Override
//public void onPrepared(MediaPlayer p) {
//        itemRow.play.setVisibility(TextView.VISIBLE);
//        }
//        });

//        downloader.setVideoDownloadListener(new Downloader.VideoDownloadListener() {
//@Override
//public void onVideoDownloaded(String url, String path) {
//        itemRow.downloadtext.setVisibility(TextView.GONE);
//        itemRow.download.setVisibility(TextView.GONE);
//        media.load(path);
//
//        Log.i("Adapter", url + " downloaded at "+path+ String.valueOf(media.isMediaPrepared));
//        }
//
//@Override
//public void onVideoFailed(String url) {
//        itemRow.downloadtext.setText("Failed !");
//        itemRow.download.setVisibility(TextView.VISIBLE);
//        }

//@Override
//public void onAlready(String path) {
//        itemRow.downloadtext.setVisibility(TextView.GONE);
//        itemRow.download.setVisibility(TextView.GONE);
//        media.load(path);
//        Log.i("Adapter", "Already downloaded at "+path);
//        }
//
//
//        });
//
//        itemRow.play.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        media.toggle();
//        }
//        });
//        }
