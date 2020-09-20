package com.wenyu.hydroelements.curve;

import java.io.Serializable;

/**
 * 第一维数据必须按升序排列，针对第一维里的每个二维数据，二维数据的第一维数据必须按升序排列
 * @author 华科水电306调度组
 *
 */
public class TripleCurve implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8054616606545380434L;
	private BaseStatistics[] value1;
	private BaseStatistics[] value2;
	private BaseStatistics value0;
	//先将一列数据固定住，不重复的存储一列数据给value0作为三维中的第一维，例如选择水头第一维，则对于每一个水头，都有一列流量对应于一列出力
	private double[][] tribleData;
	
	/**
	 * @return the value1
	 */
	public BaseStatistics[] getValue1() {
		return value1;
	}

	/**
	 * @param value1 the value1 to set
	 */
	public void setValue1(BaseStatistics[] value1) {
		this.value1 = value1;
	}

	/**
	 * @return the value2
	 */
	public BaseStatistics[] getValue2() {
		return value2;
	}

	/**
	 * @param value2 the value2 to set
	 */
	public void setValue2(BaseStatistics[] value2) {
		this.value2 = value2;
	}

	/**
	 * @return the value0
	 */
	public BaseStatistics getValue0() {
		return value0;
	}

	/**
	 * @param value0 the value0 to set
	 */
	public void setValue0(BaseStatistics value0) {
		this.value0 = value0;
	}

	public TripleCurve(double[][] data) {

		tribleData = new double [data.length][data[0].length];
		for(int i=0;i<data.length;i++)
		{
			for (int j=0;j<data[0].length;j++) {
				tribleData[i][j] = data[i][j];
			}
		}
		
		value0 = new BaseStatistics();

		double currentValue0 = data[0][0];
		value0.add(currentValue0);
		for (int i = 0; i < data.length; i++) {
			if (currentValue0 != data[i][0]) {
				currentValue0 = data[i][0];
				value0.add(data[i][0]);
			}
		}

		value1 = new BaseStatistics[value0.getArray().length];
		value2 = new BaseStatistics[value0.getArray().length];

		currentValue0 = data[0][0];
		int index = 0;
		value1[0] = new BaseStatistics();
		value2[0] = new BaseStatistics();
		for (int i = 0; i < data.length; i++) {
			if (currentValue0 != data[i][0]) {
				currentValue0 = data[i][0];
				index++;
				value1[index] = new BaseStatistics();
				value2[index] = new BaseStatistics();
			}

			value1[index].add(data[i][1]);
			value2[index].add(data[i][2]);
		}

		System.out.println();
	}

	public static void main(String[] args) {



	}

	public double getV2ByV0V1(double head, double flow) {
		// TODO Auto-generated method stub
		double[] harray = value0.getArray();

		if (head < harray[0])
			head = harray[0];
		else if (head > (harray[harray.length - 1]))
			head = harray[harray.length - 1];

		int position = CurveMathMethods.halfLocation(head, harray);
		if (harray[position] < head) {
			// double[] a1 = value1[position].getArray();
			// double[] a2 = value1[position + 1].getArray();
			double temp1, temp2;
			temp1 = CurveMathMethods.halfSearch(flow, value1[position],
					value2[position]);
			temp2 = CurveMathMethods.halfSearch(flow, value1[position + 1],
					value2[position + 1]);
			double pi = (head - harray[position])
					/ (harray[position + 1] - harray[position]);
			return pi * (temp2 - temp1) + temp1;

		} else if (harray[position] > head) {
			// double[] a1 = value1[position - 1].getArray();
			// double[] a2 = value1[position].getArray();
			double temp1, temp2;
			temp1 = CurveMathMethods.halfSearch(flow, value1[position - 1],
					value2[position - 1]);
			temp2 = CurveMathMethods.halfSearch(flow, value1[position],
					value2[position]);
			double pi = (head - harray[position - 1])
					/ (harray[position] - harray[position - 1]);
			return pi * (temp2 - temp1) + temp1;

		} else {
			return CurveMathMethods.halfSearch(flow, value1[position],
					value2[position]);
		}

	}

	public double getV1ByV0V2(double head, double power) {
		// TODO Auto-generated method stub
		double[] harray = value0.getArray();

		if (head < harray[0])
			head = harray[0];
		else if (head > (harray[harray.length - 1]))
			head = harray[harray.length - 1];

		int position = CurveMathMethods.halfLocation(head, harray);
		if (harray[position] < head) {
			double temp1, temp2;
			temp1 = CurveMathMethods.halfSearch(power, value2[position],
					value1[position]);
			temp2 = CurveMathMethods.halfSearch(power, value2[position + 1],
					value1[position + 1]);
			double pi = (head - harray[position])
					/ (harray[position + 1] - harray[position]);
			return pi * (temp2 - temp1) + temp1;

		} else if (harray[position] > head) {
			double temp1, temp2;
			temp1 = CurveMathMethods.halfSearch(power, value2[position - 1],
					value1[position - 1]);
			temp2 = CurveMathMethods.halfSearch(power, value2[position],
					value1[position]);
			double pi = (head - harray[position - 1])
					/ (harray[position] - harray[position - 1]);
			return pi * (temp2 - temp1) + temp1;

		} else {
			return CurveMathMethods.halfSearch(power, value2[position],
					value1[position]);
		}
	}

	public double[] getV1sByV0(double head) {
		// TODO Auto-generated method stub
		double[] harray = value0.getArray();

		int position;
		if (head < harray[0])
			position = CurveMathMethods.halfLocation(harray[0], harray);
		else if (head > (harray[harray.length - 1]))
			position = CurveMathMethods.halfLocation(harray[harray.length - 1],
					harray);
		else
			position = CurveMathMethods.halfLocation(head, harray);

		// int position = HalfSearch.halfLocation(head, harray);
		return value1[position].getArray();

	}

	public double[] getV2sByV0(double head) {
		// TODO Auto-generated method stub
		double[] harray = value0.getArray();

		int position;
		if (head < harray[0])
			position = CurveMathMethods.halfLocation(harray[0], harray);
		else if (head > (harray[harray.length - 1]))
			position = CurveMathMethods.halfLocation(harray[harray.length - 1],
					harray);
		else
			position = CurveMathMethods.halfLocation(head, harray);

		// int position = HalfSearch.halfLocation(head, harray);
		return value2[position].getArray();

	}

	public double getMinV0() {
		// TODO Auto-generated method stub
		return value0.getMin();
	}

	public double getMaxV0() {
		// TODO Auto-generated method stub
		return value0.getMax();
	}

	public double[][] getTribleData() {
		return tribleData;
	}

	public void setTribleData(double[][] tribleData) {
		this.tribleData = tribleData;
	}
	
	

}
