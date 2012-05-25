package jp.androidgroup.nyartoolkit.markersystem;

import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.jogl.utils.NyARGLUtil;
import jp.nyatla.nyartoolkit.markersystem.INyARMarkerSystemConfig;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystem;

public class NyARAndMarkerSystem extends NyARMarkerSystem
{

	public NyARAndMarkerSystem(INyARMarkerSystemConfig i_config)
			throws NyARException {
		super(i_config);
	}
	private float[] _projection_mat;
	protected void initInstance(INyARMarkerSystemConfig i_config) throws NyARException
	{
		super.initInstance(i_config);
		this._projection_mat=new float[16];
	}

	/**
	 * OpenGLスタイルのProjectionMatrixを返します。
	 * @param i_gl
	 * @return
	 * [readonly]
	 */
	public float[] getGlProjectionMatrix()
	{
		return this._projection_mat;
	}
	public void setProjectionMatrixClipping(double i_near,double i_far)
	{
		super.setProjectionMatrixClipping(i_near,i_far);
		NyARGLUtil.toCameraFrustumRH(this._ref_param,1,i_near,i_far,this._projection_mat);
	}
	/**
	 * この関数は、i_bufに指定idのOpenGL形式の姿勢変換行列を設定して返します。
	 * @param i_id
	 * @param i_buf
	 * @return
	 */
	public void getMarkerMatrix(int i_id,float[] i_buf)
	{
		NyARGLUtil.toCameraViewRH(this.getMarkerMatrix(i_id),1,i_buf);
	}
	/**
	 * この関数はOpenGL形式の姿勢変換行列を新規に割り当てて返します。
	 * @param i_buf
	 * @return
	 */
	public float[] getGlMarkerMatrix(int i_id)
	{
		float[] b=new float[16];
		this.getMarkerMatrix(i_id,b);
		return b;
	}
}
