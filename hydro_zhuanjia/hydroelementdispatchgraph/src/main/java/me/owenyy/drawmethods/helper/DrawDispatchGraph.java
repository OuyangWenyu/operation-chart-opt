package me.owenyy.drawmethods.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraphFactory;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchLine;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchLineItem;

public class DrawDispatchGraph {
	/**
	 * 100012是供水期保证出力上限；
	 * 100008是供水期保证出力下限；
	 * 100013是蓄水期保证出力上限；
	 * 100009是蓄水期保证出力下限；
	 * 100110+i是供水期各加大出力线；
	 * 100120 + i是蓄水期各加大出力线；
	 * 100210+i是供水期各降低出力线；
	 * 100220 + i是蓄水期各降低出力线；
	 * @param hsSpec
	 * @param input
	 * @param deliveryStartNum 供水期开始的时段编号
	 * @param storageStartNum 蓄水期开始的时段编号
	 * @param deliveryPeriodWarrentedOutputLine
	 * @param storagePeriodWarrentedOutputLine
	 * @param ZdHighers
	 * @param ZsHighers
	 * @param ZdLowers
	 * @param ZsLowers
	 * @param warrantedOutputSearch  供水期保证出力
	 * @param storageOutput  蓄水期所查得的保证出力
	 * @param dHigherNs
	 * @param dLowerNs
	 * @param sHigherNs
	 * @param sLowerNs
	 * @return
	 */
	public static DispatchGraph drawDispatchGraph(HStationSpec hsSpec, DispatchInputParas input, int deliveryStartNum,
			int storageStartNum, double[][] deliveryPeriodWarrentedOutputLine,
			double[][] storagePeriodWarrentedOutputLine, double[][] ZdHighers, double[][] ZsHighers,
			double[][] ZdLowers, double[][] ZsLowers, double warrantedOutputSearch, double storageOutput,
			double[] dHigherNs, double[] sHigherNs, double[] dLowerNs, double[] sLowerNs) {

		DispatchGraph dispatchGraph = new DispatchGraph();
		List<DispatchLine> lines = new ArrayList<DispatchLine>();
		LocalDate deliveryStart = null;
		LocalDate storageStart = null;
		if (input.getTbType().equals("MONTH")) {
			deliveryStart = LocalDate.of(LocalDate.now().getYear(), deliveryStartNum, 1);
			storageStart = LocalDate.of(LocalDate.now().getYear(), storageStartNum, 1);
		}
		if (input.getTbType().equals("DECAD")) {
			deliveryStart = LocalDate.of(LocalDate.now().getYear(), (deliveryStartNum-1) / 3+1, 1);
			storageStart = LocalDate.of(LocalDate.now().getYear(), (storageStartNum-1) / 3+1, 1);
		}
		DispatchLine dline0 = DispatchGraphFactory.createDispatchLine(input, 100012, deliveryStart,
				deliveryPeriodWarrentedOutputLine[0], warrantedOutputSearch);
		DispatchLine dline1 = DispatchGraphFactory.createDispatchLine(input, 100008, deliveryStart,
				deliveryPeriodWarrentedOutputLine[1], warrantedOutputSearch);
		DispatchLine sline0 = DispatchGraphFactory.createDispatchLine(input, 100013, storageStart,
				storagePeriodWarrentedOutputLine[0], storageOutput);
		DispatchLine sline1 = DispatchGraphFactory.createDispatchLine(input, 100009, storageStart,
				storagePeriodWarrentedOutputLine[1], storageOutput);
		lines.add(dline0);
		lines.add(dline1);
		lines.add(sline0);
		lines.add(sline1);
		for (int i = 0; i < ZdHighers.length; i++) {
			DispatchLine line = DispatchGraphFactory.createDispatchLine(input, 100110 + i, deliveryStart, ZdHighers[i],
					dHigherNs[i]);
			lines.add(line);
		}
		for (int i = 0; i < ZsHighers.length; i++) {
			DispatchLine line = DispatchGraphFactory.createDispatchLine(input, 100120 + i, storageStart, ZsHighers[i],
					sHigherNs[i]);
			lines.add(line);
		}
		for (int i = 0; i < ZdLowers.length; i++) {
			DispatchLine line = DispatchGraphFactory.createDispatchLine(input, 100210 + i, deliveryStart, ZdLowers[i],
					dLowerNs[i]);
			lines.add(line);
		}
		for (int i = 0; i < ZsLowers.length; i++) {
			DispatchLine line = DispatchGraphFactory.createDispatchLine(input, 100220 + i, storageStart, ZsLowers[i],
					sLowerNs[i]);
			lines.add(line);
		}

		/*因为调度图蓄水期末从正常蓄水位开始逆推，因此最后得到的结果最后一个时段的初水位必是正常蓄水位，
		而这样的结果在一些时段如果强制使末水位达到正常蓄水位是较难的，因此调整蓄水期末的水位和供水期初的水位保持一致*/		
		int year = lines.get(0).getWaterLevel().get(0).getBeginTime().getYear();
		LocalDate dateDelivertyBegin=LocalDate.of(year, deliveryStartNum, 1);
		LocalDate dateStorageEnd=LocalDate.of(year, deliveryStartNum, 1).minusDays(1);
		List<DispatchLineItem> swBeginAllDeliverty = new ArrayList<DispatchLineItem>();
		List<DispatchLineItem> swEndAllStorage = new ArrayList<DispatchLineItem>();
		for (int i = 0; i < lines.size(); i++) {// 每条曲线每个时段判断，把供水期初和蓄水期末的全部水位取出
			for (int j = 0; j < lines.get(i).getWaterLevel().size(); j++) {
				LocalDate beginTimeDate = lines.get(i).getWaterLevel().get(j).getBeginTime();
				LocalDate endTimeDate = lines.get(i).getWaterLevel().get(j).getEndTime();
				
				if ((!dateDelivertyBegin.isBefore(beginTimeDate)) && (!dateDelivertyBegin.isAfter(endTimeDate))) {
					// 一条线一条线的从上往下看，把该时段的所有数据都读出来，调度线没有交叉，因此以防错误，后面排序一次
					swBeginAllDeliverty.add(lines.get(i).getWaterLevel().get(j));//swBeginAllDeliverty里的关于DispatchLineItem的引用和lines里的指向同一个对象
				}
				else if((!dateStorageEnd.isBefore(beginTimeDate)) && (!dateStorageEnd.isAfter(endTimeDate))){
					swEndAllStorage.add(lines.get(i).getWaterLevel().get(j));
				}
			}
		}
		Comparator<DispatchLineItem> com=new Comparator<DispatchLineItem>() {
			public int compare(DispatchLineItem o1, DispatchLineItem o2) {
				Double k1 = o1.getLevelBegin();
				Double k2 = o2.getLevelBegin();
				Double k11 = o1.getOutput();
				Double k21 = o2.getOutput();
				return k1.compareTo(k2) + k11.compareTo(k21);
			}
		};
		swBeginAllDeliverty.sort(com);
		swEndAllStorage.sort(com);
		//排序完毕之后，把蓄水期
		for(int i=0;i<swBeginAllDeliverty.size();i++){
			swEndAllStorage.get(i).setLevelEnd(swBeginAllDeliverty.get(i).getLevelBegin());
		}
		
		dispatchGraph.setDispacthline(lines);
		dispatchGraph.setStationName(hsSpec.getName());
		return dispatchGraph;
	}

	/**
	 * 100012是供水期保证出力上限；
	 * 100008是供水期保证出力下限；
	 * 100013是蓄水期保证出力上限；
	 * 100009是蓄水期保证出力下限；
	 * 100110+i是供水期各加大出力线；
	 * 100120 + i是蓄水期各加大出力线；
	 * 100210+i是供水期各降低出力线；
	 * 100220 + i是蓄水期各降低出力线；
	 * @param hsSpec
	 * @param input
	 * @param deliveryStartNum 供水期开始的时段编号
	 * @param storageStartNum 蓄水期开始的时段编号
	 * @param deliveryPeriodWarrentedOutputLine
	 * @param storagePeriodWarrentedOutputLine
	 * @param ZdHighers
	 * @param ZsHighers
	 * @param ZdLowers
	 * @param ZsLowers
	 * @param warrantedOutputSearch  供水期保证出力
	 * @param storageOutput  蓄水期所查得的保证出力
	 * @param dHigherNs
	 * @param dLowerNs
	 * @param sHigherNs
	 * @param sLowerNs
	 * @return  分期调度图，蓄水期末和供水期初水位并不相同
	 */
	public static DispatchGraph drawDispatchGraphDifferentByStage(HStationSpec hsSpec, DispatchInputParas input, int deliveryStartNum,
			int storageStartNum, double[][] deliveryPeriodWarrentedOutputLine,
			double[][] storagePeriodWarrentedOutputLine, double[][] ZdHighers, double[][] ZsHighers,
			double[][] ZdLowers, double[][] ZsLowers, double warrantedOutputSearch, double storageOutput,
			double[] dHigherNs, double[] sHigherNs, double[] dLowerNs, double[] sLowerNs) {

		DispatchGraph dispatchGraph = new DispatchGraph();
		List<DispatchLine> lines = new ArrayList<DispatchLine>();
		LocalDate deliveryStart = null;
		LocalDate storageStart = null;
		if (input.getTbType().equals("MONTH")) {
			deliveryStart = LocalDate.of(LocalDate.now().getYear(), deliveryStartNum, 1);
			storageStart = LocalDate.of(LocalDate.now().getYear(), storageStartNum, 1);
		}
		if (input.getTbType().equals("DECAD")) {
			deliveryStart = LocalDate.of(LocalDate.now().getYear(), (deliveryStartNum-1) / 3+1, 1);
			storageStart = LocalDate.of(LocalDate.now().getYear(), (storageStartNum-1) / 3+1, 1);
		}
		DispatchLine dline0 = DispatchGraphFactory.createDispatchLine(input, 100012, deliveryStart,
				deliveryPeriodWarrentedOutputLine[0], warrantedOutputSearch);
		DispatchLine dline1 = DispatchGraphFactory.createDispatchLine(input, 100008, deliveryStart,
				deliveryPeriodWarrentedOutputLine[1], warrantedOutputSearch);
		DispatchLine sline0 = DispatchGraphFactory.createDispatchLine(input, 100013, storageStart,
				storagePeriodWarrentedOutputLine[0], storageOutput);
		DispatchLine sline1 = DispatchGraphFactory.createDispatchLine(input, 100009, storageStart,
				storagePeriodWarrentedOutputLine[1], storageOutput);
		lines.add(dline0);
		lines.add(dline1);
		lines.add(sline0);
		lines.add(sline1);
		for (int i = 0; i < ZdHighers.length; i++) {
			DispatchLine line = DispatchGraphFactory.createDispatchLine(input, 100110 + i, deliveryStart, ZdHighers[i],
					dHigherNs[i]);
			lines.add(line);
		}
		for (int i = 0; i < ZsHighers.length; i++) {
			DispatchLine line = DispatchGraphFactory.createDispatchLine(input, 100120 + i, storageStart, ZsHighers[i],
					sHigherNs[i]);
			lines.add(line);
		}
		for (int i = 0; i < ZdLowers.length; i++) {
			DispatchLine line = DispatchGraphFactory.createDispatchLine(input, 100210 + i, deliveryStart, ZdLowers[i],
					dLowerNs[i]);
			lines.add(line);
		}
		for (int i = 0; i < ZsLowers.length; i++) {
			DispatchLine line = DispatchGraphFactory.createDispatchLine(input, 100220 + i, storageStart, ZsLowers[i],
					sLowerNs[i]);
			lines.add(line);
		}

		dispatchGraph.setDispacthline(lines);
		dispatchGraph.setStationName(hsSpec.getName());
		return dispatchGraph;
	}
}
