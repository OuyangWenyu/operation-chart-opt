package me.owenyy.optimal.dp;

public class State {
	/**
	 * 该状态的数值，可能多个状态对应的benifit是一样的，均是最优的
	 */
	private double stateValue;
	private double benefitMax;//到该阶段的该状态时的benefit
	/**
	 * 状态的序列号
	 */
	private int index;
	private int[] lastStageIndicesBest;
	private int lastStageIndexBest;//选择其中一个作为典型
	/**
	 * @return the stateValue
	 */
	public double getStateValue() {
		return stateValue;
	}
	/**
	 * @param stateValue the stateValue to set
	 */
	public void setStateValue(double stateValue) {
		this.stateValue = stateValue;
	}
	/**
	 * @return the benifitMax
	 */
	public double getBenefitMax() {
		return benefitMax;
	}
	/**
	 * @param benifitMax the benifitMax to set
	 */
	public void setBenefitMax(double benefitMax) {
		this.benefitMax = benefitMax;
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
	public int[] getLastStageIndicesBest() {
		return lastStageIndicesBest;
	}
	public void setLastStageIndicesBest(int[] lastStageIndicesBest) {
		this.lastStageIndicesBest = lastStageIndicesBest;
	}
	public int getLastStageIndexBest() {
		return lastStageIndexBest;
	}
	public void setLastStageIndexBest(int lastStageIndexBest) {
		this.lastStageIndexBest = lastStageIndexBest;
	}
	
	
}