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
 *  Ryo Iizuka(nyatla)
 *  Fuu Rokubou
 */
package jp.androidgroup.nyartoolkit.markersystem.android;

import jp.androidgroup.nyartoolkit.jni.YUV420sp2RGBInterface;
import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.core.raster.rgb.NyARRgbRaster;
import jp.nyatla.nyartoolkit.core.types.NyARBufferType;
import jp.nyatla.nyartoolkit.core.types.NyARIntSize;
import jp.nyatla.nyartoolkit.jogl.utils.NyARGLUtil;
import jp.nyatla.nyartoolkit.markersystem.INyARMarkerSystemConfig;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystem;
import jp.nyatla.nyartoolkit.markersystem.NyARSensor;
import android.util.Log;

/**
 * NyARToolKit Marker system を継承したandroid用のクラスです。
 * ARToolKitなどと同じような順で使えるようにするために、singletonパターンを採用しています。
 * 
 * 呼び出す順番を間違えると大惨事になるのでサンプルを参考に使う順序に気をつけて使用してください。
 * コメントに出来る限り書きますが、間違えたり省かれたりしているので注意してください。
 * 
 * Javaではカメラ周りが独立していますが、うまく分けられなかったためこの中に含めています。
 */
public class NyARAndroidMarkerSystem extends NyARMarkerSystem
{
	/**
	 * Logging Tag
	 */
	protected final static String TAG = "NyARTK4And.NyARAndroidMarkerSystem";
	
	/**
	 * Projection Matrix
	 */
	private float[] _projection_mat;
	
	/**
	 * コンストラクタ
	 * @param i_config
	 * @throws NyARException
	 */
	public NyARAndroidMarkerSystem(INyARMarkerSystemConfig i_config) throws NyARException
	{
		super(i_config);
		this.initInstance(i_config);
		this.setProjectionMatrixClipping(FRUSTUM_DEFAULT_NEAR_CLIP, FRUSTUM_DEFAULT_FAR_CLIP);
	}
	
	protected void initInstance(INyARMarkerSystemConfig i_config) throws NyARException
	{
		super.initInstance(i_config);
		this._projection_mat = new float[16];
		
		rasterWidth = i_config.getScreenSize().w;
		rasterHeight= i_config.getScreenSize().h;
		
		
		initNyARRgbRaster(i_config.getScreenSize());
		
		initARSensor(i_config.getScreenSize());
	}

	/**
	 * OpenGLスタイルのProjectionMatrixを返します。
	 * @param i_gl
	 * @return
	 * [readonly]
	 */
	public float[] getGlProjectionMatrixf()
	{
		return this._projection_mat;
	}
	
	public void setProjectionMatrixClipping(double i_near,double i_far)
	{
		super.setProjectionMatrixClipping(i_near,i_far);
		NyARGLUtil.toCameraFrustumRHf(this._ref_param, 1, i_near, i_far, this._projection_mat);
	}
	
	/**
	 * この関数はOpenGL形式の姿勢変換行列を新規に割り当てて返します。
	 * @param i_buf
	 * @return
	 */
	public double[] getGlMarkerMatrix(int i_id)
	{
		return this.getGlMarkerMatrix(i_id,new double[16]);
	}
	/**
	 * この関数は、i_bufに指定idのOpenGL形式の姿勢変換行列を設定して返します。
	 * @param i_id
	 * @param i_buf
	 * @return
	 */
	public double[] getGlMarkerMatrix(int i_id,double[] i_buf)
	{
		NyARGLUtil.toCameraViewRH(this.getMarkerMatrix(i_id),1,i_buf);
		return i_buf;
	}
	
	/**
	 * この関数はOpenGL形式の姿勢変換行列を新規に割り当てて返します。
	 * @param i_buf
	 * @return
	 */
	public float[] getGlMarkerMatrixf(int i_id)
	{
		return this.getGlMarkerMatrixf(i_id, new float[16]);
	}
	
	/**
	 * この関数は、i_bufに指定idのOpenGL形式の姿勢変換行列を設定して返します。
	 * @param i_id
	 * @param i_buf
	 * @return
	 */
	public float[] getGlMarkerMatrixf(int i_id, float[] i_buf)
	{
		NyARGLUtil.toCameraViewRHf(this.getMarkerMatrix(i_id),1,i_buf);
		return i_buf;
	}

// 実装は後回し
	/**
	 * {@link #addARMarker(INyARRgbRaster, int, int, double)}のラッパーです。BufferedImageからマーカパターンを作ります。
	 * 引数については、{@link #addARMarker(INyARRgbRaster, int, int, double)}を参照してください。
	 * @param i_img
	 * @param i_patt_resolution
	 * @param i_patt_edge_percentage
	 * @param i_marker_size
	 * @return
	 * @throws NyARException
	 */
//	public int addARMarker(BufferedImage i_img,int i_patt_resolution,int i_patt_edge_percentage,double i_marker_size) throws NyARException
//	{
//		int w=i_img.getWidth();
//		int h=i_img.getHeight();
//		NyARBufferedImageRaster bmr=new NyARBufferedImageRaster(i_img);
//		NyARCode c=new NyARCode(i_patt_resolution,i_patt_resolution);
//		//ラスタからマーカパターンを切り出す。
//		INyARPerspectiveCopy pc=(INyARPerspectiveCopy)bmr.createInterface(INyARPerspectiveCopy.class);
//		NyARRgbRaster tr=new NyARRgbRaster(i_patt_resolution,i_patt_resolution);
//		pc.copyPatt(0,0,w,0,w,h,0,h,i_patt_edge_percentage, i_patt_edge_percentage,4, tr);
//		//切り出したパターンをセット
//		c.setRaster(tr);
//		return super.addARMarker(c,i_patt_edge_percentage,i_marker_size);
//	}
	/**
	 * この関数は、{@link #getMarkerPlaneImage(int, NyARSensor, int, int, int, int, int, int, int, int, INyARRgbRaster)}
	 * のラッパーです。取得画像を{@link #BufferedImage}形式で返します。
	 * @param i_id
	 * @param i_sensor
	 * @param i_x1
	 * @param i_y1
	 * @param i_x2
	 * @param i_y2
	 * @param i_x3
	 * @param i_y3
	 * @param i_x4
	 * @param i_y4
	 * @param i_img
	 * @return
	 * @throws NyARException
	 */
//	public BufferedImage getMarkerPlaneImage(
//		int i_id,
//		NyARSensor i_sensor,
//	    int i_x1,int i_y1,
//	    int i_x2,int i_y2,
//	    int i_x3,int i_y3,
//	    int i_x4,int i_y4,
//	    BufferedImage i_img) throws NyARException
//		{
//			NyARBufferedImageRaster bmr=new NyARBufferedImageRaster(i_img);
//			super.getMarkerPlaneImage(i_id, i_sensor, i_x1, i_y1, i_x2, i_y2, i_x3, i_y3, i_x4, i_y4,bmr);
//			return i_img;
//		}
	/**
	 * この関数は、{@link #getMarkerPlaneImage(int, NyARSensor, int, int, int, int, INyARRgbRaster)}
	 * のラッパーです。取得画像を{@link #BufferedImage}形式で返します。
	 * @param i_id
	 * マーカid
	 * @param i_sensor
	 * 画像を取得するセンサオブジェクト。通常は{@link #update(NyARSensor)}関数に入力したものと同じものを指定します。
	 * @param i_l
	 * @param i_t
	 * @param i_w
	 * @param i_h
	 * @param i_raster
	 * 出力先のオブジェクト
	 * @return
	 * 結果を格納したi_rasterオブジェクト
	 * @throws NyARException
	 */
//	public BufferedImage getMarkerPlaneImage(
//		int i_id,
//		NyARSensor i_sensor,
//	    int i_l,int i_t,
//	    int i_w,int i_h,
//	    BufferedImage i_img) throws NyARException
//    {
//		NyARBufferedImageRaster bmr=new NyARBufferedImageRaster(i_img);
//		super.getMarkerPlaneImage(i_id, i_sensor, i_l, i_t, i_w, i_h, bmr);
//		this.getMarkerPlaneImage(i_id,i_sensor,i_l+i_w-1,i_t+i_h-1,i_l,i_t+i_h-1,i_l,i_t,i_l+i_w-1,i_t,bmr);
//		return i_img;
//    }
	
	/**
	 * ラスタ（解析用）サイズの幅
	 */
	protected int rasterWidth = 640;
	
	/**
	 * ラスタ（解析用）サイズの高さ
	 */
	protected int rasterHeight = 480;
	
	/**
	 * ARSensor に与える Raster
	 * Cameraからの映像を保持するクラス
	 */
	protected NyARRgbRaster raster = null;
	
	protected NyARSensor arSensor = null;
	/**
	 * NyARRgbRaster の初期化
	 * 
	 * @param width  Rasterサイズの幅
	 * @param height Rasterサイズの高さ
	 */
	protected void initNyARRgbRaster(NyARIntSize i_size)
	{
		Log.d(TAG, "initNyARRgbRaster");
		try {
			raster = new NyARRgbRaster(i_size.w, i_size.h, NyARBufferType.BYTE1D_R8G8B8_24);
		} catch (NyARException e) {
			Log.e(TAG, "Not create NyARRgbRaster_RGB.");
		}
	}
	
	/**
	 * ARSensor 初期化
	 * @param i_size raster のサイズ
	 */
	protected void initARSensor(NyARIntSize i_size)
	{
		try {
			arSensor = new NyARSensor(i_size);
		} catch (NyARException e) {
			Log.e(TAG, "Initialize NyARSensor Error.");
		}
	}
	
	/**
	 * 状況を更新する
	 * @param frame
	 */
	public void update(byte[] frame)
	{
		Log.d(TAG, "setCapturFrame");
		// JNI を使って形式変換
		byte[] rasterBuffer = new byte[rasterWidth * rasterHeight * 3];
		YUV420sp2RGBInterface.decodeYUV420SP(rasterBuffer, frame, rasterWidth, rasterHeight, 1);
		
		try {
			// NyARRaster にセット
			raster.wrapBuffer(rasterBuffer);
			arSensor.update(raster);
		} catch (NyARException e) {
			Log.e(TAG, "Raster buffer write error.");
			return;
		}
		
		try {
			update(arSensor);
		} catch (NyARException e) {
			Log.e(TAG, "Marker system detect update error.");
		}
	}
}
