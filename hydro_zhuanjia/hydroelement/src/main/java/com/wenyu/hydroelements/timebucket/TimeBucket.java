package com.wenyu.hydroelements.timebucket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 * 以年、月、旬为时间尺度时，只允许每个时段是一年、一月或一旬，不允许多年多月多旬，不便于时间的统计，
 * 而以周、日、时、分、秒为时间尺度时，允许每个时段是多周、多日、多时、多分、多秒的情况。
 * 以年为时间尺度时，开始时间必须为某年的一月一日；
 * 以月为时间尺度时，开始时间必须为某月一日；以此类推。
 *
 * 时段为闭区间
 * @author  OwenYY
 *
 */
public class TimeBucket{
	private LocalDateTime startDateTime;

	private TimeBucketType timeType;
	private int unitsNum;//单位时段内的时间尺度的个数，年月旬时为1
	private int timeLength;//时段长度统一为s，表示的是时段内（开始结束都算在内）以秒为单位的时间戳的个数

	private LocalDateTime endDateTime;

	/**
	 * @return the startDate
	 */
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * @return the timeType
	 */
	public TimeBucketType getTimeType() {
		return timeType;
	}

	/**
	 * @param timeType the timeType to set
	 */
	public void setTimeType(TimeBucketType timeType) {
		this.timeType = timeType;
	}

	public int getUnitsNum() {
		return unitsNum;
	}

	public void setUnitsNum(int unitsNum) {
		this.unitsNum = unitsNum;
	}

	/**
	 * @return the timeLength
	 */
	public int getTimeLength() {
		return timeLength;
	}

	/**
	 * @param timeLength the timeLength to set
	 */
	public void setTimeLength(int timeLength) {
		this.timeLength = timeLength;
	}

	/**
	 * @return the endDate
	 */
	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}


	public TimeBucket(LocalDateTime startDateTime, TimeBucketType timeType) {
		super();
		this.startDateTime = startDateTime;
		this.timeType = timeType;
		unitsNum=1;//默认为1
		build();
	}
	public TimeBucket(LocalDateTime startDateTime, TimeBucketType timeType, int unitsNum) {
		super();
		this.startDateTime = startDateTime;
		this.timeType = timeType;
		this.unitsNum=unitsNum;
		build();
	}

	private void build()
	{
		if(timeType.equals(TimeBucketType.YEAR))
		{
			//不管时间是不是一月一日的，强行修正为一月一日
			int startYear=startDateTime.getYear();
			startDateTime=LocalDateTime.of(LocalDate.of(startYear, Month.JANUARY, 1)
					, LocalTime.of(0, 0, 0));
			//不管是不是闰年，都可以调用lengthOfYear函数获取天数
			timeLength=LocalDate.of(startYear, Month.JANUARY, 1).lengthOfYear() * 24 *3600;
			endDateTime=startDateTime.plus(1, ChronoUnit.YEARS).minus(1,ChronoUnit.SECONDS);
			setUnitsNum(1);
		}

		else if(timeType.equals(TimeBucketType.MONTH))
		{
			//不管时间是不是一日的，强行修正为某月一日
			startDateTime=LocalDateTime.of(
					LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(), 1)
					, LocalTime.of(0, 0, 0));
			timeLength=LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(), 1).lengthOfMonth() * 24 *3600;
			endDateTime= startDateTime.plusMonths(1).minusSeconds(1);
			setUnitsNum(1);
		}

		else if(timeType.equals(TimeBucketType.DECAD))
		{
			//不管时间是不是某一旬的第一日，强行修正为某旬第一日
			int startDay=1;
			if(startDateTime.getDayOfMonth()>10 && startDateTime.getDayOfMonth()<=20)
				startDay=11;
			else if(startDateTime.getDayOfMonth()>20)
				startDay=21;
			startDateTime=LocalDateTime.of(
					LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(), startDay)
							, LocalTime.of(0, 0, 0));

			timeLength= 10 * 24 * 3600;
			endDateTime=startDateTime.plus(10, ChronoUnit.DAYS).minus(1,ChronoUnit.SECONDS);
			if(startDay==21)
			{
				timeLength=(LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(), startDay).lengthOfMonth() - 20) * 24 *3600;
				endDateTime= startDateTime.plus(LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(), startDay).lengthOfMonth() - 20, ChronoUnit.DAYS)
						.minus(1, ChronoUnit.SECONDS);
			}
			setUnitsNum(1);
		}

		else if(timeType.equals(TimeBucketType.WEEK))
		{
			//起点必须是周一，如果不是周一，修正到本周周一，java类的week是从周一开始的，时分秒也都是000
			startDateTime=LocalDateTime.of(
					LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(),
							startDateTime.getDayOfMonth())
							, LocalTime.of(0, 0, 0));
			int minusDay=startDateTime.getDayOfWeek().getValue()-1;
			startDateTime=startDateTime.minus(minusDay, ChronoUnit.DAYS);
			timeLength=unitsNum * 7 * 24 * 3600;
			endDateTime=startDateTime.plus(unitsNum, ChronoUnit.WEEKS).minus(1,ChronoUnit.SECONDS);
		}

		else if(timeType.equals(TimeBucketType.DAY))
		{
			startDateTime=LocalDateTime.of(
					LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(),
							startDateTime.getDayOfMonth())
							, LocalTime.of(0, 0, 0));
			timeLength=unitsNum * 24 * 3600;
			endDateTime=startDateTime.plus(unitsNum, ChronoUnit.DAYS).minus(1,ChronoUnit.SECONDS);
		}

		else if(timeType.equals(TimeBucketType.HOUR))
		{
			startDateTime=LocalDateTime.of(
					LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(),
							startDateTime.getDayOfMonth())
							, LocalTime.of(startDateTime.getHour(), 0, 0));
			timeLength = unitsNum*3600;
			endDateTime= startDateTime.plus(timeLength, ChronoUnit.SECONDS).minus(1,ChronoUnit.SECONDS);
		}

		else if(timeType.equals(TimeBucketType.MINUTE))
		{
			startDateTime=LocalDateTime.of(
					LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(),
							startDateTime.getDayOfMonth())
							, LocalTime.of(startDateTime.getHour(), startDateTime.getMinute(), 0));
			timeLength = unitsNum * 60;
			endDateTime= startDateTime.plus(timeLength, ChronoUnit.SECONDS).minus(1,ChronoUnit.SECONDS);
		}

		else if(timeType.equals(TimeBucketType.SECOND))
		{
			startDateTime=LocalDateTime.of(
					LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(),
							startDateTime.getDayOfMonth())
							, LocalTime.of(startDateTime.getHour(),  startDateTime.getMinute(),startDateTime.getSecond()));
			timeLength = unitsNum ;
			endDateTime= startDateTime.plus(timeLength, ChronoUnit.SECONDS).minus(1,ChronoUnit.SECONDS);
		}
	}

}
