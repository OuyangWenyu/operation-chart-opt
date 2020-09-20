package com.wenyu.hydroelements.timebucket;


import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

public class TimeBucketTest {

	@Test
	public void testTimeBucketLocalDateTimeTimeBucketType0() {
		LocalDateTime startDateTime=LocalDateTime.of(2004, 6, 1, 10, 10, 10);
		TimeBucketType timeType=TimeBucketType.YEAR;
		TimeBucket tb=new TimeBucket(startDateTime,timeType,2);
		Assert.assertEquals(366*24*3600, tb.getTimeLength());
	}

	@Test
	public void testTimeBucketLocalDateTimeTimeBucketType1() {
		LocalDateTime startDateTime=LocalDateTime.of(2004, 2, 1, 10, 10, 10);
		TimeBucketType timeType=TimeBucketType.MONTH;
		TimeBucket tb=new TimeBucket(startDateTime,timeType,2);
		Assert.assertEquals((int)29*24*3600, tb.getTimeLength());
	}
	
	@Test
	public void testTimeBucketLocalDateTimeTimeBucketType2() {
		LocalDateTime startDateTime=LocalDateTime.of(1953, 2, 15, 10, 10, 10);
		TimeBucketType timeType=TimeBucketType.DECAD;
		TimeBucket tb=new TimeBucket(startDateTime,timeType,2);
		Assert.assertEquals(10*24*3600, tb.getTimeLength());
	}
	
	@Test
	public void testTimeBucketLocalDateTimeTimeBucketType3() {
		LocalDateTime startDateTime=LocalDateTime.of(1953, 7, 1, 10, 10, 10);
		TimeBucketType timeType=TimeBucketType.WEEK;
		TimeBucket tb=new TimeBucket(startDateTime,timeType,2);
		Assert.assertEquals(14*24*3600, tb.getTimeLength());
	}
	
	@Test
	public void testTimeBucketLocalDateTimeTimeBucketType4() {
		LocalDateTime startDateTime=LocalDateTime.of(1953, 6, 1, 10, 10, 10);
		TimeBucketType timeType=TimeBucketType.DAY;
		TimeBucket tb=new TimeBucket(startDateTime,timeType,2);
		Assert.assertEquals(2*24*3600, tb.getTimeLength());
	}
	
	@Test
	public void testTimeBucketLocalDateTimeTimeBucketType5() {
		LocalDateTime startDateTime=LocalDateTime.of(1953, 6, 1, 10, 10, 10);
		TimeBucketType timeType=TimeBucketType.HOUR;
		TimeBucket tb=new TimeBucket(startDateTime,timeType,2);
		Assert.assertEquals(2*3600, tb.getTimeLength());
	}
	
	@Test
	public void testTimeBucketLocalDateTimeTimeBucketType6() {
		LocalDateTime startDateTime=LocalDateTime.of(1953, 6, 1, 10, 10, 10);
		TimeBucketType timeType=TimeBucketType.MINUTE;
		TimeBucket tb=new TimeBucket(startDateTime,timeType,2);
		Assert.assertEquals(2*60, tb.getTimeLength());
	}

	@Test
	public void testTimeBucketLocalDateTimeTimeBucketType7() {
		LocalDateTime startDateTime=LocalDateTime.of(1953, 6, 1, 10, 10, 10);
		TimeBucketType timeType=TimeBucketType.SECOND;
		TimeBucket tb=new TimeBucket(startDateTime,timeType,2);
		Assert.assertEquals(2*1, tb.getTimeLength());
	}
}
