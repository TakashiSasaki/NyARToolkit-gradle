package com.gmail.takashi316.detector;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by sasaki on 2015/12/02.
 */
public class PreviewActivity extends Activity {

    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_preview);
    }

    @Override
    protected void onStart() {
        super.onStart();
        camera = Camera.open();
        CameraSurfaceView camera_surface_view = new CameraSurfaceView(this, camera);
        FrameLayout frame_layout = (FrameLayout) findViewById(R.id.frameLayout);
        frame_layout.addView(camera_surface_view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onStop() {
        super.onStop();
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}
