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
package jp.androidgroup.nyartoolkit.markersystem.android;

import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.markersystem.INyARMarkerSystemConfig;

/**
 * NyARAndroidMarkerSystem を singleton化するためだけに作成したクラス。
 * Flyweightパターンになっている？
 */
public class NyARAndroidMarkerSystemFactory {
	
	/**
	 * Singleton Instance
	 */
	private static NyARAndroidMarkerSystemFactory instance = new NyARAndroidMarkerSystemFactory();
	
	/**
	 * マーカーシステム本体
	 */
	private NyARAndroidMarkerSystem nyartkMarkerSystem = null;
	
	/**
	 * 
	 */
	private boolean isMarkerSystemRunning = false;
	
	/**
	 * コンストラクタ
	 */
	private NyARAndroidMarkerSystemFactory()
	{
		// no work
	}
	
	/**
	 * 仲介用のインスタンスを取得
	 * 基本的に下記のようにして使用することになる。
	 * 
	 * NyARAndroidMarkerSystemFactory.getInstance().getMarkerSystem().method();
	 * 
	 * 上記のような使い方を剃る前にかならず、Marker system の初期化をおこなってください。
	 * 
	 * @return
	 */
	public static NyARAndroidMarkerSystemFactory getInstance() {
		return instance;
	}
	
	/**
	 * Marker system 初期化
	 * 
	 * @param i_config
	 * @return
	 */
	public boolean configureMarkerSystem(INyARMarkerSystemConfig i_config)
	{
		
		try {
			nyartkMarkerSystem = new NyARAndroidMarkerSystem(i_config);
		} catch (NyARException e) {
			return false;
		}
		
		isMarkerSystemRunning = true;
		
		return isMarkerSystemRunning;
	}
	
	/**
	 * これを利用する前に、かならずどこかのタイミングで configureMarkerSystem を実行してください。
	 * 初期化されていない場合、nullが返却されます。
	 * 
	 * @return
	 */
	public NyARAndroidMarkerSystem getMarkerSystem()
	{
		return nyartkMarkerSystem;
	}
	
	/**
	 * Marker system が初期化済みかを確認するフラグ
	 * @return
	 */
	public boolean isMarkerSystemRunning()
	{
		return isMarkerSystemRunning;
	}
	
}
