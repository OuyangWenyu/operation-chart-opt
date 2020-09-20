package com.wenyu.hydroelements.curve;

import java.time.LocalDateTime;

import com.wenyu.hydroelements.timebucket.TimeBucketSequence;

/**
 * 时序的二维曲线，比如径流曲线
 * 第一维数据按照时间顺序排列，第二维数据与第一维的数据完全对应起来，
 * 水库调度里面的时间一般指一个时段，并且在一个序列里面，各个时段的时长基本一样，至少时间尺度上是保持一致的，
 * 比如每个时段时5分钟或者1小时，长期的可能不能保持时间一样长，比如每月，每旬，每年，但是时间尺度是一样的。
 * @author OwenYY
 */
public class TimeSeqCurve {
	private TimeBucketSequence dates;//java8的日期类型的数据
	private BaseStatistics datas;//double型的数据，每个时段的时段平均数据，与dates一一对应
	
	/**
	 * @return the dates
	 */
	public TimeBucketSequence getDates() {
		return dates;
	}

	/**
	 * @param dates the dates to set
	 */
	public void setDates(TimeBucketSequence dates) {
		this.dates = dates;
	}

	/**
	 * @return the datas
	 */
	public BaseStatistics getDatas() {
		return datas;
	}

	/**
	 * @param datas the datas to set
	 */
	public void setDatas(BaseStatistics datas) {
		this.datas = datas;
	}

	/**
	 * 二分法查找？直接根据时间戳以及单位时段长度进行index的查询
	 * @return 根据日期得到对应的数据
	 */
	public double getV1ByV0(LocalDateTime ldt)
	{
		double data=Double.MIN_VALUE;
		for(int i=0;i<dates.getTbNums();i++)
		{
			
			data=(datas.getArray())[i];
		}
		return data;//如果没有找到数据，就返回一个特别小的值
	}
}
