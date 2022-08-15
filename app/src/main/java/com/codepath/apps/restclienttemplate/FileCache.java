package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileCache {
    private File cacheDir;

    public FileCache(Context ctx) {
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "tw_cache");
        } else {
            cacheDir = ctx.getCacheDir();
        }
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public File getFile(String url) {
        String fileName = String.valueOf(url.hashCode());
        File f = new File(cacheDir, fileName);
        if (f.exists()) return f;
        return f;
    }
    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null) return;
        for (File f : files) {
            f.delete();
        }
    }
}
