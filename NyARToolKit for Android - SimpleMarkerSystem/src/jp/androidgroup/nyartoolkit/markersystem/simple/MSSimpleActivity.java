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
package jp.androidgroup.nyartoolkit.markersystem.simple;


import jp.androidgroup.nyartoolkit.markersystem.AbstractMarkerSystemActivity;
import jp.androidgroup.nyartoolkit.markersystem.renderer.MarkerSystemAbstractRenderer;
import jp.androidgroup.nyartoolkit.markersystem.simple.R;
import jp.nyatla.nyartoolkit.markersystem.INyARMarkerSystemConfig;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystemConfig;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MSSimpleActivity extends AbstractMarkerSystemActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout)this.findViewById(R.id.mainLayout);
	}

	@Override
	protected MarkerSystemAbstractRenderer supplyRenderer() {
		return new CubeRenderer();
	}

	@Override
	protected INyARMarkerSystemConfig supplyMarkerSystemConfig(
			int _caputureWidth, int _caputureHeight) {
		//
		NyARMarkerSystemConfig config = null;
		// サイズに合わせてカメラパラメータを切り替える
		// この実装では上記コメントの実装まで行なっていないので注意してください
		try {
			AssetManager assetMng = getResources().getAssets();
			config = new NyARMarkerSystemConfig(assetMng.open("AR/CameraParam/camera_param_640x480.dat"), _caputureWidth, _caputureHeight);
		} catch (Exception e) {
			finish();
		}
		
		return config;
	}
}