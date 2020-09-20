package me.owenyy.drawmethods.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.factory.state.HsStateValueFactory;
import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;
import com.wenyu.hydroelements.operation.behavior.ControlModelReverseOrder;
import com.wenyu.hydroelements.operation.behavior.ControlModelSingleTime;

import me.owenyy.divideperiod.helper.BasicMathMethods;
import me.owenyy.divideperiod.helper.EmpiricalFrequency;
import me.owenyy.divideperiod.helper.PeriodPair;
import me.owenyy.divideperiod.helper.RegulationYear;
import me.owenyy.divideperiod.helper.RegulationYearPeriod;

/**
 * 绘制调度图过程中一些用到的方法
 *
 */
public class HelperMethodsForDG {

	/**
	 * @param regulationYears
	 *            各调节年度数据都完备
	 * @param ef
	 *            已经计算好频率并且已经排好序的,注意此时不要再变动ef了
	 * @param key
	 *            出力值或者频率值或者编号都可以，先按出力值选 >1的是出力 小于1的是频率
	 * @param biggerNum
	 *            要选择的大于key的数目
	 * @param smallerNum
	 *            要选择的小于key的数目
	 * @param α
	 *            各修正系数 选择相似年时一并把修正系数计算了
	 * @param regulationYears
	 * @return 选择相似年，并把修正系数求出 如果biggerNum==smallerNum==0 就是返回典型年 List长度是1
	 *         修正系数也都设为1 不进行修正
	 *         如果biggerNum、smallerNum有一个值大于0，就返回相近的对应数目的调节年，包括典型年本身，
	 *         典型年修正系数为1，其余的分别计算，计算完成后还要对对各调节年进行径流数据修正
	 *         这个关键是要选择年内径流分配不同的径流数据，要不然求出的保证出力区就太小了，选择年份要多一点，然后一一修正，就这样干！！！！！！！
	 *         这个是极关键的一个函数
	 */
	public static List<RegulationYear> chooseNeighbors(List<RegulationYear> regulationYears,
			List<EmpiricalFrequency> ef, double key, int biggerNum, int smallerNum, double[] α) {
		List<RegulationYear> ryneigh = new ArrayList<RegulationYear>();

		double[] abs = new double[ef.size()];
		if (key >= 1)// 大于1表明是出力
		{
			for (int i = 0; i < abs.length; i++) {
				abs[i] = Math.abs(ef.get(i).getValue() - key);
			}
		} else {
			for (int i = 0; i < abs.length; i++) {
				abs[i] = Math.abs(ef.get(i).getFrequency() - key);
			}
		}

		int minabsindex = BasicMathMethods.indexOf1DArrayMin(abs);
		int typicalnumnew = ef.get(minabsindex).getSerialNumNew();// 应与minabsindex相等
		int typicalyearnum = ef.get(minabsindex).getSerialNumOld();
		double typicalQ = ef.get(minabsindex).getValueelse();
		if ((biggerNum == smallerNum) && (smallerNum == 0)) {

			ryneigh.add(regulationYears.get(typicalyearnum));// 找到典型年
			// α=new double[1];
			α[0] = 1.0;// 不需要修正典型年
		} else {
			int[] similaryearnumsnew1 = new int[biggerNum];
			int[] similaryearnumsnew2 = new int[smallerNum];
			int[] similaryearnums1 = new int[biggerNum];
			int[] similaryearnums2 = new int[smallerNum];
			for (int i = biggerNum; i > 0; i--) {
				similaryearnumsnew1[biggerNum - i] = typicalnumnew - i;
				similaryearnums1[biggerNum - i] = ef.get(similaryearnumsnew1[biggerNum - i]).getSerialNumOld();
			}
			System.out.print("");
			for (int i = 0; i < smallerNum; i++) {
				similaryearnumsnew2[i] = typicalnumnew + i + 1;
				similaryearnums2[i] = ef.get(similaryearnumsnew2[i]).getSerialNumOld();
			}
			double[] Qtiaojie = new double[smallerNum + biggerNum + 1];
			int[] similarnums = new int[smallerNum + biggerNum + 1];
			int[] similarnumsnew = new int[smallerNum + biggerNum + 1];
			int[] typicalyearnumarr = { typicalyearnum };

			int[] typicalyearnumnewarr = { typicalnumnew };
			similarnumsnew = BasicMathMethods.mergeArray(similaryearnumsnew1, typicalyearnumnewarr);
			similarnumsnew = BasicMathMethods.mergeArray(similarnumsnew, similaryearnumsnew2);
			similarnums = BasicMathMethods.mergeArray(similaryearnums1, typicalyearnumarr);
			similarnums = BasicMathMethods.mergeArray(similarnums, similaryearnums2);// 所有相似年
			for (int i = 0; i < similarnums.length; i++) {
				ryneigh.add(regulationYears.get(similarnums[i]));
				Qtiaojie[i] = ef.get(similarnumsnew[i]).getValueelse();
			}
			// α=new double[Qtiaojie.length];
			for (int i = 0; i < α.length; i++) {
				α[i] = typicalQ / Qtiaojie[i];
			}
		}
		return ryneigh;

	}

	/**
	 * 根据排频后的结果，在代表年附近选择指定数目的典型年，然后将根据代表年对典型年得到的修正系数计算出来
	 * @param regulationYears
	 *            各调节年度数据都完备
	 * @param ef
	 *            已经计算好频率并且已经排好序的,注意此时不要再变动ef了
	 * @param key
	 *            出力值或者频率值或者编号都可以，先按出力值选 >1的是出力 小于1的是频率，代表年的典型年的key
	 * @param number
	 *            选择的典型年的个数
	 * @param realKey
	 *            典型年的key
	 * @param α
	 *            各修正系数 选择相似年时一并把修正系数计算了
	 * @param regulationYears
	 * @return 就返回相近的对应数目的调节年，包括典型年本身， 典型年修正系数为1，其余的分别计算，计算完成后还要对对各调节年进行径流数据修正。
	 *         选择的时候要进行判断附近是往上找还是往下找
	 * 
	 */
	public static List<RegulationYear> chooseTypicals(List<RegulationYear> regulationYears, List<EmpiricalFrequency> ef,
			double key, int number, double[] α) {
		List<RegulationYear> ryneigh = new ArrayList<RegulationYear>();

		double[] abs = new double[ef.size()];
		if (key >= 1)// 大于1表明是出力
		{
			for (int i = 0; i < abs.length; i++) {
				abs[i] = Math.abs(ef.get(i).getValue() - key);
			}
		} else {
			for (int i = 0; i < abs.length; i++) {
				abs[i] = Math.abs(ef.get(i).getFrequency() - key);
			}
		}

		int minabsindex = BasicMathMethods.indexOf1DArrayMin(abs);
		int typicalnumnew = ef.get(minabsindex).getSerialNumNew();// 应与minabsindex相等
		int typicalyearnum = ef.get(minabsindex).getSerialNumOld();
		double typicalQ = ef.get(minabsindex).getValueelse();
		if (number == 0) {
			ryneigh.add(regulationYears.get(typicalyearnum));// 找到代表年
			// α=new double[1];
			α[0] = 1.0;// 不需要修正典型年
		} else {
			int biggerNum = number / 2;
			int smallerNum = number / 2;
			if (number % 2 == 1) {
				biggerNum = number / 2 + 1;
				smallerNum = number / 2;
			}
			boolean yes = true;
			while (yes) {
				if (typicalnumnew + smallerNum >= ef.size()) {
					biggerNum = biggerNum + 1;
					smallerNum = smallerNum - 1;
				} else if (typicalnumnew - biggerNum < 0) {
					biggerNum = biggerNum - 1;
					smallerNum = smallerNum + 1;
				}
				if (typicalnumnew + smallerNum < ef.size() && typicalnumnew - biggerNum >= 0) {
					yes = false;
				}
			}
			int[] similaryearnumsnew1 = new int[biggerNum];
			int[] similaryearnumsnew2 = new int[smallerNum];
			int[] similaryearnums1 = new int[biggerNum];
			int[] similaryearnums2 = new int[smallerNum];
			for (int i = biggerNum; i > 0; i--) {
				similaryearnumsnew1[biggerNum - i] = typicalnumnew - i;
				similaryearnums1[biggerNum - i] = ef.get(similaryearnumsnew1[biggerNum - i]).getSerialNumOld();
			}
			for (int i = 0; i < smallerNum; i++) {
				similaryearnumsnew2[i] = typicalnumnew + i + 1;
				similaryearnums2[i] = ef.get(similaryearnumsnew2[i]).getSerialNumOld();
			}
			double[] Qtiaojie = new double[smallerNum + biggerNum + 1];
			int[] similarnums = new int[smallerNum + biggerNum + 1];
			int[] similarnumsnew = new int[smallerNum + biggerNum + 1];
			int[] typicalyearnumarr = { typicalyearnum };

			int[] typicalyearnumnewarr = { typicalnumnew };
			similarnumsnew = BasicMathMethods.mergeArray(similaryearnumsnew1, typicalyearnumnewarr);
			similarnumsnew = BasicMathMethods.mergeArray(similarnumsnew, similaryearnumsnew2);
			similarnums = BasicMathMethods.mergeArray(similaryearnums1, typicalyearnumarr);
			similarnums = BasicMathMethods.mergeArray(similarnums, similaryearnums2);// 所有相似年
			for (int i = 0; i < similarnums.length; i++) {
				if (similarnums[i] > regulationYears.size() - 1) {
					similarnums[i] = regulationYears.size() - 1;// 一个简单处理
				}
				ryneigh.add(regulationYears.get(similarnums[i]));
				Qtiaojie[i] = ef.get(similarnumsnew[i]).getValueelse();
			}
			// α=new double[Qtiaojie.length];
			for (int i = 0; i < α.length; i++) {
				α[i] = typicalQ / Qtiaojie[i];
			}
		}
		return ryneigh;

	}

	/**
	 * @param levelLines
	 *            每个相似年的供水期水位过程，水位的个数有可能不一样的（要考虑进去，但是本方法是供蓄水期通用的方法，
	 *            不考虑时段长度不一样的情况的方法）
	 * @return 包络线
	 */
	public static double[][] envelopeLine(List<double[]> levelLines) {

		int[] linelengths = new int[levelLines.size()];
		for (int i = 0; i < levelLines.size(); i++) {
			linelengths[i] = levelLines.get(i).length;
		}
		int maxLength = BasicMathMethods.maxOf1DArray(linelengths);

		double[][] lines = new double[2][maxLength];
		double[][] alllines = new double[levelLines.size()][];
		for (int i = 0; i < alllines.length; i++) {
			alllines[i] = levelLines.get(i);
		}
		double[][] alllinestran = BasicMathMethods.transpose(alllines);// 针对每个水位过程水位个数相同的情况进行求包络线
		for (int i = 0; i < alllinestran.length; i++) {
			lines[0][i] = BasicMathMethods.minOf1DArray(alllinestran[i]);
			lines[1][i] = BasicMathMethods.maxOf1DArray(alllinestran[i]);
		}
		return lines;

	}

	/**
	 * @param levelLines
	 *            每个相似年的供水期水位过程，水位的个数有可能不一样的（要考虑进去）
	 * @param neighborRegulationYears
	 *            相似年的供水期/蓄水期/不蓄不供期的各时段各状态值
	 * @return 包络线
	 */
	public static double[][] envelopeLine(List<double[]> levelLines,
			List<RegulationYearPeriod> neighborRegulationYears) {
		PeriodPair allStart = neighborRegulationYears.get(0).getaRegulationYearPeriod().get(0);
		PeriodPair allEnd = neighborRegulationYears.get(0).getaRegulationYearPeriod()
				.get(neighborRegulationYears.get(0).getaRegulationYearPeriod().size() - 1);
		for (int i = 1; i < neighborRegulationYears.size(); i++) {
			PeriodPair tempStart = neighborRegulationYears.get(i).getaRegulationYearPeriod().get(0);
			PeriodPair tempEnd = neighborRegulationYears.get(i).getaRegulationYearPeriod()
					.get(neighborRegulationYears.get(i).getaRegulationYearPeriod().size() - 1);
			if (tempStart.getlowerDigit() < allStart.getlowerDigit())// 这种比较还有点小问题
																		// 但是目前是对的
																		// 有的调节年供/蓄水期跨年
																		// 有的不跨年的时候就会出错
				allStart = tempStart.plus(0);
			if (tempEnd.getlowerDigit() > allEnd.getlowerDigit())
				allEnd = tempEnd.plus(0);
		}

		int maxLength = allStart.getlowerDigit() > allEnd.getlowerDigit()
				? allEnd.getlowerDigit() + allEnd.getNumLowerDigit() - allStart.getlowerDigit() + 1
				: allEnd.getlowerDigit() - allStart.getlowerDigit() + 1;
		double[][] lines = new double[2][maxLength + 1];
		int startIndex = allStart.getlowerDigit();
		int endIndex = allEnd.getlowerDigit() + 1;
		// 把没有索引的位置都用-1填满，方便后面的运算
		int[] allStartOffsetIndex = new int[neighborRegulationYears.size()];// 各年调节期的起始时段相对于最早的那个时段的偏移
		int[] allEndOffsetIndex = new int[neighborRegulationYears.size()];// 各年调节期的起始时段相对于最早的那个时段的偏移
		for (int i = 0; i < neighborRegulationYears.size(); i++) {
			int tempStartDigit = neighborRegulationYears.get(i).getaRegulationYearPeriod().get(0).getlowerDigit();
			int tempEndDigit = neighborRegulationYears.get(i).getaRegulationYearPeriod()
					.get(neighborRegulationYears.get(i).getaRegulationYearPeriod().size() - 1).getlowerDigit() + 1;
			allStartOffsetIndex[i] = tempStartDigit < startIndex ? tempStartDigit
					+ neighborRegulationYears.get(i).getaRegulationYearPeriod().get(0).getNumLowerDigit() - startIndex
					: tempStartDigit - startIndex;
			allEndOffsetIndex[i] = tempEndDigit > endIndex ? endIndex
					+ neighborRegulationYears.get(i).getaRegulationYearPeriod().get(0).getNumLowerDigit() - tempEndDigit
					: endIndex - tempEndDigit;
		}

		double[][] allLines = new double[neighborRegulationYears.size()][maxLength + 1];
		for (int i = 0; i < allLines.length; i++) {
			double[] temp = null;
			if (allStartOffsetIndex[i] > 0) {
				double[] tempStartArray = new double[allStartOffsetIndex[i]];
				for (int j = 0; j < tempStartArray.length; j++) {
					tempStartArray[j] = 0;
				}
				temp = BasicMathMethods.mergeArray(tempStartArray, levelLines.get(i));
			} else {
				temp = levelLines.get(i).clone();
			}

			if (allEndOffsetIndex[i] > 0) {
				double[] tempEndArray = new double[allEndOffsetIndex[i]];
				for (int j = 0; j < tempEndArray.length; j++) {
					tempEndArray[j] = 0;
				}
				temp = BasicMathMethods.mergeArray(temp, tempEndArray);
			}
			allLines[i] = temp.clone();
		} // 构造完整的二维数组，方便后续运算

		double[][] allLinesTran = BasicMathMethods.transpose(allLines);
		for (int i = 0; i < allLinesTran.length; i++) {
			List<Double> tempForMin = new ArrayList<Double>();
			for (int j = 0; j < allLinesTran[i].length; j++) {
				if (allLinesTran[i][j] > 0)
					tempForMin.add(allLinesTran[i][j]);
			}
			lines[0][i] = BasicMathMethods.minOf1DArray(BasicMathMethods.listToDouble(tempForMin));
			lines[1][i] = BasicMathMethods.maxOf1DArray(allLinesTran[i]);
		}

		return lines;

	}

	/**
	 * @return 两段调度线（中间有一个重合的时段的才是相邻的）直接拼接在一起，两段的顺序是deliveryPeriodOutputLine在前;
	 *         若首尾数据不相同，则将蓄水期末的水位修正为供水期初的水位即可
	 */
	public static double[] mergeLine(double[] line1, double[] line2) {
		int storagelength = line2.length;
		int deliverylength = line1.length;
		int Dispatchlenth = deliverylength + storagelength - 1;

		double[] result = new double[Dispatchlenth];

		double dStartZ = line1[0];// 供水期初的水位
		double sEndZ = line2[line2.length - 1];// 蓄水期末的水位

		if (Math.abs(sEndZ - dStartZ) > 0.000000001) {
			line2[line2.length - 1] = dStartZ;// 若首尾数据不相同，则将蓄水期末的水位修正为供水期初的水位即可
		}

		for (int i = 0; i < deliverylength; i++) {
			result[i] = line1[i];
		}
		for (int j = 1; j < storagelength; j++) {
			result[j + deliverylength - 1] = line2[j];
		}
		return result;
	}

	/**
	 * @param deliveryPeriodWarrentedOutputLine
	 * @param storagePeriodWarrentedOutputLine
	 * @return 两段相邻的调度线（中间有一个重合的时段的才是相邻的）直接拼接在一起，
	 *         两段的顺序是deliveryPeriodWarrentedOutputLine在前
	 */
	public static double[][] mergeLines(double[][] line1, double[][] line2) {
		int storagelength = line2[0].length;
		int deliverylength = line1[0].length;
		int Dispatchlenth = deliverylength + storagelength - 1;

		double[][] result = new double[2][Dispatchlenth];

		double[] dStartZ = { line1[0][0], line1[1][0] };// 供水期初的两个水位
		// double dEndZ=deadWaterLevel;//供水期末的一个水位
		double[] sStartZ = { line2[0][0], line2[1][0] };// 蓄水期初的两个水位
		// double sEndZ=normalLevel;//蓄水期末的一个水位

		result[0][0] = dStartZ[0];
		result[1][0] = dStartZ[1];
		for (int i = 0; i < deliverylength - 1; i++)// 从供水期第一个时刻到供水期倒数第二个时刻
		{
			result[0][i] = line1[0][i];
			result[1][i] = line1[1][i];
		}

		result[0][deliverylength - 1] = sStartZ[0];
		result[1][deliverylength - 1] = sStartZ[1];

		for (int i = 1; i < storagelength; i++) {
			result[0][i + deliverylength - 1] = line2[0][i];
			result[1][i + deliverylength - 1] = line2[1][i];
		}

		return result;
	}

	/**
	 * @param floodControlEnd 
	 * @param floodControlBegin 
	 * @param hsSpec
	 * @param curve
	 * @param Q_in
	 * @param timelength
	 * @param N
	 * @param constrainttypes
	 * @param startYear
	 * @param startPeriod
	 *            时段开始的月份或旬份
	 * 
	 * @return 逆时程开始计算，含有汛限水位约束
	 */
	public static double[] outputRegulationCal(LocalDate floodControlBegin, LocalDate floodControlEnd, int deliOrStor, HStationSpec hsSpec, StationCurve curve, double[] Q_in,
			int[] timelength, double N, int[] constrainttypes, String tbType, int startYear, int startPeriod) {

		int periodReal = startPeriod;
		String datetime=null;
		if (tbType.equals("MONTH")) {
			if (periodReal > 12)
				periodReal = periodReal - 12;
			datetime = startYear + "-" + periodReal + "-01T00:00:00";
			if (periodReal < 10)
				datetime = startYear + "-0" + periodReal + "-01T00:00:00";
		} else {// 旬的时候
			if (periodReal > 36)
				periodReal = periodReal - 36;
			int monthtemp = (periodReal - 1) / 3 + 1;
			if (periodReal % 3 == 0) {
				datetime = startYear + "-" + monthtemp + "-21T00:00:00";
				if (monthtemp < 10)
					datetime = startYear + "-0" + monthtemp + "-21T00:00:00";
			} else if (periodReal % 3 == 1) {
				datetime = startYear + "-" + monthtemp + "-01T00:00:00";
				if (monthtemp < 10)
					datetime = startYear + "-0" + monthtemp + "-01T00:00:00";
			} else {
				datetime = startYear + "-" + monthtemp + "-11T00:00:00";
				if (monthtemp < 10)
					datetime = startYear + "-0" + monthtemp + "-11T00:00:00";
			}
		}
		
		HsStateValueFactory hsof = new HsStateValueFactory(hsSpec, tbType, constrainttypes, datetime, Q_in.length);
		List<HStationState> hss = hsof.createHsStates(Q_in,floodControlBegin,floodControlEnd);
		ControlModelReverseOrder cmst = new ControlModelReverseOrder();
		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(curve);
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(hsSpec);
		cmst.setFomulaMode(mffd0);
		hss.get(hss.size() - 1).setLevelEnd(deliOrStor > 0 ? hsSpec.getLevelDead() : hsSpec.getLevelNormal());
		double[] Z = new double[Q_in.length + 1];
		for (int i = Q_in.length - 1; i >= 0; i--)// 从正常蓄水位开始逆时程蓄水期各个时段，进行调节计算
		{
			hss.get(i).setOutput(N);
			cmst.powerControl(hss.get(i), hsSpec, curve);
			if(i>0 && (hss.get(i).getLevelBegin()>hss.get(i-1).getLevelMax())){
				hss.get(i).setLevelBegin(hss.get(i-1).getLevelMax());
				cmst.levelControl(hss.get(i), hsSpec, curve);
			}
			if (i > 0) {
				hss.get(i - 1).setLevelEnd(hss.get(i).getLevelBegin());
			}
		}
		for (int i = 0; i < Z.length; i++) {
			if (i == Z.length - 1)
				Z[i] = hss.get(i - 1).getLevelEnd();
			else
				Z[i] = hss.get(i).getLevelBegin();
		}
		return Z;

	}
	/**
	 * @param hsSpec
	 * @param curve
	 * @param Q_in
	 * @param timelength
	 * @param N
	 * @param constrainttypes
	 * @param startYear
	 * @param startPeriod
	 *            时段开始的月份或旬份
	 * 
	 * @return 逆时程开始计算
	 */
	public static double[] outputRegulationCal(int deliOrStor, HStationSpec hsSpec, StationCurve curve, double[] Q_in,
			int[] timelength, double N, int[] constrainttypes, String tbType, int startYear, int startPeriod) {

		int periodReal = startPeriod;
		String datetime=null;
		if (tbType.equals("MONTH")) {
			if (periodReal > 12)
				periodReal = periodReal - 12;
			datetime = startYear + "-" + periodReal + "-01T00:00:00";
			if (periodReal < 10)
				datetime = startYear + "-0" + periodReal + "-01T00:00:00";
		} else {// 旬的时候
			if (periodReal > 36)
				periodReal = periodReal - 36;
			int monthtemp = (periodReal - 1) / 3 + 1;
			if (periodReal % 3 == 0) {
				datetime = startYear + "-" + monthtemp + "-21T00:00:00";
				if (monthtemp < 10)
					datetime = startYear + "-0" + monthtemp + "-21T00:00:00";
			} else if (periodReal % 3 == 1) {
				datetime = startYear + "-" + monthtemp + "-01T00:00:00";
				if (monthtemp < 10)
					datetime = startYear + "-0" + monthtemp + "-01T00:00:00";
			} else {
				datetime = startYear + "-" + monthtemp + "-11T00:00:00";
				if (monthtemp < 10)
					datetime = startYear + "-0" + monthtemp + "-11T00:00:00";
			}
		}
		
		HsStateValueFactory hsof = new HsStateValueFactory(hsSpec, tbType, constrainttypes, datetime, Q_in.length);
		List<HStationState> hss = hsof.createHsStates(Q_in);
		ControlModelReverseOrder cmst = new ControlModelReverseOrder();
		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(curve);
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(hsSpec);
		cmst.setFomulaMode(mffd0);
		hss.get(hss.size() - 1).setLevelEnd(deliOrStor > 0 ? hsSpec.getLevelDead() : hsSpec.getLevelNormal());
		double[] Z = new double[Q_in.length + 1];
		for (int i = Q_in.length - 1; i >= 0; i--)// 从正常蓄水位开始顺时程供水期各个时段，进行调节计算
		{
			hss.get(i).setOutput(N);
			cmst.powerControl(hss.get(i), hsSpec, curve);
			if (i > 0) {
				hss.get(i - 1).setLevelEnd(hss.get(i).getLevelBegin());
			}
		}
		for (int i = 0; i < Z.length; i++) {
			if (i == Z.length - 1)
				Z[i] = hss.get(i - 1).getLevelEnd();
			else
				Z[i] = hss.get(i).getLevelBegin();
		}
		return Z;

	}
	/**
	 * 正序等出力绘制一条线
	 * 
	 * @param tbType
	 *            时段类型
	 * @param inflows
	 *            入流
	 * @param timeLengths
	 *            各时段长度
	 * @param N
	 *            等出力值
	 * @return 水位过程线
	 */
	public static double[] psConstantOutput(HStationSpec hsSpec, StationCurve curve, int[] constraintTypes, String tbType,
			double levelBegin, LocalDateTime startTime, double[] inflows, double N) {
		HsStateValueFactory hsof = new HsStateValueFactory(hsSpec, tbType, constraintTypes, startTime.toString(),
				inflows.length);
		List<HStationState> hss = hsof.createHsStates(inflows);
		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(curve);
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(hsSpec);
		ControlModelSingleTime cmst = new ControlModelSingleTime(mffd0);
		hss.get(0).setLevelBegin(levelBegin);
		double[] Z = new double[inflows.length + 1];
		for (int i = 0; i < inflows.length; i++)// 从初水位开始顺时程各个时段进行调节计算
		{
			hss.get(i).setOutput(N);
			cmst.powerControl(hss.get(i), hsSpec, curve);
			if (i < inflows.length - 1) {
				hss.get(i + 1).setLevelBegin(hss.get(i).getLevelEnd());
			}
		}
		for (int i = 0; i < Z.length; i++) {
			if (i == Z.length - 1)
				Z[i] = hss.get(i - 1).getLevelEnd();
			else
				Z[i] = hss.get(i).getLevelBegin();
		}
		return Z;
	}
}
