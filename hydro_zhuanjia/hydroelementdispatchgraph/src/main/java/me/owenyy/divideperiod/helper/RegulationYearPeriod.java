package me.owenyy.divideperiod.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.operation.basic.HydroDateUtil;
import com.wenyu.hydroelements.timebucket.TimeBucketType;

/**
 * 调节年度  （区别于自然年度）里的一段时期  供水期或蓄水期、或不蓄不供期 
 *
 */
public class RegulationYearPeriod 
{
	private DeliOrStor id;//供蓄期的标志
	private List<PeriodPair> aRegulationYearPeriod;
	private List<Double> runoff;//这一段调节期的历史径流
	private List<Integer> periodlength;//这一段调节期每个时段的秒数

	public RegulationYearPeriod() {
		super();
	}
	
	public RegulationYearPeriod(DeliOrStor id,List<PeriodPair> aRegulationYearPeriod, List<Double> runoff, List<Integer> periodlength) {
		super();
		this.id=id;
		this.aRegulationYearPeriod = aRegulationYearPeriod;
		this.runoff = runoff;
		this.periodlength = periodlength;
	}
	
	public DeliOrStor getId() {
		return id;
	}
	public void setId(DeliOrStor id) {
		this.id = id;
	}
	
	public List<PeriodPair> getaRegulationYearPeriod() {
		return aRegulationYearPeriod;
	}
	public void setaRegulationYearPeriod(List<PeriodPair> aRegulationYearPeriod) {
		this.aRegulationYearPeriod = aRegulationYearPeriod;
	}

	public List<Double> getRunoff() {
		return runoff;
	}
	public void setRunoff(List<Double> runoff) {
		this.runoff = runoff;
	}
	
	
	public List<Integer> getPeriodlength() {
		return periodlength;
	}
	public void setPeriodlength(List<Integer> periodlength) {
		this.periodlength = periodlength;
	}
	
	/** 
	 * 初始化一个调节年各个时期的各项数据
	 * @param id  供蓄水期的标志
	 * @param runoffData   按该调节期顺序排好的径流数据
	 * @param startDate    该时期起始时间 ，Date类型
	 * @param length  时期里的时段个数（例如一年12个月  一年36个旬）
	 */
	public void initialAllData(DeliOrStor id,double[] runoffData,LocalDate startDate,int length)
	{
		aRegulationYearPeriod = new ArrayList<PeriodPair>();
		int year=startDate.getYear();
		int period=startDate.getMonthValue();
		TimeBucketType periodtype=TimeBucketType.MONTH;
		if(length==36)
		{
			period=HydroDateUtil.getDecad(startDate);
			periodtype=TimeBucketType.DECAD;
		}
		PeriodPair ppTemp=new PeriodPair(year,period, TimeBucketType.YEAR, periodtype);
		for(int i=0;i<runoffData.length;i++)
		{
			aRegulationYearPeriod.add(ppTemp);
			ppTemp=ppTemp.plus(1);
		}
		
		runoff = new ArrayList<Double>();//这一段调节期的历史径流
		for(int i=0;i<runoffData.length;i++)
		{
			runoff.add(runoffData[i]);
		}
		
		periodlength = new ArrayList<Integer>();//这一段调节期每个时段的秒数
		LocalDate timeTemp=startDate.plusDays(0);//还是本天
		for(int i=0;i<runoffData.length;i++)
		{
			periodlength.add(HydroDateUtil.secondsOfNowPeriod(timeTemp, periodtype));
			if(periodtype.equals(TimeBucketType.MONTH)) 
				timeTemp=timeTemp.plusMonths(1);
			else if(periodtype.equals(TimeBucketType.DECAD)) 
				timeTemp=HydroDateUtil.addDecad(timeTemp, 1);
		}
	
		this.id=id;//供蓄期的标志
		
	}
	
	/** 
	 * 初始化一个调节年各个时期的各项数据
	 * @param id  供蓄水期的标志
	 * @param runoffData   按该调节期顺序排好的径流数据
	 * @param startDate    各个时段的其实日期
	 * @param length  时期里的时段个数（例如一年12个月  一年36个旬）
	 */
	public void initialAllData(DeliOrStor id,double[] runoffData,LocalDate[] startDate,int length)
	{
		aRegulationYearPeriod = new ArrayList<PeriodPair>();
		TimeBucketType periodtype=TimeBucketType.MONTH;
		for(int i=0;i<startDate.length;i++){
			int year=startDate[i].getYear();
			int period=startDate[i].getMonthValue();
			
			if(length==36)
			{
				period=HydroDateUtil.getDecad(startDate[i]);
				periodtype=TimeBucketType.DECAD;
			}
			PeriodPair ppTemp=new PeriodPair(year,period, TimeBucketType.YEAR, periodtype);
			aRegulationYearPeriod.add(ppTemp);
		}
		
		runoff = new ArrayList<Double>();//这一段调节期的历史径流
		for(int i=0;i<runoffData.length;i++)
		{
			runoff.add(runoffData[i]);
		}
		
		periodlength = new ArrayList<Integer>();//这一段调节期每个时段的秒数
		
		for(int i=0;i<runoffData.length;i++)
		{
			LocalDate timeTemp=startDate[i].plusDays(0);//还是本天
			periodlength.add(HydroDateUtil.secondsOfNowPeriod(timeTemp, periodtype));
		}
	
		this.id=id;//供蓄期的标志
		
	}
}
