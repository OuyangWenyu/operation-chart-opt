package me.owenyy.optimal.dp;

import java.util.List;

/**
 * 逆序取最优值的时候，两个阶段之间的联系
 *
 */
public class TwoStagesRelation {
	/**
	 * 本阶段的一个target
	 */
	private double target;
	/**
	 * target对应的index
	 */
	private int targetIndex;
	
	/**
	 * 正序看对应的下一个阶段的目标的index
	 */
	private int nextTargetIndex;
	
	
	/**
	 *  target对应的index上的状态各个取值
	 */
	private List<Double> targetStateValues;
	/**
	 * target减去各个状态取值后剩下的target值们对应的上一个阶段的各个index
	 */
	private List<Integer> lastTargetIndex;
	
	
	public TwoStagesRelation(double target) {
		super();
		this.target = target;
	}
	/**
	 * @return the target
	 */
	public double getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(double target) {
		this.target = target;
	}
	/**
	 * @return the targetIndex
	 */
	public int getTargetIndex() {
		return targetIndex;
	}
	/**
	 * @param targetIndex the targetIndex to set
	 */
	public void setTargetIndex(int targetIndex) {
		this.targetIndex = targetIndex;
	}
	public int getNextTargetIndex() {
		return nextTargetIndex;
	}
	public void setNextTargetIndex(int nextTargetIndex) {
		this.nextTargetIndex = nextTargetIndex;
	}
	/**
	 * @return the targetStateValues
	 */
	public List<Double> getTargetStateValues() {
		return targetStateValues;
	}
	/**
	 * @param targetStateValues the targetStateValues to set
	 */
	public void setTargetStateValues(List<Double> targetStateValues) {
		this.targetStateValues = targetStateValues;
	}
	/**
	 * @return the lastTargetIndex
	 */
	public List<Integer> getLastTargetIndex() {
		return lastTargetIndex;
	}
	/**
	 * @param lastTargetIndex the lastTargetIndex to set
	 */
	public void setLastTargetIndex(List<Integer> lastTargetIndex) {
		this.lastTargetIndex = lastTargetIndex;
	}
	
	
}
