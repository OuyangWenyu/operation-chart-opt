package com.wenyu.hydroelements.operation.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseStatistics  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4506411205958426697L;
	private List<Double> data;
	private double[] array;
	private boolean ready;
	private double max;
	private double min;
	private double sum;
	private double mean;
	private double sumsq;//数据序列之和
	private int n;
	
	public BaseStatistics(){
		data = new ArrayList<Double>();
	}
	
	public BaseStatistics(double[] datas){
		data = new ArrayList<Double>();
		for(int i=0;i<datas.length;i++){
			data.add(datas[i]);
		}
		build();
	}
	
	public void add(double value){
		data.add(value);
		ready = false;
	}
	
	public void clear(){
		data.clear();
		ready = false;
	}
	
	public void build(){
		max = Double.MIN_VALUE;
		min = Double.MAX_VALUE;
		sum = 0;
		sumsq = 0;
		n = data.size();
		array = new double[n];
		for(int i=0;i<n;i++){
			if(data.get(i) > max)max = data.get(i);
			if(data.get(i) < min)min = data.get(i);
	//		sum = sum + data.get(i);
	//		sumsq = sumsq + (data.get(i) * data.get(i));
			array[i] = data.get(i);
		}
	//	mean = sum / n;
		ready = true;		
	}

	public double getMax() {
		if(!ready)build();
		return max;
	}

	public double getMin() {
		if(!ready)build();
		return min;
	}

	public double getSum() {
		if(!ready)build();
		return sum;
	}

	public double getMean() {
		if(!ready)build();
		return mean;
	}

	public double getSumsq() {
		if(!ready)build();
		return sumsq;
	}

	public int getN() {
		if(!ready)build();
		return n;
	}	
	
	public double[] getArray(){
		if(!ready)build();
		return array;
	}

	/**
	 * @return the data
	 */
	public List<Double> getData() {
		return data;
	}
}
