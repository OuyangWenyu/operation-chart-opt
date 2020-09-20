package com.wenyu.hydroelements.operation.basic;

/**
 * 包括约束是否满足的简单检查
 * @author  OwenYY
 *
 */
public class CheckConstraint {
	/**
	 * 检查流量约束
	 * @param min 最小流量值
	 * @param max 最大流量值
	 * @param decisionflux 当前时段平均下泄流量值
	 * @return 0 小于最小流量,1,大于最大流量,2满足流量约束
	 */
	public static int CheckFluxLimit(double min,double max,double decisionflux) {
		if (decisionflux < (min - 1))
			return 0;
		else if (decisionflux > (max + 1))
			return 1;
		else {
			return 2;
		}
	}
	
	/**
	 * @param min
	 * @param max
	 * @param decisionWaterHead
	 * @return 检查水头约束
	 */
	public static boolean CheckWaterhead(double min,double max,double decisionWaterHead) {
		if ((decisionWaterHead < min - 0.1)
				|| (decisionWaterHead > max + 0.1))
			return false;
		return true;
	}
	
	
	
}
