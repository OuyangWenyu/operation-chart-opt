package me.owenyy.divideperiod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;
import com.wenyu.hydroelements.operation.basic.HydroDateUtil;
import com.wenyu.hydroelements.operation.basic.MultiFomulaMode;
import com.wenyu.hydroelements.timebucket.TimeBucketType;

import me.owenyy.divideperiod.helper.BasicMathMethods;
import me.owenyy.divideperiod.helper.BasicMethod;
import me.owenyy.divideperiod.helper.EmpiricalFrequency;

/**
 * 根据之前的划分供蓄水期的办法，针对每年最后计算各年的供水期平均出力 和蓄水期的最小平均出力。
 * 先以serveTime的方法去试验此类，其它方法等写文章的时候再分析
 * 
 * @author OwenYY
 *
 */
public class MakeSureByOutput implements DivideDeliveryAndStorage {
	private HStationSpec hsSpec;
	private LocalDate floodControlBegin;
	private LocalDate floodControlEnd;
	private StationCurve curve;
	private double assuranceRate;
	private List<EmpiricalFrequency> def;
	private List<EmpiricalFrequency> sef;
	private List<EmpiricalFrequency> sefForMinOutput;
	private double GuaranteedOutputSearch;//供水期平均出力之保证出力
	private double storageMinOutputSearch;//蓄水期平均最小出力
	private double storageAllMinOutputSearch;// 蓄水期出现的最小出力
	private double sGuaranteedOutputSearch;//蓄水期平均出力之保证出力
	private DivideDeliveryAndStorage serveTime;// 分供蓄水期的方式
	private MultiFomulaMode mfm;
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
	public LocalDate getFloodControlBegin() {
		return floodControlBegin;
	}

	public void setFloodControlBegin(LocalDate floodControlBegin) {
		this.floodControlBegin = floodControlBegin;
	}

	public LocalDate getFloodControlEnd() {
		return floodControlEnd;
	}

	public void setFloodControlEnd(LocalDate floodControlEnd) {
		this.floodControlEnd = floodControlEnd;
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
	 * @return the assuranceRate
	 */
	public double getAssuranceRate() {
		return assuranceRate;
	}

	/**
	 * @param assuranceRate
	 *            the assuranceRate to set
	 */
	public void setAssuranceRate(double assuranceRate) {
		this.assuranceRate = assuranceRate;
	}

	/**
	 * @return the def
	 */
	public List<EmpiricalFrequency> getDef() {
		return def;
	}

	/**
	 * @param def
	 *            the def to set
	 */
	public void setDef(List<EmpiricalFrequency> def) {
		this.def = def;
	}

	/**
	 * @return the sef
	 */
	public List<EmpiricalFrequency> getSef() {
		return sef;
	}

	/**
	 * @param sef
	 *            the sef to set
	 */
	public void setSef(List<EmpiricalFrequency> sef) {
		this.sef = sef;
	}

	public List<EmpiricalFrequency> getSefForMinOutput() {
		return sefForMinOutput;
	}

	public void setSefForMinOutput(List<EmpiricalFrequency> sefForMinOutput) {
		this.sefForMinOutput = sefForMinOutput;
	}

	public double getGuaranteedOutputSearch() {
		return GuaranteedOutputSearch;
	}

	public void setGuaranteedOutputSearch(double guaranteedOutputSearch) {
		GuaranteedOutputSearch = guaranteedOutputSearch;
	}

	public double getStorageMinOutputSearch() {
		return storageMinOutputSearch;
	}

	public void setStorageMinOutputSearch(double storageMinOutputSearch) {
		this.storageMinOutputSearch = storageMinOutputSearch;
	}

	public double getStorageAllMinOutputSearch() {
		return storageAllMinOutputSearch;
	}

	public void setStorageAllMinOutputSearch(double storageAllMinOutputSearch) {
		this.storageAllMinOutputSearch = storageAllMinOutputSearch;
	}

	public double getsGuaranteedOutputSearch() {
		return sGuaranteedOutputSearch;
	}

	public void setsGuaranteedOutputSearch(double sGuaranteedOutputSearch) {
		this.sGuaranteedOutputSearch = sGuaranteedOutputSearch;
	}

	/**
	 * @return the serveTime
	 */
	public DivideDeliveryAndStorage getServeTime() {
		return serveTime;
	}

	/**
	 * @param serveTime
	 *            the serveTime to set
	 */
	public void setServeTime(DivideDeliveryAndStorage serveTime) {
		this.serveTime = serveTime;
	}

	public MultiFomulaMode getMfm() {
		return mfm;
	}

	public void setMfm(MultiFomulaMode mfm) {
		this.mfm = mfm;
	}

	public int[] getProvideSaveTimeFinal(double[] inflowtemp, double V_benifit, String tbType, int startYear,
			int startPeriod) {

		int yearPeriods = 12;// 12个月
		if (tbType.equals("DECAD"))
			yearPeriods = 36;
		int colNumTemp = yearPeriods;
		int rowNumTemp = inflowtemp.length / colNumTemp;

		double[][] inflow = new double[rowNumTemp][colNumTemp];
		for (int i = 0; i < rowNumTemp; i++) {
			for (int j = 0; j < colNumTemp; j++) {
				inflow[i][j] = inflowtemp[j + i * colNumTemp];
			}
		}
		int[] providestateFinal = serveTime.getProvideSaveTimeFinal(inflowtemp, V_benifit, tbType, startYear,
				startPeriod);
		int[][] providestatetemp = new int[rowNumTemp][colNumTemp];
		for (int i = 0; i < rowNumTemp; i++) {
			for (int j = 0; j < colNumTemp; j++) {
				providestatetemp[i][j] = providestateFinal[j + yearPeriods * i];
			}
		}

		// 找出按照现在的分年方式里，分期结果里-1出现最早的index，以方便后续计算
		int[] storageBeginIndex = new int[providestatetemp.length];
		for (int i = 0; i < providestatetemp.length; i++) {
			storageBeginIndex[i] = BasicMathMethods.containsIndex(providestatetemp[i], -1);
		}
		int minStorageIndex = BasicMathMethods.minOf1DArray(storageBeginIndex);

		if (minStorageIndex > 0) {// 表示分期结果没有从蓄水期初开始
			inflow = BasicMathMethods.reverse2DArray(inflow, 1, minStorageIndex);
			providestatetemp = BasicMathMethods.reverse2DArray(providestatetemp, 1, minStorageIndex);
			rowNumTemp = inflow.length;
			colNumTemp = inflow[0].length;
			startPeriod = startPeriod + minStorageIndex;
		}

		int[] finalResult = new int[colNumTemp];
		double[] dOutputs = new double[rowNumTemp];
		double[] dRegulatedFlows = new double[rowNumTemp];
		double[] sOutputs = new double[rowNumTemp];
		double[] sRegulatedFlows = new double[rowNumTemp];
		double[] sOutputsMin = new double[rowNumTemp];
		double[] sRegulatedFlowsForMin = new double[rowNumTemp];
		def = new ArrayList<EmpiricalFrequency>();
		sef = new ArrayList<EmpiricalFrequency>();

		sefForMinOutput = new ArrayList<EmpiricalFrequency>();

		for (int i = 0; i < providestatetemp.length; i++) {

			int[] sIndex = searchStorage(providestatetemp[i]);
			int[] dIndex = searchDelivery(providestatetemp[i]);
			double[] drunoff = searchRunOff(dIndex, inflow[i]);
			int[] dlength = calTimeLength(i, startYear, startPeriod, tbType, dIndex);
			double[] srunoff = searchRunOff(sIndex, inflow[i]);
			int[] slength = calTimeLength(i, startYear, startPeriod, tbType, sIndex);
			double[] dOutputavgandflow = BasicMethod.avgOutputOfPeriod(mfm,i + startYear, startPeriod, tbType, dIndex,
					hsSpec, curve, drunoff, dlength, hsSpec.getLevelNormal());
			dOutputs[i] = dOutputavgandflow[0];
			dRegulatedFlows[i] = dOutputavgandflow[1];
			def.add(new EmpiricalFrequency(i, dOutputs[i], dRegulatedFlows[i]));
			double[] sOutputavgandflow = null;
			double[] sOutputMinAndFlow =null;
			if(floodControlBegin!=null && floodControlEnd!=null){//如果有汛限水位，那么调度图的绘制方式有所不同
				sOutputavgandflow = BasicMethod.avgOutputOfPeriodWithFloodControl(mfm,i + startYear, startPeriod, tbType, sIndex,
						hsSpec, curve, srunoff, slength, hsSpec.getLevelDead(),floodControlBegin,floodControlEnd);
				sOutputMinAndFlow = BasicMethod.minOutputOfPeriodWithFloodControl(mfm,i + startYear, startPeriod, tbType, sIndex,
						hsSpec, curve, srunoff, slength, hsSpec.getLevelDead(),floodControlBegin,floodControlEnd);
			} else{
				sOutputavgandflow = BasicMethod.avgOutputOfPeriod(mfm,i + startYear, startPeriod, tbType, sIndex,
						hsSpec, curve, srunoff, slength, hsSpec.getLevelDead());
				sOutputMinAndFlow = BasicMethod.minOutputOfPeriod(mfm,i + startYear, startPeriod, tbType, sIndex,
						hsSpec, curve, srunoff, slength, hsSpec.getLevelDead());
			}
			sOutputs[i] = sOutputavgandflow[0];
			sRegulatedFlows[i] = sOutputavgandflow[1];
			sef.add(new EmpiricalFrequency(i, sOutputs[i], sRegulatedFlows[i]));

			sOutputsMin[i] = sOutputMinAndFlow[0];
			sRegulatedFlowsForMin[i] = sOutputMinAndFlow[1];
			sefForMinOutput.add(new EmpiricalFrequency(i, sOutputsMin[i], sRegulatedFlowsForMin[i]));
		}
		def = EmpiricalFrequency.frequencyCal(def);
		sef = EmpiricalFrequency.frequencyCal(sef);
		sefForMinOutput = EmpiricalFrequency.frequencyCal(sefForMinOutput);
		setStorageAllMinOutputSearch(sefForMinOutput.get(sefForMinOutput.size() - 1).getValue());
		setGuaranteedOutputSearch(EmpiricalFrequency.searchValueByFreq(def, assuranceRate));//实质上取保证出力还是直接取对应年份的供水期平均出力较好，取插值的值容易出现反演计算时不满足年保证率的情况
		setStorageMinOutputSearch(sef.get(sef.size() - 1).getValue());
		setsGuaranteedOutputSearch(EmpiricalFrequency.searchValueByFreq(sef, assuranceRate));
		int dIndexTemp = EmpiricalFrequency.searchRoundIndexByFreq(def, assuranceRate);
		int[] deliRepresentative = providestatetemp[def.get(dIndexTemp).getSerialNumOld()];
		int[] storRepresentative = providestatetemp[sef.get(sef.size() - 1).getSerialNumOld()];
		for (int i = 0; i < storRepresentative.length; i++) {
			if (storRepresentative[i] < 0)
				finalResult[i] = -1;
			else if (deliRepresentative[i] > 0)
				finalResult[i] = 1;
			else
				finalResult[i] = 0;
		}
		int[] results = new int[finalResult.length * rowNumTemp];
		for (int i = 0; i < rowNumTemp; i++) {
			for (int j = 0; j < colNumTemp; j++) {
				results[j + yearPeriods * i] = finalResult[j];
			}
		}
		return results;
	}

	private int[] calTimeLength(int i, int startYear, int startPeriod, String tbType, int[] dIndex) {
		int[] length = new int[dIndex.length];

		for (int j = 0; j < dIndex.length; j++) {
			int periodReal = (startPeriod + dIndex[j]);
			LocalDate time = null;
			TimeBucketType tbtype = TimeBucketType.MONTH;
			if (tbType.equals("MONTH")) {
				int year = startYear + i;
				if (periodReal > 12) {
					periodReal = periodReal - 12;
					year = year + 1;
				}
				time = LocalDate.of(year, periodReal, 1);
			} else if (tbType.equals("DECAD")) {
				tbtype = TimeBucketType.DECAD;
				periodReal = (startPeriod + dIndex[j]);
				int year = startYear + i;
				if (periodReal > 36) {
					periodReal = periodReal - 36;
					year = year + 1;
				}
				if (periodReal % 3 == 1)
					time = LocalDate.of(year, (periodReal - 1) / 3 + 1, 1);
				else
					time = LocalDate.of(year, (periodReal - 1) / 3 + 1, periodReal % 3 == 0 ? 21 : 11);
			}
			length[j] = HydroDateUtil.secondsOfNowPeriod(time, tbtype);
		}
		return length;
	}

	private double[] searchRunOff(int[] Index, double[] r) {
		double[] runoff = new double[Index.length];
		for (int i = 0; i < runoff.length; i++) {
			runoff[i] = r[Index[i]];
		}
		return runoff;
	}

	private int[] searchStorage(int[] providestatetemp) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for (int i = 0; i < providestatetemp.length; i++) {
			if (providestatetemp[i] < 0) {
				arr.add(i);
				if (i == providestatetemp.length - 1 || providestatetemp[i + 1] >= 0)
					break;
			}

		}
		int[] result = new int[arr.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = arr.get(i);
		}
		return result;
	}

	private int[] searchDelivery(int[] providestatetemp) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for (int i = 0; i < providestatetemp.length; i++) {
			if (providestatetemp[i] > 0) {
				arr.add(i);
				if (i == providestatetemp.length - 1 || providestatetemp[i + 1] <= 0)
					break;
			}
		}
		int[] result = new int[arr.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = arr.get(i);
		}
		return result;
	}

}
