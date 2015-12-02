package com.gmail.takashi316.detector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.view.SurfaceView;

/**
 * Created by sasaki on 2015/12/02.
 */
public class CameraSurfaceView extends SurfaceView {
    public CameraSurfaceView(Context context, Camera camera) {
        super(context);
        SurfaceHolderCallback surface_holder_callback = new SurfaceHolderCallback(camera);
        getHolder().addCallback(surface_holder_callback);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(200, 200, 100, paint);
    }
}
