/* 
 * PROJECT: NyARToolkit Professional Addon
 * --------------------------------------------------------------------------------
 * The NyARToolkit is Java edition ARToolKit class library.
 * Copyright (C)2012 Ryo Iizuka
 * wm@nyatla.jp
 * http://nyatla.jp
 * 
 * This work is based on the ARToolKit4.
 * Copyright 2010-2011 ARToolworks, Inc. All rights reserved.
 *
 */
package jp.nyatla.nyartoolkit.dev.pro.core.integralimage;

import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.core.raster.INyARGrayscaleRaster;
import jp.nyatla.nyartoolkit.core.raster.NyARRaster;
import jp.nyatla.nyartoolkit.core.types.NyARBufferType;
import jp.nyatla.nyartoolkit.pro.core.integralimage.driver.INyARIntegralImageDriver;
import jp.nyatla.nyartoolkit.pro.core.integralimage.driver.NyARIntegralImageDriver_INT1D;
import jp.nyatla.nyartoolkit.pro.core.integralimage.driver.NyARIntegralImageGenerator_INT1D;
import jp.nyatla.nyartoolkit.pro.core.surf.NyARGaussTable;




public class NyARIntegralImage extends NyARRaster
{
	public interface IIntegralImageGenerator
	{
		public void genIntegralImage(INyARGrayscaleRaster i_in) throws NyARException;

	}	
	public NyARIntegralImage(int i_width,int i_height) throws NyARException
	{
		super(i_width,i_height,NyARBufferType.INT1D,true);
		NyARGaussTable.initalizeTable();
	}
	private INyARGrayscaleRaster _last_input_raster=null;
	private IIntegralImageGenerator _ig;
	public Object createInterface(Class<?> iIid) throws NyARException
	{
		if(iIid==INyARIntegralImageDriver.class){
			//画像タイプからファクトリに作らせろ
//			return new IntegralImageDriver_INT1D(this);
			return new NyARIntegralImageDriver_INT1D(this);
		}
		// TODO Auto-generated method stub
		throw new NyARException();
	}	
	public void genIntegralImage(INyARGrayscaleRaster i_in) throws NyARException
	{
		if(i_in!=_last_input_raster){
			this._ig=new NyARIntegralImageGenerator_INT1D(this);
			this._last_input_raster=i_in;
		}
		this._ig.genIntegralImage(i_in);
	}
}

