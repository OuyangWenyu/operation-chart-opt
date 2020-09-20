package com.wenyu.hydroelements.timebucket;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

public class TimeBucketSequenceTest {

	@Test
	public void testTimeBucketSequence0() {
		TimeBucketType tbType=TimeBucketType.YEAR;
		int unitNums = 3;
		LocalDateTime startDateTime = LocalDateTime.of(2001, 7, 9, 13, 21, 55);
		int tbNums = 10;
		TimeBucketSequence tbs=new TimeBucketSequence(tbType,unitNums,startDateTime,tbNums);
		Assert.assertEquals(2010, tbs.getTbSeq().get(tbNums-1).getStartDateTime().getYear());
	}
	
	@Test
	public void testTimeBucketSequence1() {
		TimeBucketType tbType=TimeBucketType.MONTH;
		int unitNums = 3;
		LocalDateTime startDateTime = LocalDateTime.of(2001, 7, 9, 13, 21, 55);
		int tbNums = 10;
		TimeBucketSequence tbs=new TimeBucketSequence(tbType,unitNums,startDateTime,tbNums);
		Assert.assertEquals(4, tbs.getTbSeq().get(tbNums-1).getStartDateTime().getMonthValue());
	}
	@Test
	public void testTimeBucketSequence2() {
		TimeBucketType tbType=TimeBucketType.DECAD;
		int unitNums = 3;
		LocalDateTime startDateTime = LocalDateTime.of(2001, 7, 9, 13, 21, 55);
		int tbNums = 10;
		TimeBucketSequence tbs=new TimeBucketSequence(tbType,unitNums,startDateTime,tbNums);
		Assert.assertEquals(10, tbs.getTbSeq().get(tbNums-1).getStartDateTime().getMonthValue());
		Assert.assertEquals(1, tbs.getTbSeq().get(tbNums-1).getStartDateTime().getDayOfMonth());
	}
	@Test
	public void testTimeBucketSequence3() {
		TimeBucketType tbType=TimeBucketType.WEEK;
		int unitNums = 1;
		LocalDateTime startDateTime = LocalDateTime.of(2016, 5, 1, 13, 21, 55);
		int tbNums = 3;
		TimeBucketSequence tbs=new TimeBucketSequence(tbType,unitNums,startDateTime,tbNums);
		Assert.assertEquals(9, tbs.getTbSeq().get(tbNums-1).getStartDateTime().getDayOfMonth());
	}
	@Test
	public void testTimeBucketSequence4() {
		TimeBucketType tbType=TimeBucketType.DAY;
		int unitNums = 3;
		LocalDateTime startDateTime = LocalDateTime.of(2001, 7, 9, 13, 21, 55);
		int tbNums = 250;
		TimeBucketSequence tbs=new TimeBucketSequence(tbType,unitNums,startDateTime,tbNums);
		Assert.assertEquals(2003, tbs.getTbSeq().get(tbNums-1).getStartDateTime().getYear());
	}
	@Test
	public void testTimeBucketSequence5() {
		TimeBucketType tbType=TimeBucketType.HOUR;
		int unitNums = 1;
		LocalDateTime startDateTime = LocalDateTime.of(2001, 7, 9, 13, 21, 55);
		int tbNums = 10;
		TimeBucketSequence tbs=new TimeBucketSequence(tbType,unitNums,startDateTime,tbNums);
		Assert.assertEquals(22, tbs.getTbSeq().get(tbNums-1).getStartDateTime().getHour());
	}
	@Test
	public void testTimeBucketSequence6() {
		TimeBucketType tbType=TimeBucketType.MINUTE;
		int unitNums = 5;
		LocalDateTime startDateTime = LocalDateTime.of(2001, 7, 9, 13, 20, 55);
		int tbNums = 10;
		TimeBucketSequence tbs=new TimeBucketSequence(tbType,unitNums,startDateTime,tbNums);
		Assert.assertEquals(5, tbs.getTbSeq().get(tbNums-1).getStartDateTime().getMinute());
	}
	@Test
	public void testTimeBucketSequence7() {
		TimeBucketType tbType=TimeBucketType.SECOND;
		int unitNums = 5;
		LocalDateTime startDateTime = LocalDateTime.of(2001, 7, 9, 13, 21, 55);
		int tbNums = 11;
		TimeBucketSequence tbs=new TimeBucketSequence(tbType,unitNums,startDateTime,tbNums);
		Assert.assertEquals(45, tbs.getTbSeq().get(tbNums-1).getStartDateTime().getSecond());
	}

}
