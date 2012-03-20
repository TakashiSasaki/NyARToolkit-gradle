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
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * カメラキャプチャを制御するクラス
 * 
 * 4:3 固定で、320x240 ～ 640x480 の範囲内で許容される最小サイズでキャプチャするような
 * アルゴリズムが実装されている。これは、大きい画面ほど処理の負荷が高くカクカクになってしまうため。
 * デュアルコアが搭載されている端末であれば、800x600より一回り小さいサイズまで耐えられそう。
 * 
 * 画面比の問題があるため、基本的には 4:3 固定で使用すること。
 * カメラパラメータファイルを準備して 16:9 にすることも可能ではあるが、その場合は、 desireWitdh、Height を、
 * 指定の比率に書き換えて使用してください。
 * 必要に応じて、setter を作って、new したタイミングで、画面サイズに合わせて書き換える処理を入れてもいいと思います。
 * 
 * 書き換える際は、ソースコードをよく読んで、用法を間違えずにご利用ください。
 */
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
	 * 設定可能なキャプチャ映像の最小幅
	 */
	private int minDesiredWidth = 320;
	
	/**
	 * 設定可能なキャプチャ映像の最小高さ
	 */
	private int minDesiredHeight = 240;
	
	/**
	 * (上限)キャプチャ映像の幅
	 */
	private int maxDesiredWidth = 640;
	
	/**
	 * (上限)キャプチャ映像の高さ
	 */
	private int maxDesiredHeight = 480;
	
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
		
		// 許容される比率の最低値
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) minDesiredWidth / minDesiredHeight;
		
		// カメラの設定を取得する
		Camera.Parameters parameters = camera.getParameters();
		
		// 適応サイズ有無確認フラグ
		boolean _isFoundCapSize = false;
		
		// キャプチャサイズ初期化
		captureWidth  = maxDesiredWidth;
		captureHeight = maxDesiredHeight;
		
		// カメラのキャプチャサイズのリストを取得
		List<Size> sizes = parameters.getSupportedPreviewSizes();
		
		// 適切なサイズはどれかな？
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
				continue;
			}
			if (size.height <= maxDesiredHeight) {
				if (captureHeight!=size.height && captureHeight <= size.height) {
					continue;
				}
				parameters.setPreviewSize(size.width, size.height);
				captureWidth = size.width;
				captureHeight = size.height;
				_isFoundCapSize = true;
			}
		}
		
		if (!_isFoundCapSize) {
			parameters.setPreviewSize(desiredWidth, desiredHeight);
		}
		
		// フレームレート
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
