package me.owenyy.optimal.dp;

import java.util.List;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;

/**
 * 一维的各个变量
 *
 */
public class OneDimensionalStates {
	/**
	 * 该维数据所处的更高一维的index位置
	 */
	private int index;
	/**
	 * 存储到达该整体状态的各个最优代价值
	 */
	private List<State2D> allStatesIn1D;
	/**
	 * 存储各个最优代价值对应的当前单阶段的状态值，
	 * 因为两阶段择优合并成一个阶段的时候，
	 * 不仅要把整体的状态值存储下来，
	 * 如何在两个状态间进行状态值分配的情况也要保存下来！！
	 * 里面的cost还是先只存储整体的最优状态值
	 */
	private List<State2D> statesNow;
	/**
	 * 该阶段的状态取值范围
	 */
	private Interval stateValueInterval;
	
	
	
	/**
	 * @param index 该维数据所处的更高一维的index位置
	 */
	public OneDimensionalStates(int index) {
		super();
		this.index = index;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * @return the allStatesIn1D
	 */
	public List<State2D> getAllStatesIn1D() {
		return allStatesIn1D;
	}
	/**
	 * @param allStatesIn1D the allStatesIn1D to set
	 */
	public void setAllStatesIn1D(List<State2D> allStatesIn1D) {
		this.allStatesIn1D = allStatesIn1D;
	}
	public List<State2D> getStatesNow() {
		return statesNow;
	}
	public void setStatesNow(List<State2D> statesNow) {
		this.statesNow = statesNow;
	}
	public Interval getStateValueInterval() {
		return stateValueInterval;
	}
	public void setStateValueInterval(Interval stateValueInterval) {
		this.stateValueInterval = stateValueInterval;
	}
	
}
