package me.owenyy.drawmethods;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.curve.TimeSeqCurveFactory;
import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;
import com.wenyu.service.InitialContainer;
import com.wenyu.service.TimeSeqCurveManager;

import me.owenyy.divideperiod.DivideDeliveryAndStorage;
import me.owenyy.divideperiod.MakeSureByOutput;
import me.owenyy.divideperiod.FixedDividePeriod;
import me.owenyy.divideperiod.helper.BasicMathMethods;
import me.owenyy.divideperiod.helper.EmpiricalFrequency;
import me.owenyy.divideperiod.helper.EveryYearByDivideResult;
import me.owenyy.divideperiod.helper.RegulationYear;
import me.owenyy.drawmethods.helper.DrawDispatchGraph;
import me.owenyy.drawmethods.helper.DrawDispatchLine;
import me.owenyy.drawmethods.helper.HelperMethodsForDG;
import me.owenyy.drawmethods.helper.MultiFomulaForDispatch0;

/**
 * 尝试调度线多画，供水期的选取按实际的日期来进行选择，
 * 实际上蓄水期绘制调度线时选择的径流过程不同，对成图的影响是较大的，如果想要一个比较细的保证出力区，就选择较丰的年份作为水库的蓄水期绘制时的径流过程
 * 
 * 
 * 20170217改为经典的（具体方法成勘院《水能设计》一书），对年调节水库的调度图绘制方法
 * @author OwenYY
 *
 */
public class ClassicMethod implements DispatchGraphBehavior {

	public ClassicMethod() {

	}

	public void makeDispatchGraph(PowerControlHStation hStation, DispatchInputParas input) {
		DispatchGraph dispatchGraph = new DispatchGraph();
		HStationSpec hsSpec = hStation.getHydroStation().getHsSpec();
		/* 先把调节年度的数据生成 */
		int periodstart = hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue();// 时段号，1表示第一个时段
		String tbType = input.getTbType();
		if (tbType == "DECAD") {
			periodstart = (hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue() - 1) * 3 + 1;
		}

		//TimeSeqCurveManager tscm = InitialContainer.getContext().getBean(TimeSeqCurveManager.class);
		//TimeSeqCurve runoffData = tscm.createTimeSeqCurve(hsSpec.getId()/10*10, 1000, tbType, hStation.getHydroStation().getHsStates().size());
		double[] data=new double[hStation.getHydroStation().getHsStates().size()];
		for(int i=0;i<data.length;i++){
			data[i]=hStation.getHydroStation().getHsStates().get(i).getInflowReal();
		}
		TimeSeqCurveFactory timeSeqCurveFactory = new TimeSeqCurveFactory(tbType, 1,  hStation.getHydroStation().getHsStates().get(0).getTimeStart(), hStation.getHydroStation().getHsStates().size(), data);
		TimeSeqCurve runoffData = timeSeqCurveFactory.curvePlotting();
		
		
		MakeSureByOutput divideDeliveryAndStorage = new MakeSureByOutput();
		DivideDeliveryAndStorage  serveTime= new FixedDividePeriod(
				hStation.getHydroStation().getHsStates().get(0).getTimeStart().getYear(),
				periodstart);//对于古水电站，可能需要更改始末供蓄水期
		divideDeliveryAndStorage.setServeTime(serveTime);
		divideDeliveryAndStorage.setAssuranceRate(input.getAssuranceRate());
		divideDeliveryAndStorage.setCurve(hStation.getHydroStation().getStationCurves());
		divideDeliveryAndStorage.setHsSpec(hsSpec);
		
		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(hStation.getHydroStation().getStationCurves());
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(hsSpec);
		divideDeliveryAndStorage.setMfm(mffd0);
		
		if(hsSpec.getLevelFloodLimiting()>0){
			LocalDate floodControlBegin=hStation.getHydroStation().getHsStates().get(0).getTimeStart().toLocalDate();
			divideDeliveryAndStorage.setFloodControlBegin(floodControlBegin);
			LocalDate floodControlEnd=floodControlBegin.plusMonths(4).minusDays(1);
			divideDeliveryAndStorage.setFloodControlEnd(floodControlEnd);
		}
		
		int[] divideResult =divideDeliveryAndStorage.getProvideSaveTimeFinal(runoffData.getDatas().getArray(),
				hStation.getHydroStation().getHsSpec().getStorageRegulating(), tbType,
				hStation.getHydroStation().getHsStates().get(0).getTimeStart().getYear(), periodstart);
		
		EveryYearByDivideResult generateRegulationYear = new EveryYearByDivideResult();

		List<RegulationYear> regulationYears = new ArrayList<RegulationYear>();
		regulationYears = generateRegulationYear.generateRegulationYearsBeginWithSetvalue(runoffData, hsSpec,
				divideResult);// 一个生成regulationYears的方法

		double warrantedOutputSearch = divideDeliveryAndStorage.getGuaranteedOutputSearch();
		hsSpec.setOutputGuaranteed(warrantedOutputSearch);

		int[] constraintTypes = new int[] { 101, 201, 301 };
		DrawDispatchLine drawDispatchLine = new DrawDispatchLine(hStation.getHydroStation().getHsSpec(), hStation.getHydroStation().getStationCurves(),
				tbType);

		List<EmpiricalFrequency> def = divideDeliveryAndStorage.getDef();
		List<EmpiricalFrequency> sef = divideDeliveryAndStorage.getSef();

		// 找出供水期平均流量与当前保证出力代表年接近的多个调节年
		double[] dcorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份供水期的各月入库径流时使用的修正系数，各典型年的，也包括本年的
		// 找出供水期平均流量与当前保证出力代表年接近的多个调节年
		List<RegulationYear> dneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, def,
				input.getAssuranceRate(), input.getTypicalYearsNum(), dcorrectionFactors);// 相似年，并且将流量数据修正好

		// 得到供水期保证出力上下限
		double[][] deliveryPeriodWarrentedOutputLine = drawDispatchLine.drawDeliveryLine(constraintTypes,
				dneighborRegulationYears, dcorrectionFactors, hsSpec.getOutputGuaranteed());
		System.out.println("供水期保证出力上下限：");
		for (int i = 0; i < deliveryPeriodWarrentedOutputLine[1].length; i++) {
			System.out.print(deliveryPeriodWarrentedOutputLine[1][i]);
			System.out.print("	");
		}
		System.out.println();
		for (int i = 0; i < deliveryPeriodWarrentedOutputLine[0].length; i++) {
			System.out.print(deliveryPeriodWarrentedOutputLine[0][i]);
			System.out.print("	");
		}
		System.out.println();
		
		//蓄水期仍按保证出力对典型年进行逆时序调节计算，典型年则根据蓄水期来水量进行选择，选择保证率对应的年份
		//计算蓄水期入库水量（按流量计即可），重新按照蓄水期径流量进行排频
		sef=new ArrayList<EmpiricalFrequency>();
		for(int i=0;i<regulationYears.size();i++)
		{
			sef.add(new EmpiricalFrequency(i,
					BasicMathMethods.sumArray(
							BasicMathMethods.listToDouble(
									regulationYears.get(i).getStoragePeriod().getRunoff())),
					BasicMathMethods.sumArray(
							BasicMathMethods.listToDouble(
									regulationYears.get(i).getStoragePeriod().getRunoff()))));
		}
		sef=EmpiricalFrequency.frequencyCal(sef);//经验排频	
		
		double[] scorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份蓄水期的各月入库径流时使用的修正系数
		
		List<RegulationYear> sneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
				input.getAssuranceRate(), input.getTypicalYearsNum(), scorrectionFactors);// 相似年，并且将流量数据修正好

		// 得到蓄水期保证出力上下限
		double[][] storagePeriodWarrentedOutputLine = hsSpec.getLevelFloodLimiting()>0?(drawDispatchLine.drawStorageLineFL(divideDeliveryAndStorage.getFloodControlBegin(),divideDeliveryAndStorage.getFloodControlEnd(),constraintTypes,
				sneighborRegulationYears, scorrectionFactors, hsSpec.getOutputGuaranteed())):(drawDispatchLine.drawStorageLine(constraintTypes,
				sneighborRegulationYears, scorrectionFactors, hsSpec.getOutputGuaranteed()/*divideDeliveryAndStorage.getGuaranteedOutputSearch()*/));
		System.out.println("蓄水期保证出力上下限：");
		for (int i = 0; i < storagePeriodWarrentedOutputLine[1].length; i++) {
			System.out.print(storagePeriodWarrentedOutputLine[1][i]);
			System.out.print("	");
		}
		System.out.println();
		for (int i = 0; i < storagePeriodWarrentedOutputLine[0].length; i++) {
			System.out.print(storagePeriodWarrentedOutputLine[0][i]);
			System.out.print("	");
		}
		System.out.println();	
		
		
		// 供水期降低出力线
		double[][] ZdLowers = new double[input.getReducePwerLineNum()][];

		double dLowerMinOutput = input.getReduceMultiples()[0]*hsSpec.getOutputGuaranteed();//采用给定的最小倍数做最小降低出力//def.get(def.size() - 1).getValue();// 把供水期最小的出力值找出来
		double dLowerMinFrequency = def.get(def.size() - 1).getFrequency();// 循环的下限是最枯年的数据

		double dLowerMaxOutput = hsSpec.getOutputGuaranteed();// 保证出力是上限

		double[] dLowerNs = new double[input.getReducePwerLineNum()];
		double[] dLowerChosenFrequencies = new double[input.getReducePwerLineNum()];
		for (int k = 0; k < input.getReducePwerLineNum(); k++)// 先把数据线性插值出来
		{
			if (k == 0) {
				dLowerNs[k] = dLowerMinOutput;
				dLowerChosenFrequencies[k] = dLowerMinFrequency;
			} else {
				dLowerNs[k] = input.getReduceMultiples()[k]*hsSpec.getOutputGuaranteed();//dLowerMinOutput + (dLowerMaxOutput - dLowerMinOutput) / input.getReducePwerLineNum() * k;
				dLowerChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(def, dLowerNs[k]);
				// lowerChosenFrequencies[k]=lowerMinFrequency+(lowerMaxFrequency-lowerMinFrequency)/input.getReducePwerLineNum()*k;
				// 频率不应插值，而应该使用插值的出力对应的水文年来进行选取，此处需更改
			}
		}

		for (int k = 0; k < input.getReducePwerLineNum(); k++) {

			double[] mindcorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份供水期的各月入库径流时使用的修正系数，各典型年的，也包括本年的
			List<RegulationYear> mindneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, def,
					input.getAssuranceRate(), input.getTypicalYearsNum(), mindcorrectionFactors);
			double[][] deliveryPeriodMinOutputLine = drawDispatchLine.drawDeliveryLine(constraintTypes,
					mindneighborRegulationYears, mindcorrectionFactors, dLowerNs[k]);
			ZdLowers[k] = deliveryPeriodMinOutputLine[0];
			System.out.println("供水期第" + k + "条降低出力线：");
			for (int i = 0; i < ZdLowers[k].length; i++) {
				System.out.print(ZdLowers[k][i]);
				System.out.print("	");
			}
			System.out.println("");
		}
		// 正序推一条最低水位过程线
		/*LocalDateTime startForLowestLine = LocalDateTime.of(1955, 6, 1, 0, 0);
		double[] inflowForLowest = { 491, 660, 563, 619, 339 };
		double[] ZsLowest = HelperMethodsForDG.psConstantOutput(hStation.getHydroStation().getHsSpec(), hStation.getHydroStation().getStationCurves(),
				constraintTypes, tbType, hStation.getHydroStation().getHsSpec().getLevelDead(), startForLowestLine, inflowForLowest,
				divideDeliveryAndStorage.getGuaranteedOutputSearch());
		System.out.println("最低水位过程线：");
		for (int i = 0; i < ZsLowest.length; i++) {
			System.out.print(ZsLowest[i]);
			System.out.print("	");
		}*/
		
		// 蓄水期降低出力线，选用的出力和径流都与供水期的保持一致
		double[][] ZsLowers = new double[input.getReducePwerLineNum()][];

		double sLowerMinOutput = dLowerMinOutput;
		double sLowerMinFrequency = sef.get(sef.size()-1).getFrequency();// 循环的下限是最枯年的数据

		double sLowerMaxOutput = divideDeliveryAndStorage.getGuaranteedOutputSearch();

		double[] sLowerNs = new double[input.getReducePwerLineNum()];
		double[] sLowerChosenFrequencies = new double[input.getReducePwerLineNum()];
		for (int k = 0; k < input.getReducePwerLineNum(); k++)// 先把数据线性插值出来，从这开始下面的代码都得改
		{
			if (k == 0) {
				sLowerNs[k] = sLowerMinOutput;
				sLowerChosenFrequencies[k] = sLowerMinFrequency;
			} else {
				sLowerNs[k] = input.getReduceMultiples()[k]*hsSpec.getOutputGuaranteed();//sLowerMinOutput + (sLowerMaxOutput - sLowerMinOutput) / input.getReducePwerLineNum() * k;
				sLowerChosenFrequencies[k] = dLowerChosenFrequencies[k];// 出力在蓄水期的对应频率里找很麻烦，因此直接用对应供水期的频率
			}
		}

		for (int k = 0; k < input.getReducePwerLineNum(); k++) {
			// 选出力的时候用供水期的，使用的时候，流量还是用蓄水期排频的会产生交叉，因此都用保证率对应的径流即可
			double[] minscorrectionFactors = new double[input.getTypicalYearsNum() + 1];
			List<RegulationYear> minsneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
					input.getAssuranceRate(), input.getTypicalYearsNum(), minscorrectionFactors);
			double[][] sPeriodMinOutputLine = hsSpec.getLevelFloodLimiting()>0?(drawDispatchLine.drawStorageLineFL(divideDeliveryAndStorage.getFloodControlBegin(),divideDeliveryAndStorage.getFloodControlEnd(),constraintTypes,
					sneighborRegulationYears, scorrectionFactors, hsSpec.getOutputGuaranteed())):(drawDispatchLine.drawStorageLine(constraintTypes,
					minsneighborRegulationYears, minscorrectionFactors, sLowerNs[k]));
			ZsLowers[k] = sPeriodMinOutputLine[0];
			System.out.println("蓄水期第" + k + "条降低出力线：");
			for (int i = 0; i < ZsLowers[k].length; i++) {
				System.out.print(ZsLowers[k][i]);
				System.out.print("	");
			}
			System.out.println("");
		}
		
		// 供水期加大出力线，加大出力的选择与装机容量相关，装机容量线性插值或者直接选择保证出力的倍数，这里选择装机容量插值
		double[][] ZdHighers = new double[input.getAugmentPwerLineNum()][];

		
		double dHigherMaxOutput = hsSpec.getPowerInstalled();
		//def.get(0).getValue();// 把供水期最大的出力值找出来
		double dHigherMaxFrequency = def.get(0).getFrequency();// 循环的上限是最大平均出力对应的年份

		double dHigherMinOutput = hsSpec.getOutputGuaranteed();// 保证出力是下限

		double[] dHigherNs = new double[input.getAugmentPwerLineNum()];
		double[] dHigherChosenFrequencies = new double[input.getAugmentPwerLineNum()];
		for (int k = 0; k < input.getAugmentPwerLineNum(); k++)// 把数据线性插值出来
		{
			if (k == 0) {
				dHigherNs[k] = dHigherMaxOutput;
				dHigherChosenFrequencies[k] = dHigherMaxFrequency;
			} else {
				dHigherNs[k] = input.getArgumentMultiples()[k]*hsSpec.getOutputGuaranteed();//dHigherMaxOutput - (dHigherMaxOutput - dHigherMinOutput) / input.getAugmentPwerLineNum()*k;
				dHigherChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(def, dHigherNs[k]);
			}
		}

		for (int k = 0; k < dHigherNs.length; k++) {
			double[] maxdcorrectionFactors = new double[input.getTypicalYearsNum() + 1];
			List<RegulationYear> maxdneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, def,
					input.getAssuranceRate(), input.getTypicalYearsNum(), maxdcorrectionFactors);// 流量还用保证率附近那几年的
			double[][] deliveryPeriodMinOutputLine = drawDispatchLine.drawDeliveryLine(constraintTypes,
					maxdneighborRegulationYears, maxdcorrectionFactors, dHigherNs[k]);
			ZdHighers[k] = deliveryPeriodMinOutputLine[1];
			System.out.println("供水期第" + k + "条加大出力线：");
			for (int i = 0; i < ZdHighers[k].length; i++) {
				System.out.print(ZdHighers[k][i]);
				System.out.print("	");
			}
			System.out.println("");
		}
		
		// 蓄水期加大出力线，加大出力与供水期保持一致
		double[][] ZsHighers = new double[input.getAugmentPwerLineNum()][];

		double sHigherMaxOutput = hsSpec.getPowerInstalled();;// 最大出力与供水期一致
		double sHigherMaxFrequency = sef.get(0).getFrequency();
		double sHigherMinOutput = hsSpec.getOutputGuaranteed();;// 蓄水期最小出力是下限

		double[] sHigherNs = new double[input.getAugmentPwerLineNum()];
		double[] sHigherChosenFrequencies = new double[input.getAugmentPwerLineNum()];
		for (int k = 0; k < input.getAugmentPwerLineNum(); k++)// 先把数据线性插值出来
		{
			if (k == 0) {
				sHigherNs[k] = sHigherMaxOutput;
				sHigherChosenFrequencies[k] = sHigherMaxFrequency;
			} else {
				sHigherNs[k] = input.getArgumentMultiples()[k]*hsSpec.getOutputGuaranteed();//sHigherMaxOutput	- (sHigherMaxOutput - sHigherMinOutput) / (input.getAugmentPwerLineNum()) * k;
				sHigherChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(sef, sHigherNs[k]);// 频率还是从供水期里对应出力寻找
			}
		}
		for (int k = 0; k < input.getAugmentPwerLineNum(); k++) {
			double[] maxscorrectionFactors = new double[input.getTypicalYearsNum() + 1];
			List<RegulationYear> maxsneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
					input.getAssuranceRate(), input.getTypicalYearsNum(), maxscorrectionFactors);
			double[][] sPeriodMinOutputLine = hsSpec.getLevelFloodLimiting()>0?(drawDispatchLine.drawStorageLineFL(divideDeliveryAndStorage.getFloodControlBegin(),divideDeliveryAndStorage.getFloodControlEnd(),constraintTypes,
					sneighborRegulationYears, scorrectionFactors, hsSpec.getOutputGuaranteed())):(drawDispatchLine.drawStorageLine(constraintTypes,
					maxsneighborRegulationYears, maxscorrectionFactors, sHigherNs[k]));
			ZsHighers[k] = sPeriodMinOutputLine[1];
			System.out.println("蓄水期第" + k + "条加大出力线：");
			for (int i = 0; i < ZsHighers[k].length; i++) {
				System.out.print(ZsHighers[k][i]);
				System.out.print("	");
			}
			System.out.println("");
		}

		// 把供蓄水期结合起来
		int deliverystartnum = regulationYears.get(0).getDeliveryPeriod().getaRegulationYearPeriod().get(0)
				.getlowerDigit();// 供水期初的时段编号
		int storagestartnum = regulationYears.get(0).getStoragePeriod().getaRegulationYearPeriod().get(0)
				.getlowerDigit();// 蓄水期初的时段编号
		dispatchGraph = DrawDispatchGraph.drawDispatchGraph(hsSpec, input, deliverystartnum, storagestartnum,
				deliveryPeriodWarrentedOutputLine, storagePeriodWarrentedOutputLine, ZdHighers, ZsHighers, ZdLowers,
				ZsLowers, hsSpec.getOutputGuaranteed(), hsSpec.getOutputGuaranteed(), dHigherNs,
				sHigherNs, dLowerNs, sLowerNs);
		// dispatchGraph.getDispacthline().remove(dispatchGraph.getDispacthline().size()-2);
		hStation.setDispatchGraph(dispatchGraph);
	}
}
