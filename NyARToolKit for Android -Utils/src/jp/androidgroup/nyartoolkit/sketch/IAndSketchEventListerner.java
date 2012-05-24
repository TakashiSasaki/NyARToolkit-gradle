package jp.androidgroup.nyartoolkit.sketch;

import javax.microedition.khronos.opengles.GL10;

public interface IAndSketchEventListerner {
	public void onResume();
	public void onStart() throws Exception;
	public void onDestroy() throws Exception;
	public void onStop() throws Exception;
	public void onGlChanged(GL10 i_gl,int i_width,int i_height);
}
