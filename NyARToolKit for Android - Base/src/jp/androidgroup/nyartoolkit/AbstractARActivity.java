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
 *  Atsuo Igarashi
 *  Fuu Rokubou
 */
package jp.androidgroup.nyartoolkit;

import jp.androidgroup.nyartoolkit.camera.CameraEventListener;
import jp.androidgroup.nyartoolkit.camera.CameraPreview;
import jp.androidgroup.nyartoolkit.renderer.DefaultRenderer;
import jp.androidgroup.nyartoolkit.wrapper.NyARToolKitWrapper;
import jp.nyatla.nyartoolkit.core.NyARCode;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * このクラスは、ARアプリケーションを作成するActivityの基礎となるクラスです。
 * このクラス内でカメラやOpenGLのSurfaceを構築します。また、AR処理の一部を実装しているので、
 * 継承したクラスではその結果のみを使う事になります。
 * 
 * なお、これは Single Marker のみ処理できる Wrapper を実装しています。
 * 複数のマーカーを認識するものは処理できないので大幅な書き換えが必要です。
 */
public abstract class AbstractARActivity extends Activity implements CameraEventListener
{
	/**
	 * Logging Tag
	 */
	protected final static String TAG = "NyARTK4And.ARBaseActivity";
	
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
	protected DefaultRenderer renderer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
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
	 * Marker pattern file を読み込んで登録する処理
	 * @return
	 */
	protected abstract NyARCode supplyMarkerPattern();
	
	/**
	 * Frame Layout の設定
	 * @return
	 */
	protected abstract FrameLayout supplyFrameLayout();
	
	
	/**
	 * OpenGL Renderer の設定
	 * @return
	 */
	protected abstract DefaultRenderer supplyRenderer();
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		
		// NyARToolKitWapeer の初期化
		NyARToolKitWrapper.getInstance().isNyARRunning = false;
		
		// Marker pattern file の読み込み
		NyARCode markerPatt = supplyMarkerPattern();
		NyARToolKitWrapper.getInstance().setNyARCode(markerPatt);
		
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
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		if (glView != null) {
			glView.onResume();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		if (glView != null) glView.onPause();
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void cameraPreviewStarted(int width, int height, int rate) {
		Log.d(TAG, "cameraPreviewStarted");
		
		// Assets にアクセスするために情報取得
		AssetManager assetManger = getResources().getAssets();
		// NyARTK の キャプチャサイズやラスタサイズの初期化、カメラパラメータ設定をこのタイミングで行う。
		NyARToolKitWrapper.getInstance().initNyARToolKit(assetManger, width, height);
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cameraPreviewFrame(byte[] frame) {
		Log.d(TAG, "cameraPreviewFrame");
		
		// Raster に画像データをセットする
		NyARToolKitWrapper.getInstance().setCapturFrame(frame);
		
		// 描画処理へ
		if (glView != null) glView.requestRender();
	}

	@Override
	public void cameraPreviewStopped() {
		Log.d(TAG, "cameraPreviewStoped");
		// TODO Auto-generated method stub
		
	}
}