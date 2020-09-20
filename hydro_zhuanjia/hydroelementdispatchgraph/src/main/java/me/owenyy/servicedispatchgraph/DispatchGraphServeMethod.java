package me.owenyy.servicedispatchgraph;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;

/**
 * 提供一些调度图使用时的相关方法
 * @author  OwenYY
 *
 */
public class DispatchGraphServeMethod {
	/**
	 * @param ayear 给定年
	 * @param dispatchGraph
	 * @return  给出当年的蓄水期起始时间
	 */
	public static LocalDate[] calStorageStartEnd(int ayear,DispatchGraph dispatchGraph){
		String year = String.valueOf(ayear);
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDate beginTimeDate = null;
		LocalDate endTimeDate = null;
		for(int j=0;j<dispatchGraph.getDispacthline().size();j++){
			if(dispatchGraph.getDispacthline().get(j).getName().contains("Storage")){
				String beginTime = year + dispatchGraph.getDispacthline().get(j).getWaterLevel().get(0).getBeginTime()
						.toString().substring(4, 10) + " 00:00:00";
				String endTime = year + dispatchGraph.getDispacthline().get(j).getWaterLevel().get(dispatchGraph.getDispacthline().get(j).getWaterLevel().size()-1).getEndTime()
						.toString().substring(4, 10) + " 23:59:59";
				beginTimeDate = LocalDate.parse(beginTime, dateFormat);
				endTimeDate = LocalDate.parse(endTime, dateFormat);
				break;
			}
		}
		LocalDate[] results=new LocalDate[2];
		results[0]=beginTimeDate;
		results[1]=endTimeDate;
		return results;
	}
}
