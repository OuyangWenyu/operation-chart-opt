package me.owenyy.useregulation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraphHowToUse;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchLineItem;


public class SchedulingRuleUseDG implements DispatchGraphHowToUse {

	/**
	 * 应充分考虑使用时候是以月、旬还是日的方式
	 * 
	 * @see com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraphHowToUse#SearchOutput(java.lang.String,
	 *      double,
	 *      com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph)
	 */
	public double searchOutput(LocalDate date, double SW, DispatchGraph dispatchGraph) {
		double output = 0;

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String year = String.valueOf(date.getYear());

		List<DispatchLineItem> swBeginAll = new ArrayList<DispatchLineItem>();
		for (int i = 0; i < dispatchGraph.getDispacthline().size(); i++) {// 每条曲线每个时段判断
			for (int j = 0; j < dispatchGraph.getDispacthline().get(i).getWaterLevel().size(); j++) {
				String beginTime = year + dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j).getBeginTime()
						.toString().substring(4, 10) + " 00:00:00";
				String endTime = year + dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j).getEndTime()
						.toString().substring(4, 10) + " 23:59:59";
				LocalDate beginTimeDate = LocalDate.parse(beginTime, dateFormat);
				LocalDate endTimeDate = LocalDate.parse(endTime, dateFormat);
				if ((!date.isBefore(beginTimeDate)) && (!date.isAfter(endTimeDate))) {
					// 一条线一条线的从上往下看，把该时段的所有数据都读出来，调度线没有交叉，因此以防错误，后面排序一次
					swBeginAll.add(dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j));
				}
			}
		}
		
		if(swBeginAll.size()==0){
			//查询没有得到出力结果，后面采用流量控制或者其它方式进行调度控制
			
			return -1;
		}
		
		swBeginAll.sort(new Comparator<DispatchLineItem>() {
			public int compare(DispatchLineItem o1, DispatchLineItem o2) {
				Double k1 = o1.getLevelBegin();
				Double k2 = o2.getLevelBegin();
				Double k11 = o1.getOutput();
				Double k21 = o2.getOutput();
				return k1.compareTo(k2) + k11.compareTo(k21);
			}
		});
		
		if(SW>swBeginAll.get(swBeginAll.size()-1).getLevelBegin()){
			return swBeginAll.get(swBeginAll.size()-1).getOutput();
		}
		
		int wOutputUpperIndex = -1;
		int wOutputLowerIndex = -1;
		for (int i = 0; i < swBeginAll.size(); i++) {
			if (i > 0 && swBeginAll.get(i).getOutput() == swBeginAll.get(i - 1).getOutput()) {
				wOutputUpperIndex = i;
				wOutputLowerIndex = i - 1;
			}
		}
		// 把当前时段的各条水位线的数据取出来后，就可以根据当前水位的大小进行出力判断了
		// 先找出保证出力对应的编号，只要落在两个保证出力线之间（包括两条线上）都按保证出力出
		// 其它的情况，只要是水位在出力线上就按照水位对应的最小出力给指令
		// 如果当前水位不落在出力线上，加大出力区就按照离当前水位最近的上面的加大出力线出力进行指令，降低出力区就按照下面那条降低出力线的除了指令进行出力
		// 要是超出了最高水位线的水位和最低水位线的水位，也要做处理
		List<Integer> tempEqual = new ArrayList<Integer>();
		List<Integer> tempHigher = new ArrayList<Integer>();
		for (int j = 0; j < swBeginAll.size(); j++) {
			if (SW == swBeginAll.get(j).getLevelBegin()) {
				tempEqual.add(j);
			} else if (SW > swBeginAll.get(j).getLevelBegin()) {
				tempHigher.add(j);
			}
		}

		if (tempEqual.size() == 0 && tempHigher.size() == 0) {
			// 查找最低出力值，破坏出力区出力暂时取最小值的0.7倍
			double[] outputall = new double[swBeginAll.size()];
			for (int j = 0; j < outputall.length; j++) {
				outputall[j] = swBeginAll.get(j).getOutput();// 把所有调度线对应的出力取出
			}
			double temp_output = outputall[0];
			for (int j = 1; j < outputall.length; j++) {
				if (outputall[j] < temp_output) {
					temp_output = 0.7 * outputall[j];
				}
			}
			output = temp_output * 0.7;// 出力为0？？？
		} else {
			if (tempEqual.size() > 0) {
				if (tempEqual.contains(wOutputUpperIndex) || tempEqual.contains(wOutputLowerIndex)) {
					output = swBeginAll.get(wOutputUpperIndex).getOutput();
				} else {
					output = swBeginAll.get(tempEqual.get(0)).getOutput();
				}
			} else {
				if (tempHigher.get(tempHigher.size() - 1) >= wOutputUpperIndex) {// 如果水位在加大出力区
					output = swBeginAll.get(tempHigher.get(tempHigher.size() - 1) + 1).getOutput();// 出力为加大出力
				} else if (tempHigher.get(tempHigher.size() - 1) == wOutputLowerIndex) {
					output = swBeginAll.get(wOutputUpperIndex).getOutput();// 保证出力
				} else {// 降低出力
					output = swBeginAll.get(tempHigher.get(tempHigher.size() - 1)).getOutput();
				}
			}
		}
		return output;
	}

	public double[] searchLevelRestrict(LocalDate date, double SW, DispatchGraph dispatchGraph) {
		double[] result = new double[2];

		String year = String.valueOf(date.getYear());

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		List<DispatchLineItem> swEndAll = new ArrayList<DispatchLineItem>();
		for (int i = 0; i < dispatchGraph.getDispacthline().size(); i++) {// 每条曲线每个时段判断
			for (int j = 0; j < dispatchGraph.getDispacthline().get(i).getWaterLevel().size(); j++) {
				String beginTime = year + dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j).getBeginTime()
						.toString().substring(4, 10) + " 00:00:00";
				String endTime = year + dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j).getEndTime()
						.toString().substring(4, 10) + " 23:59:59";
				LocalDate beginTimeDate = LocalDate.parse(beginTime, dateFormat);
				LocalDate endTimeDate = LocalDate.parse(endTime, dateFormat);
				if ((!date.isBefore(beginTimeDate)) && (!date.isAfter(endTimeDate))) {
					// 一条线一条线的从上往下看，把该时段的所有数据都读出来，调度线没有交叉，因此以防错误，后面排序一次
					swEndAll.add(dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j));
				}
			}
		}
		
		if(swEndAll.size()==0){
			//查询没有得到出力结果，后面采用流量控制或者其它方式进行调度控制
			return new double[]{-1,-1};
		}

		swEndAll.sort(new Comparator<DispatchLineItem>() {
			public int compare(DispatchLineItem o1, DispatchLineItem o2) {
				Double k1 = o1.getLevelEnd();
				Double k2 = o2.getLevelEnd();
				Double k11 = o1.getOutput();
				Double k21 = o2.getOutput();
				return k1.compareTo(k2) + k11.compareTo(k21);
			}
		});
		
		result[1] = swEndAll.get(swEndAll.size()-1).getLevelEnd();
		result[0] = swEndAll.get(0).getLevelEnd();
		return result;
	}

}
