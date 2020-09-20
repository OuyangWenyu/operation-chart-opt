package com.wenyu.hydroelements.operation.basic;

import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;

/**
 * 基本的水量平衡计算，出力水位等迭代运算等，主要用于中长期调度计算
 *
 */
public class BasicWaterResourceCal {
	/**
	 * 根据初、末水位获取流量差值
	 * @param curve 电站特性曲线
	 * @param Currentwaterlevel 时段初水位
	 * @param Nextwaterlevel 时段末水位
	 * @param currentPeriodLength 时段长度(统一以秒为单位) 
	 * @return
	 */
	public static double GetFluxFromDeltaWaterLevel(StationCurve curve,double Currentwaterlevel,
		double Nextwaterlevel, int currentPeriodLength) {
		double Deltaflux;
		double Currentcontent = 0;
		double Nextcontent = 0;
		Currentcontent = curve.getCapacityByLevel(Currentwaterlevel);
		Nextcontent = curve.getCapacityByLevel(Nextwaterlevel);
		Deltaflux = (Currentcontent - Nextcontent) * 1E8
				/ currentPeriodLength;
		return Deltaflux;
	}
	
	/**
	 *	库容水位曲线、下泄流量水位曲线是一定的，时段类型给定，时段初水位、时段末水位、时段平均入流、出流四者知3得1，水量平衡公式
	 */
	/**
	 * @param a 
	 * @param b
	 * @param c  四个量中的三个
	 * @param vs 库容水位曲线
	 * @param ds 流量水位曲线
	 * @param dt 时段类型  对应枚举类的一个常量
	 * @param choice a、b、c三个数按顺序：1是时段初库容、时段末库容、时段平均入流  求出流   
	 * 2是时段初库容、时段平均入流、出流  求时段末库容 
	 * 3是时段平均入流、时段平均出流、时段末库容，求时段初库容 
	 *  4是时段平均出流、时段末库容、入流，求时段初库容
	 * @return  计算中所有的数据都不能为小于0的数。
	 * @throws Exception 
	 */
	public static double waterBalanceCalculate(double a,double b,double c,double dt,int choice) throws Exception
	{
		double d=0;
		double v0,vt,inflow,outflow;
		switch(choice)
		{
		case 1:
			v0=a;
			vt=b;
			inflow=c;
			d=inflow-(vt-v0)*1e8/dt;
			break;
		case 2:
			v0=a;
			inflow=b;
			outflow=c;
			d=(a*1e8+(b-outflow)*dt)/1e8;
			break;
		case 3:
			vt=c;
			inflow=a;
			outflow=b;
			d=vt-(inflow-outflow)*dt/1e8;
			break;
		case 4:
			break;
		}
		/*if(d<0) 
		{
			System.out.println("抛出一个异常:");
			throw new Exception("--------!水量平衡不能满足!--------");
		}*/
		return d;
	}

	
}
