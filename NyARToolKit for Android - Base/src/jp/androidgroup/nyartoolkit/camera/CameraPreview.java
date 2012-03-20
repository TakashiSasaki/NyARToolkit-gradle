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
 *  Yasuhide Matsumoto
 *  Fuu Rokubou
 */
package jp.androidgroup.nyartoolkit.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView
	implements SurfaceHolder.Callback, Camera.PreviewCallback
{
	/**
	 * Logging Tag
	 */
	protected final static String TAG = "NyARTK4And.CameraPreview";
	
	/**
	 * ハードウェアの Camera
	 */
	private Camera camera;
	
	/**
	 * 設定したいキャプチャ映像の幅
	 */
	private int desiredWidth = 320;
	
	/**
	 * 設定したいキャプチャ映像の高さ
	 */
	private int desiredHeight = 240;
	
	/**
	 * 設定したいフレームレート
	 */
	private int desiredFrameRate = 30;
	
	/**
	 * 設定されているキャプチャ映像の幅
	 */
	private int captureWidth;
	
	/**
	 * 設定されているキャプチャ映像の高さ
	 */
	private int captureHeight;
	
	/**
	 * 設定されているフレームレート
	 */
	private int frameRate;
	
	/**
	 * カメライベント管理
	 */
	private CameraEventListener listener;
	
	
	/**
	 * @param context
	 * @param attrs
	 */
	public CameraPreview(Context context, CameraEventListener cameraEvt)
	{
		super(context);
		
		Log.d(TAG, "Constructor of CameraPreview");
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		listener = cameraEvt;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "onSurfaceCreated");
		Log.i(TAG, "Camera Open");
		camera = Camera.open();
		
		// 解析用の画像を取得するための処理を登録する
		// OneShot を辞めたい場合はここでリスナーを登録する
//		camera.setPreviewCallback(this);
		
		// Preview
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			camera.release();
			Log.i(TAG, "Camera Release");
			Log.e(TAG, "Set Display holder");
		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, "onSurfaceChanged");
		if (holder.getSurface() == null) {
			Log.e(TAG, "No surfaceholder in surfaceChanged method.");
			return;
		}
		if (camera==null) {
			Log.e(TAG, "No camera in surfaceChanged method.");
			return;
		}
		
		// カメラの設定を取得する
		Camera.Parameters parameters = camera.getParameters();
		
		// 取得した設定を書き換える
		parameters.setPreviewSize(desiredWidth, desiredHeight);
		parameters.setPreviewFrameRate(desiredFrameRate);
		
		// 再設定
		camera.setParameters(parameters);
		
		// パラメータ確認
		parameters = camera.getParameters();
		captureWidth = parameters.getPreviewSize().width;
		captureHeight = parameters.getPreviewSize().height;
		frameRate = parameters.getPreviewFrameRate();
		
		// 
		Log.d(TAG, "Start camera preview.");
		camera.startPreview();
		camera.setOneShotPreviewCallback(this);
		
		if (listener!=null) {
			listener.cameraPreviewStarted(captureWidth, captureHeight, frameRate);
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Log.d(TAG, "Catch camera preview frame.");
		if (listener != null) {
			listener.cameraPreviewFrame(data);
		}
		// One shot preview を利用する
		camera.setOneShotPreviewCallback(this);
		// OneShot を辞めたい場合は、上記の行をコメントアウト、下記をコメントイン
		//camera.setPreviewCallbackWithBuffer(this);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
		if (camera != null) {
			camera.setOneShotPreviewCallback(null);
			camera.stopPreview();
			
			camera.release();
			camera = null;
		}
		
		if (listener != null) {
			listener.cameraPreviewStopped();
		}
	}
}
