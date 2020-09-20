package me.owenyy.drawmethods;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;
/**
 * @author  OwenYY
 *
 */
public interface DispatchGraphBehavior {
	/**
	 * 绘制月/旬的水库调度图
	 * @param input
	 */
	public void makeDispatchGraph(PowerControlHStation pch,DispatchInputParas input);
}
