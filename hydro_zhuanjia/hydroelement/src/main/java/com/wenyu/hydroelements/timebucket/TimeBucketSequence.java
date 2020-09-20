package com.wenyu.hydroelements.timebucket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 时间段的序列数据
 * @author OwenYY
 *
 */
public class TimeBucketSequence {
	private List<TimeBucket> tbSeq;
	private TimeBucketType tbType;
	private int unitNums;
	private LocalDateTime startDateTime;
	private int tbNums;
	
	/**
	 * @return the tbSeq
	 */
	public List<TimeBucket> getTbSeq() {
		return tbSeq;
	}

	/**
	 * @param tbSeq the tbSeq to set
	 */
	public void setTbSeq(List<TimeBucket> tbSeq) {
		this.tbSeq = tbSeq;
	}

	/**
	 * @return the tbType
	 */
	public TimeBucketType getTbType() {
		return tbType;
	}

	/**
	 * @return the unitNums
	 */
	public int getUnitNums() {
		return unitNums;
	}

	/**
	 * @return the startDateTime
	 */
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @return the tbNums
	 */
	public int getTbNums() {
		return tbNums;
	}

	public TimeBucketSequence(TimeBucketType tbType,int unitNums,LocalDateTime startDateTime
			,int tbNums)
	{
		this.startDateTime=startDateTime;
		this.tbType=tbType;
		this.tbNums=tbNums;
		this.unitNums=unitNums;
		build();
	}

	private void build() {
		tbSeq=new ArrayList<>();
		LocalDateTime temp=LocalDateTime.of(startDateTime.getYear(), 
				startDateTime.getMonth(),startDateTime.getDayOfMonth(),
				startDateTime.getHour(),startDateTime.getMinute(),
				startDateTime.getSecond());
		for(int i=0;i<tbNums;i++)
		{
			TimeBucket timeBucket=new TimeBucket(
					LocalDateTime.of(temp.getYear(), temp.getMonth(),
							temp.getDayOfMonth(),temp.getHour(),
							temp.getMinute(),temp.getSecond()), tbType, unitNums);
			tbSeq.add(timeBucket);
			
			long amountToAdd=Duration.between(timeBucket.getStartDateTime()
					, timeBucket.getEndDateTime()).getSeconds()+1;
			temp=timeBucket.getStartDateTime().plus(amountToAdd, ChronoUnit.SECONDS);
		}
	}
	
	
}
