package me.owenyy.simuoperation;


import java.util.List;

import org.junit.Test;

import com.wenyu.factory.EngineerBureau;
import com.wenyu.factory.PowerControlStationConstructor;
import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.service.DispatchGraphManager;
import com.wenyu.service.InitialContainer;

import me.owenyy.drawmethods.helper.MultiFomulaForDispatch1;
import me.owenyy.servicedispatchgraph.MakeDispatchGraph;
import me.owenyy.useregulation.SchedulingRuleUseDG;

public class TwoStationsTest {

	@Test
	public void testSimuOperation() {
		PowerControlStationConstructor hsof = InitialContainer.getContext().getBean(PowerControlStationConstructor.class);
		PowerControlHStation wdd = hsof.constructStation(new EngineerBureau(17060100, new int[]{10001,10002,10003}, new int[]{101,102,201,301}, "DECAD", 1, "1956-07-01T00:00", 1944), 19, 25);
		MultiTimePowerControlQueryDG mtpcq=new MultiTimePowerControlQueryDG();
		mtpcq.setPowerHydroStation(wdd);
		mtpcq.setLevel_Begin(975);
		SchedulingRuleUseDG howToUseDG=new SchedulingRuleUseDG();
		mtpcq.setHowToUseDG(howToUseDG);
		MultiFomulaForDispatch1 mffd0 = new MultiFomulaForDispatch1();
		mffd0.setCurve(wdd.getHydroStation().getStationCurves());
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(wdd.getHydroStation().getHsSpec());
		mtpcq.setCalMethods(mffd0);
		mtpcq.simuOperation();
		
		PowerControlHStation bht = hsof.constructStation(new EngineerBureau(17060200, new int[]{10001,10002,10003}, new int[]{101,102,201,301}, "DECAD", 1, "1956-07-01T00:00", 1944), 19, 25);
		initialRangeInflow(wdd.getHydroStation().getHsStates(),bht.getHydroStation().getHsStates());
		
		MakeDispatchGraph m=new MakeDispatchGraph(bht);
		DispatchGraph dg=m.makeDispatchGraph();
		DispatchGraphManager dgm=InitialContainer.getContext().getBean(DispatchGraphManager.class);
		dgm.writeDispatchGraph(bht.getHydroStation().getHsSpec().getId(),dg);
	}
	private void initialRangeInflow(List<HStationState> hsStates1, List<HStationState> hsStates2) {
		for(int i=0;i<hsStates2.size();i++){
			hsStates2.get(i).setInflowReal(hsStates1.get(i).getOutflow()+hsStates2.get(i).getInflowRange());
		}
	}

}
