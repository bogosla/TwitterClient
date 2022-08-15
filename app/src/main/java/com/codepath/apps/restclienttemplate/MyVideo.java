package com.codepath.apps.restclienttemplate;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;

import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public class MyVideo extends TextureView implements TextureView.SurfaceTextureListener {
    private final static String TAG = "MyVideo";
    
    boolean isLoaded, isMediaPrepared;
    String url;
    MediaPlayer mp;
    Surface surface;
    SurfaceTexture st;

    private VideoOK listener;

    public void setOnPreparedListener(VideoOK listener) {
        this.listener = listener;
    }

    interface VideoOK {
        void onPrepared(MediaPlayer p);
    }

    public MyVideo(@NonNull Context context) {
        super(context);
    }

    public MyVideo(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }



    public void load(String localPath) {
        this.url = localPath;
        isLoaded = true;
        if (this.isAvailable())
            prepareVideo(getSurfaceTexture());
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        isMediaPrepared = false;
        prepareVideo(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    
    public void prepareVideo(SurfaceTexture t) {
        this.surface = new Surface(t);
        mp = new MediaPlayer();
        mp.setSurface(surface);

        try {
            mp.setDataSource(url);
            mp.prepareAsync();
            mp.setOnPreparedListener(mediaPlayer -> {
                isMediaPrepared = true;
                mp.setLooping(true);
                // interface
                listener.onPrepared(mediaPlayer);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void toggle() {
        if (mp != null) {
            if (mp.isPlaying())
                mp.pause();
            else
                mp.start();
        }
    }
}
