package com.gmail.takashi316.detector;

import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.core.types.NyARIntSize;
import jp.nyatla.nyartoolkit.markersystem.NyARSensor;

/**
 * Created by sasaki on 2015/12/02.
 */
public class MyARSensor extends NyARSensor {
    /**
     * 画像サイズ（スクリーンサイズ）を指定して、インスタンスを生成します。
     *
     * @param i_size 画像のサイズ。
     * @throws NyARException
     */
    public MyARSensor(int width, int height) throws NyARException {
        super(new NyARIntSize(width, height));
    }

    public MyARSensor(SurfaceHolderCallback surfaceHolderCallback) throws NyARException {
        super(new NyARIntSize(surfaceHolderCallback.getPreviewWidth(), surfaceHolderCallback.getPreviewHeight()));
    }
}
