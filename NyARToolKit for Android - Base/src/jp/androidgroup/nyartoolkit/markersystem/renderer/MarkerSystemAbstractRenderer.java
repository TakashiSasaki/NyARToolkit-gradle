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
package jp.androidgroup.nyartoolkit.markersystem.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.androidgroup.nyartoolkit.markersystem.android.NyARAndroidMarkerSystemFactory;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Markersystem用のOpenGL ES Renderer
 * モデルデータの読み出しなどが含まれない、純粋にOpenGL ESで処理する場合の基本クラス。
 * 継承したクラス内で何かしらの描画処理を行わないと何もしないので注意してください。
 */
public class MarkerSystemAbstractRenderer implements GLSurfaceView.Renderer
{
	/**
	 * Logging Tag
	 */
	protected final static String TAG = "NyARTK4And.DefaultRenderer";
	
	/**
	 * 継承したクラス内でマーカーを追加する処理を記述します。
	 * MarkerSystemを初期化した後でないと使えないため、配布サンプルでは、start preview 以降、
	 * onResumeにも書けないので、 Activity.cameraPreviewFrame でフラグ確認を行ってから、
	 * この処理を行なっています。
	 * 
	 * マーカー読み込みだけであれば、クラス変数を定義して、コンストラクタに書いても構いません。
	 * addMarkerは、継承したクラスのこのメソッドに書いてください。
	 * 
	 * @param assetMng
	 * @return
	 */
	public boolean configureARScene(AssetManager assetMng)
	{
		return false;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d(TAG, "onSurfaceCreated");
		// Transparent background
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "onSurfaceChanged");
		gl.glViewport(0, 0, width, height);
		
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		if (NyARAndroidMarkerSystemFactory.getInstance().isMarkerSystemRunning()) {
			draw(gl);
		}
	}
	
	/**
	 * 継承したクラスで表示したいものを実装してください
	 * @param gl
	 */
	public void draw(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	}

}