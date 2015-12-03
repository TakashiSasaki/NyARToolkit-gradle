package com.gmail.takashi316.detector;

import android.graphics.ImageFormat;
import android.hardware.Camera;

import java.io.IOException;

import jp.nyatla.nyartoolkit.core.NyARException;

/**
 * Created by sasaki on 2015/12/04.
 */
public class MarkerSystemCallbackActivity extends PreviewActivity {
    MarkerSystemCallback markerSystemCallback;

    @Override
    protected void onStart() {
        super.onStart();
        try {
            this.markerSystemCallback = new MarkerSystemCallback(this.surfaceHolderCallback.getPreviewWidth(), this.surfaceHolderCallback.getPreviewHeight(), getResources().getAssets());

            //TODO: move camera settings to appropreate class
            Camera.Parameters cparam = this.camera.getParameters();
            cparam.setPreviewFormat(ImageFormat.NV21);
            cparam.setPreviewSize(this.surfaceHolderCallback.getPreviewWidth(), this.surfaceHolderCallback.getPreviewHeight());
            //cparam.setPreviewFrameRate(10);
            this.camera.setParameters(cparam);

            this.camera.addCallbackBuffer(this.markerSystemCallback.getCurrentBuffer());
            this.camera.setPreviewCallbackWithBuffer(this.markerSystemCallback);
        } catch (NyARException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
