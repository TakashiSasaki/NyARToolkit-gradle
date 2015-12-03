package com.gmail.takashi316.detector;

import android.os.Bundle;

import jp.nyatla.nyartoolkit.core.NyARException;

/**
 * Created by sasaki on 2015/12/04.
 */
public class PreviewCallbackActivity extends PreviewActivity {
    CameraPreviewCallback cameraPreviewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            this.cameraPreviewCallback = new CameraPreviewCallback(surfaceHolderCallback.getPreviewWidth(), surfaceHolderCallback.getPreviewHeight());
            camera.addCallbackBuffer(this.cameraPreviewCallback.getCurrentBuffer());
            camera.setPreviewCallbackWithBuffer(this.cameraPreviewCallback);
        } catch (NyARException e) {
            e.printStackTrace();
        }
    }
}
