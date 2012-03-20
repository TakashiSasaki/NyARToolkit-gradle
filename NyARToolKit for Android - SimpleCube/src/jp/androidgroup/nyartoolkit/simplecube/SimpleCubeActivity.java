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
package jp.androidgroup.nyartoolkit.simplecube;

import java.io.IOException;
import java.io.InputStream;

import jp.androidgroup.nyartoolkit.AbstractARActivity;
import jp.androidgroup.nyartoolkit.renderer.DefaultRenderer;
import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.core.NyARException;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Simple Cube AR Application
 * 
 */
public class SimpleCubeActivity extends AbstractARActivity {
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected NyARCode supplyMarkerPattern() {
		NyARCode markerPatt = null;
		// assets から、パターンファイルを取り出す
		try {
			//
			markerPatt = new NyARCode( 16, 16);
			// assets から取り出すためのマネージャー
			AssetManager assetManager = getResources().getAssets();
			InputStream _markerIS = assetManager.open("AR/Marker/hiro.pat");
			// パターンセット
			markerPatt.loadARPatt(_markerIS);
		} catch (IOException e) {
			Log.e(TAG, "ARToolKit marker file is not found.");
		} catch (NyARException e) {
			Log.e(TAG, "Not created NyARCode.");
		}
		
		return markerPatt;
	}

	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout)this.findViewById(R.id.mainLayout);
	}

	@Override
	protected DefaultRenderer supplyRenderer() {
		return new CubeRenderer();
	}
}