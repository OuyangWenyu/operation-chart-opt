package com.wenyu.hydroelements.curve;

import java.io.Serializable;

/**
 * 二维曲线，第一组数据必须按升序排列，第二组数据与第一组数据对应起来
 * @author 华科水电306调度组
 *
 */
public class DoubleCurve implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6415277668713282847L;
	private BaseStatistics v0;
	private BaseStatistics v1;
	private double[][] curveData;
	
	//data里面的数据最好按照升序排列
	public DoubleCurve(double[][] data){
		curveData = new double [data.length][data[0].length];
		v0 = new BaseStatistics();
		v1 = new BaseStatistics();
		
		for(int i=0;i<data.length;i++)
		{
			v0.add(data[i][0]);
			v1.add(data[i][1]);
		}
		for(int i=0;i<data.length;i++)
		{
			for (int j=0;j<data[0].length;j++) {
				curveData[i][j] = data[i][j];
			}
		}
	}

	public double getV1ByV0(double value) {
		// TODO Auto-generated method stub
		return CurveMathMethods.halfSearch(value, v0, v1);
	}
	
	public double getV0ByV1(double value) {
		// TODO Auto-generated method stub
		return CurveMathMethods.halfSearch(value, v1, v0);
	}

	public double[] getV1Array() {
		// TODO Auto-generated method stub
		return v1.getArray();
	}

	public double[] getV0Array() {
		// TODO Auto-generated method stub
		return v0.getArray();
	}

	public double getMinV0() {
		// TODO Auto-generated method stub
		return v0.getMin();
	}

	public double getMaxV0() {
		// TODO Auto-generated method stub
		return v0.getMax();
	}

	public double[][] getCurveData() {
		return curveData;
	}

	public void setCurveData(double[][] curveData) {
		this.curveData = curveData;
	}
	
	
}
