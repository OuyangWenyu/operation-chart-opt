package me.owenyy.divideperiod.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.CommonConsts;
import com.wenyu.hydroelements.operation.basic.HydroDateUtil;
import com.wenyu.hydroelements.timebucket.TimeBucketType;

/**
 * 每年的供蓄期都直接按照divideResult里的来
 */
public class EveryYearByDivideResult {

	/**
	 * 每年的供蓄期都直接按照divideResult里的来， runoffdata和divideResult的开始时间应该保持一致，
	 * 生成从供水期初开始的水文年数据，用以供水期的调度图绘制
	 */
	public List<RegulationYear> generateRegulationYearsBeginWithDelivery(TimeSeqCurve runoffData, HStationSpec hsSpec,
			int[] divideResult) {
		List<RegulationYear> regulationYears = new ArrayList<RegulationYear>();

		int provideLabel = DeliOrStor.DELIVERY.getID();
		/*
		 * int saveLabel = DeliOrStor.STORAGE.getID(); int notProvideSaveLabel =
		 * DeliOrStor.NOT_DELI_OR_STOR.getID();
		 */

		int periodsAYear = CommonConsts.MONTHS_PER_YEAR;
		if (runoffData.getDates().getTbType().equals(TimeBucketType.DECAD))
			periodsAYear = CommonConsts.DECADS_PER_YEAR;
		int[][] divideResultEveryear = new int[(int) (divideResult.length / periodsAYear)][periodsAYear];
		divideResultEveryear = BasicMathMethods.array1DTo2D(divideResult, periodsAYear);

		int[] yearNum = new int[periodsAYear];
		int startPeriod = runoffData.getDates().getStartDateTime().getMonthValue();
		if (runoffData.getDates().getTbType().equals(TimeBucketType.DECAD))
			startPeriod = HydroDateUtil.getDecad(runoffData.getDates().getStartDateTime().toLocalDate());
		for (int i = 0; i < periodsAYear; i++) {
			yearNum[i] = (startPeriod + i) % periodsAYear==0?periodsAYear:(startPeriod + i) % periodsAYear;// 年度内的时段编号
		}

		while (notContinuousDelivery(divideResultEveryear) > 0) {
			divideResultEveryear = BasicMathMethods.reverse2DArray(divideResultEveryear, 1,
					notContinuousDelivery(divideResultEveryear));
			yearNum = BasicMathMethods.reverseArray(yearNum, notContinuousDelivery(divideResultEveryear));
		} // 若供水期不连续，调整所有年份供水期都在divideResultEveryear的每一维里

		int[] deliveryStartIndexs = new int[divideResultEveryear.length];
		for (int i = 0; i < deliveryStartIndexs.length; i++) {
			for (int j = 0; j < periodsAYear; j++) {
				if (divideResultEveryear[i][j] == provideLabel) {
					deliveryStartIndexs[i] = j;
					break;
				}
			}
		}
		int deliveryStartIndex = BasicMathMethods.minOf1DArray(deliveryStartIndexs);
		int deliveryStartPeriod = yearNum[deliveryStartIndex];// 找到所有年的供水期初最早的月份（旬号）

		int deliveryStartIndexInRunoffData = 0;
		for (int i = 0; i < yearNum.length; i++) {
			if (deliveryStartPeriod == HydroDateUtil.getPeriod(
					runoffData.getDates().getTbSeq().get(i).getStartDateTime().toLocalDate(),
					runoffData.getDates().getTbType())) {
				deliveryStartIndexInRunoffData = i;
			}
		}

		if (deliveryStartIndexInRunoffData == 0) {
		} else {
			divideResultEveryear = BasicMathMethods.reverse2DArray(divideResultEveryear, 1,
					deliveryStartIndexInRunoffData);
		}

		LocalDate regulationYearStart = runoffData.getDates().getTbSeq().get(deliveryStartIndexInRunoffData)
				.getStartDateTime().toLocalDate();// 最早的供水期开始时间，在第一年里未必是供水期

		int runofflength = runoffData.getDatas().getArray().length;
		if (deliveryStartIndexInRunoffData > 0)
			runofflength = runofflength - periodsAYear;// 以供水期开始，数据就相应地去掉了一年
		double[] runoffs = new double[runofflength];
		for (int i = 0; i < runofflength; i++) {
			runoffs[i] = runoffData.getDatas().getArray()[i + deliveryStartIndexInRunoffData];
		}

		double[][] runoffEveryYear = new double[runofflength / periodsAYear][periodsAYear];
		runoffEveryYear = BasicMathMethods.array1DTo2D(runoffs, periodsAYear);

		/* 接着就可以把所有数据写入regulationYears对象中了 */
		LocalDate regulationYearStartANewYear = regulationYearStart.plusDays(0);// 赋值本天
		for (int i = 0; i < divideResultEveryear.length; i++) {
			/* 对每一年 */
			regulationYears.add(RegulationYear.generateRegulationYear(divideResultEveryear[i], runoffEveryYear[i],
					regulationYearStartANewYear));
			regulationYearStartANewYear = regulationYearStartANewYear.plusYears(1);
		}
		return regulationYears;
	}

	/**
	 * 每年的供蓄期都直接按照divideResult里的来， runoffdata和divideResult的开始时间应该保持一致，
	 * 生成从蓄水期初开始的水文年数据，用以蓄水期的调度图绘制运用
	 */
	public List<RegulationYear> generateRegulationYearsBeginWithStorage(TimeSeqCurve runoffData, HStationSpec hsSpec,
			int[] divideResult) {
		List<RegulationYear> regulationYears = new ArrayList<RegulationYear>();


		int saveLabel = DeliOrStor.STORAGE.getID();

		int periodsAYear = CommonConsts.MONTHS_PER_YEAR;
		if (runoffData.getDates().getTbType().equals(TimeBucketType.DECAD))
			periodsAYear = CommonConsts.DECADS_PER_YEAR;
		int[][] divideResultEveryear = new int[(int) (divideResult.length / periodsAYear)][periodsAYear];
		divideResultEveryear = BasicMathMethods.array1DTo2D(divideResult, periodsAYear);

		int[] yearNum = new int[periodsAYear];
		int startPeriod = runoffData.getDates().getStartDateTime().getMonthValue();
		if (runoffData.getDates().getTbType().equals(TimeBucketType.DECAD))
			startPeriod = HydroDateUtil.getDecad(runoffData.getDates().getStartDateTime().toLocalDate());
		for (int i = 0; i < periodsAYear; i++) {
			yearNum[i] = (startPeriod + i) % periodsAYear ==0?periodsAYear:(startPeriod + i) % periodsAYear;// 年度内的时段编号
				
		}

		while (notContinuousStorage(divideResultEveryear) > 0) {
			int tempNotContinuous=notContinuousStorage(divideResultEveryear);
			divideResultEveryear = BasicMathMethods.reverse2DArray(divideResultEveryear, 1,tempNotContinuous);
			yearNum = BasicMathMethods.reverseArray(yearNum, tempNotContinuous);
		} // 若蓄水期不连续，调整所有年份蓄水期都在divideResultEveryear的每一维里

		int[] storageStartIndexs = new int[divideResultEveryear.length];
		for (int i = 0; i < storageStartIndexs.length; i++) {
			for (int j = 0; j < periodsAYear; j++) {
				if (divideResultEveryear[i][j] == saveLabel) {
					storageStartIndexs[i] = j;
					break;
				}
			}
		}
		int storageStartIndex = BasicMathMethods.minOf1DArray(storageStartIndexs);
		int storageStartPeriod = yearNum[storageStartIndex];// 找到所有年的蓄水期初最早的月份（旬号）

		int storageStartIndexInRunoffData = 0;
		for (int i = 0; i < yearNum.length; i++) {
			if (storageStartPeriod == HydroDateUtil.getPeriod(
					runoffData.getDates().getTbSeq().get(i).getStartDateTime().toLocalDate(),
					runoffData.getDates().getTbType())) {
				storageStartIndexInRunoffData = i;
			}
		}

		/*if (storageStartIndexInRunoffData == 0) {
		} else {
			divideResultEveryear = BasicMathMethods.reverse2DArray(divideResultEveryear, 1,
					storageStartIndexInRunoffData);
		}*/

		LocalDate regulationYearStart = runoffData.getDates().getTbSeq().get(storageStartIndexInRunoffData)
				.getStartDateTime().toLocalDate();// 最早的蓄水期开始时间，在第一年里未必是蓄水期

		int runofflength = runoffData.getDatas().getArray().length;
		if (storageStartIndexInRunoffData > 0)
			runofflength = runofflength - periodsAYear;// 如果蓄水期开始与给定的径流数据的开始时间不一致，数据就相应地去掉了一年
		double[] runoffs = new double[runofflength];
		for (int i = 0; i < runofflength; i++) {
			runoffs[i] = runoffData.getDatas().getArray()[i + storageStartIndexInRunoffData];
		}

		double[][] runoffEveryYear = new double[runofflength / periodsAYear][periodsAYear];
		runoffEveryYear = BasicMathMethods.array1DTo2D(runoffs, periodsAYear);

		/* 接着就可以把所有数据写入regulationYears对象中了 */
		LocalDate regulationYearStartANewYear = regulationYearStart.plusDays(0);// 赋值本天
		for (int i = 0; i < divideResultEveryear.length; i++) {
			/* 对每一年 */
			regulationYears.add(RegulationYear.generateRegulationYear(divideResultEveryear[i], runoffEveryYear[i],
					regulationYearStartANewYear));
			regulationYearStartANewYear = regulationYearStartANewYear.plusYears(1);
		}
		return regulationYears;
	}

	/**
	 * 每年的供蓄期都直接按照divideResult里的来， runoffdata和divideResult的开始时间应该保持一致
	 * 直接以给定的起始时间来构造调节年度，不管供水期或者蓄水期在一个regulationyear里是不是连续的
	 */
	public List<RegulationYear> generateRegulationYearsBeginWithSetvalue(TimeSeqCurve runoffData, HStationSpec hsSpec,
			int[] divideResult) {
		List<RegulationYear> regulationYears = new ArrayList<RegulationYear>();

		int periodsAYear = CommonConsts.MONTHS_PER_YEAR;
		if (runoffData.getDates().getTbType().equals(TimeBucketType.DECAD))
			periodsAYear = CommonConsts.DECADS_PER_YEAR;
		int[][] divideResultEveryear = new int[(int) (divideResult.length / periodsAYear)][periodsAYear];
		divideResultEveryear = BasicMathMethods.array1DTo2D(divideResult, periodsAYear);

		int[] yearNum = new int[periodsAYear];
		int startPeriod = runoffData.getDates().getStartDateTime().getMonthValue();
		if (runoffData.getDates().getTbType().equals(TimeBucketType.DECAD))
			startPeriod = HydroDateUtil.getDecad(runoffData.getDates().getStartDateTime().toLocalDate());
		for (int i = 0; i < periodsAYear; i++) {
			yearNum[i] = (startPeriod + i) % periodsAYear ==0?periodsAYear:(startPeriod + i) % periodsAYear;// 年度内的时段编号
		}

		LocalDate regulationYearStart = runoffData.getDates().getTbSeq().get(0).getStartDateTime().toLocalDate();// 最早的蓄水期开始时间，在第一年里未必是蓄水期

		int runofflength = runoffData.getDatas().getArray().length;
		double[] runoffs = new double[runofflength];
		for (int i = 0; i < runofflength; i++) {
			runoffs[i] = runoffData.getDatas().getArray()[i];
		}

		double[][] runoffEveryYear = new double[runofflength / periodsAYear][periodsAYear];
		runoffEveryYear = BasicMathMethods.array1DTo2D(runoffs, periodsAYear);

		/* 接着就可以把所有数据写入regulationYears对象中了 */
		LocalDate regulationYearStartANewYear = regulationYearStart.plusDays(0);// 赋值本天
		for (int i = 0; i < divideResultEveryear.length; i++) {
			/* 对每一年 */
			regulationYears.add(RegulationYear.generateRegulationYear(divideResultEveryear[i], runoffEveryYear[i],
					regulationYearStartANewYear));
			regulationYearStartANewYear = regulationYearStartANewYear.plusYears(1);
		}
		return regulationYears;
	}
	
	private int notContinuousStorage(int[][] divideResultEveryear) {
		int saveLabel = DeliOrStor.STORAGE.getID();
		int tempi=-1;
		int[][] storageLabelIndexEveryYear = new int[divideResultEveryear.length][];
		int reserveindex = -1;
		for (int i = 0; i < divideResultEveryear.length; i++) {
			storageLabelIndexEveryYear[i] = BasicMathMethods.indexOfSameElements(divideResultEveryear[i],
					saveLabel);
			if (BasicMathMethods.indexOfJumpPointInArray(storageLabelIndexEveryYear[i]) >= 0)// 蓄水期不连续
			{
				reserveindex = BasicMathMethods.indexOfJumpPointInArray(storageLabelIndexEveryYear[i]);
				tempi=i;
				break;
			}
		}
		return reserveindex>0?storageLabelIndexEveryYear[tempi][reserveindex]:-1;
	}

	/**
	 * @param divideResultEveryear
	 * @return 看供水期的分布是不是连续的，若是的返回-1 不是返回第一次发现不连续的那一年后半部分的供水期第一个编号
	 */
	private int notContinuousDelivery(int[][] divideResultEveryear) {
		int provideLabel = DeliOrStor.DELIVERY.getID();
		int[][] deliveryLabelIndexEveryYear = new int[divideResultEveryear.length][];
		int reserveindex = -1;
		for (int i = 0; i < divideResultEveryear.length; i++) {
			deliveryLabelIndexEveryYear[i] = BasicMathMethods.indexOfSameElements(divideResultEveryear[i],
					provideLabel);
			if (BasicMathMethods.indexOfJumpPointInArray(deliveryLabelIndexEveryYear[i]) >= 0)// 供水期不连续
			{
				reserveindex = BasicMathMethods.indexOfJumpPointInArray(divideResultEveryear[i]);
				break;
			}
		}
		return reserveindex;
	}

}
