package com.wenyu.hydroelements.hydrostation.constraint;

import java.time.LocalDateTime;
import java.util.List;

import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.HStationState;

public class BuildConstraint {
	/**
	 * @param cons  按照约束类型排好顺序的各个约束值
	 * @return 按约束顺序放进数组中，第二维的数据里第一个数是最小值，第二个数是最大值
	 */
	public static double[][] consToArray(List<ConstraintItem> cons)
	{
		double[][] arrays=new double[cons.size()][2];
		for(int i=0;i<cons.size();i++)
		{
			arrays[i][0]=cons.get(i).getValueMin();
			arrays[i][1]=cons.get(i).getValueMax();
		}
		return arrays;
	}
	
	/**
	 * 根据约束编码对相对应的状态数据进行初始化
	 * @param max_min  按约束类型排好序的各个约束的最小值和最大值
	 */
	public static void buildConstraint(List<ConstraintItem> list,HStationState hsState)
	{
		for(int i=0;i<list.size();i++)
		{
			int type=list.get(i).getType();
			switch(type){
			case 101:{
				hsState.setLevelMax(list.get(i).getValueMax());
				hsState.setLevelMin(list.get(i).getValueMin());
				break;
			}
			case 102:{
				hsState.setLevelDownMax(list.get(i).getValueMax());
				hsState.setLevelDownMin(list.get(i).getValueMin());
				break;
			}
			case 201:{
				hsState.setOutflowMax(list.get(i).getValueMax());
				hsState.setOutflowMin(list.get(i).getValueMin());
				break;
			}
			case 301:{
				hsState.setOutputMax(list.get(i).getValueMax());
				hsState.setOutputMin(list.get(i).getValueMin());
				break;
			}
			}
		}
	}
	
	/**
	 * 根据约束编码及HStationSpec中的数据对相对应的约束进行初始化
	 * @param max_min  按约束类型排好序的各个约束的最小值和最大值，这里只是一个简单的约束，最高水位设置为正常蓄水位，最低水位设为死水位，如果碰到有汛限水位等其它要求还需要再考虑！！！！
	 */
	public static void buildConstraint(List<ConstraintItem> list,HStationSpec hsSpec)
	{
		for(int i=0;i<list.size();i++)
		{
			int type=list.get(i).getType();
			switch(type){
			case 101:{
				list.get(i).setValueMax(hsSpec.getLevelNormal());
				list.get(i).setValueMin(hsSpec.getLevelDead());
				break;
			}
			case 201:{
				list.get(i).setValueMax(hsSpec.getDischargeAbilityMax()>10?hsSpec.getDischargeAbilityMax():10000);
				list.get(i).setValueMin(hsSpec.getDischargeDemandMin()>0?hsSpec.getDischargeDemandMin():0);
				break;
			}
			case 301:{
				list.get(i).setValueMax(hsSpec.getPowerInstalled());
				list.get(i).setValueMin(0);
				break;
			}
			}
		}
	}
	
	
	/**
	 * 给各状态值加上汛限水位约束
	 * @param tbType
	 * @param hsStates
	 * @param levelFloodLimiting
	 * @param floodControlStart  时段编号 从1开始
	 * @param floodControlEnd  时段编号
	 */
	public static void valueFloodLimitLevelForStates(String tbType,List<HStationState> hsStates,double levelFloodLimiting,int floodControlStart,int floodControlEnd){
		if(tbType.equals("DECAD")){
			int startMonth=(floodControlStart-1)/3+1;
			int endMonth=(floodControlEnd-1)/3+1;
			int startDay=1;
			int endDay=1;
			if(floodControlStart%3==0)
				startDay=21;
			else if(floodControlStart%3==2)
				startDay=11;
			if(floodControlEnd%3==0)
				endDay=21;
			else if(floodControlEnd%3==2)
				endDay=11;
			for(int i=0;i<hsStates.size();i++){
				int nowYear=hsStates.get(i).getTimeStart().getYear();
				LocalDateTime start=LocalDateTime.of(nowYear, startMonth, startDay, 0, 0);
				LocalDateTime end=LocalDateTime.of(nowYear, endMonth, endDay, 0, 0);
				if(!start.isAfter(hsStates.get(i).getTimeStart()) && !end.isBefore(hsStates.get(i).getTimeEnd())){
					hsStates.get(i).setLevelMax(levelFloodLimiting);
				}
			}
		}
		else if(tbType.equals("MONTH")){//已经在数据库中给定过约束就会生成汛限水位约束，这里再次确认修改
			for(int i=0;i<hsStates.size();i++){
				int nowYear=hsStates.get(i).getTimeStart().getYear();
				LocalDateTime start=LocalDateTime.of(nowYear, floodControlStart, 1, 0, 0);
				LocalDateTime end=LocalDateTime.of(nowYear, floodControlEnd+1, 1, 0, 0);
				if(!start.isAfter(hsStates.get(i).getTimeStart()) && !end.isBefore(hsStates.get(i).getTimeEnd())){
					hsStates.get(i).setLevelMax(levelFloodLimiting);
				}
			}
		}
	}
}
