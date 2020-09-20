package me.owenyy.optimal.dp;

import java.util.List;

/**
 * 动态规划状态变量
 *
 */
public class State2D {
	/**
	 * 该状态的数值，可能多个状态对应的cost是一样的，均是最优的
	 */
	private List<Double> stateValue;
	private double cost;
	/**
	 * 二维状态的行序列
	 */
	private int rowIndex;
	/**
	 * 二维状态的列索引
	 */
	private int columnIndex;


	public State2D(List<Double> stateValue, double cost, int rowIndex, int columnIndex) {
		super();
		this.setStateValue(stateValue);
		this.cost = cost;
		this.rowIndex = rowIndex;
		this.columnIndex = columnIndex;
	}
	
	
	public List<Double> getStateValue() {
		return stateValue;
	}


	public void setStateValue(List<Double> stateValue) {
		this.stateValue = stateValue;
	}


	/**
	 * @return the rowIndex
	 */
	public int getRowIndex() {
		return rowIndex;
	}
	/**
	 * @param rowIndex the rowIndex to set
	 */
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	/**
	 * @return the columnIndex
	 */
	public int getColumnIndex() {
		return columnIndex;
	}
	/**
	 * @param columnIndex the columnIndex to set
	 */
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	
	
}
