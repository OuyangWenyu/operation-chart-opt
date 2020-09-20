package me.owenyy.optimal.dp;

import java.util.ArrayList;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;

/**
 *  一维动态规划（此种说法参照文章：http://hawstein.com/posts/dp-novice-to-advanced.html）
 *  以求单个水库水位为第i个离散值的最大发电量为例进行代码结构构思
 */
public abstract class DP1D {
	private double[] precisions;//每个阶段的离散精度
	protected int stageNumber;//总共的阶段数，阶段编号从0开始计
	protected Interval[] states_max_mins;//各阶段取值范围，由于是离散值且给定上下界可以取到，按闭区间处理较方便
	protected State[][] states;//一维表示阶段数，第二维表示各状态
	protected double[] bestStateValuesChoosen;
	protected double benefitMaxGlobal;
	protected int [] bestStateIndiceChoosen;
	public DP1D(int stageNumber, Interval[] states_max_mins, double[] precisions) {
		super();
		this.stageNumber = stageNumber;
		this.states_max_mins = states_max_mins;
		this.precisions = precisions;
	}
	/**
	 * @return the stageNumber
	 */
	public int getStageNumber() {
		return stageNumber;
	}
	/**
	 * @param stageNumber the stageNumber to set
	 */
	public void setStageNumber(int stageNumber) {
		this.stageNumber = stageNumber;
	}
	/**
	 * @return the states
	 */
	public State[][] getStates() {
		return states;
	}
	/**
	 * @param states the states to set
	 */
	public void setStates(State[][] states) {
		this.states = states;
	}
	public Interval[] getStates_max_mins() {
		return states_max_mins;
	}
	public void setStates_max_mins(Interval[] states_max_mins) {
		this.states_max_mins = states_max_mins;
	}
	public double[] getBestStateValuesChoosen() {
		return bestStateValuesChoosen;
	}
	public void setBestStateValuesChoosen(double[] bestStateValuesChoosen) {
		this.bestStateValuesChoosen = bestStateValuesChoosen;
	}
	public double getBenefitMaxGlobal() {
		return benefitMaxGlobal;
	}
	public void setBenefitMaxGlobal(double benefitMaxGlobal) {
		this.benefitMaxGlobal = benefitMaxGlobal;
	}
	/**
	 * 为了达到目的，正序进行递推运算，存储各阶段最优状态信息
	 */
	public void positiveSequenceToTarget()
	{
		//从最简单子问题开始递推
		states=new State[stageNumber][];
		for(int i=0;i<stageNumber;i++)
		{
			adjustMaxMin(i);//调整当前阶段状态离散值上下限
			initialAStage(i,states_max_mins[i].getInf(),states_max_mins[i].getSup(),precisions[i]);//初始化该阶段各状态量
			if(i==0) continue;
			for(int j=0;j<states[i].length;j++){
				State[] lastStatesOfNowState=adjustMaxMin(i,states[i][j],states[i-1]);
				calMaxBenifitsInAState(i,states[i][j],lastStatesOfNowState);//根据上一个阶段的最优值，计算该阶段各状态量最优值
			}
		}
	}
	/**
	 * 调整当前状态states[i][j]对应的顺序上一个阶段states[i-1]（即逆序下一个阶段）离散值上下限
	 * @param nowStageIndex
	 * @param state
	 * @param statesLastStage
	 * @return 修正边界后的statesLastStage
	 */
	protected abstract State[] adjustMaxMin(int nowStageIndex, State state, State[] statesLastStage);
	/**
	 * 根据需求调整该阶段的离散范围上下限
	 * @param interval
	 */
	protected abstract void adjustMaxMin(int stageIndex);
	/**
	 * 计算当前阶段当前状态的各个benefit值，并选择与本状态匹配最优的上阶段状态值更新本状态的各个属性
	 * @param stageIndex 使用此函数，stageIndex从1开始，即第二个阶段
	 * @param state
	 * @param statesLastStage
	 */
	protected abstract void calMaxBenifitsInAState(int stageIndex, State state, State[] statesLastStage);
	/**
	 * @param min
	 * @param max
	 * @param precision
	 * @return 第一个阶段的初始化，初始化各状态值，从最大状态值到最小状态值，给定离散精度
	 */
	private void initialAStage(int stageIndex,double min,double max,double precision){
		if(Math.abs(min-max)<precision){
			states[stageIndex]=new State[1];
			states[stageIndex][0]=new State();
			states[stageIndex][0].setIndex(0);
			states[stageIndex][0].setLastStageIndexBest(-1);
			states[stageIndex][0].setLastStageIndicesBest(null);
			states[stageIndex][0].setStateValue(max);
			states[stageIndex][0].setBenefitMax(0);
		}
		else if(min-max>precision)
			throw new NumberIsTooSmallException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL,
                    max, min, true);
		else{
			ArrayList<Double> temps=new ArrayList<Double>();
			temps.add(max);
			while(temps.get(temps.size()-1)-precision>min){
				double temp=temps.get(temps.size()-1)-precision;
				temps.add(temp);
			}
			temps.add(min);	
			states[stageIndex]=new State[temps.size()];
			for(int j=0;j<temps.size();j++){
				states[stageIndex][j]=new State();
				states[stageIndex][j].setIndex(j);
				states[stageIndex][j].setLastStageIndexBest(-1);
				states[stageIndex][j].setLastStageIndicesBest(null);
				states[stageIndex][j].setStateValue(temps.get(j).doubleValue());
				states[stageIndex][j].setBenefitMax(0);
			}
		}
	}
	
	/**************逆序取优*************/
	/**
	 * 从正序寻优后的states里面把需要的数据取出来
	 */
	public void negativeSequenceToTarget()
	{
		for(int i=states.length-1;i>=0;i--){
			if(i==states.length-1)
				findBestBenefit(states[i]);
			else{
				findBestState(i,bestStateIndiceChoosen[i]);
			}
		}
	}
	/**
	 * 逆序把最优轨迹取出，根据顺序下一个阶段，即逆序上一个阶段，把本阶段的最优解取出来，更新bestStateValuesChoosen、bestStateIndiceChoosen
	 * @param bestStateIndexFromNextStage 
	 * @param statesPresentStage
	 */
	private void findBestState(int indexPresentStage,int bestStateIndexFromNextStage) {
		int i=indexPresentStage;
		int j=bestStateIndexFromNextStage;
		bestStateValuesChoosen[i]=states[i][j].getStateValue();
		if(i>0)
			bestStateIndiceChoosen[i-1]=states[i][j].getLastStageIndexBest();
	}
	private void findBestBenefit(State[] statesFinalStage) {
		benefitMaxGlobal=statesFinalStage[0].getBenefitMax();
		bestStateValuesChoosen=new double[stageNumber];
		bestStateValuesChoosen[stageNumber-1]=statesFinalStage[0].getStateValue();
		bestStateIndiceChoosen=new int[stageNumber];
		bestStateIndiceChoosen[stageNumber-1]=0;
		bestStateIndiceChoosen[stageNumber-2]=statesFinalStage[0].getLastStageIndexBest();
		for(int i=1;i<statesFinalStage.length;i++){
			if(statesFinalStage[i].getBenefitMax()>benefitMaxGlobal){
				benefitMaxGlobal=statesFinalStage[i].getBenefitMax();
				bestStateValuesChoosen[stageNumber-1]=statesFinalStage[i].getStateValue();
				bestStateIndiceChoosen[stageNumber-1]=i;
				bestStateIndiceChoosen[stageNumber-2]=statesFinalStage[i].getLastStageIndexBest();
			}
		}
	}
}
