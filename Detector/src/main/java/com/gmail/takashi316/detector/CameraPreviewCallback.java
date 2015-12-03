package com.gmail.takashi316.detector;

import android.graphics.ImageFormat;
import android.hardware.Camera;

/**
 * Created by sasaki on 2015/12/03.
 */
public class CameraPreviewCallback implements Camera.PreviewCallback {

    byte[][] buffer;
    int bufferIndex;

    public CameraPreviewCallback(int preview_width, int preview_height) {
        this.buffer = new byte[2][preview_width * preview_height * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];
        this.bufferIndex = 1;
    }

    public byte[] getCurrentBuffer() {
        return this.buffer[(this.bufferIndex + 1) % 2];
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        this.bufferIndex = (this.bufferIndex + 1) % 2;
        camera.addCallbackBuffer(this.buffer[this.bufferIndex]);
    }
}
