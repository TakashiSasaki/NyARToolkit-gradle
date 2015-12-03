package com.gmail.takashi316.detector;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by sasaki on 2015/12/02.
 */
public class SurfaceHolderCallback implements SurfaceHolder.Callback {

    private Camera camera;

    public SurfaceHolderCallback() {
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
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
