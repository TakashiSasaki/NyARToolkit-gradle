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
package jp.androidgroup.nyartoolkit.jni;

/**
 * @author Sixwish
 *
 */
public class YUV420sp2RGBInterface
{
	// JNI Library を初期化、クラスを new した瞬間に実行される
	static {
		System.loadLibrary("yuv420sp2rgb");
	}
	
	/**
	 * YUV から RGB 形式に変換する処理
	 * 
	 * @param rgb NyARTK に渡す rgb 形式の int 配列
	 * @param yuv420sp Android のカメラから取得した YUV 形式の byte 配列
	 * @param width  幅
	 * @param height 高さ
	 * @param type 種類
	 */
	public static native void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height, int type);
	
	/**
	 * YUV から RGB 形式に変換する処理
	 * (基本的にこちらを使う)
	 * 
	 * @param rgb NyARTK に渡す rgb 形式の byte 配列
	 * @param yuv420sp Android のカメラから取得した YUV 形式の byte 配列
	 * @param width  幅
	 * @param height 高さ
	 * @param type 種類
	 */
	public static native void decodeYUV420SP(byte[] rgb, byte[] yuv420sp, int width, int height, int type);

}
