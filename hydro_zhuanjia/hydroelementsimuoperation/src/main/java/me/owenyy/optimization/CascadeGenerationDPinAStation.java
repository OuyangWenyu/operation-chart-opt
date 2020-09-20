package me.owenyy.optimization;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;

import com.wenyu.factory.topology.Series;
import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;
import com.wenyu.hydroelements.operation.statistics.BasicMathMethods;

import me.owenyy.optimal.dp.DP1D;
import me.owenyy.optimal.dp.State;
import me.owenyy.simuoperation.MultiStationTimeOpe;

public class CascadeGenerationDPinAStation extends DP1D{
	private int stationDPIndex;
	private Series series;
	private Series[] seriesNow;
	private MultiStationTimeOpe msto;
	private double[] levelBegins;
	//private MultiTimeLevelControl mtlc=new MultiTimeLevelControl();
	public CascadeGenerationDPinAStation(int stageNumber, Interval[] states_max_mins, double[] precisions,
			int stationDPIndex,Series series,MultiStationTimeOpe msto, double[] levelBegins) {
		super(stageNumber, states_max_mins, precisions);
		this.stationDPIndex=stationDPIndex;
		this.series=series;
		this.msto=msto;
		this.levelBegins=levelBegins;
	}
	/**
	 * @return the series
	 */
	public Series getSeries() {
		return series;
	}

	/**
	 * @param series the series to set
	 */
	public void setSeries(Series series) {
		this.series = series;
	}

	/**
	 * @return the msto
	 */
	public MultiStationTimeOpe getMsto() {
		return msto;
	}

	/**
	 * @param msto the msto to set
	 */
	public void setMsto(MultiStationTimeOpe msto) {
		this.msto = msto;
	}

	public void dp(){
		seriesNow=new Series[stageNumber-1];
		for(int i=0;i<stageNumber-1;i++)
			seriesNow[i]=series.get(i);
		
		positiveSequenceToTarget();
		negativeSequenceToTarget();
		System.out.println(benefitMaxGlobal);
	}

	/* 首先需要判断statesLastStage里的state哪些不能到state，重新修正边界，然后再根据新的边界计算benefit
	 * @see me.owenyy.optimal.dp.DP1D#calMaxBenifitsInAState(int, me.owenyy.optimal.dp.State, me.owenyy.optimal.dp.State[])
	 */
	@Override
	protected void calMaxBenifitsInAState(int stageIndex, State state, State[] statesLastStage) {
		double[] levelBeginsNow=new double[levelBegins.length];
		System.arraycopy(levelBegins, 0, levelBeginsNow, 0, levelBeginsNow.length);
		Series now=seriesNow[stageIndex-1];//series.get(stageIndex-1);
		now.getLeadStation().getHydroStation().getHsStates().get(0).setLevelEnd(state.getStateValue());
		double max=0;
		ArrayList<Integer> temp=new ArrayList<Integer>();
		for(int i=0;i<statesLastStage.length;i++){
			now.getLeadStation().getHydroStation().getHsStates().get(0).setLevelBegin(statesLastStage[i].getStateValue());
			levelBeginsNow[0]=statesLastStage[i].getStateValue();
			for(int j=0;j<series.getStations().size();j++){//判断古水的时段初水位
				if(series.getStations().get(j).getHsSpec().getLevelFloodLimiting()>0){
					 if(((stageIndex-1)%12>=0) &&((stageIndex-1)%12<=4)){//6-10月初都是2245
						 levelBeginsNow[4]=2245;
					 }//6月开始
					 else 
						 levelBeginsNow[4]=2265;
				}
			}
			double[] levelEnds=new double[1];
			levelEnds[0]=state.getStateValue();
			msto.seriesRunoffStationsSimuOpera(now,levelBeginsNow,levelEnds);//古水升不到2265就会出力很小
			double nowBenefit=now.getGeneration(0)/10000;
			//需要给保证出力加惩罚
			double outputGua=series.getLeadStation().getHydroStation().getHsSpec().getOutputGuaranteed();
			int outputGuaInte=(int)(outputGua+1);//取整，便于后面运算满足保证率
			double outputLeadNow=now.getOutput(0);
			if(outputLeadNow<outputGuaInte) 
				nowBenefit=nowBenefit-5*(Math.pow((outputGuaInte-outputLeadNow),2));
			if(nowBenefit+statesLastStage[i].getBenefitMax()>max){
				max=nowBenefit+statesLastStage[i].getBenefitMax();
				state.setBenefitMax(max);
				state.setLastStageIndexBest(i);
			}
			else if(nowBenefit+statesLastStage[i].getBenefitMax()==max) temp.add(i);
		}
		int[] lastStageIndicesBest =new int[temp.size()];
		for(int i=0;i<lastStageIndicesBest.length;lastStageIndicesBest[i]=temp.get(i),i++);
		state.setLastStageIndicesBest(lastStageIndicesBest);
	}
	
	/** 根据下泄的边界值，来确定上游时段末水位的范围，结合时段末最大最小水位来确定时段的水位范围；
	 *  然后两个范围取交集得到最终的范围。
	 * @see me.owenyy.optimal.dp.DP1D#adjustMaxMin(int, org.apache.commons.math3.geometry.euclidean.oned.Interval)
	 */
	@Override
	protected void adjustMaxMin(int stageIndex) {
		if(stageIndex==0) return;
		double lastStageUpper=states[stageIndex-1][0].getStateValue();
		double lastStageLower=states[stageIndex-1][states[stageIndex-1].length-1].getStateValue();
		StationCurve stationCurve=series.getLeadStation().getHydroStation().getStationCurves();
		
		double V0=stationCurve.getCapacityByLevel(lastStageUpper);
		double inflow=series.getLeadStation().getHydroStation().getHsStates().get(stageIndex-1).getInflowReal();
		double outflowMin=0;
		long timelength=series.getLeadStation().getHydroStation().getHsStates().get(stageIndex-1).getTimeLength();
		double discreteMax=states_max_mins[stageIndex].getSup();
		try {
			double temp= BasicMathMethods.waterBalanceCalculate(
					V0, inflow, outflowMin, timelength, 2);
			temp=stationCurve.getLevelByCapacity(temp);
			discreteMax=Math.min(discreteMax, temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		double discreteMin=states_max_mins[stageIndex].getInf();
		double outflowMax=10000;
		V0=stationCurve.getCapacityByLevel(lastStageLower);
		try {
			double temp= BasicMathMethods.waterBalanceCalculate(
					V0, inflow, outflowMax, timelength, 2);
			temp=stationCurve.getLevelByCapacity(temp);
			discreteMin=Math.max(discreteMin, temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		states_max_mins[stageIndex]=new Interval(discreteMin, discreteMax);
	}
	/* 判断水位能否满足
	 * @see me.owenyy.optimal.dp.DP1D#adjustMaxMin(int, me.owenyy.optimal.dp.State, me.owenyy.optimal.dp.State[])
	 */
	@Override
	protected State[] adjustMaxMin(int nowStageIndex, State state, State[] statesLastStage) {
		if(nowStageIndex==11)
			System.out.print("");
		double stageLevel=state.getStateValue();
		StationCurve stationCurve=series.getLeadStation().getHydroStation().getStationCurves();
		double Vt=stationCurve.getCapacityByLevel(stageLevel);
		double inflow=series.getLeadStation().getHydroStation().getHsStates().get(nowStageIndex-1).getInflowReal();
		double outflowMin=0;
		long timelength=series.getLeadStation().getHydroStation().getHsStates().get(nowStageIndex-1).getTimeLength();
		double discreteMin=statesLastStage[statesLastStage.length-1].getStateValue();
		try {
			double temp= BasicMathMethods.waterBalanceCalculate(
					inflow, outflowMin, Vt, timelength, 3);
			temp=stationCurve.getLevelByCapacity(temp);
			discreteMin=Math.max(discreteMin, temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		double outflowMax=10000;
		double discreteMax=statesLastStage[0].getStateValue();
		try {
			double temp= BasicMathMethods.waterBalanceCalculate(
					inflow, outflowMax, Vt, timelength, 3);
			temp=stationCurve.getLevelByCapacity(temp);
			discreteMax=Math.min(discreteMax, temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int srcPos=0;
		int srcEndPos=0;
		if(statesLastStage.length==1) {srcPos=0;srcEndPos=0;}
		else{
		for(int i=0;i<statesLastStage.length-1;i++){//最大值向下边界取
			if(statesLastStage[i].getStateValue()==discreteMax)
				srcPos=i;
			else if(statesLastStage[i].getStateValue()>discreteMax 
					&& statesLastStage[i+1].getStateValue()<=discreteMax)
				srcPos=i+1;
			if((statesLastStage[i].getStateValue()>=discreteMin 
					&& statesLastStage[i+1].getStateValue()<discreteMin))//最小值向上边界取
				srcEndPos=i;
			else if(statesLastStage[i+1].getStateValue()==discreteMin)
				srcEndPos=i+1;
		}}
		State[] dest=new State[srcEndPos-srcPos+1];
		System.arraycopy(statesLastStage, srcPos, dest, 0, dest.length);
		return dest;
	}

}
