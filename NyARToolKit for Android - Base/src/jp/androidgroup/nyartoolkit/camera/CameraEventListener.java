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
 *  Yasuhide Matsumoto
 *  Fuu Rokubou
 */
package jp.androidgroup.nyartoolkit.camera;

/**
 * Camera用のイベントリスナー
 */
public interface CameraEventListener {
	/**
	 * カメラプレビューをスタートする
	 * 
	 * @param width カメラプレビューの 幅(pixels)
	 * @param height カメラプレビューの 高さ(pixels)
	 * @param rate フレームレート
	 */
	public void cameraPreviewStarted(int width, int height, int rate);
	
	/**
	 * 新しいフレームが準備できた場合に呼ばれる
	 * @param frame キャプチャ画像のバイト配列
	 */
	public void cameraPreviewFrame(byte[] frame);
	
	/**
	 * プレビューを止める
	 */
	public void cameraPreviewStopped();
	

}
