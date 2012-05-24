package jp.androidgroup.nyartoolkit.sketch;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.androidgroup.nyartoolkit.R;
import jp.androidgroup.nyartoolkit.R.layout;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class AndSketch extends Activity
{
	public ArrayList<IAndSketchEventListerner> _evlistener=new ArrayList<IAndSketchEventListerner>();
	
	
	final static int AST_SETUP=0;
	final static int AST_RUN  =1;
	int _ast=AST_SETUP;
	public AndSketch()
	{
	}
	//Activityのハンドラ
	@Override
	protected void onResume() {
		super.onResume();
		this._glView.onResume();
		try {
			for(IAndSketchEventListerner i : this._evlistener) {
				i.onResume();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	protected void onPause() {
		super.onPause();
		this._glView.onPause();
		try {
//			for(IAndSketchEventListerner i : this._evlistener) {
//				i.onPause();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(this.getClass().getName(), "onCreate");
		this.setupDefaultActivity();
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this._ast=AST_RUN;
	}
	private int _frid=0xffffffff;
	private int _sw;
	private int _sh;
	public void initView(int i_rid)
	{
		this.initView(i_rid,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}
	public FrameLayout initView(int i_rid,int i_width,int i_height)
	{
		this._frid=i_rid;
		this._sw=i_width;
		this._sh=i_height;
		// Create the GL view
		return this._frame_layout;
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		this._frame_layout=((FrameLayout)this.findViewById(this._frid));
		if(this._frame_layout==null){
			Log.d(this.getClass().getName(), "Layout not found. Should call initView in constructor!");
			finish();
		}
		this.setupDefaultView();
		this._frame_layout.addView(this._glView, 0, new LayoutParams(this._sw,this._sh));
		return;
	}
	@Override
	protected void onStop()
	{
		super.onStop();
		try {
			for(IAndSketchEventListerner i : this._evlistener) {
				i.onStop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	protected void onDestory()
	{
		super.onDestroy();
		try {
			for(IAndSketchEventListerner i : this._evlistener) {
				i.onDestroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/////////////////////
	protected GLSurfaceView _glView;
	private GLSurfaceView.Renderer _render;
	private FrameLayout _frame_layout;
	
	/**
	 * onCreate関数からコールします。	
	 */
	protected void setupDefaultActivity()
	{
		// タイトルは不要
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// フルスクリーン表示
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.getWindow().setFormat(PixelFormat.TRANSLUCENT);
		// 画面がスリープに入らないようにする
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// 横向き固定
//		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);		
	}
	/**
	 * onCreate関数からコールします。
	 */
	protected void setupDefaultView()
	{
		this._glView=new GLSurfaceView(this);
		this._glView.setEGLConfigChooser( 8, 8, 8, 8, 16, 0);
		this._glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this._render=new DefaultRenderer(this);
		this._glView.setRenderer(this._render);
	}
	


	



//
//	関数ハンドラ
//
	
	public void setup(GL10 gl)
	{
	}
	/**
	 * 継承したクラスで表示したいものを実装してください
	 * @param gl
	 */
	public void draw(GL10 gl)
	{
	}

	public void addListener(IAndSketchEventListerner i_evl) throws Exception
	{
		this._evlistener.add(i_evl);
		i_evl.onStart();
	}
	public void _finish(Exception e)
	{
		if(e!=null){
			e.printStackTrace();
		}
		super.finish();
	}
}



/**
 * Single Marker Detector にのみ対応した OpenGL ES Renderer
 * モデルデータの読み出しなどが含まれない、純粋にOpenGL ESで処理する場合の基本クラス。
 * 継承したクラス内で何かしらの描画処理を行わないと何もしないので注意してください。
 */
class DefaultRenderer implements GLSurfaceView.Renderer
{
	private AndSketch _parent;
	public DefaultRenderer(AndSketch i_parent)
	{
		this._parent=i_parent;
	}
	/**
	 * Logging Tag
	 */	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// Transparent background
		gl.glClearColor(0.5f, 0.0f, 0.0f, 0.f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		this._parent.setup(gl);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		for(IAndSketchEventListerner i : this._parent._evlistener) {
			i.onGlChanged(gl,width,height);
		}
		gl.glViewport(0, 0, width, height);
	}
	@Override
	public void onDrawFrame(GL10 gl)
	{
		this._parent.draw(gl);
	}
}
