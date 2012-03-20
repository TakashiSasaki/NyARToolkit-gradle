/**
 * NyARToolkit for Android SDK
 *   Copyright (C)2012 NyARToolkit for Android team
 *   Copyright (C)2012 R.Iizuka(nyatla)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * For further information please contact.
 *	http://sourceforge.jp/projects/nyartoolkit-and/
 * 
 * This work is based on the original ARToolKit developed by
 *   Hirokazu Kato
 *   Mark Billinghurst
 *   HITLab, University of Washington, Seattle
 *    http://www.hitl.washington.edu/artoolkit/
 *   Ryo Iizuka(nyatla)
 *    http://nyatla.jp/nyatoolkit/
 * 
 * Contributor(s)
 *  Fuu Rokubou
 */
package jp.androidgroup.nyartoolkit.markersystem;

import jp.androidgroup.nyartoolkit.camera.CameraEventListener;
import jp.androidgroup.nyartoolkit.camera.CameraPreview;
import jp.androidgroup.nyartoolkit.markersystem.android.NyARAndroidMarkerSystemFactory;
import jp.androidgroup.nyartoolkit.markersystem.renderer.MarkerSystemAbstractRenderer;
import jp.nyatla.nyartoolkit.markersystem.INyARMarkerSystemConfig;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystemConfig;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

/**
 * このクラスは、NyARToolKitのMarkerSystemを採用したアプリケーションを作成するための、
 * 基盤となるクラスです。
 * 
 * 
 */
public abstract class AbstractMarkerSystemActivity extends Activity
	implements CameraEventListener
{
	/**
	 * Logging Tag
	 */
	protected final static String TAG = "NyARTK4And.AbstractMarkerSystemActivity";
	
	/**
	 * Camera preview と GLView を使う場合は FrameLayout を使う
	 * FrameLayout の内容は、継承したクラスの {@link supplyFrameLayout()} 内で処理してください。
	 */
	protected FrameLayout mainLayout; 
	
	/**
	 * CameraPreview
	 */
	private CameraPreview preview;
	
	/**
	 * モデルデータを描画する GL SurfaceView
	 */
	private GLSurfaceView glView;
	
	/**
	 * モデルデータの内容を定義する
	 */
	protected MarkerSystemAbstractRenderer renderer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		initWindow();
	}
	
	/**
	 * 画面の初期設定
	 * 継承したクラス内でさらに書き換えるもよし。
	 */
	protected void initWindow()
	{
		// タイトルは不要
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// フルスクリーン表示
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		// 画面がスリープに入らないようにする
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// 横向き固定
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	/**
	 * Frame Layout の設定
	 * @return
	 */
	protected abstract FrameLayout supplyFrameLayout();
	
	/**
	 * OpenGL Renderer の設定
	 * @return
	 */
	protected abstract MarkerSystemAbstractRenderer supplyRenderer();
	
	@Override
	protected void onStart()
	{
		Log.d(TAG, "onStart");
		super.onStart();
		
		// レイアウト設定
		mainLayout = supplyFrameLayout();
		if (mainLayout==null) {
			Log.d(TAG, "not create frame layout");
			finish();
		}
		
		// Renderer 設定
		renderer = supplyRenderer();
		if (renderer == null) {
			Log.d(TAG, "not create renderer");
			finish();
		}
		
		// CameraPreview
		preview = new CameraPreview(this, this);
		
		// Create the GL view
		glView = new GLSurfaceView(this);
		glView.setEGLConfigChooser( 8, 8, 8, 8, 16, 0);
		glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		
		glView.setRenderer(renderer);
		glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		// Add surface views
		mainLayout.addView( glView, 0, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mainLayout.addView(preview, 1, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
	}
	
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		if (glView != null) {
			glView.onResume();
		}
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		if (glView != null) glView.onPause();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
	}
	
	/**
	 * Marker System の config を作成するメソッド。
	 * 継承したクラス内で初期化をおこなってください。
	 * 
	 * @param _caputureWidth
	 * @param _caputureHeight
	 * @return
	 */
	protected abstract INyARMarkerSystemConfig supplyMarkerSystemConfig(int _caputureWidth, int _caputureHeight);
	
	@Override
	public void cameraPreviewStarted(int width, int height, int rate) {
		NyARMarkerSystemConfig config;
		// Config設定
		config = (NyARMarkerSystemConfig)supplyMarkerSystemConfig(width, height);
		
		if (config==null) {
			try {
				AssetManager assetMng = getResources().getAssets();
				config = new NyARMarkerSystemConfig(assetMng.open("AR/CameraParam/camera_param_640x480.dat"), width, height);
			} catch (Exception e) {
				finish();
			}
		}
		// Marker system 初期化
		if (!NyARAndroidMarkerSystemFactory.getInstance().configureMarkerSystem(config)) {
			Log.e(TAG, "fail to marker system initialization.");
			finish();
		}
		captureWidth = width;
		captureHeight= height;
	}
	
	/**
	 * キャプチャサイズの幅
	 * CameraPreviewクラスで指定されるのでここでは値を設定しないでください
	 */
	protected int captureWidth  = 0;
	protected int captureHeight = 0;
	
	protected boolean isFirstUpdate = true;
	
	// TODO メソッド拡張、NyARSensor を渡すようにしたほうが良いかもしれない。
	// その場合、CameraPreview を拡張して、 jmf のコードに類似させたほうが無難か？
	@Override
	public void cameraPreviewFrame(byte[] frame) {
		Log.d(TAG, "cameraPreviewFrame");
		// Marker system が初期化されているかを確認
		// 初期化前でも実行されてしまう可能性が十分にあるため防護策として入れている
		if (!NyARAndroidMarkerSystemFactory.getInstance().isMarkerSystemRunning()) {
			return;
		// 初期化一回目なら、rendererに書かれている マーカーを登録する処理を実行する
		} else if (isFirstUpdate) {
			if (!renderer.configureARScene(getResources().getAssets())) {
				Log.e(TAG, "failed to marker pattern setup.");
				return;
			}
			isFirstUpdate = false;
		}
		
		// Raster に画像データをセットする
		Log.d(TAG, "setCapturFrame");
		
		// rasterBuffer を NyARSensor に入れる方法を検討のこと。
		NyARAndroidMarkerSystemFactory.getInstance().getMarkerSystem().update(frame);
		
		if (glView != null) glView.requestRender();
	}
	
	@Override
	public void cameraPreviewStopped() {
		Log.d(TAG, "cameraPreviewStoped");
		// TODO Auto-generated method stub
		
	}

}
