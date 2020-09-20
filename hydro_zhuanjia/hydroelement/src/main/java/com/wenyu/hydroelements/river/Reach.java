package com.wenyu.hydroelements.river;

import java.util.List;

/**
 * 可以根据水文学上原理进行详细的水系、流域的设计，
 * 可以按照Strahler分级来定义河段的相关属性等。
 * 
 * 目前此类仅为主干流上 表征电站之间的河段，也可以指任意一段距离的河段，目前仅用于区间径流的设置。
 * @author  OwenYY
 *
 */
public class Reach {
	
	/**
	 * 沿主流水流方向的上游节点名称（河流之间的节点是自然节点，水工建筑物也可以看成一个节点，只不过是人工节点）
	 */
	private String riverNodeStart;
	private String riverNodeEnd;
	private List<ReachState> rStates;
	/**
	 * @return the riverNodeStart
	 */
	public String getRiverNodeStart() {
		return riverNodeStart;
	}
	/**
	 * @param riverNodeStart the riverNodeStart to set
	 */
	public void setRiverNodeStart(String riverNodeStart) {
		this.riverNodeStart = riverNodeStart;
	}
	/**
	 * @return the riverNodeEnd
	 */
	public String getRiverNodeEnd() {
		return riverNodeEnd;
	}
	/**
	 * @param riverNodeEnd the riverNodeEnd to set
	 */
	public void setRiverNodeEnd(String riverNodeEnd) {
		this.riverNodeEnd = riverNodeEnd;
	}
	public List<ReachState> getrStates() {
		return rStates;
	}
	public void setrStates(List<ReachState> rStates) {
		this.rStates = rStates;
	}
	
	
}
