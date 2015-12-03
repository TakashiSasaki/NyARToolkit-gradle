package com.gmail.takashi316.detector;

import android.content.res.AssetManager;
import android.hardware.Camera;

import java.io.IOException;

import jp.androidgroup.nyartoolkit.markersystem.NyARAndMarkerSystem;
import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystemConfig;

/**
 * Created by sasaki on 2015/12/04.
 */
public class MarkerSystemCallback extends CameraPreviewCallback {

    private NyARAndMarkerSystem nyARAndMarkerSystem;
    private int markerId1, markerId2;
    float[] glProjectionMatrix;
    float[] glMarkerMatrix1;
    float[] glMarkerMatrix2;
    boolean markerExists;

    public MarkerSystemCallback(int preview_width, int preview_height, AssetManager assetManager) throws NyARException, IOException {
        super(preview_width, preview_height);
        nyARAndMarkerSystem = new NyARAndMarkerSystem(new NyARMarkerSystemConfig(preview_width, preview_height));
        markerId1 = nyARAndMarkerSystem.addARMarker(assetManager.open("AR/data/hiro.pat"), 16, 25, 80);
        markerId2 = nyARAndMarkerSystem.addARMarker(assetManager.open("AR/data/kanji.pat"), 16, 25, 80);
        glProjectionMatrix = nyARAndMarkerSystem.getGlProjectionMatrix();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        this.bufferIndex = (this.bufferIndex + 1) % 2;
        camera.addCallbackBuffer(this.buffer[this.bufferIndex]);
        synchronized (this) {
            try {
                this._rgb_raster.wrapBuffer(data);
                this._gs_raster.wrapBuffer(data);
                this.update(this._rgb_raster);
                nyARAndMarkerSystem.update(this);
                markerExists = false;
                if (nyARAndMarkerSystem.isExistMarker(markerId1)) {
                    glMarkerMatrix1 = nyARAndMarkerSystem.getGlMarkerMatrix(markerId1);
                    markerExists = true;
                }
                if (nyARAndMarkerSystem.isExistMarker(markerId2)) {
                    glMarkerMatrix2 = nyARAndMarkerSystem.getGlMarkerMatrix(markerId2);
                    markerExists = true;
                }
                if (!markerExists) {
                    this.autofocusCount += 1;
                }
                if (this.autofocusCount % AUTOFOCUS_INTERVAL == 0) {
                    camera.autoFocus(null);
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (NyARException e) {
                e.printStackTrace();
            }
        }//synchronized
    }//onPreviewFrame
}

