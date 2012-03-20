/**
 * NyARToolkit for Android SDK
 *   Copyright (C)2010 NyARToolkit for Android team
 *   Copyright (C)2010 R.Iizuka(nyatla)
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
package jp.androidgroup.nyartoolkit.simplecube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import jp.androidgroup.nyartoolkit.renderer.DefaultRenderer;
import jp.androidgroup.nyartoolkit.wrapper.NyARToolKitWrapper;

import android.util.Log;

/**
 * シンプルな箱を書くだけのRenderer
 * 
 */
public class CubeRenderer extends DefaultRenderer {

	/**
	 * Logging Tag
	 */
	protected final static String TAG = "NyARTK4And.Simple.Renderer";
	
	private double markerWidth = 80.0;
	
	/**
	 * 頂点バッファ
	 */
	private FloatBuffer vertexBuffer;
	
	/**
	 * 色のバッファ
	 */
	private FloatBuffer colorBuffer;
	
	/**
	 * 法線
	 */
	private ByteBuffer indexBuffer;
	
	public CubeRenderer()
	{
		float length = (float)markerWidth/2.0f;
		// 左下から周回。Zのみ、マーカーの半分を上に上げるために加算
		float[] square = {
				-length, -length, -length+length, // 0(bottom)
				 length, -length, -length+length, // 
				 length,  length, -length+length, // 
				-length,  length, -length+length, // 3
				-length, -length,  length+length, // 4(top)
				 length, -length,  length+length, // 
				 length,  length,  length+length, // 
				-length,  length,  length+length
				};
		vertexBuffer = buildFloatBuffer(square);
		
		// 色設定
		float colors[] = {
				0.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 0.0f, 1.0f,
				0.0f, 1.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f, 1.0f,
				1.0f, 0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				0.0f, 1.0f, 1.0f, 1.0f
		};
		colorBuffer = buildFloatBuffer(colors);
		
		// 面設定
		byte indices[] = {
				0, 4, 5,	0, 5, 1,
				1, 5, 6, 	1, 6, 2, 
				2, 6, 7, 	2, 7, 3, 
				3, 7, 4, 	3, 4, 0, 
				4, 7, 6, 	4, 6, 5, 
				3, 0, 1, 	3, 1, 2
		};
		indexBuffer = buildByteBuffer(indices);
	}
	
	public FloatBuffer buildFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	public ByteBuffer buildByteBuffer(byte[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length);
		bb.put(arr);
		bb.position(0);
		return bb;
	}

	public void _drawModel(GL10 gl)
	{
		gl.glColorPointer( 4, GL10.GL_FLOAT, 0, colorBuffer);
		gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, vertexBuffer);
		
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, indexBuffer);
		
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	/* (non-Javadoc)
	 * @see jp.nyatla.nyartoolkit.android.renderer.DefaultRenderer#draw(javax.microedition.khronos.opengles.GL10)
	 */
	@Override
	public void draw(GL10 gl)
	{
		Log.d(TAG, "draw");
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
//		gl.glLoadIdentity();
		gl.glLoadMatrixf(NyARToolKitWrapper.getInstance().getGlProjectionMatrix(), 0);
		
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glFrontFace(GL10.GL_CW);
		
		if (NyARToolKitWrapper.getInstance().queryMarkerVisible()) {
			Log.d(TAG, "draw visible");
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glLoadMatrixf(NyARToolKitWrapper.getInstance().queryMarkerTransformation(), 0);
			
			Log.d(TAG, "draw model");
			_drawModel(gl);
		}
		
	}
	
	
}
