package com.wenyu.hydroelements.operation.statistics;

import java.time.LocalDateTime;
import java.util.Map;

public class StationSimuResultModel {
	//**************************调度结果*********************************
	/**电站名*/
	private String stationName;
	/**起始时间*/
	private LocalDateTime timeStart;
	/**终止时间*/
	private LocalDateTime timeEnd;
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
	public LocalDateTime getTimeStart() {
		return timeStart;
	}

	/**
	 * @param timeStart the timeStart to set
	 */
	public void setTimeStart(LocalDateTime timeStart) {
		this.timeStart = timeStart;
	}

	/**
	 * @return the timeEnd
	 */
	public LocalDateTime getTimeEnd() {
		return timeEnd;
	}

	/**
	 * @param timeEnd the timeEnd to set
	 */
	public void setTimeEnd(LocalDateTime timeEnd) {
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
