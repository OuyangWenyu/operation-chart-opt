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

import me.owenyy.divideperiod.DivideDeliveryAndStorage;
import me.owenyy.divideperiod.MakeSureByOutput;
import me.owenyy.divideperiod.ServeTime;
import me.owenyy.divideperiod.FixedDividePeriod;
import me.owenyy.divideperiod.helper.EmpiricalFrequency;
import me.owenyy.divideperiod.helper.EveryYearByDivideResult;
import me.owenyy.divideperiod.helper.RegulationYear;
import me.owenyy.drawmethods.helper.DrawDispatchGraph;
import me.owenyy.drawmethods.helper.DrawDispatchLine;
import me.owenyy.drawmethods.helper.HelperMethodsForDG;
import me.owenyy.drawmethods.helper.PresetOutputAndFrequency;

/**
 * 调度线多画，供水期的选取按实际的日期来进行选择，
 * 实际上蓄水期绘制调度线时选择的径流过程不同，对成图的影响是较大的，如果想要一个比较细的保证出力区，就选择较丰的年份作为水库的蓄水期绘制时的径流过程
 * 
 * @author OwenYY
 *
 */
public class MethodDifferentOutput implements DispatchGraphBehavior {

	public MethodDifferentOutput() {

	}

	public void makeDispatchGraph(PowerControlHStation hStation, DispatchInputParas input) {
		DispatchGraph dispatchGraph = new DispatchGraph();
		HStationSpec hsSpec = hStation.getHydroStation().getHsSpec();
		/* 先把调节年度的数据生成 */
		int periodstart = hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue();// 时段号，1表示第一个时段
		String tbType = input.getTbType();
		if (tbType == "DECAD") {
			periodstart = (hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue() - 1) * 3 + 1;
			if(hStation.getHydroStation().getHsStates().get(0).getTimeStart().getDayOfMonth()>10)
				periodstart=periodstart+1;
			if(hStation.getHydroStation().getHsStates().get(0).getTimeStart().getDayOfMonth()>20)
				periodstart=periodstart+1;
		}

		TimeSeqCurveManager tscm = InitialContainer.getContext().getBean(TimeSeqCurveManager.class);
		TimeSeqCurve runoffData = tscm.createTimeSeqCurve(hsSpec.getId()/10*10, 1000, tbType, hStation.getHydroStation().getHsStates().size());

		MakeSureByOutput divideDeliveryAndStorage = new MakeSureByOutput();
		divideDeliveryAndStorage.setServeTime(new ServeTime());
		divideDeliveryAndStorage.setAssuranceRate(0.9);
		divideDeliveryAndStorage.setCurve(hStation.getHydroStation().getStationCurves());
		divideDeliveryAndStorage.setHsSpec(hsSpec);
		divideDeliveryAndStorage.getProvideSaveTimeFinal(runoffData.getDatas().getArray(),
				hStation.getHydroStation().getHsSpec().getStorageRegulating(), tbType,
				hStation.getHydroStation().getHsStates().get(0).getTimeStart().getYear(), periodstart);
		// DivideDeliveryAndStorage serveTime = new ServeTime();
		DivideDeliveryAndStorage serveTime = new FixedDividePeriod(
				hStation.getHydroStation().getHsStates().get(0).getTimeStart().getYear(),
				periodstart/*
							 * ,input.getDeliveryPeriodMonth(),input.
							 * getStoragePeriodMonth()
							 */);

		int[] divideResult = serveTime.getProvideSaveTimeFinal(runoffData.getDatas().getArray(),
				hStation.getHydroStation().getHsSpec().getStorageRegulating(), tbType,
				hStation.getHydroStation().getHsStates().get(0).getTimeStart().getYear(), periodstart);
		EveryYearByDivideResult generateRegulationYear = new EveryYearByDivideResult();

		List<RegulationYear> regulationYears = new ArrayList<RegulationYear>();
		regulationYears = generateRegulationYear.generateRegulationYearsBeginWithSetvalue(runoffData, hsSpec,
				divideResult);// 一个生成regulationYears的方法

		/*double warrantedOutputSearch = divideDeliveryAndStorage.getGuaranteedOutputSearch();
		hsSpec.setOutputGuaranteed(warrantedOutputSearch);*/

		int[] constraintTypes = new int[] { 101, 201, 301 };
		DrawDispatchLine drawDispatchLine = new DrawDispatchLine(hStation.getHydroStation().getHsSpec(), hStation.getHydroStation().getStationCurves(),
				tbType);

		List<EmpiricalFrequency> def = divideDeliveryAndStorage.getDef();
		List<EmpiricalFrequency> sef = divideDeliveryAndStorage.getSef();

		//蓄水期还是先按保证出力算，但是后面加大出力值给多一点，但是加大出力值的给法也是分两种去给，同供水期类似
		double[] scorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份蓄水期的各月入库径流时使用的修正系数
		// 找水量偏平水年的几年比较好?
		List<RegulationYear> sneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
				sef.get(sef.size()-2).getFrequency(), input.getTypicalYearsNum(), scorrectionFactors);// 相似年，并且将流量数据修正好

		// 得到蓄水期保证出力上下限
		double[][] storagePeriodWarrentedOutputLine = drawDispatchLine.drawStorageLine(constraintTypes,
				sneighborRegulationYears, scorrectionFactors, hsSpec.getOutputGuaranteed()/*divideDeliveryAndStorage.getGuaranteedOutputSearch()*/);
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
		System.out.println("该区出力值是："+hsSpec.getOutputGuaranteed());
		
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
		System.out.println("该区出力值是："+hsSpec.getOutputGuaranteed());
		
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
			System.out.println("该区出力值是："+dLowerNs[k]);
		}

		// 供水期加大出力线（本方法的调度出力线是较多的 为了和蓄水期的水位过程线能连接在一起 把蓄水期的几个加大出力的值也给放进来进行计算
		// 但是实际中并不会使用到）
		// 因此考虑分成两部分来画供水期加大出力线 一部分是跟原来的方法一致的 另一部分是对蓄水期加大出力线进行运算得到的
		// 这也是为了与调度图常用形式统一起来
		double[][] ZdHighers = new double[input.getAugmentPwerLineNum()][];

		PresetOutputAndFrequency poafZdH=new PresetOutputAndFrequency();
		poafZdH.linearInterpolation(input.getAugmentPwerLineNum(), def, def.get(0).getValue(), hsSpec.getOutputGuaranteed());
		double[] dHigherNs = poafZdH.getOutputs();
		//double[] dHigherChosenFrequencies = poafZdH.getFrequencies();

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
			System.out.println("该区出力值是："+dHigherNs[k]);
		}

		// 蓄水期降低出力线
		double[][] ZsLowers = new double[input.getReducePwerLineNum()][];

		double sLowerMinOutput = dLowerMinOutput;// 出力怎么选呢？选保证出力做最小出力？暂时先这样把
		double sLowerMinFrequency = sef.get(sef.size() / 2).getFrequency();// 循环的下限是最枯年的数据

		double sLowerMaxOutput = divideDeliveryAndStorage.getGuaranteedOutputSearch();

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
			double[] minscorrectionFactors = new double[input.getTypicalYearsNum() + 1];
			List<RegulationYear> minsneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
					sef.get(sef.size()-2).getFrequency(), input.getTypicalYearsNum(), minscorrectionFactors);
			double[][] sPeriodMinOutputLine = drawDispatchLine.drawStorageLine(constraintTypes,
					minsneighborRegulationYears, minscorrectionFactors, sLowerNs[k]);
			ZsLowers[k] = sPeriodMinOutputLine[0];
			System.out.println("蓄水期第" + k + "条降低出力线：");
			for (int i = 0; i < ZsLowers[k].length; i++) {
				System.out.print(ZsLowers[k][i]);
				System.out.print("	");
			}
			System.out.println("");
			System.out.println("该区出力值是："+sLowerNs[k]);
		}

		// 蓄水期加大出力线
		PresetOutputAndFrequency poafZsH=new PresetOutputAndFrequency();
		poafZsH.linearInterpolation(input.getAugmentPwerLineNum(), sef, hsSpec.getPowerInstalled(), hsSpec.getOutputGuaranteed());
		double[][] ZsHighers = new double[input.getAugmentPwerLineNum()][];

		
		double[] sHigherNs = poafZsH.getOutputs();
		//double[] sHigherChosenFrequencies =poafZsH.getFrequencies();
		
		
		for (int k = 0; k < input.getAugmentPwerLineNum() ; k++) {
			double[] maxscorrectionFactors = new double[input.getTypicalYearsNum() + 1];
			/*
			 * List<RegulationYear>
			 * maxsneighborRegulationYears=HelperMethodsForDG.
			 * chooseDistantRelatives( regulationYears,sef,
			 * input.getAssuranceRate(), input.getTypicalYearsNum()-2, 2,
			 * sHigherChosenFrequencies[k], maxscorrectionFactors);
			 */
			List<RegulationYear> maxsneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
					sef.get(sef.size()-2).getFrequency(), input.getTypicalYearsNum(), maxscorrectionFactors);
			double[][] sPeriodMinOutputLine = drawDispatchLine.drawStorageLine(constraintTypes,
					maxsneighborRegulationYears, maxscorrectionFactors, sHigherNs[k]);
			ZsHighers[k] = sPeriodMinOutputLine[1];
			System.out.println("蓄水期第" + k + "条加大出力线：");
			for (int i = 0; i < ZsHighers[k].length; i++) {
				System.out.print(ZsHighers[k][i]);
				System.out.print("	");
			}
			System.out.println("");
			System.out.println("该区出力值是："+sHigherNs[k]);
		}

		// 把供蓄水期结合起来
		int deliverystartnum = regulationYears.get(0).getDeliveryPeriod().getaRegulationYearPeriod().get(0)
				.getlowerDigit();// 供水期初的时段编号
		int storagestartnum = regulationYears.get(0).getStoragePeriod().getaRegulationYearPeriod().get(0)
				.getlowerDigit();// 蓄水期初的时段编号
		dispatchGraph = DrawDispatchGraph.drawDispatchGraph(hsSpec, input, deliverystartnum, storagestartnum,
				deliveryPeriodWarrentedOutputLine, storagePeriodWarrentedOutputLine, ZdHighers, ZsHighers, ZdLowers,
				ZsLowers, hsSpec.getOutputGuaranteed(), divideDeliveryAndStorage.getStorageMinOutputSearch(), dHigherNs,
				sHigherNs, dLowerNs, sLowerNs);
		// dispatchGraph.getDispacthline().remove(dispatchGraph.getDispacthline().size()-2);
		hStation.setDispatchGraph(dispatchGraph);
	}
}
