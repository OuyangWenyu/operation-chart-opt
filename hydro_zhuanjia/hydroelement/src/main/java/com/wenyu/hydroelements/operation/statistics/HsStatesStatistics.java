package com.wenyu.hydroelements.operation.statistics;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.StatUtils;

import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.operation.basic.HydroDateUtil;

/**
 * 水库状态参数统计
 *
 */
public class HsStatesStatistics {
	private String stationName;
	private List<HStationState> states;//输入一个水库的状态值
	private List<StationSimuResultModel> stateResults;//数据输出到结果之中再进行处理
	private OutputPeriodAvg avgs;//月或旬的时候用，各时段的多年平均数据
	
	/**
	 * @return the stationName
	 */
	public String getStationName() {
		return stationName;
	}

	/**
	 * @param stationName the stationName to set
	 */
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	/**
	 * @return the states
	 */
	public List<HStationState> getStates() {
		return states;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(List<HStationState> states) {
		this.states = states;
	}

	/**
	 * @return the stateResults
	 */
	public List<StationSimuResultModel> getStateResults() {
		return stateResults;
	}

	/**
	 * @param stateResults the stateResults to set
	 */
	public void setStateResults(List<StationSimuResultModel> stateResults) {
		this.stateResults = stateResults;
	}

	public OutputPeriodAvg getAvgs() {
		return avgs;
	}

	public void setAvgs(OutputPeriodAvg avgs) {
		this.avgs = avgs;
	}

	public HsStatesStatistics(String stationName,List<HStationState> states) {
		super();
		this.stationName=stationName;
		this.states = states;
		stateResults=new ArrayList<>();
		readDataIntoStateResults();
		
		int periodNum=12;
		if(states.get(0).getTimeLength()>2000000 && states.get(0).getTimeLength()<3000000)//秒数在一月之内
		{
			 periodNum=12;//一年12个月
			 generatePeriodAvg(periodNum);
		}
		else if(states.get(0).getTimeLength()>500000 && states.get(0).getTimeLength()<2000000)
		{
			periodNum=36;//一年36个旬
			generatePeriodAvg(periodNum);
		}
		else if(states.get(0).getTimeLength()<500000)//天
		{
			
		}
		
		
	}

	
	
	
	
	/**
	 * 函数生成一年各月或各旬的多年平均数据
	 * @param periodNum  一年多少个月/旬
	 */
	private void generatePeriodAvg(int periodNum) {
		OutputPeriodAvg avg=new OutputPeriodAvg();
		avg.setStationName(stationName);
		Map<OutputVarsType,double[]> periodavgs=new HashMap<>();
		periodavgs.put(OutputVarsType.GENERATION, calMulYearsAvgPerPeriod(OutputVarsType.GENERATION,periodNum));
		periodavgs.put(OutputVarsType.HEAD_GROSS, calMulYearsAvgPerPeriod(OutputVarsType.HEAD_GROSS,periodNum));
		periodavgs.put(OutputVarsType.HEAD_LOSS, calMulYearsAvgPerPeriod(OutputVarsType.HEAD_LOSS,periodNum));
		periodavgs.put(OutputVarsType.HEAD_NET, calMulYearsAvgPerPeriod(OutputVarsType.HEAD_NET,periodNum));
		periodavgs.put(OutputVarsType.INFLOW, calMulYearsAvgPerPeriod(OutputVarsType.INFLOW,periodNum));
		periodavgs.put(OutputVarsType.LEVEL_BEGIN, calMulYearsAvgPerPeriod(OutputVarsType.LEVEL_BEGIN,periodNum));
		periodavgs.put(OutputVarsType.LEVEL_DOWN, calMulYearsAvgPerPeriod(OutputVarsType.LEVEL_DOWN,periodNum));
		periodavgs.put(OutputVarsType.LEVEL_END, calMulYearsAvgPerPeriod(OutputVarsType.LEVEL_END,periodNum));
		periodavgs.put(OutputVarsType.LEVEL_UP, calMulYearsAvgPerPeriod(OutputVarsType.LEVEL_UP,periodNum));
		periodavgs.put(OutputVarsType.OUTFLOW, calMulYearsAvgPerPeriod(OutputVarsType.OUTFLOW,periodNum));
		periodavgs.put(OutputVarsType.OUTFLOW_GENERATION, calMulYearsAvgPerPeriod(OutputVarsType.OUTFLOW_GENERATION,periodNum));
		periodavgs.put(OutputVarsType.OUTFLOW_SURPLUS, calMulYearsAvgPerPeriod(OutputVarsType.OUTFLOW_SURPLUS,periodNum));
		periodavgs.put(OutputVarsType.OUTPUT, calMulYearsAvgPerPeriod(OutputVarsType.OUTPUT,periodNum));
		avg.setAvgs(periodavgs);
		setAvgs(avg);
	}

	/**
	 *  生成List《StationSimuResultModel》 stateResults数据
	 */
	private void readDataIntoStateResults()
	{
		for(int i=0;i<states.size();i++)
		{
			StationSimuResultModel ssrm=new StationSimuResultModel();
			ssrm.setStationName(stationName);
			ssrm.setTimeStart(states.get(i).getTimeStart());
			ssrm.setTimeEnd(states.get(i).getTimeEnd());
			ssrm.setTimeLength(states.get(i).getTimeLength());
			Map<OutputVarsType,Double> resultsTemp=new HashMap<OutputVarsType,Double>();
			resultsTemp.put(OutputVarsType.GENERATION, states.get(i).getGeneration());
			resultsTemp.put(OutputVarsType.HEAD_GROSS, states.get(i).getHeadGross());
			resultsTemp.put(OutputVarsType.HEAD_LOSS, states.get(i).getHeadLoss());
			resultsTemp.put(OutputVarsType.HEAD_NET, states.get(i).getHeadPure());
			resultsTemp.put(OutputVarsType.INFLOW, states.get(i).getInflowReal());
			resultsTemp.put(OutputVarsType.LEVEL_BEGIN, states.get(i).getLevelBegin());
			resultsTemp.put(OutputVarsType.LEVEL_DOWN, states.get(i).getLevelDown());
			resultsTemp.put(OutputVarsType.LEVEL_END, states.get(i).getLevelEnd());
			resultsTemp.put(OutputVarsType.LEVEL_UP, (states.get(i).getLevelBegin()+states.get(i).getLevelEnd())/2);
			resultsTemp.put(OutputVarsType.OUTFLOW, states.get(i).getOutflow());
			resultsTemp.put(OutputVarsType.OUTFLOW_GENERATION, states.get(i).getOutflowGeneration());
			resultsTemp.put(OutputVarsType.OUTFLOW_SURPLUS, states.get(i).getOutflowDesert());
			resultsTemp.put(OutputVarsType.OUTPUT, states.get(i).getOutput());
			ssrm.setResults(resultsTemp);
			stateResults.add(ssrm);
		}
	}


	/**
	 * @param sign OutputVarsType里选择一个变量来输出
	 * @return  该变量的所有时段数据
	 */
	public double[] choseOneVar(OutputVarsType sign)
	{
		double[] var=new double[stateResults.size()];
		for(int i=0;i<stateResults.size();i++)
		{
			var[i]=stateResults.get(i).getResults().get(sign);
		}
		return var;
	}
	
	/**
	 * @param percent  保证率
	 * @return   统计保证出力（多年出力月保证率）
	 */
	public double statisticsWarrantedOutput(double percent)
	{
		double warrantedOutput=0;
		double[] outputs=new double[stateResults.size()];
		for(int i=0;i<outputs.length;i++)
		{
			outputs[i]=stateResults.get(i).getResults().get(OutputVarsType.OUTPUT);
		}
		warrantedOutput=BasicMathMethods.calFrequencyValue(outputs, percent);
		return warrantedOutput;
	}
	/**
	 * @param percent  保证率
	 * @return   统计保证出力（年保证率），取每年最小时段出力，然后排频
	 */
	public double statisticsWarrantedOutputMonth(double percent)
	{
		double warrantedOutput=0;
		double[] outputs=new double[stateResults.size()];
		for(int i=0;i<outputs.length;i++)
		{
			outputs[i]=stateResults.get(i).getResults().get(OutputVarsType.OUTPUT);
		}
		double[][] outputs2D=new double[outputs.length/12][12];//二维的数据，每行代表一年，每列是一个月
		for(int i=0;i<outputs2D.length;i++){
			for(int j=0;j<12;j++){
				outputs2D[i][j]=outputs[i*12+j];
			}
		}
		double[] minEveryYear=new double[outputs2D.length];
		for(int i=0;i<outputs2D.length;i++){
			minEveryYear[i]=BasicMathMethods.minOf1DArray(outputs2D[i]);
		}
		warrantedOutput=BasicMathMethods.calFrequencyValue(minEveryYear, percent);
		return warrantedOutput;
	}
	
	/**
	 * @param percent  保证率
	 * @return   统计保证出力（多年出力月保证率）
	 */
	public double statisticsMinOutputMonth()
	{
		double minOutput=0;
		double[] outputs=new double[stateResults.size()];
		for(int i=0;i<outputs.length;i++)
		{
			outputs[i]=stateResults.get(i).getResults().get(OutputVarsType.OUTPUT);
		}
		double[][] outputs2D=new double[outputs.length/12][12];//二维的数据，每行代表一年，每列是一个月
		for(int i=0;i<outputs2D.length;i++){
			for(int j=0;j<12;j++){
				outputs2D[i][j]=outputs[i*12+j];
			}
		}
		double[] minEveryYear=new double[outputs2D.length];
		for(int i=0;i<outputs2D.length;i++){
			minEveryYear[i]=BasicMathMethods.minOf1DArray(outputs2D[i]);
		}
		minOutput=BasicMathMethods.minOf1DArray(minEveryYear);
		return minOutput;
	}
	
	/**
	 * @param sign  OutputVarsType里选择一个变量来输出
	 * @param numsAPeriod  一年的时段数
	 * @return  每年的该变量数据
	 */
	public double[][] showYearsData(int numsAPeriod,OutputVarsType sign)
	{
		double[][] var=new double[stateResults.size()/numsAPeriod][];
		var=BasicMathMethods.array1DTo2D(choseOneVar(sign), numsAPeriod);
		return var;
	}
	
	/**
	 * @param sign  OutputVarsType里选择一个变量来输出
	 * @return  所有数据一年一月占一行，组成一个二维数组
	 */
	public double[][] showMonthsData(OutputVarsType sign)
	{
		int years=states.get(states.size()-1).getTimeStart().getYear()-states.get(0).getTimeStart().getYear();
		double[][] var=new double[years*12][];
		double[] data=choseOneVar(sign);
		
		LocalDateTime[] monthStarts=new LocalDateTime[var.length];
		LocalDateTime[] monthEnds=new LocalDateTime[var.length];
		int[] startIndexs=new int[var.length];
		
		for(int i=0;i<var.length;i++)
		{
			if(i==0)
			{
				monthStarts[i]=states.get(0).getTimeStart();
				startIndexs[i]=0;
				monthEnds[i]=states.get(0).getTimeStart().plusMonths(1).minusDays(1);//DateUtil.addDay(DateUtil.addMonth(states.get(0).getTimeStart(), 1), -1);
			}
			else
			{
				monthStarts[i]=monthStarts[i-1].plusMonths(1);//DateUtil.addMonth(monthStarts[i-1], 1);
				startIndexs[i]=Period.between(monthStarts[i-1].toLocalDate(),monthStarts[i].toLocalDate()).getDays()+startIndexs[i-1];//DateUtil.getIntervalDays(monthStarts[i-1],monthStarts[i])+startIndexs[i-1];
				monthEnds[i]=monthStarts[i-1].plusMonths(2).minusDays(1);//DateUtil.addDay(DateUtil.addMonth(monthStarts[i-1], 2),-1);
			}
			
			List<Double> a=new ArrayList<>();
			int tempdays=Period.between(monthStarts[i].toLocalDate(),monthEnds[i].toLocalDate()).getDays()+1;//DateUtil.getIntervalDays(monthStarts[i], monthEnds[i])+1;
			for(int j=startIndexs[i];j<startIndexs[i]+tempdays;j++)
			{
				a.add(data[j]);
			}
			var[i]=BasicMathMethods.listToDouble(a);
		}
		return var;
	}
	
	/**
	 * @param numsAPeriod
	 * @return  根据选择的OutputVarsType对象，计算每个时段的多年平均值
	 */
	public double[] calMulYearsAvgPerPeriod(OutputVarsType type,int numsAPeriod)
	{
		double[][] genes=showYearsData(numsAPeriod,type);
		double[][] trans=BasicMathMethods.transpose(genes);
		double[] perPeriod=new double[trans.length];
		for(int i=0;i<trans.length;i++)
		{
			perPeriod[i]=(StatUtils.mean(trans[i]));
		}
		return perPeriod;
	}
	
	
	/**
	 * @param numsAPeriod 一年里的时段数目
	 * @return  根据选择的OutputVarsType对象，计算多年平均值
	 */
	public double calMulYearsAvg(OutputVarsType type,int numsAPeriod)
	{
		double avg=0;
		double[][] genes=showYearsData(numsAPeriod,type);
		double[] perYear=new double[genes.length];
		for(int i=0;i<genes.length;i++)
		{
			perYear[i]=(StatUtils.sum(genes[i]));
		}
		avg=StatUtils.mean(perYear);
		return avg;
	}
	
	/**
	 * @param numsAPeriod
	 * @return  计算多年平均发电量  单位为亿千瓦时
	 */
	public double calMulYearsAvgPower(int numsAPeriod)
	{
		double avg=0;
		if(numsAPeriod<35)
		{
			double[][] genes=showYearsData(numsAPeriod,OutputVarsType.GENERATION);
			double[] perYear=new double[genes.length];
			for(int i=0;i<genes.length;i++)
			{
				perYear[i]=(StatUtils.sum(genes[i]))/10000;//转单位为亿千瓦时
			}
			avg=StatUtils.mean(perYear);
		}
		else if(numsAPeriod>300)//按天来
		{
			double[] genes=choseOneVar(OutputVarsType.GENERATION);
			
			int years=states.get(states.size()-1).getTimeStart().getYear()-states.get(0).getTimeStart().getYear();
			double[] perYear=new double[years];
			
			LocalDateTime[] yearStarts=new LocalDateTime[years];
			LocalDateTime[] yearEnds=new LocalDateTime[years];
			int[] startIndexs=new int[years];
			
			for(int i=0;i<years;i++)
			{
				if(i==0)
				{
					yearStarts[i]=states.get(0).getTimeStart();
					startIndexs[i]=0;
					yearEnds[i]=states.get(0).getTimeStart().plusYears(1).minusDays(1);//DateUtil.addDay(DateUtil.addYear(states.get(0).getTimeStart(), 1), -1);
				}
				else
				{
					yearStarts[i]=yearStarts[i-1].plusYears(1);//DateUtil.addYear(yearStarts[i-1], 1);
					startIndexs[i]=Period.between(yearStarts[i-1].toLocalDate(),yearStarts[i].toLocalDate()).getDays();//DateUtil.getIntervalDays(yearStarts[i-1],yearStarts[i])+startIndexs[i-1];
					yearEnds[i]=yearEnds[i-1].plusYears(1);//DateUtil.addYear(yearEnds[i-1], 1);
				}
				List<Double> a=new ArrayList<>();
				int tempdays=Period.between(yearStarts[i].toLocalDate(),yearEnds[i].toLocalDate()).getDays()+1;//DateUtil.getIntervalDays(yearStarts[i], yearEnds[i])+1;
				for(int j=startIndexs[i];j<startIndexs[i]+tempdays;j++)
				{
					a.add(genes[j]);
				}
				double[] ayear=BasicMathMethods.listToDouble(a);
				perYear[i]=StatUtils.sum(ayear);
			}
			avg=StatUtils.mean(perYear);
			
		}
		return avg;
	}
	
	
	/**
	 * @return  计算某些时段（月或者旬）电量和的多年平均电量
	 */
	public double calSomePeriodsYearsAvgPower(int numsAPeriod,int[] periodNums)
	{
		double avg=0;
		
		if(numsAPeriod<300)
		{
			int periodStart=0;
			if(numsAPeriod==12)
				periodStart=states.get(0).getTimeStart().getMonthValue();
			else
				periodStart=HydroDateUtil.getDecad(states.get(0).getTimeStart().toLocalDate());
		
			Frequency f = new Frequency();
			for(int i=0;i<periodNums.length;i++)
			{
				f.addValue(periodNums[i]);
			}
			List<Integer> indextemp=new ArrayList<>();
			int[] periodnumtemp=new int[numsAPeriod];
			for(int i=0;i<numsAPeriod;i++)
			{
				periodnumtemp[i]=(periodStart-1+i)%numsAPeriod+1;
				if(f.getCount(periodnumtemp[i])>0)
				{
					indextemp.add(i);
				}
			}
			
			double[][] genes=showYearsData(numsAPeriod,OutputVarsType.GENERATION);
			double[][] chosen=new double[genes.length][indextemp.size()];
			for(int i=0;i<chosen.length;i++)
			{
				for(int j=0;j<indextemp.size();j++)
				{
					chosen[i][j]=genes[i][indextemp.get(j)];
				}
			}
			
			
			double[] perYear=new double[chosen.length];
			for(int i=0;i<chosen.length;i++)
			{
				perYear[i]=(StatUtils.sum(chosen[i]))/10000;//转单位为亿千瓦时
			}
			avg=StatUtils.mean(perYear);
		}
		else//日的时候，函数参数给的时段号是月份
		{
			double[] genes=choseOneVar(OutputVarsType.GENERATION);
			
			int years=states.get(states.size()-1).getTimeStart().getYear()-states.get(0).getTimeStart().getYear();
			double[] perYear=new double[years];
			
			LocalDateTime[] yearStarts=new LocalDateTime[years];
			LocalDateTime[] yearEnds=new LocalDateTime[years];
			int[] startIndexs=new int[years];
			
			for(int i=0;i<years;i++)
			{
				if(i==0)
				{
					yearStarts[i]=states.get(0).getTimeStart();
					startIndexs[i]=0;
					yearEnds[i]=states.get(0).getTimeStart().plusYears(1).minusDays(1);//DateUtil.addDay(DateUtil.addYear(states.get(0).getTimeStart(), 1), -1);
				}
				else
				{
					yearStarts[i]=yearStarts[i-1].plusYears(1);//DateUtil.addYear(yearStarts[i-1], 1);
					startIndexs[i]=Period.between(yearStarts[i-1].toLocalDate(),yearStarts[i].toLocalDate()).getDays()+startIndexs[i-1];//DateUtil.getIntervalDays(yearStarts[i-1],yearStarts[i])+startIndexs[i-1];
					yearEnds[i]=yearEnds[i-1].plusYears(1);//DateUtil.addYear(yearEnds[i-1], 1);
				}
				List<Double> a=new ArrayList<>();
				int tempdays=Period.between(yearStarts[i].toLocalDate(),yearEnds[i].toLocalDate()).getDays()+1;//DateUtil.getIntervalDays(yearStarts[i], yearEnds[i])+1;
				for(int j=startIndexs[i];j<startIndexs[i]+tempdays;j++)
				{
					LocalDateTime temp=yearStarts[i].plusDays(j-startIndexs[i]);//DateUtil.addDay(yearStarts[i],j-startIndexs[i]);
					if(BasicMathMethods.contains(periodNums, temp.getMonthValue()))
					{
						a.add(genes[j]);
					}

				}
				double[] ayear=BasicMathMethods.listToDouble(a);
				perYear[i]=StatUtils.sum(ayear);
			}
			avg=StatUtils.mean(perYear);
		}
		
		return avg;
	}
	
	
}
