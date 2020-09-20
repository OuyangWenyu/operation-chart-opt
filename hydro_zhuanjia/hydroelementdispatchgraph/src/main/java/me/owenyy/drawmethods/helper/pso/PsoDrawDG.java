package me.owenyy.drawmethods.helper.pso;

import java.util.List;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;

import me.owenyy.divideperiod.MakeSureByOutput;
import me.owenyy.divideperiod.helper.EmpiricalFrequency;
import me.owenyy.divideperiod.helper.RegulationYear;
import me.owenyy.drawmethods.helper.DrawDispatchGraph;
import me.owenyy.drawmethods.helper.DrawDispatchLine;
import me.owenyy.drawmethods.helper.HelperMethodsForDG;

public class PsoDrawDG {
	private double dGuaranteedOutput;
	private double sGuaranteedOutput;
	
	public double getdGuaranteedOutput() {
		return dGuaranteedOutput;
	}

	public void setdGuaranteedOutput(double dGuaranteedOutput) {
		this.dGuaranteedOutput = dGuaranteedOutput;
	}

	public double getsGuaranteedOutput() {
		return sGuaranteedOutput;
	}

	public void setsGuaranteedOutput(double sGuaranteedOutput) {
		this.sGuaranteedOutput = sGuaranteedOutput;
	}

	public PsoDrawDG(double dGuaranteedOutput, double sGuaranteedOutput) {
		super();
		this.dGuaranteedOutput = dGuaranteedOutput;
		this.sGuaranteedOutput = sGuaranteedOutput;
	}

	public void makeDG(PowerControlHStation hStation, DispatchGraph dispatchGraph, DGInputParasPlus inputPlus,
			MakeSureByOutput divideDeliveryAndStorage, List<RegulationYear> regulationYears) {
		DispatchInputParas input=inputPlus.getInput();
		HStationSpec hsSpec = hStation.getHydroStation().getHsSpec();
		@SuppressWarnings("unused")
		int periodstart = hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue();// 时段号，1表示第一个时段
		String tbType = input.getTbType();
		if (tbType == "DECAD") {
			periodstart = (hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue() - 1) * 3 + 1;
		}
		//double warrantedOutputSearch = divideDeliveryAndStorage.getGuaranteedOutputSearch();
		hsSpec.setOutputGuaranteed(dGuaranteedOutput);

		double dFrequency=inputPlus.getdRepresFre();
		double sFrequency=inputPlus.getsRepresFre();
		
		int[] constraintTypes = new int[] { 101, 201, 301 };
		DrawDispatchLine drawDispatchLine = new DrawDispatchLine(hStation.getHydroStation().getHsSpec(),
				hStation.getHydroStation().getStationCurves(), tbType);

		// 找出供水期平均流量与当前保证出力代表年接近的多个调节年
		List<EmpiricalFrequency> def = divideDeliveryAndStorage.getDef();
		double[] dcorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份供水期的各月入库径流时使用的修正系数，各典型年的，也包括本年的
		// 找出供水期平均流量与当前保证出力代表年接近的多个个调节年
		List<RegulationYear> dneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, def,
				/*input.getAssuranceRate()*/dFrequency, input.getTypicalYearsNum(), dcorrectionFactors);// 相似年，并且将流量数据修正好

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

		double[] dLowerNs = new double[input.getReducePwerLineNum()];
		double[] dLowerChosenFrequencies = new double[input.getReducePwerLineNum()];
		for (int k = 0; k < input.getReducePwerLineNum(); k++)// 先把数据线性插值出来
		{
			dLowerNs[k] = (inputPlus.getdReduceMultiples())[k] * dGuaranteedOutput;//出力要从小到大
			dLowerChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(def, dLowerNs[k]);
			
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
					/*input.getAssuranceRate()*/dFrequency, input.getTypicalYearsNum(), mindcorrectionFactors);
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

		double[] dHigherNs = new double[input.getAugmentPwerLineNum()];
		double[] dHigherChosenFrequencies = new double[input.getAugmentPwerLineNum()];
		for (int k = 0; k < input.getAugmentPwerLineNum(); k++)// 先把数据线性插值出来
		{
			dHigherNs[k] = (inputPlus.getdArgumentMultiples())[k] * dGuaranteedOutput;//出力要从小到大
			dHigherChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(def, dHigherNs[k]);
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
					/*input.getAssuranceRate()*/dFrequency, input.getTypicalYearsNum(), maxdcorrectionFactors);// 流量还用保证率附近那几年的
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
		// 按给定频率选择保证出力典型年
		List<RegulationYear> sneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
				/*sef.get(sef.size() - 1).getFrequency()*/sFrequency, input.getTypicalYearsNum(), scorrectionFactors);// 相似年，并且将流量数据修正好

		// 得到蓄水期保证出力上下限
		double[][] storagePeriodWarrentedOutputLine = drawDispatchLine.drawStorageLine(constraintTypes,
				sneighborRegulationYears, scorrectionFactors, sGuaranteedOutput/*divideDeliveryAndStorage.getStorageMinOutputSearch()*/);
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

		
		double[] sLowerNs = new double[input.getReducePwerLineNum()];
		double[] sLowerChosenFrequencies = new double[input.getReducePwerLineNum()];
		for (int k = 0; k < input.getReducePwerLineNum(); k++)// 先把数据线性插值出来，从这开始下面的代码都得改
		{
			sLowerNs[k] = (inputPlus.getsReduceMultiples())[k] * sGuaranteedOutput;//出力要从小到大
			sLowerChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(sef, sLowerNs[k]);
		}

		for (int k = 0; k < input.getReducePwerLineNum(); k++) {
			// 选出力的时候用供水期的，使用的时候，流量还是用蓄水期排频的
			double[] minscorrectionFactors = new double[input.getTypicalYearsNum() + 1];// 修正所选出的年份供水期的各月入库径流时使用的修正系数，各典型年的，也包括本年的
			// 找出蓄水期平均流量与当前最小出力典型年接近的多个个调节年
			List<RegulationYear> minsneighborRegulationYears = HelperMethodsForDG.chooseTypicals(regulationYears, sef,
					/*sLowerChosenFrequencies[k]*/sFrequency, input.getTypicalYearsNum(), minscorrectionFactors);
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

		
		double[] sHigherNs = new double[input.getAugmentPwerLineNum()];
		double[] sHigherChosenFrequencies = new double[input.getAugmentPwerLineNum()];
		for (int k = 0; k < input.getAugmentPwerLineNum(); k++)// 先把数据线性插值出来
		{
			sHigherNs[k] = (inputPlus.getsArgumentMultiples())[k] * sGuaranteedOutput;//出力要从小到大
			sHigherChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(sef, sHigherNs[k]);
		
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
					/*sHigherChosenFrequencies[k]*/sFrequency, input.getTypicalYearsNum(), maxscorrectionFactors);
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
				.getlowerDigit() ;// 供水期初的时段编号
		int storagestartnum = regulationYears.get(0).getStoragePeriod().getaRegulationYearPeriod().get(0)
				.getlowerDigit() ;// 蓄水期初的时段编号
		dispatchGraph = DrawDispatchGraph.drawDispatchGraphDifferentByStage(hsSpec, input, deliverystartnum, storagestartnum,
				deliveryPeriodWarrentedOutputLine, storagePeriodWarrentedOutputLine, ZdHighers, ZsHighers, ZdLowers,
				ZsLowers, dGuaranteedOutput, /*divideDeliveryAndStorage.getStorageMinOutputSearch()*/sGuaranteedOutput, dHigherNs,
				sHigherNs, dLowerNs, sLowerNs);
		// dispatchGraph.getDispacthline().remove(dispatchGraph.getDispacthline().size()-2);
		hStation.setDispatchGraph(dispatchGraph);
	}
}
