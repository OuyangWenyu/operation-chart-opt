package com.wenyu.hydroelements.hydrostation.dispatchgraph;

import java.time.LocalDate;

/**
 * 调度图使用办法即调度图调度规则，因为每次的使用方法可能不同因此提供使用接口
 * @author  OwenYY
 *
 */
public interface DispatchGraphHowToUse {
	/**
	 * 根据时间水位调度图查询出力
	 * @param date  起始日期
	 * @param SW  水位
	 * @param dispatchGraph  调度图
	 * @return
	 */
	public double searchOutput(LocalDate date,double SW,DispatchGraph dispatchGraph);
	/**
	 * 根据时间以及调度图查询水位约束
	 * @param date
	 * @param dispatchGraph
	 * @return
	 */
	public double[] searchLevelRestrict(LocalDate date,double SW,DispatchGraph dispatchGraph);
}
