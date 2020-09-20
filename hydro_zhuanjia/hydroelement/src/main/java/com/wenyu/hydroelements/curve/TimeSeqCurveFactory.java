package com.wenyu.hydroelements.curve;

import java.time.LocalDateTime;

import com.wenyu.hydroelements.timebucket.TimeBucketSequence;
import com.wenyu.hydroelements.timebucket.TimeBucketType;

public class TimeSeqCurveFactory {
	private String tbType;//英文大写，年月旬周日时分秒 八个之一
	private int unitNums;//一个单元里面包括几个单位
	private LocalDateTime startDateTime;
	private int tbNums;//总的时段个数
	
	private double[] datas;
	
	/**
	 * @param tbType 时段类型
	 * @param unitNums 单位时段时间单元个数
	 * @param startDateTime 开始时间
	 * @param tbNums 总共时段长
	 * @param datas 数据
	 */
	public TimeSeqCurveFactory(String tbType, int unitNums, LocalDateTime startDateTime, int tbNums, double[] datas) {
		super();
		this.tbType = tbType;
		this.unitNums = unitNums;
		this.startDateTime = startDateTime;
		this.tbNums = tbNums;
		this.datas = datas;
	}

	public TimeSeqCurve curvePlotting()
	{
		TimeSeqCurve tsc=new TimeSeqCurve();
		TimeBucketType tbt=null;
		for(int i=0;i<TimeBucketType.values().length;i++)
		{
			if(tbType.equals((TimeBucketType.values())[i].name()))
			{
				tbt=(TimeBucketType.values())[i];
				break;
			}
		}
		TimeBucketSequence tbs=new TimeBucketSequence(
				tbt, unitNums, startDateTime, tbNums);
		tsc.setDates(tbs);
		BaseStatistics bs=new BaseStatistics(datas);
		tsc.setDatas(bs);
		return tsc;
		
	}
}
