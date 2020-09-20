package me.owenyy.drawmethods.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;
import com.wenyu.service.InitialContainer;
import com.wenyu.service.TimeSeqCurveManager;

import me.owenyy.divideperiod.helper.BasicMathMethods;
import me.owenyy.divideperiod.helper.DeliOrStor;
import me.owenyy.divideperiod.helper.RegulationYear;
import me.owenyy.divideperiod.helper.RegulationYearPeriod;

/**
 * 绘制调度线的 方法类
 *
 */
public class DrawDispatchLine {
	private HStationSpec hsSpec;
	private StationCurve curve;
	private String tbType;

	public DrawDispatchLine(HStationSpec hsSpec, StationCurve curve, String tbType) {
		super();
		this.hsSpec = hsSpec;
		this.curve = curve;
		this.tbType = tbType;
	}

	/**
	 * @return the tbType
	 */
	public String getTbType() {
		return tbType;
	}

	/**
	 * @param tbType
	 *            the tbType to set
	 */
	public void setTbType(String tbType) {
		this.tbType = tbType;
	}

	/**
	 * @return the hsSpec
	 */
	public HStationSpec getHsSpec() {
		return hsSpec;
	}

	/**
	 * @param hsSpec
	 *            the hsSpec to set
	 */
	public void setHsSpec(HStationSpec hsSpec) {
		this.hsSpec = hsSpec;
	}

	/**
	 * @return the curve
	 */
	public StationCurve getCurve() {
		return curve;
	}

	/**
	 * @param curve
	 *            the curve to set
	 */
	public void setCurve(StationCurve curve) {
		this.curve = curve;
	}

	/**
	 * @param startYear
	 * @param startPeriod
	 * @param constraintTypes
	 * @param dneighborRegulationYears
	 *            选定的几个代表年的供水期
	 * @param dcorrectionFactors
	 *            每个代表年的流量修正系数
	 * @param N
	 *            等出力计算用的出力值
	 * @return 逆时程绘制各个代表年供水期的调度线，然后取上下包络线
	 */
	public double[][] drawDeliveryLine(int[] constraintTypes, List<RegulationYear> dneighborRegulationYears,
			double[] dcorrectionFactors, double N) {

		// 修正供水期入库流量
		double[][] amendQind = new double[dneighborRegulationYears.size()][];
		for (int i = 0; i < amendQind.length; i++) {
			amendQind[i] = BasicMathMethods.arrayMultiplyNumber(
					BasicMathMethods.listToDouble(dneighborRegulationYears.get(i).getDeliveryPeriod().getRunoff()),
					dcorrectionFactors[i]);
		}

		// 按各月出力等于给定出力，对修正后的各典型年供水期，自死水位（年消落水位）开始，逆时程进行水库调节计算，记录下各调节年供水期水位值
		List<double[]> dlevelLines = new ArrayList<double[]>();
		for (int i = 0; i < dneighborRegulationYears.size(); i++) {
			int startYear = dneighborRegulationYears.get(i).getDeliveryPeriod().getaRegulationYearPeriod().get(0)
					.gethighDigit();
			int startPeriod = dneighborRegulationYears.get(i).getDeliveryPeriod().getaRegulationYearPeriod().get(0)
					.getlowerDigit();
			double[] level = HelperMethodsForDG.outputRegulationCal(DeliOrStor.DELIVERY.getID(), hsSpec, curve,
					amendQind[i],
					BasicMathMethods.listToInt(dneighborRegulationYears.get(i).getDeliveryPeriod().getPeriodlength()),
					N, constraintTypes, tbType, startYear, startPeriod);
			dlevelLines.add(level);// 各典型年的水位过程记录下来
		}

		// 取包络线
		List<RegulationYearPeriod> dneighbors = new ArrayList<RegulationYearPeriod>();
		for (int i = 0; i < dneighborRegulationYears.size(); i++) {
			dneighbors.add(dneighborRegulationYears.get(i).getDeliveryPeriod());
		}
		double[][] deliveryPeriodWarrentedOutputLine = HelperMethodsForDG.envelopeLine(dlevelLines, dneighbors);

		return deliveryPeriodWarrentedOutputLine;
	}

	/**
	 * @param hsSpec
	 * @param sneighborRegulationYears
	 * @param scorrectionFactors
	 * @param N
	 * @return 逆时程绘制各个代表年蓄水期的调度线，然后取上下包络线
	 */
	public double[][] drawStorageLine(int[] constraintTypes, List<RegulationYear> sneighborRegulationYears,
			double[] scorrectionFactors, double N) {
		// 修正蓄水期入库流量
		double[][] amendQins = new double[sneighborRegulationYears.size()][];
		for (int i = 0; i < amendQins.length; i++) {
			amendQins[i] = BasicMathMethods.arrayMultiplyNumber(
					BasicMathMethods.listToDouble(sneighborRegulationYears.get(i).getStoragePeriod().getRunoff()),
					scorrectionFactors[i]);
		}

		List<double[]> slevelLines = new ArrayList<double[]>();
		for (int i = 0; i < sneighborRegulationYears.size(); i++) {
			int startYear = sneighborRegulationYears.get(i).getStoragePeriod().getaRegulationYearPeriod().get(0)
					.gethighDigit();
			int startPeriod = sneighborRegulationYears.get(i).getStoragePeriod().getaRegulationYearPeriod().get(0)
					.getlowerDigit();
			double[] level = HelperMethodsForDG.outputRegulationCal(DeliOrStor.STORAGE.getID(), hsSpec, curve,
					amendQins[i],
					BasicMathMethods.listToInt(sneighborRegulationYears.get(i).getStoragePeriod().getPeriodlength()), N,
					constraintTypes, tbType, startYear, startPeriod);
			slevelLines.add(level);// 各典型年的水位过程记录下来
		}

		// 取包络线
		List<RegulationYearPeriod> sneighbors = new ArrayList<RegulationYearPeriod>();
		for (int i = 0; i < sneighborRegulationYears.size(); i++) {
			sneighbors.add(sneighborRegulationYears.get(i).getStoragePeriod());
		}
		double[][] storagePeriodWarrentedOutputLine = HelperMethodsForDG.envelopeLine(slevelLines, sneighbors);

		return storagePeriodWarrentedOutputLine;
	}
	
	/**
	 * @param hsSpec
	 * @param sneighborRegulationYears
	 * @param scorrectionFactors
	 * @param N
	 * @return 绘制有汛限水位的各个代表年蓄水期的调度线，然后取上下包络线
	 */
	public double[][] drawStorageLineFL(LocalDate floodControlBegin, LocalDate floodControlEnd, int[] constraintTypes,
			List<RegulationYear> sneighborRegulationYears, double[] scorrectionFactors, double N) {
		//修正蓄水期入库流量
		// 修正蓄水期入库流量
		double[][] amendQins = new double[sneighborRegulationYears.size()][];
		for (int i = 0; i < amendQins.length; i++) {
			amendQins[i] = BasicMathMethods.arrayMultiplyNumber(
					BasicMathMethods.listToDouble(sneighborRegulationYears.get(i).getStoragePeriod().getRunoff()),
					scorrectionFactors[i]);
		}	
		
		List<double[]> slevelLines = new ArrayList<double[]>();
		for (int i = 0; i < sneighborRegulationYears.size(); i++) {
			int startYear = sneighborRegulationYears.get(i).getStoragePeriod().getaRegulationYearPeriod().get(0)
					.gethighDigit();
			int startPeriod = sneighborRegulationYears.get(i).getStoragePeriod().getaRegulationYearPeriod().get(0)
					.getlowerDigit();
			double[] level = HelperMethodsForDG.outputRegulationCal(floodControlBegin,floodControlEnd,DeliOrStor.STORAGE.getID(), hsSpec, curve,
					amendQins[i],
					BasicMathMethods.listToInt(sneighborRegulationYears.get(i).getStoragePeriod().getPeriodlength()), N,
					constraintTypes, tbType, startYear, startPeriod);
			slevelLines.add(level);// 各典型年的水位过程记录下来
		}
		
		
		//取包络线
		List<RegulationYearPeriod> sneighbors = new ArrayList<RegulationYearPeriod>();
		for (int i = 0; i < sneighborRegulationYears.size(); i++) {
			sneighbors.add(sneighborRegulationYears.get(i).getStoragePeriod());
		}
		double[][] storagePeriodWarrentedOutputLine = HelperMethodsForDG.envelopeLine(slevelLines, sneighbors);

		return storagePeriodWarrentedOutputLine;		
	}
	
	/**
	 * @param constraintTypes
	 * @param startTime
	 * @param periodNums 总时段个数
	 * @param storageMinOutput
	 * @return
	 */
	public double[] storageLowestLine(int[] constraintTypes,LocalDateTime startTime,int periodNums,double storageMinOutput){
		TimeSeqCurveManager tscm = InitialContainer.getContext().getBean(TimeSeqCurveManager.class);
		TimeSeqCurve runoffData = tscm.createTimeSeqCurve(hsSpec.getId(), 1000, tbType, periodNums);
		int aYearNum=12;
		if(tbType=="DECAD")
			aYearNum=36;
		double[][] inflow=BasicMathMethods.array1DTo2D(runoffData.getDatas().getArray(), aYearNum);
		double[][] temp=BasicMathMethods.transpose(inflow);
		double[] minInflows=new double[aYearNum];
		for(int i=0;i<minInflows.length;i++){
			minInflows[i]=BasicMathMethods.minOf1DArray(temp[i]);
		}
		double[] storageLowestLine=HelperMethodsForDG.psConstantOutput(hsSpec, curve, constraintTypes, tbType, hsSpec.getLevelDead(), startTime, minInflows, storageMinOutput);
		return storageLowestLine;
	}
}
