package com.wenyu.service.excelutil.fromdbway1;

import java.util.Date;
import java.util.Map;

import com.wenyu.hydroelements.operation.statistics.OutputVarsType;

public class StationSimuResultModel {
	//**************************调度结果*********************************
	/**电站名*/
	private String stationName;
	/**起始时间*/
	private Date timeStart;
	/**终止时间*/
	private Date timeEnd;
	/**时段长度(按秒)*/
	private long timeLength;
	
	private Map<OutputVarsType,Double> results;

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

	/**
	 * @return the timeStart
	 */
	public Date getTimeStart() {
		return timeStart;
	}

	/**
	 * @param timeStart the timeStart to set
	 */
	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}

	/**
	 * @return the timeEnd
	 */
	public Date getTimeEnd() {
		return timeEnd;
	}

	/**
	 * @param timeEnd the timeEnd to set
	 */
	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}

	/**
	 * @return the timeLength
	 */
	public long getTimeLength() {
		return timeLength;
	}

	/**
	 * @param timeLength the timeLength to set
	 */
	public void setTimeLength(long timeLength) {
		this.timeLength = timeLength;
	}

	/**
	 * @return the results
	 */
	public Map<OutputVarsType, Double> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(Map<OutputVarsType, Double> results) {
		this.results = results;
	}
	
	
	
	
}
