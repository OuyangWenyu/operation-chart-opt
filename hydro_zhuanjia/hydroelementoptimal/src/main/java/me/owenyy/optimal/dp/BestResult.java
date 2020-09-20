package me.owenyy.optimal.dp;

import java.util.ArrayList;
import java.util.List;

public class BestResult {
	/**
	 * 目标值
	 */
	private double target;
	/**
	 * 对应目标的最优cost
	 */
	private double bestCost;
	/**
	 * 一组最优解的话，List的size是1，double[]存放各个单阶段的状态值；
	 * 多组最优解的话，List的size根据实际情况来定
	 */
	private List<double[]> allStates;
	/**
	 * 对应state的cost
	 */
	private List<double[]> allStatesCosts;
	public BestResult(double target) {
		super();
		this.target = target;
		allStates=new ArrayList<double[]>();
		allStatesCosts=new ArrayList<double[]>();
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
	 * @return the bestCost
	 */
	public double getBestCost() {
		return bestCost;
	}
	/**
	 * @param bestCost the bestCost to set
	 */
	public void setBestCost(double bestCost) {
		this.bestCost = bestCost;
	}
	/**
	 * @return the allStates
	 */
	public List<double[]> getAllStates() {
		return allStates;
	}
	/**
	 * @param allStates the allStates to set
	 */
	public void setAllStates(List<double[]> allStates) {
		this.allStates = allStates;
	}
	/**
	 * @return the allStatesCosts
	 */
	public List<double[]> getAllStatesCosts() {
		return allStatesCosts;
	}
	/**
	 * @param allStatesCosts the allStatesCosts to set
	 */
	public void setAllStatesCosts(List<double[]> allStatesCosts) {
		this.allStatesCosts = allStatesCosts;
	}
	
	
}
