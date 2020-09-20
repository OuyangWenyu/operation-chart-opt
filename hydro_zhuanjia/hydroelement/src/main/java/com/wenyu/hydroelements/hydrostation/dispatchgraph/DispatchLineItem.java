package com.wenyu.hydroelements.hydrostation.dispatchgraph;

import java.io.Serializable;
import java.time.LocalDate;


public class DispatchLineItem implements Serializable {

	private static final long serialVersionUID = -5989988304371844858L;

	//开始时间
	private LocalDate beginTime;

	//结束时间
	private LocalDate endTime;

	
	/**
	 * 时段初水位
	 */
	private double levelBegin;
	/**
	 * 时段末水位
	 */
	private double levelEnd;
	
	/**
	 * 时段初出力指示，也就是时段平均出力
	 */
	private double output;

	public LocalDate getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(LocalDate beginTime) {
		this.beginTime = beginTime;
	}

	public LocalDate getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDate endTime) {
		this.endTime = endTime;
	}

	public double getLevelBegin() {
		return levelBegin;
	}

	public void setLevelBegin(double levelBegin) {
		this.levelBegin = levelBegin;
	}

	public double getLevelEnd() {
		return levelEnd;
	}

	public void setLevelEnd(double levelEnd) {
		this.levelEnd = levelEnd;
	}

	public double getOutput() {
		return output;
	}

	public void setOutput(double output) {
		this.output = output;
	}



}
