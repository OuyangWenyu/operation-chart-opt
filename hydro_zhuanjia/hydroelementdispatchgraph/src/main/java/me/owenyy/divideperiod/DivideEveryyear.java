package me.owenyy.divideperiod;

import me.owenyy.divideperiod.helper.BasicMethod;
/**
 * 每一年都重新判断供蓄水期，按照万俊主编的书《水资源开发利用》里P56的方法
 */
public class DivideEveryyear implements DivideDeliveryAndStorage {
	/**
	 * 每一年都重新判断供蓄水期，按照万俊主编的书《水资源开发利用》里P56的方法
	 * 
	 * @see service.dispatch.dispatchgraph.helpermethods.divideperiod.DivideDeliveryAndStorage#getProvideSaveTimeFinal(double[],
	 *      double, int)
	 */
	public int[] getProvideSaveTimeFinal(double[] inflowtemp, double V_benifit, String tbType, int startYear,
			int startPeriod) {
		/*
		 * 首先水电站水库供、蓄期的划分按水能计算中采用的等流量试算方法进行。先用简单的方法
		 */
		int[] period = BasicMethod.initialPeriod(inflowtemp, tbType);//初始化的是一年的一个供蓄水期的分期情况
		int yearPeriods = period.length;
		int rowNumTemp = inflowtemp.length/yearPeriods;
		int colNumTemp = yearPeriods;
		int[][] stateTemp=new int[rowNumTemp][colNumTemp];
		for(int i=0; i<rowNumTemp; i++){
			for(int j=0; j<colNumTemp; j++){
				stateTemp[i][j] = period[j];
			}
		}
		for(int i=0;i<stateTemp.length;i++){
			//BasicMethod.judgeSavePeriod(startIdTemp, endIdTemp, inflowtemp, V_benifit, yearPeriods, startYear, startPeriod)
		}
		return period;
	}

}
