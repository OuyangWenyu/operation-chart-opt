package com.wenyu.hydroelements.operation.basic;

/**
 * 水库调度里面有一些具体的计算方式方法，在多种情况下是不同的，设立一个接口
 * @author  OwenYY
 *
 */
public interface MultiFomulaMode {
	/**
	 * @param headGross 毛水头
	 * @return 水头损失的计算方式的不同，根据毛水头得到净水头的方式也不同
	 */
	public double calHeadPure(double headGross);
	/**
	 * @param outflow 下泄流量
	 * @return 尾水位的影响因素多方面的，电站层面有顶托，如果涉及到水动力的辅助细化，就更复杂
	 */
	public double tailLevelByOutflow(double outflow);
	
	/**
	 * @return 水电站某一时段平均出力最大值的确定方式有很多种
	 */
	public double maxPowerState(double headPure);
}
