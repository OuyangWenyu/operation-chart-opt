package com.wenyu.service.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.entity.TimeSequenceCurveTemp;
import com.wenyu.hydroelements.curve.BaseStatistics;
import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.timebucket.TimeBucketSequence;
import com.wenyu.hydroelements.timebucket.TimeBucketType;

/**
 * 把状态数据转为TimeSeq数据，以便将数据写进数据库
 * 
 * @author OwenYY
 *
 */
public class HsStateToTimeSeq {
	/**
	 * 根据电站的某个变量编号，取出对应的各状态值。
	 * 
	 * @param type
	 *            变量的类型
	 * @param states
	 *            电站的状态值
	 */
	public static TimeSeqCurve hsStatesToTimeSeqCurve(int type, List<HStationState> states) {
		TimeSeqCurve tsc = new TimeSeqCurve();
		double[] values = new double[states.size()];
		for (int i = 0; i < states.size(); i++) {
			values[i] = valueSetting(type, states.get(i));
		}
		BaseStatistics datas = new BaseStatistics(values);
		tsc.setDatas(datas);
		TimeBucketType tbType = judgeTimeBucketType(states);
		int unitNums = judgeUnitNums(states, tbType);
		TimeBucketSequence dates = new TimeBucketSequence(tbType, unitNums, states.get(0).getTimeStart(),
				states.size());
		tsc.setDates(dates);
		return tsc;
	}

	/**
	 * @param states
	 * @param tbType
	 * @return 根据状态和时段类型，判断时段的个数（暂时还么写完）
	 */

	public static int judgeUnitNums(List<HStationState> states, TimeBucketType tbType) {
		
		return 1;
	}

	public static TimeBucketType judgeTimeBucketType(List<HStationState> states) {
		TimeBucketType type = null;
		LocalDateTime one = states.get(0).getTimeStart().minusSeconds(0);
		LocalDateTime two = states.get(1).getTimeStart().minusSeconds(0);
		if (one.until(two, ChronoUnit.YEARS) > 0)
			type = TimeBucketType.YEAR;
		else if (one.until(two, ChronoUnit.MONTHS) > 0)
			type = TimeBucketType.MONTH;
		else if (one.until(two, ChronoUnit.DAYS) > 7 && one.until(two, ChronoUnit.DAYS) < 12)// 这个暂时不太严谨
			type = TimeBucketType.DECAD;
		else if (one.until(two, ChronoUnit.DAYS) == 7)
			type = TimeBucketType.WEEK;
		else if (one.until(two, ChronoUnit.DAYS) < 7 && one.until(two, ChronoUnit.DAYS) > 0)// 这个暂时不太严谨
			type = TimeBucketType.DAY;
		else if (one.until(two, ChronoUnit.DAYS) < 1 && one.until(two, ChronoUnit.HOURS) > 0)
			type = TimeBucketType.HOUR;
		else if (one.until(two, ChronoUnit.HOURS) < 1 && one.until(two, ChronoUnit.MINUTES) > 0)
			type = TimeBucketType.MINUTE;
		else
			type = TimeBucketType.SECOND;
		return type;
	}

	/**
	 * @param type
	 *            变量类型
	 * @param hStationState
	 *            电站一个时段状态值
	 * @return 取对应变量的值
	 */
	public static double valueSetting(int type, HStationState hStationState) {
		double value = -1;
		switch (type) {
		case 1000:
			value = hStationState.getInflowReal();
			break;
		case 1001:

			break;
		case 1002:
			value = hStationState.getOutflow();
			break;
		case 1003:
			value = hStationState.getOutflowGeneration();
			break;
		case 1004:
			value = hStationState.getOutflowDesert();
			break;
		case 2000:
			value = hStationState.getOutput();
			break;
		case 3001:
			value = hStationState.getLevelBegin();
			break;
		case 3002:
			value = hStationState.getLevelEnd();
			break;
		case 3003:
			value = hStationState.getLevelDown();
			break;
		case 4001:
			value = hStationState.getHeadGross();
			break;
		case 4002:
			value = hStationState.getHeadPure();
			break;
		case 5001:
			value = hStationState.getGeneration();
			break;
		}
		return value;
	}

	/**
	 * @param belongto
	 * @param type
	 * @param tsc
	 * @return 将tsc装换为实体类的形式
	 */
	public static List<TimeSequenceCurveTemp> timeSeqToEntityTemp(int belongto, int type, TimeSeqCurve tsc) {
		List<TimeSequenceCurveTemp> temps = new ArrayList<TimeSequenceCurveTemp>();
		for (int i = 0; i < tsc.getDates().getTbNums(); i++) {
			TimeSequenceCurveTemp temp = new TimeSequenceCurveTemp();
			temp.setValueavg(tsc.getDatas().getArray()[i]);
			temp.setBelongto(belongto);
			temp.setEndtime(tsc.getDates().getTbSeq().get(i).getEndDateTime());
			temp.setType(type);
			temp.setStarttime(tsc.getDates().getTbSeq().get(i).getStartDateTime());
			temp.setTimebuckettype(tsc.getDates().getTbType().toString());
			temp.setTimelength(tsc.getDates().getTbSeq().get(i).getTimeLength());
			temps.add(temp);
		}
		return temps;
	}
	
	/**
	 *  把状态数据转为TimeSequenceCurveTemp数据
	 */
	public static List<TimeSequenceCurveTemp> hsStatesToTimeSequenceCurve(int belongto,int[] types, List<HStationState> states){
		List<TimeSequenceCurveTemp> tscts=new ArrayList<TimeSequenceCurveTemp>();
		for(int i=0;i<types.length;i++){
			TimeSeqCurve tsc=hsStatesToTimeSeqCurve(types[i], states);
			tscts.addAll(timeSeqToEntityTemp(belongto, types[i], tsc));
		}
		return tscts;
	}
	
	
}
