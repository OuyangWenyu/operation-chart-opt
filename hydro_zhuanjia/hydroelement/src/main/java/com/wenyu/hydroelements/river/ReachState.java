package com.wenyu.hydroelements.river;

import java.time.LocalDateTime;

public class ReachState {
	/**起始时间*/
	private LocalDateTime timeStart;
	/**终止时间*/
	private LocalDateTime timeEnd;
	/**
	 * 河段起点的断面时段平均流量
	 */
	private double startNodeFlow;
	/**
	 * 河段终点的断面时段平均流量
	 */
	private double endNodeFlow;
	/**
	 * 始末节点断面径流之差，即通常理解的电站间的区间径流
	 */
	private double nodeFlowDif;
	
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
	 * @return the startNodeFlow
	 */
	public double getStartNodeFlow() {
		return startNodeFlow;
	}
	/**
	 * @param startNodeFlow the startNodeFlow to set
	 */
	public void setStartNodeFlow(double startNodeFlow) {
		this.startNodeFlow = startNodeFlow;
	}
	/**
	 * @return the endNodeFlow
	 */
	public double getEndNodeFlow() {
		return endNodeFlow;
	}
	/**
	 * @param endNodeFlow the endNodeFlow to set
	 */
	public void setEndNodeFlow(double endNodeFlow) {
		this.endNodeFlow = endNodeFlow;
	}
	/**
	 * @return the nodeFlowDif
	 */
	public double getNodeFlowDif() {
		return nodeFlowDif;
	}
	/**
	 * @param nodeFlowDif the nodeFlowDif to set
	 */
	public void setNodeFlowDif(double nodeFlowDif) {
		this.nodeFlowDif = nodeFlowDif;
	}
	
}
