package com.gmail.takashi316.detector;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Created by sasaki on 2015/12/02.
 */
public class PreviewActivity extends Activity {

    Camera camera;
    SurfaceHolderCallback surfaceHolderCallback;
    SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_preview);
        this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        camera = Camera.open();
        this.surfaceHolderCallback = new SurfaceHolderCallback();
        this.surfaceHolderCallback.setCamera(camera);
        this.surfaceView.getHolder().addCallback(this.surfaceHolderCallback);
        this.surfaceView.setLayoutParams(this.surfaceHolderCallback.getLayoutParams());
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.surfaceHolderCallback.setCamera(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}
