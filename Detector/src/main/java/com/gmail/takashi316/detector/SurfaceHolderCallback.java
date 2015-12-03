package com.gmail.takashi316.detector;

import android.content.Context;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by sasaki on 2015/12/02.
 */
public class SurfaceHolderCallback implements SurfaceHolder.Callback {

    private Camera camera;

    public int getPreviewHeight() {
        return previewHeight;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    private int previewHeight = 0, previewWidth = 0;
    private int screenWidth, screenHeight;

    public SurfaceHolderCallback(Context context) {
        final WindowManager window_manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = window_manager.getDefaultDisplay();
        final DisplayMetrics display_metrics = new DisplayMetrics();
        display.getMetrics(display_metrics);
        screenHeight = display_metrics.heightPixels;
        screenWidth = display_metrics.widthPixels;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        if (this.camera == null) return;
        List<Camera.Size> sizes = this.camera.getParameters().getSupportedPreviewSizes();
        this.previewHeight = 0;
        this.previewWidth = 0;
        for (Camera.Size size : sizes) {
            if (previewWidth > size.width || previewHeight > size.height) {
                continue;
            }
            if (size.height <= screenHeight && size.width <= screenWidth) {
                previewHeight = size.height;
                previewWidth = size.width;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (this.camera != null) {
            try {
                this.camera.setPreviewDisplay(holder);
                this.camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.camera != null) {
            try {
                this.camera.setPreviewDisplay(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}