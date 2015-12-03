package com.gmail.takashi316.detector;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

/**
 * Created by sasaki on 2015/12/02.
 */
public class SurfaceHolderCallback implements SurfaceHolder.Callback {

    private Camera camera;
    private int maxHeight = 0, maxWidth = 0;

    public SurfaceHolderCallback() {
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    private void getMaxPreviewSize() {
        List<Camera.Size> sizes = this.camera.getParameters().getSupportedPreviewSizes();
        for (Camera.Size size : sizes) {
            if (maxHeight < size.height) {
                maxHeight = size.height;
                maxWidth = size.width;
            }
        }
    }

    FrameLayout.LayoutParams getLayoutParams() {
        this.getMaxPreviewSize();
        return new FrameLayout.LayoutParams(maxWidth, maxHeight);
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
