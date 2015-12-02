package com.gmail.takashi316.detector;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceView;

/**
 * Created by sasaki on 2015/12/02.
 */
public class CameraSurfaceView extends SurfaceView {
    public CameraSurfaceView(Context context, Camera camera) {
        super(context);
        SurfaceHolderCallback surface_holder_callback = new SurfaceHolderCallback(camera);
    }
}
