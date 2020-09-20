package com.wenyu.hydroelements.hydrostation.dispatchgraph;

import java.io.Serializable;
import java.util.List;

/**
 * 调度线不包括年消落水位和正常蓄水位
 */
public class DispatchLine implements Serializable{
	private static final long serialVersionUID = -4118796842405503430L;
	/**调度线编号*/
	private int id;
	/**调度线名称*/
	private String name;
	
	/**调度线水位过程*/
	private List<DispatchLineItem> waterLevel;

	/**调度线出力值，对应调度线上的调度区，调度线不一定是一条完整的全年度的调度线，也可能供蓄水期是分开的*/
	private double output;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	/**调度线水位过程*/
	public List<DispatchLineItem> getWaterLevel() {
		return waterLevel;
	}
	/**调度线水位过程*/
	public void setWaterLevel(List<DispatchLineItem> waterLevel) {
		this.waterLevel = waterLevel;
	}

	public double getOutput() {
		return output;
	}

	public void setOutput(double output) {
		this.output = output;
	}

}
