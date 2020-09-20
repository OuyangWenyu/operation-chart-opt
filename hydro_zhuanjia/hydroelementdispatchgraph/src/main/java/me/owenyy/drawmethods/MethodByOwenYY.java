package me.owenyy.drawmethods;

import java.util.ArrayList;
import java.util.List;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;
import com.wenyu.service.InitialContainer;
import com.wenyu.service.TimeSeqCurveManager;

import me.owenyy.divideperiod.MakeSureByOutput;
import me.owenyy.divideperiod.ServeTime;
import me.owenyy.divideperiod.helper.EmpiricalFrequency;
import me.owenyy.divideperiod.helper.EveryYearByDivideResult;
import me.owenyy.divideperiod.helper.RegulationYear;
import me.owenyy.drawmethods.helper.DrawDispatchGraph;
import me.owenyy.drawmethods.helper.DrawDispatchLine;
import me.owenyy.drawmethods.helper.HelperMethodsForDG;
import me.owenyy.drawmethods.helper.MultiFomulaForDispatch0;

/**
 * 自己根据原来经典的方法加一些新思路进去，形成的新的绘制调度图的方法： 主要思路是基于分期绘制
 * 这是一个初步的实现
 * @author OwenYY
 *
 */
public class MethodByOwenYY implements DispatchGraphBehavior {

	public MethodByOwenYY() {
		super();
	}

	public void makeDispatchGraph(PowerControlHStation hStation,DispatchInputParas input) {
		DispatchGraph dispatchGraph = new DispatchGraph();// 两个保证出力区线，1个降低出力线，2个加大出力区线
		HStationSpec hsSpec = hStation.getHydroStation().getHsSpec();
		/* 先把调节年度的数据生成 */
		int periodstart = hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue();// 时段号，1表示第一个时段
		String tbType = input.getTbType();
		if (tbType == "DECAD") {
			periodstart = (hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue() - 1) * 3 + 1;
		}

		TimeSeqCurveManager tscm = InitialContainer.getContext().getBean(TimeSeqCurveManager.class);
		TimeSeqCurve runoffData = tscm.createTimeSeqCurve(hsSpec.getId(), 1000, tbType, hStation.getHydroStation().getHsStates().size());

		MakeSureByOutput divideDeliveryAndStorage = new MakeSureByOutput();
		divideDeliveryAndStorage.setServeTime(new ServeTime());
		divideDeliveryAndStorage.setAssuranceRate(0.9);
		divideDeliveryAndStorage.setCurve(hStation.getHydroStation().getStationCurves());
		divideDeliveryAndStorage.setHsSpec(hsSpec);
		
		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(hStation.getHydroStation().getStationCurves());
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(hsSpec);
		divideDeliveryAndStorage.setMfm(mffd0);
		
		int[] divideResult = divideDeliveryAndStorage.getProvideSaveTimeFinal(runoffData.getDatas().getArray(),
				hStation.getHydroStation().getHsSpec().getStorageRegulating(), tbType,
				hStation.getHydroStation().getHsStates().get(0).getTimeStart().getYear(), periodstart);

		EveryYearByDivideResult generateRegulationYear = new EveryYearByDivideResult();

		List<RegulationYear> regulationYears = new ArrayList<RegulationYear>();
		regulationYears = generateRegulationYear.generateRegulationYearsBeginWithStorage(runoffData, hsSpec, divideResult);// 一个生成regulationYears的方法

		double warrantedOutputSearch = divideDeliveryAndStorage.getGuaranteedOutputSearch();
		hsSpec.setOutputGuaranteed(warrantedOutputSearch);

		int[] constraintTypes = new int[] { 101, 201, 301 };
		DrawDispatchLine drawDispatchLine = new DrawDispatchLine(hStation.getHydroStation().getHsSpec(), hStation.getHydroStation().getStationCurves(),
				tbType);

		// 找出供水期平均流量与当前保证出力代表年接近的多个调节年
		List<EmpiricalFrequency> def = divideDeliveryAndStorage.getDef();
		double[] dcorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份供水期的各月入库径流时使用的修正系数，各典型年的，也包括本年的
		// 找出供水期平均流量与当前保证出力代表年接近的多个个调节年
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

		// 供水期降低出力线
		double[][] ZdLowers = new double[input.getReducePwerLineNum()][];

		double dLowerMinOutput = def.get(def.size() - 1).getValue();// 把供水期最小的出力值找出来
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
				dLowerNs[k] = dLowerMinOutput + (dLowerMaxOutput - dLowerMinOutput) / input.getReducePwerLineNum() * k;
				dLowerChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(def, dLowerNs[k]);
				// lowerChosenFrequencies[k]=lowerMinFrequency+(lowerMaxFrequency-lowerMinFrequency)/input.getReducePwerLineNum()*k;
				// 频率不应插值，而应该使用插值的出力对应的水文年来进行选取，此处需更改
			}
		}

		for (int k = 0; k < input.getReducePwerLineNum(); k++) {

			double[] mindcorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份供水期的各月入库径流时使用的修正系数，各典型年的，也包括本年的
			// 找出供水期平均流量与当前最小出力典型年接近的多个个调节年
			/*
			 * List<RegulationYear>
			 * mindneighborRegulationYears=HelperMethodsForDG.
			 * chooseDistantRelatives( regulationYears,def,
			 * input.getAssuranceRate(), input.getTypicalYearsNum()-2, 2,
			 * dLowerChosenFrequencies[k],
			 * mindcorrectionFactors);//相似年，因为比较靠下，再往下找可能就没线了，因此都往上找算了，
			 * 并且将流量数据修正好
			 */
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

		// 供水期加大出力线
		double[][] ZdHighers = new double[input.getAugmentPwerLineNum()][];

		double dHigherMaxOutput = def.get(0).getValue();// 把供水期最大的出力值找出来
		double dHigherMaxFrequency = def.get(0).getFrequency();// 循环的下限是最枯年的数据

		double dHigherMinOutput = hsSpec.getOutputGuaranteed();// 保证出力是下限

		double[] dHigherNs = new double[input.getAugmentPwerLineNum()];
		double[] dHigherChosenFrequencies = new double[input.getAugmentPwerLineNum()];
		for (int k = 0; k < input.getAugmentPwerLineNum(); k++)// 先把数据线性插值出来
		{
			if (k == 0) {
				dHigherNs[k] = dHigherMaxOutput;
				dHigherChosenFrequencies[k] = dHigherMaxFrequency;
			} else {
				dHigherNs[k] = dHigherMaxOutput
						- (dHigherMaxOutput - dHigherMinOutput) / input.getAugmentPwerLineNum() * k;
				// 频率不应插值，而应该使用插值的出力对应的水文年来进行选取，此处需更改
				dHigherChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(def, dHigherNs[k]);
				// higherChosenFrequencies[k]=higherMaxFrequency+(higherMaxFrequency-higherMinFrequency)/input.getReducePwerLineNum()*k;
			}
		}

		for (int k = 0; k < input.getAugmentPwerLineNum(); k++) {
			double[] maxdcorrectionFactors = new double[input.getTypicalYearsNum() + 1];
			// 修正所选出的年份供水期的各月入库径流时使用的修正系数，各典型年的，也包括本年的
			// 找出供水期平均流量与当前最小出力典型年接近的多个个调节年
			/*
			 * List<RegulationYear>
			 * maxdneighborRegulationYears=HelperMethodsForDG.
			 * chooseDistantRelatives( regulationYears,def,
			 * input.getAssuranceRate(), input.getTypicalYearsNum()-2, 2,
			 * dHigherChosenFrequencies[k], maxdcorrectionFactors);
			 */List<RegulationYear> maxdneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, def,
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

		// 然后是蓄水期的，蓄水期和原来的方法略有不同，采用的出力是新的方法
		List<EmpiricalFrequency> sef = divideDeliveryAndStorage.getSef();
		double[] scorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份蓄水期的各月入库径流时使用的修正系数
		// 按给定保证率/保证出力选择保证出力代表年
		// List<RegulationYear>
		// srepresentativeRegulationYear=chooseNeighbors(sef, warrantedOutput,
		// 0, 0,scorrectionFactors);//典型（或代表）年
		// 找出供水期平均流量与当前保证出力代表年接近的多个个调节年（计算好修正系数）
		List<RegulationYear> sneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
				sef.get(sef.size() - 1).getFrequency(), input.getTypicalYearsNum(), scorrectionFactors);// 相似年，并且将流量数据修正好

		// 得到蓄水期保证出力上下限
		double[][] storagePeriodWarrentedOutputLine = drawDispatchLine.drawStorageLine(constraintTypes,
				sneighborRegulationYears, scorrectionFactors, divideDeliveryAndStorage.getStorageMinOutputSearch());
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

		// 蓄水期降低出力线
		double[][] ZsLowers = new double[input.getReducePwerLineNum()][];

		double sLowerMinOutput = divideDeliveryAndStorage.getGuaranteedOutputSearch();// 出力怎么选呢？选保证出力做最小出力？暂时先这样把
		double sLowerMinFrequency = sef.get(sef.size() - 1).getFrequency();// 循环的下限是最枯年的数据

		double sLowerMaxOutput = divideDeliveryAndStorage.getStorageMinOutputSearch();// divideDeliveryAndStorage.getStorageMinOutputSearch()是上限

		double[] sLowerNs = new double[input.getReducePwerLineNum()];
		double[] sLowerChosenFrequencies = new double[input.getReducePwerLineNum()];
		for (int k = 0; k < input.getReducePwerLineNum(); k++)// 先把数据线性插值出来，从这开始下面的代码都得改
		{
			if (k == 0) {
				sLowerNs[k] = sLowerMinOutput;
				sLowerChosenFrequencies[k] = sLowerMinFrequency;
			} else {
				sLowerNs[k] = sLowerMinOutput + (sLowerMaxOutput - sLowerMinOutput) / input.getReducePwerLineNum() * k;
				sLowerChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(sef, sLowerNs[k]);// 出力在供水期的对应频率里找
			}
		}

		for (int k = 0; k < input.getReducePwerLineNum(); k++) {
			// 选出力的时候用供水期的，使用的时候，流量还是用蓄水期排频的
			double[] minscorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份供水期的各月入库径流时使用的修正系数，各典型年的，也包括本年的
			// 找出蓄水期平均流量与当前最小出力典型年接近的多个个调节年
			/*
			 * List<RegulationYear>
			 * minsneighborRegulationYears=HelperMethodsForDG.
			 * chooseDistantRelatives( regulationYears,sef,
			 * input.getAssuranceRate(), input.getTypicalYearsNum()-2, 2,
			 * sLowerChosenFrequencies[k], minscorrectionFactors);
			 */
			List<RegulationYear> minsneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
					sLowerChosenFrequencies[k], input.getTypicalYearsNum(), minscorrectionFactors);
			double[][] sPeriodMinOutputLine = drawDispatchLine.drawStorageLine(constraintTypes,
					minsneighborRegulationYears, minscorrectionFactors, sLowerNs[k]);
			ZsLowers[k] = sPeriodMinOutputLine[0];
			System.out.println("蓄水期第" + k + "条降低出力线：");
			for (int i = 0; i < ZsLowers[k].length; i++) {
				System.out.print(ZsLowers[k][i]);
				System.out.print("	");
			}
			System.out.println("");
		}

		// 蓄水期加大出力线
		double[][] ZsHighers = new double[input.getAugmentPwerLineNum()][];

		double sHigherMaxOutput = sef.get(0).getValue();// 把蓄水期最大的出力值找出来
		double sHigherMaxFrequency = sef.get(0).getFrequency();

		double sHigherMinOutput = divideDeliveryAndStorage.getStorageMinOutputSearch();// 蓄水期最小出力是下限

		double[] sHigherNs = new double[input.getAugmentPwerLineNum()];
		double[] sHigherChosenFrequencies = new double[input.getAugmentPwerLineNum()];
		for (int k = 0; k < input.getAugmentPwerLineNum(); k++)// 先把数据线性插值出来
		{
			if (k == 0) {
				sHigherNs[k] = sHigherMaxOutput;
				sHigherChosenFrequencies[k] = sHigherMaxFrequency;
			} else {
				sHigherNs[k] = sHigherMaxOutput
						- (sHigherMaxOutput - sHigherMinOutput) / input.getAugmentPwerLineNum() * k;
				sHigherChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(sef, sHigherNs[k]);// 频率还是从供水期里对应出力寻找
			}
		}

		for (int k = 0; k < input.getAugmentPwerLineNum(); k++) {
			double[] maxscorrectionFactors = new double[input.getTypicalYearsNum() + 1];
			/*
			 * List<RegulationYear>
			 * maxsneighborRegulationYears=HelperMethodsForDG.
			 * chooseDistantRelatives( regulationYears,sef,
			 * input.getAssuranceRate(), input.getTypicalYearsNum()-2, 2,
			 * sHigherChosenFrequencies[k], maxscorrectionFactors);
			 */
			List<RegulationYear> maxsneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
					sHigherChosenFrequencies[k], input.getTypicalYearsNum(), maxscorrectionFactors);
			double[][] sPeriodMinOutputLine = drawDispatchLine.drawStorageLine(constraintTypes,
					maxsneighborRegulationYears, maxscorrectionFactors, sHigherNs[k]);
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
				.getlowerDigit() + 1;// 供水期初的时段编号
		int storagestartnum = regulationYears.get(0).getStoragePeriod().getaRegulationYearPeriod().get(0)
				.getlowerDigit() + 1;// 蓄水期初的时段编号
		dispatchGraph = DrawDispatchGraph.drawDispatchGraph(hsSpec, input, deliverystartnum, storagestartnum,
				deliveryPeriodWarrentedOutputLine, storagePeriodWarrentedOutputLine, ZdHighers, ZsHighers, ZdLowers,
				ZsLowers, warrantedOutputSearch, divideDeliveryAndStorage.getStorageMinOutputSearch(), dHigherNs,
				sHigherNs, dLowerNs, sLowerNs);
		// dispatchGraph.getDispacthline().remove(dispatchGraph.getDispacthline().size()-2);
		hStation.setDispatchGraph(dispatchGraph);
	}
}
