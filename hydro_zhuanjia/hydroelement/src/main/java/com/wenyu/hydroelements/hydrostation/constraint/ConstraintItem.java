/*
 * @(#) Algorithm.java           1.0 2015/1/15/ ChaoWang
 *  
 * Copyright(C) 2011-2015 ChaoWang   All Rights Reserved
 */
package com.wenyu.hydroelements.hydrostation.constraint;

import java.io.Serializable;

/**
 * 原来的基础上去掉了Builder，变成POJO，
 * 时间类型也去掉，改成纯粹的最大最小值
 * @author  OwenYY
 *
 */
public class ConstraintItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4722663943876907008L;

	private int type;
	private double valueMin;
	private double valueMax;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public double getValueMax() {
		return valueMax;
	}
	public void setValueMax(double valueMax) {
		this.valueMax = valueMax;
	}
	public double getValueMin() {
		return valueMin;
	}
	public void setValueMin(double valueMin) {
		this.valueMin = valueMin;
	}

}
