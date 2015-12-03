package com.gmail.takashi316.detector;

import android.graphics.ImageFormat;
import android.hardware.Camera;

import jp.androidgroup.nyartoolkit.utils.NyARAndYUV420GsRaster;
import jp.androidgroup.nyartoolkit.utils.NyARAndYUV420RgbRaster;
import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.core.types.NyARIntSize;
import jp.nyatla.nyartoolkit.markersystem.NyARSensor;

/**
 * Created by sasaki on 2015/12/03.
 */
public class CameraPreviewCallback extends NyARSensor implements Camera.PreviewCallback {

    byte[][] buffer;
    int bufferIndex;
    protected NyARAndYUV420RgbRaster _rgb_raster;
    //TODO: マーカーが見つからない時だけオートフォーカスを実行するように変更する。
    final static int AUTOFOCUS_INTERVAL = 100;
    int autofocusCount;

    public CameraPreviewCallback(int preview_width, int preview_height) throws NyARException {
        super(new NyARIntSize(preview_width, preview_height));
        this.buffer = new byte[2][preview_width * preview_height * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];
        this.bufferIndex = 1;
        this._rgb_raster = new NyARAndYUV420RgbRaster(preview_width, preview_height);
    }

    @Override
    protected void initResource(NyARIntSize s) throws NyARException {
        this._gs_raster = new NyARAndYUV420GsRaster(s.w, s.h);
    }

    public byte[] getCurrentBuffer() {
        return this.buffer[(this.bufferIndex + 1) % 2];
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
            } catch (NyARException e) {
                e.printStackTrace();
            }
        }//synchronized
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.autofocusCount += 1;
        if (this.autofocusCount % AUTOFOCUS_INTERVAL == 0) {
            camera.autoFocus(null);
        }
    }//onPreviewFrame
}//CameraPreviewCallback
