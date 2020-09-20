package me.owenyy.optimal.dp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;

import com.wenyu.hydroelements.operation.statistics.BasicMathMethods;

/**
 *  二维动态规划（此种说法参照文章：http://hawstein.com/posts/dp-novice-to-advanced.html）
 *  以求k台机组出i个单位出力的最小耗流量为例进行代码结构构思
 *  这里的二维是阶段间的转移当做是一步一步的，而阶段内的状态转移是可以跨越的
 */
public abstract class DP2D {
	/*例如厂内寻求耗流最小，目标出力要进行离散  假如总共4台机组出10个出力是最后的目标 ，那么问题就是4台机10个出力的最小耗流Q4（10），
	 * 寻求其子问题，递推方程也比较清楚了，就是：
	 * k台机组出i个单位出力的最小耗流=
	 * min{k-1台机组出i个单位出力的最小耗流，k台机组出i-m个单位出力的最小耗流耗流加上k台机组中多出m个出力产生的最小耗流}
	*/
	private int stageNumber;//总共的阶段数，二维DP的第一维个数
	/**
	 * 全局所有状态值（寻优）
	 */
	private List<OneDimensionalStates> globalStates;
	/**
	 * 单阶段单状态的代价值，以机组组合为例，是各台机组出一定的出力所需的耗流量
	 */
	private List<List<State2D>> singleStateCosts;
	
	public DP2D(int stageNumber) {
		super();
		this.stageNumber = stageNumber;
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
	 * @return the globalStates
	 */
	public List<OneDimensionalStates> getGlobalStates() {
		return globalStates;
	}
	/**
	 * @param globalStates the globalStates to set
	 */
	public void setGlobalStates(List<OneDimensionalStates> globalStates) {
		this.globalStates = globalStates;
	}
	/**
	 * @return the singleStateCosts
	 */
	public List<List<State2D>> getSingleStateCosts() {
		return singleStateCosts;
	}
	/**
	 * @param singleStateCosts the singleStateCosts to set
	 */
	public void setSingleStateCosts(List<List<State2D>> singleStateCosts) {
		this.singleStateCosts = singleStateCosts;
	}
	
	/**************************正序寻优*****************************************/
	/**
	 * 输入必要的原始数据，初始化singleStateCosts数据
	 */
	public abstract List<List<State2D>> inputEssensialData();
	/**
	 * 为了达到目的，正序进行递推运算，存储各阶段最优状态信息
	 */
	public void positiveSequenceToTarget()
	{
		globalStates=new ArrayList<OneDimensionalStates>();
		singleStateCosts=inputEssensialData();
		OneDimensionalStates firstDimension=new OneDimensionalStates(0);
		//从最简单子问题开始递推
		for(int i=0;i<stageNumber;i++)
		{
			if(i==0)
			{
				firstDimension=initialFirstDimension();
				globalStates.add(firstDimension);
				continue;
			}
			OneDimensionalStates nowStage=new OneDimensionalStates(i);
			nowStage.setStateValueInterval(calStateValueInterval(globalStates.get(i-1)));
			
			//有了取值范围以后，可以进行均匀离散，也可以进行不均匀的数据离散而是直接赋值，都可以，override的函数来定
			nowStage.setAllStatesIn1D(initialAllStatesIn1D(nowStage.getStateValueInterval(),i));
			nowStage.setStatesNow(initialStatesNowIn1D(nowStage.getStateValueInterval(),i));
			stateTransition(globalStates.get(i-1),nowStage);
			globalStates.add(nowStage);
		}
	}
	
	/**
	 * 在寻优前，应该对被寻优阶段的各个状态值进行初始化，一般现将状态值和cost值都初始化为0
	 * @return
	 */
	public abstract List<State2D> initialStatesNowIn1D(Interval stateValueInterval,int index);

	/**
	 * @param stateValueInterval  给定约束范围，以防出界
	 * @param i 当前的nowStage所处的第一维的index
	 * @return 在约束范围内，采用要么离散，要么外部赋值的方式给nowStage的各状态赋value值，override函数决定
	 */
	public abstract List<State2D> initialAllStatesIn1D(Interval stateValueInterval, int i);
	/**
	 * 利用本维的数据及上一维的最优状态情况，求出本维寻优取值的范围
	 * @param lastDimension  上一维的最优状态情况
	 * @return 本维寻优取值的范围
	 */
	public Interval calStateValueInterval(OneDimensionalStates lastDimension)
	{		
		Interval nowInterval=calNowStateValueRange(lastDimension,singleStateCosts.get(lastDimension.getIndex()+1));
		return nowInterval;
	}
	/**
	 * @param lastDimension  上一维的最优状态值
	 * @param list  该维的数据
	 * @return  本维寻优取值的范围
	 */
	public abstract Interval calNowStateValueRange(OneDimensionalStates lastDimension, List<State2D> list);
	
	/**
	 * 初始化第一维的所有状态，也即求出第一维的各最优状态，要同时存储单阶段最优值和全阶段最优值
	 * @return 
	 */
	public abstract OneDimensionalStates initialFirstDimension() ;
	
	//状态转移方程
	/**
	 * 正序寻优的过程，两个阶段的取值范围，离散的各个值都已经有了，直接执行正序寻优过程
	 * 两阶段择优合并成一个阶段的时候，不仅要把整体的状态值存储下来，如何在两个状态间进行状态值分配的情况也要保存下来！！
	 * @param lastDimension
	 * @param nowDimension
	 */
	public void stateTransition(OneDimensionalStates lastDimension,OneDimensionalStates nowDimension)
	{
		//以机组出力为例，k台机组出i个单位出力的最小耗流=min{k-1台机组出i-n个单位出力的最小耗流+第k台机产生n个出力产生耗流的最小耗流，k台机组出i-m个单位出力的最小耗流耗流加上（第）k台机组中多出m个出力产生的最小耗流}
		for(int i=0;i<nowDimension.getAllStatesIn1D().size();i++)
		{
			double cost1=dimensionChangeCost(lastDimension,nowDimension,i);
			
			innerDimensionCost(nowDimension,i,cost1);
		}
		
	}
	
	
	/**
	 * 维度之间跨越产生的cost计算，并更新该阶段的状态作为暂时的最优状态
	 * @param lastDimension
	 * @param nowDimension
	 * @param index 正在进行寻优的维的维内寻优到第几个，其对应的上一维的同状态值的index
	 * @return
	 */
	public abstract double dimensionChangeCost(OneDimensionalStates lastDimension, OneDimensionalStates nowDimension,int index);
	/**
	 * 维度内状态转移产生的cost计算，计算完毕后与cost1比较如果更优，就更新该阶段状态为最优状态，否则状态不变
	 * @param nowDimension
	 * @param index 维内寻优到第几个
	 * @return
	 */
	public abstract void innerDimensionCost(OneDimensionalStates nowDimension,int index,double cost1);
	
	
	
	/**************逆序取优*************/
	/**
	 * 从正序寻优后的globalStates里面把需要的数据取出来
	 */
	public void negativeSequenceToTarget()
	{
		double[] allTargets=new double[globalStates.get(globalStates.size()-1).getAllStatesIn1D().size()];
		for(int i=0;i<allTargets.length;i++)//先把寻优范围内的所有值取出来
			allTargets[i]=globalStates.get(globalStates.size()-1).getAllStatesIn1D().get(i).getStateValue().get(0);
		
		List<BestResult> bestResults=new ArrayList<BestResult>();
		
		for(int i=0;i<allTargets.length;i++)
		{
			if(i==23)
				System.out.print("");
			BestResult bestResult=new BestResult(allTargets[i]);
			
			findTheBestProcess(bestResult,globalStates);
			
			bestResults.add(bestResult);
		}
	}

	
	/**
	 * 根据正序寻优的结果以及目标值，找出最优过程，首尾两端的lastIndex或nextIndex要特殊处理
	 * @param bestResult  给定目标值的对象
	 * @param globalStates  已经正序寻优完毕的
	 */
	private List<List<TwoStagesRelation>> findTheBestProcess(BestResult bestResult, List<OneDimensionalStates> globalStates) {
		double target=bestResult.getTarget();
		//不考虑执行效率，直接逐个判断到底对应于哪一个index
		int targetIndex=findStageStateByTarget(target,globalStates.size()-1,globalStates);
		bestResult.setBestCost(
				globalStates.get(globalStates.size()-1).getAllStatesIn1D().get(targetIndex).getCost());
		List<List<Double>> statesResults=new ArrayList<List<Double>>();
		List<List<TwoStagesRelation>> allStagesStatesValues=new ArrayList<List<TwoStagesRelation>>(); 
		for(int i=globalStates.size()-1;i>=0;i--)
		{
			if(i==globalStates.size()-1)
			{
				TwoStagesRelation first=new TwoStagesRelation(target);
				first.setTargetIndex(targetIndex);
				first.setNextTargetIndex(Integer.MIN_VALUE);//最后一个阶段没有下一个阶段，特殊处理
				List<Double> stageStatesValue=new ArrayList<Double>();
				for(int j=0;j<globalStates.get(i).getStatesNow().get(targetIndex).getStateValue().size();j++)
				{
					List<Double> stateTemp=new ArrayList<Double>();
					stageStatesValue.add(globalStates.get(i).getStatesNow().get(targetIndex).getStateValue().get(j));
					stateTemp.add(stageStatesValue.get(j));
					statesResults.add(stateTemp);
				}
				first.setTargetStateValues(stageStatesValue);
				List<Integer> lastIndex=new ArrayList<Integer>();//正序来看的上一个阶段
				for(int j=0;j<stageStatesValue.size();j++)
				{
					double targetTemp=target-stageStatesValue.get(j);
					lastIndex.add(findStageStateByTarget(targetTemp,globalStates.size()-2,globalStates));
				}
				first.setLastTargetIndex(lastIndex);
				List<TwoStagesRelation> firstStage=new ArrayList<TwoStagesRelation>();
				firstStage.add(first);
				allStagesStatesValues.add(firstStage);
			}
			
			else if(i==0)
			{
				//上一个阶段指的是正序来看后一个阶段，即倒序择优时候的上一个阶段
				List<TwoStagesRelation> lastStage=allStagesStatesValues.get(globalStates.size()-1-(i+1));
				List<TwoStagesRelation> nowStage=new ArrayList<TwoStagesRelation>();
				for(int j=0;j<lastStage.size();j++)
				{
					TwoStagesRelation lastStageTemp=lastStage.get(j);
					
					List<TwoStagesRelation> nowStageTemp=new ArrayList<TwoStagesRelation>();
					
					for(int t=0;t<lastStageTemp.getLastTargetIndex().size();t++)
					{//对lastStageTemp.getLastTargetIndex()里的每一个值都要接着找其上一阶段
						int nowTargetIndex=lastStageTemp.getLastTargetIndex().get(t);
						double targetNow=0;
						if(nowTargetIndex>=0 && nowTargetIndex<Integer.MAX_VALUE-100)//出现了nowTargetIndex=-1的情况时，说明剩余的状态值是0，即不需要再寻优了
							targetNow=globalStates.get(i).getAllStatesIn1D().get(nowTargetIndex)
							.getStateValue().get(0);
						else if(nowTargetIndex==Integer.MAX_VALUE)
							targetNow=lastStageTemp.getTarget();
						TwoStagesRelation oneOfNowTemp=new TwoStagesRelation(targetNow);
						oneOfNowTemp.setTargetIndex(nowTargetIndex);
						oneOfNowTemp.setNextTargetIndex(lastStageTemp.getTargetIndex());
						List<Double> stageStatesValue=new ArrayList<Double>();
						if(nowTargetIndex<0 || nowTargetIndex==Integer.MAX_VALUE)
						{
							List<Double> notFindAgainState=new ArrayList<Double>();
							if(nowTargetIndex<0)
								notFindAgainState.add(0.0);
							else 
								notFindAgainState.add(lastStageTemp.getTarget());
							oneOfNowTemp.setTargetStateValues(notFindAgainState);
						}
						else
						{
							for(int s=0;s<globalStates.get(i).getStatesNow().get(nowTargetIndex).getStateValue().size();s++)
							{
								stageStatesValue.add(globalStates.get(i).getStatesNow().get(nowTargetIndex).getStateValue().get(s));
							}
							oneOfNowTemp.setTargetStateValues(stageStatesValue);
						}
						
						nowStageTemp.add(oneOfNowTemp);
					}
					nowStage.addAll(nowStageTemp);
					
				}
				allStagesStatesValues.add(nowStage);
			}
			
			
			else
			{
				//上一个阶段指的是正序来看后一个阶段，即倒序择优时候的上一个阶段
				List<TwoStagesRelation> lastStage=allStagesStatesValues.get(globalStates.size()-1-(i+1));
				List<TwoStagesRelation> nowStage=new ArrayList<TwoStagesRelation>();
				int nowValuesNumCountCount=0;
				for(int j=0;j<lastStage.size();j++)
				{
					TwoStagesRelation lastStageTemp=lastStage.get(j);
					
					List<TwoStagesRelation> nowStageTemp=new ArrayList<TwoStagesRelation>();
					
					int nowValuesNumCount=nowValuesNumCountCount;
					for(int t=0;t<lastStageTemp.getLastTargetIndex().size();t++)
					{//对lastStageTemp.getLastTargetIndex()里的每一个值都要接着找其上一阶段
						
						
						int nowTargetIndex=lastStageTemp.getLastTargetIndex().get(t);
						double targetNow=0;
						if(nowTargetIndex>=0 && nowTargetIndex<Integer.MAX_VALUE-100)//出现了nowTargetIndex=-1的情况时，说明剩余的状态值是0，即不需要再寻优了
							targetNow=globalStates.get(i).getAllStatesIn1D().get(nowTargetIndex)
							.getStateValue().get(0);
						else if(nowTargetIndex==Integer.MAX_VALUE)
							targetNow=lastStageTemp.getTarget();
						TwoStagesRelation oneOfNowTemp=new TwoStagesRelation(targetNow);
						oneOfNowTemp.setTargetIndex(nowTargetIndex);
						oneOfNowTemp.setNextTargetIndex(lastStageTemp.getTargetIndex());
						List<Double> stageStatesValue=new ArrayList<Double>();
						
						
						
						
						if(nowTargetIndex<0)
						{
							List<Integer> notFindAgain=new ArrayList<Integer>();
							notFindAgain.add(nowTargetIndex-1);
							oneOfNowTemp.setLastTargetIndex(notFindAgain);
							List<Double> notFindAgainState=new ArrayList<Double>();
							notFindAgainState.add(0.0);
							oneOfNowTemp.setTargetStateValues(notFindAgainState);
							
							
							int nowValuesNum=nowValuesNumCount+1;
							//现在就知道了对应的有多少个第一个状态一样的值了，添加进来一些前阶段相同的状态的数组来
							
							for(int tt=nowValuesNumCount;tt<nowValuesNum-1;tt++)
							{//对lastStageTemp.getLastTargetIndex()里的每一个值都要接着找其上一阶段
								List<Double> temp=new ArrayList<Double>();
								for(int m=0;m<statesResults.get(tt).size();m++)
								{
									temp.add(statesResults.get(tt).get(m));
								}
								statesResults.add(tt+1, temp);
								
							}
							
							int tt=nowValuesNumCount;
							for(int s=0;s<1;s++)
							{
								stageStatesValue.add(notFindAgainState.get(0));
								
								List<Double> temp=statesResults.get(tt+s);
								temp.add(stageStatesValue.get(s));
								
							}
							
							nowValuesNumCount=nowValuesNum;
						}
						else if(nowTargetIndex==Integer.MAX_VALUE)
						{
							List<Integer> notFindAgain=new ArrayList<Integer>();
							notFindAgain.add(Integer.MAX_VALUE);
							oneOfNowTemp.setLastTargetIndex(notFindAgain);
							List<Double> notFindAgainState=new ArrayList<Double>();
							notFindAgainState.add(lastStageTemp.getTarget());
							oneOfNowTemp.setTargetStateValues(notFindAgainState);
						}
						else
						{
							int nowValuesNum=nowValuesNumCount+globalStates.get(i).getStatesNow().get(nowTargetIndex).getStateValue().size();
							//现在就知道了对应的有多少个第一个状态一样的值了，添加进来一些前阶段相同的状态的数组来
							
							for(int tt=nowValuesNumCount;tt<nowValuesNum-1;tt++)
							{//对lastStageTemp.getLastTargetIndex()里的每一个值都要接着找其上一阶段
								List<Double> temp=new ArrayList<Double>();
								for(int m=0;m<statesResults.get(tt).size();m++)
								{
									temp.add(statesResults.get(tt).get(m));
								}
								statesResults.add(tt+1, temp);
								
							}
							
							int tt=nowValuesNumCount;
							for(int s=0;s<globalStates.get(i).getStatesNow().get(nowTargetIndex).getStateValue().size();s++)
							{
								stageStatesValue.add(globalStates.get(i).getStatesNow().get(nowTargetIndex).getStateValue().get(s));
								
								List<Double> temp=statesResults.get(tt+s);
								temp.add(stageStatesValue.get(s));
								
							}
							
							nowValuesNumCount=nowValuesNum;
							
							oneOfNowTemp.setTargetStateValues(stageStatesValue);
							List<Integer> lastIndex=new ArrayList<Integer>();//正序来看的上一个阶段
							for(int s=0;s<stageStatesValue.size();s++)
							{
								double targetTemp=targetNow-stageStatesValue.get(s);
								lastIndex.add(findStageStateByTarget(targetTemp,/*globalStates.size()-1-(i+1)*/i-1,globalStates));
							}
							oneOfNowTemp.setLastTargetIndex(lastIndex);
						}
						
						nowStageTemp.add(oneOfNowTemp);
						nowValuesNumCountCount=nowValuesNumCount;
					}
					
					nowStage.addAll(nowStageTemp);
					
				}
				allStagesStatesValues.add(nowStage);
				
			}
				
		}
		
		List<double[]> statesResultsforFinalResults=new ArrayList<double[]>();
		for(int i=0;i<statesResults.size();i++)
		{
			double[] temp=BasicMathMethods.listToDouble(statesResults.get(i));
			double nowSum=BasicMathMethods.sumArray(temp);
			double surplus=target-nowSum;
			statesResults.get(i).add(surplus);
			double[] resultReverse=BasicMathMethods.listToDouble(statesResults.get(i));
			double[] result=BasicMathMethods.reverseArray(resultReverse);
			statesResultsforFinalResults.add(result);
		}
		
		bestResult.setAllStates(statesResultsforFinalResults);
		
		return allStagesStatesValues;
		
	}
	
	
	/**
	 * @param target  目标
	 * @param index  阶段编号
	 * @param globalStates 全局各阶段的寻优的结果
	 */
	private int findStageStateByTarget(double target,int index,List<OneDimensionalStates> globalStates)
	{
		int targetIndex=-(globalStates.size()-1-index);//如果没找到对应的值targetIndex就给负值
		for(int i=0;i<globalStates.get(index).getAllStatesIn1D().size();i++)
		{
			if(globalStates.get(index).getAllStatesIn1D().get(i).getStateValue().get(0)==target)
			{
				targetIndex=i;
				break;
			}
		}
		if(targetIndex<0 && target>globalStates.get(index).getAllStatesIn1D()
				.get(globalStates.get(index).getAllStatesIn1D().size()-1).getStateValue().get(0))//值是超过上限的，targetIndex给一个超大值
		{
			targetIndex=Integer.MAX_VALUE;
		}
		return targetIndex;
	}
}
