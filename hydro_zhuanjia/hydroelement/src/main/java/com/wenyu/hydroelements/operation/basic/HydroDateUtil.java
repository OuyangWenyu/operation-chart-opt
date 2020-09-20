package com.wenyu.hydroelements.operation.basic;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.wenyu.hydroelements.hydrostation.dispatchgraph.CommonConsts;
import com.wenyu.hydroelements.timebucket.TimeBucketType;

/**
 * 旬的不好处理，这个类专门处理一下旬相关的日期
 * 
 * @author OwenYY
 *
 */
public class HydroDateUtil {
	/**
	 * @param date
	 * @return 返回date对应的一年中的旬编号，旬号从1-36
	 */
	public static int getDecad(LocalDate date) {
		int decadNum = 0;
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		if (day >= 1 && day <= 10) {
			decadNum = (month-1) * 3 + 1;
		} else if (day >= 11 && day <= 20) {
			decadNum = (month-1) * 3 + 2;
		} else {
			decadNum = month * 3;
		}
		return decadNum;

	}

	/**
	 * 增加日期的旬份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param decadAmount
	 *            增加数量。暂时只能为正数
	 * @return 增加旬份后的日期
	 */
	public static LocalDate addDecad(LocalDate date, int decadAmount) {
		LocalDate myDate = null;
		if (decadAmount < 3) {
			if (decadAmount == 1) {
				myDate = addOneDecad(date);
			} else if (decadAmount < 1) {
				myDate =date;
			}
			else{
				myDate = addOneDecad(addOneDecad(date));
			}
		} else {
			if (decadAmount % 3 == 0)
				myDate = date.plusMonths(decadAmount / 3);
			else if (decadAmount % 3 == 1)
				myDate = addOneDecad(date.plusMonths(decadAmount / 3));
			else
				myDate = addOneDecad(addOneDecad(date.plusMonths(decadAmount / 3)));
		}
		return myDate;
	}

	/**
	 * @param date
	 * @return 当前日期的基础上加上一旬
	 */
	private static LocalDate addOneDecad(LocalDate date) {
		LocalDate myDate = null;
		int nowDay = date.getDayOfMonth();
		if (nowDay < 21) {
			myDate = date.plusDays(10);
		} else {
			if (date.lengthOfMonth() == 31 && date.getDayOfMonth() > 30)
				myDate = LocalDate.of(date.plusMonths(1).getYear(), date.plusMonths(1).getMonthValue(), 10);
			else
				myDate = LocalDate.of(date.plusMonths(1).getYear(), date.plusMonths(1).getMonthValue(),
						date.getDayOfMonth() - 20);
		}
		return myDate;
	}

	public static int secondsOfNowPeriod(LocalDate time, TimeBucketType periodType) {
		int seconds = 0;
		if (periodType.equals(TimeBucketType.DAY)) {
			seconds = (24 * 3600);
		} else if (periodType.equals(TimeBucketType.DECAD)) {
			int nowDecadNum = HydroDateUtil.getDecad(time)-1;
			if (time.isLeapYear())
				seconds = CommonConsts.LEAPYEAR_SECONDS_PER_DECAD[nowDecadNum];
			else
				seconds = CommonConsts.COMMONYEAR_SECONDS_PER_DECAD[nowDecadNum];

		} else if (periodType.equals(TimeBucketType.HOUR)) {
			seconds = 3600;
		} else if (periodType.equals(TimeBucketType.MINUTE)) {
			seconds = 60;
		} else if (periodType.equals(TimeBucketType.MONTH)) {
			int i = time.getMonthValue() - 1;
			if (time.isLeapYear()) {
				seconds = CommonConsts.LEAPYEAR_SECONDS_PER_MONTH[i];
			} else {
				seconds = CommonConsts.COMMONYEAR_SECONDS_PER_MONTH[i];
			}

		} else if (periodType.equals(TimeBucketType.SECOND)) {
			seconds = 1;
		} else if (periodType.equals(TimeBucketType.YEAR)) {
			if (time.isLeapYear()) {
				seconds = CommonConsts.SECONDS_PER_LEAPYEAR;
			} else {
				seconds = CommonConsts.SECONDS_PER_COMMONYEAR;
			}
		}
		return seconds;
	}

	public static int getPeriod(LocalDate date, TimeBucketType periodType) {
		int period = 0;
		period = date.getMonthValue();
		if (periodType.equals(TimeBucketType.DECAD)) {
			period = HydroDateUtil.getDecad(date);
		}
		return period;
	}

	/**
	 * @param startDateTime
	 * @param tbType
	 * @param unitNums
	 * @param tbNums
	 * @return 根据时间类型，起始时间，单位时段的时段个数、总的单位时段个数计算时段末的时间
	 */
	public static LocalDateTime calEndTime(LocalDateTime startDateTime, String tbType, int unitNums, int tbNums) {
		LocalDateTime endDateTime = null;
		switch (tbType) {
		case "YEAR":
			endDateTime = startDateTime.plusYears(unitNums * tbNums);
			break;
		case "MONTH":
			endDateTime = startDateTime.plusMonths(unitNums * tbNums);
			break;
		case "DECAD":
			endDateTime = addDecad(startDateTime.toLocalDate(), unitNums * tbNums).atStartOfDay();
			break;
		case "WEEK":
			endDateTime = startDateTime.plusWeeks(unitNums * tbNums);
			break;
		case "DAY":
			endDateTime = startDateTime.plusDays(unitNums * tbNums);
			break;
		case "HOUR":
			endDateTime = startDateTime.plusHours(unitNums * tbNums);
			break;
		case "MINUTE":
			endDateTime = startDateTime.plusMinutes(unitNums * tbNums);
			break;
		case "SECOND":
			endDateTime = startDateTime.plusSeconds(unitNums * tbNums);
			break;
		}
		return endDateTime;
	}

	/**
	 * @param year
	 * @param decad
	 * @return 根据年和旬的编号得到对应的日期
	 */
	public static LocalDate getLocalDateByYearAndDecad(int year, int decad) {
		LocalDate date = null;
		if (decad % 3 == 1) {
			date = LocalDate.of(year, decad / 3 + 1, 1);
		} else if (decad % 3 == 2) {
			date = LocalDate.of(year, decad / 3 + 1, 11);
		} else {
			date = LocalDate.of(year, decad / 3, 21);
		}
		return date;
	}
}
