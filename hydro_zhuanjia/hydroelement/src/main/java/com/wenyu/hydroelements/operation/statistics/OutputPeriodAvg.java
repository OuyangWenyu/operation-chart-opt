package com.wenyu.hydroelements.operation.statistics;

import java.util.Map;

import com.wenyu.hydroelements.timebucket.TimeBucketType;


/**
 * 时段平均
 *
 */
public class OutputPeriodAvg {
	/**电站名*/
	private String stationName;
	private TimeBucketType pt;
	private Map<OutputVarsType,double[]> avgs;
	/**
	 * @return the stationName
	 */
	public String getStationName() {
		return stationName;
	}
	/**
	 * @param stationName the stationName to set
	 */
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public TimeBucketType getPt() {
		return pt;
	}
	public void setPt(TimeBucketType pt) {
		this.pt = pt;
	}
	/**
	 * @return the avgs
	 */
	public Map<OutputVarsType, double[]> getAvgs() {
		return avgs;
	}
	/**
	 * @param avgs the avgs to set
	 */
	public void setAvgs(Map<OutputVarsType, double[]> avgs) {
		this.avgs = avgs;
	}
	
	
}
