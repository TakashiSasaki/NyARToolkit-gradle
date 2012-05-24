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
package jp.androidgroup.nyartoolkit.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.androidgroup.nyartoolkit.wrapper.NyARToolKitWrapper;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Single Marker Detector にのみ対応した OpenGL ES Renderer
 * モデルデータの読み出しなどが含まれない、純粋にOpenGL ESで処理する場合の基本クラス。
 * 継承したクラス内で何かしらの描画処理を行わないと何もしないので注意してください。
 */
public class DefaultRenderer implements GLSurfaceView.Renderer
{
	/**
	 * Logging Tag
	 */
	protected final static String TAG = "NyARTK4And.DefaultRenderer";
	
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
		if (NyARToolKitWrapper.getInstance().isNyARRunning) {
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