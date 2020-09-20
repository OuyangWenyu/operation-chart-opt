package me.owenyy.simuoperation;

import java.util.List;

import com.wenyu.factory.topology.Series;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;
import com.wenyu.hydroelements.operation.behavior.ControlModelSingleTime;
import com.wenyu.service.DispatchGraphManager;
import com.wenyu.service.InitialContainer;

import me.owenyy.drawmethods.ClassicMethod;
import me.owenyy.drawmethods.helper.MultiFomulaForDispatch0;
import me.owenyy.drawmethods.helper.pso.MultiTimeLevelControl;
import me.owenyy.drawmethods.helper.pso.MultiTimePowerControlQueryDG;
import me.owenyy.useregulation.SchedulingRuleUseDG;

/**
 * 多个串联梯级电站模拟运行，复杂拓扑的参考超哥的代码重新编写，还是考虑分开构造和模拟运行，构造梯级的代码按照拓扑结构来编写，
 * 这里直接编写模拟串联水库运行的代码，模拟运行方式和水库的匹配放在哪里，边写边想
 * 水利拓扑为树状结构，因此在分析数据结构之后，再给出水利拓扑的代码，先给出线性拓扑
 * @author  OwenYY
 *
 */
public class MultiStationTimeOpe {
	/**
	 * 一库N级模拟运行 一库是调节式  N级是径流式
	 * @param series
	 */
	public void seriesSimuOpera(Series series) {
		double[] levelBegins={2815,2605,2525,2340,2245,1906,1818,1732,1619,1477,1408,1307};
		
		MultiTimePowerControlQueryDG mtpcq = new MultiTimePowerControlQueryDG();
		mtpcq.setPowerHydroStation(series.getLeadStation());
		mtpcq.setLevel_Begin(levelBegins[0]);
		SchedulingRuleUseDG howToUseDG = new SchedulingRuleUseDG();
		mtpcq.setHowToUseDG(howToUseDG);
		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(series.getLeadStation().getHydroStation().getStationCurves());
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(series.getLeadStation().getHydroStation().getHsSpec());
		mtpcq.setCalMethods(mffd0);
		mtpcq.simuOperation();
		initialRangeInflow(series.getLeadStation().getHydroStation().getHsStates(), series.getStations().get(0).getHsStates());
		
		MultiTimeLevelControl mtlc=new MultiTimeLevelControl();
		for(int i=0;i<series.getStations().size();i++){
			mtlc.setHydroStation(series.getStations().get(i));
			mtlc.setLevel_Begin(levelBegins[i+1]);
			mffd0.setCurve(series.getStations().get(i).getStationCurves());
			mffd0.setFixedHeadLoss(1);
			mffd0.setHsSpec(series.getStations().get(i).getHsSpec());
			mtlc.setCalMethods(mffd0);
			mtlc.simuOperation();
			if(i<series.getStations().size()-1)
			initialRangeInflow(series.getStations().get(i).getHsStates(), series.getStations().get(i+1).getHsStates());
			
		}
		
	}
	
	/**
	 * N级中有调节式
	 * @param series
	 * @param regulationId 调节电站在拓扑结构中的编号
	 * @param levelBegins
	 */
	public void seriesSimuOpera(Series series,int[] regulationId,double[] levelBegins) {
		MultiTimePowerControlQueryDG mtpcq = new MultiTimePowerControlQueryDG();
		mtpcq.setPowerHydroStation(series.getLeadStation());
		mtpcq.setLevel_Begin(levelBegins[0]);
		SchedulingRuleUseDG howToUseDG = new SchedulingRuleUseDG();
		mtpcq.setHowToUseDG(howToUseDG);
		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(series.getLeadStation().getHydroStation().getStationCurves());
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(series.getLeadStation().getHydroStation().getHsSpec());
		mtpcq.setCalMethods(mffd0);
		mtpcq.simuOperation();
		initialRangeInflow(series.getLeadStation().getHydroStation().getHsStates(), series.getStations().get(0).getHsStates());
		
		MultiTimeLevelControl mtlc=new MultiTimeLevelControl();
		for(int i=0;i<series.getStations().size();i++){
			boolean iIsRe=false;
			for(int j=0;j<regulationId.length;j++){
				if(regulationId[j]==i){
					for(int k=0;k<series.getReStations().get(j).getHydroStation().getHsStates().size();k++)
						ControlModelSingleTime.valueCopy(series.getReStations().get(j).getHydroStation().getHsStates().get(k),series.getStations().get(i).getHsStates().get(k));//两者应该是指向同一个对象的引用，不赋值其实也可以
					if(series.getReStations().get(j).getDispatchGraph()==null){//使用经典方法绘制调度图
						ClassicMethod classicMethod= new ClassicMethod();
						DispatchInputParas input=new DispatchInputParas("MONTH",0.90, 4, 3, 5);//外部输入绘制调度图所需的数据
						double[] argumentMultiples = {1.8,1.6,1.4,1.2};//插值不行，不容易满足保证出力，因此采用倍数去计算，结果会好一点
						input.setArgumentMultiples(argumentMultiples);
						double[] reduceMultiples = {0.7,0.8,0.9};
						input.setReduceMultiples(reduceMultiples);
						classicMethod.makeDispatchGraph(series.getReStations().get(j), input);
						DispatchGraphManager dgm=InitialContainer.getContext().getBean(DispatchGraphManager.class);
						dgm.writeDispatchGraph(series.getReStations().get(j).getHydroStation().getHsSpec().getId(),series.getReStations().get(j).getDispatchGraph());
					}
					mtpcq.setPowerHydroStation(series.getReStations().get(j));
					mtpcq.setLevel_Begin(levelBegins[i+1]);
					mtpcq.setHowToUseDG(howToUseDG);
					mffd0.setCurve(series.getReStations().get(j).getHydroStation().getStationCurves());
					mffd0.setFixedHeadLoss(1);
					mffd0.setHsSpec(series.getReStations().get(j).getHydroStation().getHsSpec());
					mtpcq.setCalMethods(mffd0);
					mtpcq.simuOperation();
					for(int k=0;k<series.getReStations().get(j).getHydroStation().getHsStates().size();k++)
						ControlModelSingleTime.valueCopy(series.getStations().get(i).getHsStates().get(k), series.getReStations().get(j).getHydroStation().getHsStates().get(k));
					if(i<series.getStations().size()-1)
						initialRangeInflow(series.getReStations().get(j).getHydroStation().getHsStates(), series.getStations().get(i+1).getHsStates());
					iIsRe=true;
				}
			}
			if(!iIsRe){
				mtlc.setHydroStation(series.getStations().get(i));
				mtlc.setLevel_Begin(levelBegins[i+1]);
				mffd0.setCurve(series.getStations().get(i).getStationCurves());
				mffd0.setFixedHeadLoss(1);
				mffd0.setHsSpec(series.getStations().get(i).getHsSpec());
				mtlc.setCalMethods(mffd0);
				mtlc.simuOperation();
				if(i<series.getStations().size()-1)
					initialRangeInflow(series.getStations().get(i).getHsStates(), series.getStations().get(i+1).getHsStates());
			}
			
		}
		
	}
	
	private void initialRangeInflow(List<HStationState> hsStates1, List<HStationState> hsStates2) {
		for (int i = 0; i < hsStates2.size(); i++) {
			hsStates2.get(i).setInflowReal(hsStates1.get(i).getOutflow() + hsStates2.get(i).getInflowRange());
		}
	}
	
	/**
	 * 全部径流式
	 * @param series
	 */
	public void seriesRunoffStationsSimuOpera(Series series,double[] levelBegins) {
		MultiTimeLevelControl mtlc=new MultiTimeLevelControl();
		mtlc.setHydroStation(series.getLeadStation().getHydroStation());
		mtlc.setLevel_Begin(levelBegins[0]);
		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(series.getLeadStation().getHydroStation().getStationCurves());
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(series.getLeadStation().getHydroStation().getHsSpec());
		mtlc.setCalMethods(mffd0);
		mtlc.simuOperation();
		initialRangeInflow(series.getLeadStation().getHydroStation().getHsStates(), series.getStations().get(0).getHsStates());
		
		
		for(int i=0;i<series.getStations().size();i++){
			mtlc.setHydroStation(series.getStations().get(i));
			mtlc.setLevel_Begin(levelBegins[i+1]);
			mffd0.setCurve(series.getStations().get(i).getStationCurves());
			mffd0.setFixedHeadLoss(1);
			mffd0.setHsSpec(series.getStations().get(i).getHsSpec());
			mtlc.setCalMethods(mffd0);
			mtlc.simuOperation();
			if(i<series.getStations().size()-1)
			initialRangeInflow(series.getStations().get(i).getHsStates(), series.getStations().get(i+1).getHsStates());
			
		}
		
	}
	
	/**
	 * 全部径流式，如美自身水位控制运行，多时段末水位均给定
	 * @param series
	 * @param levelBegins
	 * @param levelEnds
	 */
	public void seriesRunoffStationsSimuOpera(Series series,double[] levelBegins,double[] levelEnds) {
		
		MultiTimeLevelControl mtlc=new MultiTimeLevelControl();
		mtlc.setHydroStation(series.getLeadStation().getHydroStation());
		mtlc.setLevel_Begin(levelBegins[0]);
		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(series.getLeadStation().getHydroStation().getStationCurves());
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(series.getLeadStation().getHydroStation().getHsSpec());
		mtlc.setCalMethods(mffd0);
		mtlc.simuOperationNotRunoff(levelEnds);
		initialRangeInflow(series.getLeadStation().getHydroStation().getHsStates(), series.getStations().get(0).getHsStates());
		
		
		for(int i=0;i<series.getStations().size();i++){
			mtlc.setHydroStation(series.getStations().get(i));
			mtlc.setLevel_Begin(levelBegins[i+1]);
			mffd0.setCurve(series.getStations().get(i).getStationCurves());
			mffd0.setFixedHeadLoss(1);
			mffd0.setHsSpec(series.getStations().get(i).getHsSpec());
			mtlc.setCalMethods(mffd0);
			mtlc.simuOperation();
			if(i<series.getStations().size()-1)
			initialRangeInflow(series.getStations().get(i).getHsStates(), series.getStations().get(i+1).getHsStates());
			
		}
		
	}
}
