package me.owenyy.divideperiod.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.factory.state.HsStateValueFactory;
import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;
import com.wenyu.hydroelements.hydrostation.constraint.BuildConstraint;
import com.wenyu.hydroelements.operation.basic.MultiFomulaMode;
import com.wenyu.hydroelements.operation.behavior.ControlModelSingleTime;


public class BasicMethod {
	/**
	 * 
	 * @param inflowtemp
	 * @param tbType
	 * @return 初始化供蓄水期，首先水电站水库供、蓄期的划分按水能计算中采用的等流量试算方法进行。具体实现是：将水文年度序列资料按年排序，
	 *         并求得各月的平均流量Q月，
	 *         以及多年平均流量Q年，根据Q月和Q年进行判断，如果Q月>Q年，则可认为对应该月为蓄水月份，反之如果Q月<Q年，
	 *         则可认为对应该月为供水月份。
	 */
	public static int[] initialPeriod(double[] inflowtemp, String tbType) {
		int yearPeriods = 12;
		if (tbType == "DECAD") {
			yearPeriods = 36;
		}
		double[][] inflows = BasicMathMethods.array1DTo2D(inflowtemp, yearPeriods);
		/*
		 * for(int i=0;i<inflows.length;i++){ for(int
		 * j=0;j<inflows[0].length;j++){ System.out.print(inflows[i][j]+","); }
		 * System.out.println(""); }
		 */
		double[][] temps = BasicMathMethods.transpose(inflows);
		double[] avgs = new double[temps.length];
		for (int i = 0; i < avgs.length; i++)
			avgs[i] = BasicMathMethods.avgArray(temps[i]);

		double allavg = BasicMathMethods.avgArray(avgs);
		int[] provideStateSeries = new int[yearPeriods];// 1表示供水，-1表示蓄水，0表示不蓄不供期
		for (int i = 0; i < yearPeriods; i++) {
			if (avgs[i] > allavg)
				provideStateSeries[i] = DeliOrStor.STORAGE.getID();
			else if (avgs[i] < allavg)
				provideStateSeries[i] = DeliOrStor.DELIVERY.getID();
			else
				provideStateSeries[i] = DeliOrStor.NOT_DELI_OR_STOR.getID();
		}
		return provideStateSeries;
	}

	// 判断供水期，返回不满足判断条件时段id
	public static ArrayList<Integer> judgeProvidePeriod(int startIdTemp, int endIdTemp, double[] inflowtemp,
			double V_benifit, int yearPeriods, int startYear, int startPeriod) {
		// V_benifit单位为亿m³
		double Q_adjust = 0;// 调节流量m³/s
		int totaldays = 0;
		double waterall = 0;
		for (int i = startIdTemp; i <= endIdTemp; i++) {
			int yearId = startYear + (int) (i / yearPeriods);// 年份
			int periodId = (i + startPeriod) % yearPeriods;// 时段，从1开始，方便查询天数
			int days = getMonthDayNum(yearId, periodId);
			waterall = waterall + inflowtemp[i] * days * 24 * 3600;// 单位m³
			totaldays = totaldays + days;
		}
		Q_adjust = (waterall + V_benifit * Math.pow(10, 8)) / (totaldays * 24 * 3600);
		ArrayList<Integer> nottruePeriodIdList = new ArrayList<Integer>();// 用于存储不满足判断条件时段id
		for (int i = startIdTemp; i <= endIdTemp; i++) {
			if (Q_adjust <= inflowtemp[i]) {
				nottruePeriodIdList.add(i);
			}
		}
		return nottruePeriodIdList;
	}

	// 判断蓄水期，返回不满足判断条件时段的id，即index
	public static ArrayList<Integer> judgeSavePeriod(int startIdTemp, int endIdTemp, double[] inflowtemp,
			double V_benifit, int yearPeriods, int startYear, int startPeriod) {
		// V_benifit单位为亿m³
		double Q_adjust = 0;// 调节流量m³/s
		int totaldays = 0;
		double waterall = 0;
		for (int i = startIdTemp; i <= endIdTemp; i++) {
			int yearId = startYear + (int) (i / yearPeriods);// 年份
			int periodId = (i + startPeriod) % yearPeriods;// 时段，从1开始，方便查询天数
			int days = getMonthDayNum(yearId, periodId);
			waterall = waterall + inflowtemp[i] * days * 24 * 3600;// 总计的来水量，单位m³
			totaldays = totaldays + days;
		}
		Q_adjust = (waterall - V_benifit * Math.pow(10, 8)) / (totaldays * 24 * 3600);
		ArrayList<Integer> nottruePeriodIdList = new ArrayList<Integer>();// 用于存储不满足判断条件时段id
		for (int i = startIdTemp; i <= endIdTemp; i++) {
			if (Q_adjust >= inflowtemp[i]) {
				nottruePeriodIdList.add(i);
			}
		}
		return nottruePeriodIdList;
	}

	// 计算不同年份旬的天数
	// int yearNum实际年份, int xunNum从1开始
	public static int getXunDayNum(int yearNum, int xunNum) {
		int xunDayNum = 10;
		if (xunNum % 3 == 1 || xunNum % 3 == 2) {
			xunDayNum = 10;
		} else if (xunNum == 3 || xunNum == 9 || xunNum == 15 || xunNum == 21 || xunNum == 24 || xunNum == 30
				|| xunNum == 36) {
			xunDayNum = 11;
		} else if (xunNum == 12 || xunNum == 18 || xunNum == 27 || xunNum == 33) {
			xunDayNum = 10;
		} else {
			if (yearNum % 4 == 0 && yearNum % 100 != 0 || yearNum % 400 == 0) {
				xunDayNum = 9;
			} else {
				xunDayNum = 8;
			}
		}
		return xunDayNum;
	}

	// 计算不同年份月的天数
	// int yearNum实际年份, int monthNum从1开始
	public static int getMonthDayNum(int yearNum, int monthNum) {
		int monthDayNum = 30;
		if (monthNum == 1 || monthNum == 3 || monthNum == 5 || monthNum == 7 || monthNum == 8 || monthNum == 10
				|| monthNum == 12) {
			monthDayNum = 31;
		} else if (monthNum == 4 || monthNum == 6 || monthNum == 9 || monthNum == 11) {
			monthDayNum = 30;
		} else {
			if (yearNum % 4 == 0 && yearNum % 100 != 0 || yearNum % 400 == 0) {
				monthDayNum = 29;
			} else {
				monthDayNum = 28;
			}
		}
		return monthDayNum;
	}

	public static void judgePeriodOfAYear(double beneficialStorage, String pt, int[] period, int[] length,
			double[] runoff) {

	}

	/**
	 * 已知分期的情况，如果一个调度年的供蓄水期分配正确，则不动作，返回false 否则需要对供蓄水期进行调整 返回true
	 * 
	 * @param Qs
	 *            根据现有分期情况计算的蓄水期平均时段调节流量
	 */
	public static boolean needAdjustStoragePeriod(double Qs, double[] storageRunoff) {
		boolean needAdjust = false;
		for (int i = 0; i < storageRunoff.length; i++) {
			if (Qs > storageRunoff[i]) {
				needAdjust = true;
				break;
			}
		}
		return needAdjust;
	}

	/**
	 * 已知分期的情况，如果一个调度年的供蓄水期分配正确，则不动作，返回false 否则需要对供蓄水期进行调整 返回true
	 * 
	 * @param Qd
	 *            根据现有分期情况计算的供水期平均时段调节流量
	 */
	public static boolean needAdjustDeliveryPeriod(double Qd, double[] deliveryRunoff) {
		boolean needAdjust = false;
		for (int i = 0; i < deliveryRunoff.length; i++) {
			if (Qd < deliveryRunoff[i]) {
				needAdjust = true;
				break;
			}
		}
		return needAdjust;
	}

	/**
	 * 给定初水位和各时段入库径流按等流量调节计算一段时期的平均出力
	 * 
	 * @param index
	 * @param tbType
	 * @param startPeriod
	 * @param startYear
	 */
	public static double[] avgOutputOfPeriod(MultiFomulaMode mfm,int year, int startPeriod, String tbType, int[] index, HStationSpec hsSpec,
			StationCurve curve, double[] runoff, int[] length, double levelBegin) {
		double outpurAvg = 0;
		double outPutSum = 0;
		double[] results = outputCal(mfm,year, startPeriod, tbType, index, hsSpec, curve, runoff, length, levelBegin, -1,
				-1);
		double[] output = new double[runoff.length];
		for (int i = 0; i < output.length; i++) {
			output[i] = results[i];
			outPutSum = outPutSum + output[i];
		}
		double regulatedFlow = results[results.length - 1];

		outpurAvg = outPutSum / output.length;
		// 计算完成后把供/蓄水期平均出力取出来
		double[] result = new double[2];
		result[0] = outpurAvg;
		result[1] = regulatedFlow;
		return result;
	}

	/**
	 * 给定初水位和各时段入库径流按等流量调节计算一段时期内的最小出力
	 * 
	 * @param index
	 * @param tbType
	 * @param startPeriod
	 * @param startYear
	 */
	public static double[] minOutputOfPeriod(MultiFomulaMode mfm,int year, int startPeriod, String tbType, int[] index, HStationSpec hsSpec,
			StationCurve curve, double[] runoff, int[] length, double levelBegin) {
		double outpurMin = 0;
		double[] results = outputCal(mfm,year, startPeriod, tbType, index, hsSpec, curve, runoff, length, levelBegin, -1,
				-1);
		double[] output = new double[runoff.length];
		for (int i = 0; i < output.length; i++) {
			output[i] = results[i];
		}
		double regulatedFlow = results[results.length - 1];
		outpurMin = BasicMathMethods.minOf1DArray(output);
		// 计算完成后把供/蓄水期平均出力取出来
		double[] result = new double[2];
		result[0] = outpurMin;
		result[1] = regulatedFlow;
		return result;
	}

	/**
	 * @param mfm
	 * @param year
	 * @param startPeriod
	 * @param tbType
	 * @param index
	 * @param hsSpec
	 * @param curve
	 * @param runoff
	 * @param length
	 * @param levelBegin
	 * @param floodBegin
	 * @param floodEnd
	 * @return  顺时程计算调节流量和平均出力
	 */
	public static double[] outputCal(MultiFomulaMode mfm,int year, int startPeriod, String tbType, int[] index, HStationSpec hsSpec,
			StationCurve curve, double[] runoff, int[] length, double levelBegin, int floodBegin, int floodEnd) {
		double[] output = new double[runoff.length];
		// 先计算调节流量，调节流量应该正好能使水库水位在供水期末到达死水位
		double[] waterTemp = new double[runoff.length];
		for (int i = 0; i < runoff.length; i++) {
			waterTemp[i] = (double) (runoff[i] * length[i]);
		}
		double waterSum = 0;
		int lengthSum = 0;
		for (int i = 0; i < waterTemp.length; i++) {
			waterSum = waterSum + waterTemp[i];
			lengthSum = lengthSum + length[i];
		}
		double regulatedFlow = levelBegin > hsSpec.getLevelDead() + 1//这可以判断是供水期还是蓄水期，即使是有汛限水位的电站，蓄水期末同样升至正常蓄水位，因此调节流量计算方法一致
				? ((waterSum + hsSpec.getStorageRegulating() * 1e8) / lengthSum)
				: ((waterSum - hsSpec.getStorageRegulating() * 1e8) / lengthSum);
		//HsStateOriginFactory hsof = InitialContainer.getContext().getBean(HsStateOriginFactory.class);
		int[] constrainttypes = { 101, 102, 201, 301 };
		int periodReal = (startPeriod + index[0]);
		String datetime = "";
		if (tbType.equals("MONTH")) {
			if (periodReal > 12)
			{
				periodReal = periodReal - 12;
				year = year + 1;
			}
			datetime = year + "-" + periodReal + "-01T00:00:00";
			if (periodReal < 10)
				datetime = year + "-0" + periodReal + "-01T00:00:00";
		} else {// 旬的时候
			if (periodReal > 36)
			{
				periodReal = periodReal - 36;
				year = year + 1;
			}
			int monthtemp = (periodReal - 1) / 3 + 1;
			if (periodReal % 3 == 0) {
				datetime = year + "-" + monthtemp + "-21T00:00:00";
				if (monthtemp < 10)
					datetime = year + "-0" + monthtemp + "-21T00:00:00";
			} else if (periodReal % 3 == 1) {
				datetime = year + "-" + monthtemp + "-01T00:00:00";
				if (monthtemp < 10)
					datetime = year + "-0" + monthtemp + "-01T00:00:00";
			} else {
				datetime = year + "-" + monthtemp + "-11T00:00:00";
				if (monthtemp < 10)
					datetime = year + "-0" + monthtemp + "-11T00:00:00";
			}
		}
		HsStateValueFactory hsvf=new HsStateValueFactory(hsSpec, tbType, constrainttypes, datetime, output.length);
		List<HStationState> hss = hsvf.createHsStates(runoff);
		//List<HStationState> hss = hsof.createHsStates(hsSpec.getId(), constrainttypes, tbType, 1, datetime,output.length);
		if (floodBegin > 0 && floodEnd > floodBegin) {
			BuildConstraint.valueFloodLimitLevelForStates(tbType, hss, hsSpec.getLevelFloodLimiting(), floodBegin,
					floodEnd);
		}
		
		ControlModelSingleTime cmst = new ControlModelSingleTime(mfm);
		hss.get(0).setLevelBegin(levelBegin);
		for (int i = 0; i < output.length; i++)// 从正常蓄水位开始顺时程供水期各个时段，进行调节计算
		{
			hss.get(i).setOutflow(regulatedFlow);
			cmst.flowControl(hss.get(i), hsSpec, curve);
			if (i < output.length - 1) {
				hss.get(i + 1).setLevelBegin(hss.get(i).getLevelEnd());
			}
			output[i] = hss.get(i).getOutput();
		}
		double[] result = new double[output.length + 1];
		result = BasicMathMethods.mergeArray(output, new double[] { regulatedFlow });
		return result;
	}

	public static double[] avgOutputOfPeriodWithFloodControl(MultiFomulaMode mfm,int year, int startPeriod, String tbType, int[] index,
			HStationSpec hsSpec, StationCurve curve, double[] runoff, int[] length, double levelBegin,
			LocalDate floodControlBegin, LocalDate floodControlEnd) {
		double outpurAvg = 0;
		double outPutSum = 0;
		double[] results = null;
		if (tbType.equals("MONTH"))
			results = outputCal(mfm,year, startPeriod, tbType, index, hsSpec, curve, runoff, length, levelBegin, floodControlBegin.getMonthValue(), floodControlEnd.getMonthValue());
		else{
			int floodstartindex=0;
			int floodendindex=0;
			if(floodControlBegin.getDayOfMonth()<10)
				floodstartindex=(floodControlBegin.getMonthValue()-1)*3+1;
			else if(floodControlBegin.getDayOfMonth()<20)
				floodstartindex=(floodControlBegin.getMonthValue()-1)*3+2;
			else 
				floodstartindex=floodControlBegin.getMonthValue()*3;
			
			if(floodControlEnd.getDayOfMonth()<10)
				floodendindex=(floodControlEnd.getMonthValue()-1)*3+1;
			else if(floodControlBegin.getDayOfMonth()<20)
				floodendindex=(floodControlEnd.getMonthValue()-1)*3+2;
			else 
				floodendindex=floodControlEnd.getMonthValue()*3;
			results = outputCal(mfm,year, startPeriod, tbType, index, hsSpec, curve, runoff, length, levelBegin, floodstartindex, floodendindex);
		}
		double[] output = new double[runoff.length];
		for (int i = 0; i < output.length; i++) {
			output[i] = results[i];
			outPutSum = outPutSum + output[i];
		}
		double regulatedFlow = results[results.length - 1];

		outpurAvg = outPutSum / output.length;
		// 计算完成后把供/蓄水期平均出力取出来
		double[] result = new double[2];
		result[0] = outpurAvg;
		result[1] = regulatedFlow;
		return result;
	}

	public static double[] minOutputOfPeriodWithFloodControl(MultiFomulaMode mfm,int year, int startPeriod, String tbType, int[] index,
			HStationSpec hsSpec, StationCurve curve, double[] runoff, int[] length, double levelBegin,
			LocalDate floodControlBegin, LocalDate floodControlEnd) {
		double outpurMin = 0;
		double[] results = null;
		if (tbType.equals("MONTH"))
			results = outputCal(mfm,year, startPeriod, tbType, index, hsSpec, curve, runoff, length, levelBegin, floodControlBegin.getMonthValue(), floodControlEnd.getMonthValue());
		else{
			int floodstartindex=0;
			int floodendindex=0;
			if(floodControlBegin.getDayOfMonth()<10)
				floodstartindex=(floodControlBegin.getMonthValue()-1)*3+1;
			else if(floodControlBegin.getDayOfMonth()<20)
				floodstartindex=(floodControlBegin.getMonthValue()-1)*3+2;
			else 
				floodstartindex=floodControlBegin.getMonthValue()*3;
			
			if(floodControlEnd.getDayOfMonth()<10)
				floodendindex=(floodControlEnd.getMonthValue()-1)*3+1;
			else if(floodControlBegin.getDayOfMonth()<20)
				floodendindex=(floodControlEnd.getMonthValue()-1)*3+2;
			else 
				floodendindex=floodControlEnd.getMonthValue()*3;
			results = outputCal(mfm,year, startPeriod, tbType, index, hsSpec, curve, runoff, length, levelBegin, floodstartindex, floodendindex);
		}
		double[] output = new double[runoff.length];
		for (int i = 0; i < output.length; i++) {
			output[i] = results[i];
		}
		double regulatedFlow = results[results.length - 1];

		outpurMin = BasicMathMethods.minOf1DArray(output);
		// 计算完成后把供/蓄水期平均出力取出来
		double[] result = new double[2];
		result[0] = outpurMin;
		result[1] = regulatedFlow;
		return result;
	}

}
