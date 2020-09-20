package me.owenyy.drawmethods.helper.pso;

import java.util.List;

import com.wenyu.factory.topology.Series;
import com.wenyu.hydroelements.hydrostation.HStationState;

import me.owenyy.drawmethods.helper.MultiFomulaForDispatch0;
import me.owenyy.useregulation.SchedulingRuleUseDG;

/**
 * 多个串联梯级电站模拟运行，复杂拓扑的参考超哥的代码重新编写，还是考虑分开构造和模拟运行，构造梯级的代码按照拓扑结构来编写，
 * 这里直接编写模拟串联水库运行的代码，模拟运行方式和水库的匹配放在哪里，边写边想
 * 水利拓扑为树状结构，因此在分析数据结构之后，再给出水利拓扑的代码，先给出线性拓扑
 * @author  OwenYY
 *
 */
public class MultiStationTimeOpe{
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
	
	private void initialRangeInflow(List<HStationState> hsStates1, List<HStationState> hsStates2) {
		for (int i = 0; i < hsStates2.size(); i++) {
			hsStates2.get(i).setInflowReal(hsStates1.get(i).getOutflow() + hsStates2.get(i).getInflowRange());
		}
	}
}
