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
package jp.androidgroup.nyartoolkit.wrapper;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.util.Log;
import jp.androidgroup.nyartoolkit.jni.YUV420sp2RGBInterface;
import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.core.param.NyARParam;
import jp.nyatla.nyartoolkit.core.raster.rgb.NyARRgbRaster;
import jp.nyatla.nyartoolkit.core.transmat.NyARTransMatResult;
import jp.nyatla.nyartoolkit.core.types.NyARBufferType;
import jp.nyatla.nyartoolkit.detector.NyARSingleDetectMarker;
import jp.nyatla.nyartoolkit.jogl.utils.NyARGLUtil;


/**
 * NyARToolKit の Core
 * 
 * 基本的にこのクラスを経由してNyARToolKitを使用する。
 * 
 * Detector の切替えなどもここで行う。
 */
/**
 * @author Sixwish
 *
 */
public class NyARToolKitWrapper
{
	/**
	 * Logging Tag
	 */
	protected final static String TAG = "NyARTK4And.NyARToolKitWrapper";
	
	/**
	 * The single instance of NyARToolKitWrapper
	 */
	protected static NyARToolKitWrapper instance = null;
	
	/**
	 * NyARToolKit が初期化完了しているかどうかの判断フラグ
	 */
	public boolean isNyARRunning = false;
	
	/**
	 * ラスタ（解析用）サイズの幅
	 */
	protected int rasterWidth = 640;
	
	/**
	 * ラスタ（解析用）サイズの高さ
	 */
	protected int rasterHeight = 480;
	
	/**
	 * キャプチャサイズの幅
	 */
	protected int captureWidth;
	
	/**
	 * キャプチャサイズの高さ
	 */
	protected int captureHeight;
	
	/**
	 * NyARTK で解析に使う Raster データ
	 */
	protected NyARRgbRaster raster = null;
	
	/**
	 * @see jp.nyatla.nyartoolkit.coreparam.NyARParam
	 */
	protected NyARParam arCameraParam = null;
	
	/**
	 * マーカーのパターン情報が含まれるクラス
	 */
	protected NyARCode arMarkerPattern = null;
	
	/**
	 * マーカーの一辺のサイズ
	 * 1mm = 1 で設定すると丁度いいはずです。
	 */
	protected double markerWidth = 80.0;
	
	/**
	 * OpenGL ES projection matrix
	 * (CameraFrustumRHF)
	 */
	protected float[] glProjectionMatrix = null;
	
	/**
	 * @see jp.nyatla.nyartoolkit.detector.NyARSingleIdMarkerDetector
	 */
	protected NyARSingleDetectMarker detector = null;
	
	/**
	 * マーカーを検出位しているかどうかのフラグ
	 */
	protected boolean detectMarker = false;
	
	/**
	 * マーカーの変換行列
	 */
	protected NyARTransMatResult transmat_result = new NyARTransMatResult();
	
	/**
	 * Constructor
	 */
	protected NyARToolKitWrapper()
	{
		captureWidth = rasterWidth;
		captureHeight = rasterHeight;
	}
	
	/**
	 * Single instance of the NyARToolKitWrapper class.
	 * 
	 * @return The single instance of NyARToolKitWrapper
	 */
	static public NyARToolKitWrapper getInstance()
	{
		if (instance==null) {
			instance = new NyARToolKitWrapper();
		}
		return instance;
	}
	
	/**
	 * Set Marker Pattern
	 * @param markerPatt
	 */
	public void setNyARCode(NyARCode markerPatt)
	{
		arMarkerPattern = markerPatt;
	}
	
	/**
	 * Rasterサイズ取得
	 * @return Raster width
	 */
	public int getRasterWidth()
	{
		return rasterWidth;
	}
	
	/**
	 * Rasterサイズ取得
	 * @return raster height
	 */
	public int getRasterHeight()
	{
		return rasterHeight;
	}
	
	/**
	 * キャプチャサイズの設定
	 * @param width キャプチャサイズの幅
	 * @param height キャプチャサイズの高さ
	 */
	public void setRasterSize(int width, int height)
	{
		this.rasterWidth  = width;
		this.rasterHeight = height;
	}
	
	/**
	 * 設定されているキャプチャサイズ
	 * @return capture width
	 */
	public int getCaptureWidth()
	{
		return captureWidth;
	}
	
	/**
	 * 設定されているキャプチャサイズ
	 * @return capture height
	 */
	public int getCaptureHeight() {
		return captureHeight;
	}
	
	/**
	 * キャプチャサイズの設定
	 * @param width キャプチャサイズの幅
	 * @param height キャプチャサイズの高さ
	 */
	public void setCaptureSize(int width, int height)
	{
		this.captureWidth = width;
		this.captureHeight = height;
	}
	
	/**
	 * NyARToolKit の初期化
	 *  ・キャプチャサイズ
	 *  ・ラスタサイズ
	 *  ・カメラパラメータ
	 *  ・Raster初期化
	 *  ・Detector初期化
	 * 
	 * キャプチャサイズを大きくして処理が重い場合は、この処理内でのサイズを変更するようにしてください。
	 * なお、その場合、 raster への書き込みサイズも変更してください。
	 * 
	 * @param assetManger assets にアクセスするために使用する。
	 * @param width  キャプチャサイズの幅
	 * @param height キャプチャサイズの高さ
	 */
	public void initNyARToolKit(AssetManager assetManger, int width, int height)
	{
		Log.d(TAG, "Initialize NyARToolKit.");
		// キャプチャサイズの幅や高さを設定
		captureWidth  = rasterWidth  = width;
		captureHeight = rasterHeight = height;
		
		// カメラパラメータ初期化
		setNyARParam(assetManger, width, height);
		
		// Raster 初期化
		initNyARRgbRaster(rasterWidth, rasterHeight);
		
		// Detector 初期化
		// TODO あとで他のDetectorも使えるようにする
		try {
			detector = NyARSingleDetectMarker.createInstance(arCameraParam, arMarkerPattern, markerWidth);
			detector.setContinueMode(true);
		} catch (NyARException e) {
			Log.e(TAG, "Detector create error.");
		}
		
		isNyARRunning = true;
	}
	
	/**
	 * NyARPram の初期化
	 * camera_param.dat を読み込み適応する処理
	 * パラメータファイルは、assets/CameraParam にあります。
	 * 
	 * アスペクト比が、4:3 以外のキャプチャ、ラスタサイズを使う場合は、この処理内で
	 * 読み込むパラメータファイルを変更する必要があります。
	 * 
	 * @param width  Rasterサイズの幅
	 * @param height Rasterサイズの高さ
	 */
	protected void setNyARParam(AssetManager assetManger, int width, int height)
	{
		Log.d(TAG, "setNyARParam");
		try {
			// assets から取り出す
			InputStream inputStream = assetManger.open("AR/CameraParam/camera_param_640x480.dat");
			// init camera parameter
			arCameraParam = new NyARParam();
			arCameraParam.loadARParam(inputStream);
			arCameraParam.changeScreenSize(width, height);
		} catch (IOException e) {
			Log.e(TAG, "Camera parameter file is not found.");
		} catch (NyARException e) {
			Log.e(TAG, "Not create NyARParam.");
		}
	}
	
	/**
	 * OpenGLのProjection Matrix を取得する。
	 * 最初の一回のみ計算を行う。
	 * {@link setNyARParam } を実行後に呼び出してください。
	 * @return
	 */
	public float[] getGlProjectionMatrix()
	{
		if (arCameraParam==null) {
			return null;
		}
		if (glProjectionMatrix==null) {
			 glProjectionMatrix = new float[16];
			double[] _glProjection = new double[16];
			NyARGLUtil.toCameraFrustumRH(arCameraParam, 1.0, 10, 10000, _glProjection);
			
			for (int i=0; i<_glProjection.length; i++) {
				glProjectionMatrix[i] = (float) _glProjection[i];
			}
		}
		
		return glProjectionMatrix;
	}

	/**
	 * NyARRgbRaster の初期化
	 * 
	 * @param width  Rasterサイズの幅
	 * @param height Rasterサイズの高さ
	 */
	protected void initNyARRgbRaster(int width, int height)
	{
		Log.d(TAG, "initNyARRgbRaster");
		try {
			raster = new NyARRgbRaster(width, height, NyARBufferType.BYTE1D_R8G8B8_24);
		} catch (NyARException e) {
			Log.e(TAG, "Not create NyARRgbRaster_RGB.");
		}
	}
	public void setCapturFrame(byte[] frame)
	{
		Log.d(TAG, "setCapturFrame");
		// JNI を使って形式変換
		byte[] rasterBuffer = new byte[rasterWidth * rasterHeight * 3];
		YUV420sp2RGBInterface.decodeYUV420SP(rasterBuffer, frame, rasterWidth, rasterHeight, 1);
		
		try {
			// NyARRaster にセット
			raster.wrapBuffer(rasterBuffer);
			// Detect処理
			detectMarker = detector.detectMarkerLite(raster, 110);
			
			if (!detectMarker) {
				// 非認識時の処理
				transmat_result = null;
				transmat_result = new NyARTransMatResult();
			} else {
				Log.d(TAG, "Marker Detected.");
				
				// Matrix 取得
				detector.getTransmat(transmat_result);
			}
			
		} catch (NyARException e) {
			Log.e(TAG, "Raster buffer write error.");
			return;
		}
	}
	
	/**
	 * マーカーを検出しているかどうかを返す
	 * 識別時は、true
	 * @return
	 */
	public boolean queryMarkerVisible()
	{
		return detectMarker;
	}
	
	/**
	 * マーカーの変換行列を OpenGL ES 用にしたものを取得する
	 * @return
	 */
	public float[] queryMarkerTransformation()
	{
		double[] _resultMat = new double[16];
		NyARGLUtil.toCameraViewRH(transmat_result, 1.0, _resultMat);
		
		float[] _resultMatf = new float[16];
		for (int i=0; i<_resultMat.length; i++) {
			_resultMatf[i] = (float)_resultMat[i];
		}
		
		return _resultMatf;
	}
	
}
