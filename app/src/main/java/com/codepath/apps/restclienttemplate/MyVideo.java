package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public class MyVideo extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener {
    private MediaPlayer mplayer;
    private Uri url;
    private MediaPlayer.OnCompletionListener mCompleteListener;


    public MediaPlayer getMplayer() {
        return mplayer;
    }

    public void setUrl(Uri url) {
        this.url = url;
    }

    public MyVideo(@NonNull Context context) {
        super(context);
    }

    public MyVideo(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideo(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        Surface surface = new Surface(surfaceTexture);

        try {
            mplayer = new MediaPlayer();
            mplayer.setLooping(true);
            mplayer.setDataSource(getContext(), Uri.parse("http://www.w3schools.com/html/mov_bbb.mp4"));
            mplayer.setSurface(surface);
            mplayer.prepareAsync();
            mplayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                Log.i("MYVIEW", "ONPREPARED");
            });

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            mplayer.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        surfaceTexture.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }


    public void setmCompleteListener(MediaPlayer.OnCompletionListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }


    @Override
    protected void onDetachedFromWindow() {
        if (mplayer != null)  {
            mplayer.stop();
            mplayer.release();
            mplayer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("MYVIEW", "ONTOUCH OK");
        return false;
    }
}
