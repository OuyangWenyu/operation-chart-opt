package me.owenyy.servicedispatchgraph;


import org.junit.Test;

import com.wenyu.factory.EngineerBureau;
import com.wenyu.factory.PowerControlStationConstructor;
import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.service.DispatchGraphManager;
import com.wenyu.service.InitialContainer;

public class MakeDispatchGraphTest {

	@Test
	public void testMakeDispatchGraph() {
		PowerControlStationConstructor hsof = InitialContainer.getContext().getBean(PowerControlStationConstructor.class);
		PowerControlHStation hss = hsof.constructStation(new EngineerBureau(17060100, new int[]{10001,10002,10003}, new int[]{101,102,201,301}, "DECAD", 1, "1956-07-01T00:00", 1944), 16, 25);
		MakeDispatchGraph m=new MakeDispatchGraph(hss);
		DispatchGraph dg=m.makeDispatchGraph();
		DispatchGraphManager dgm=InitialContainer.getContext().getBean(DispatchGraphManager.class);
		dgm.writeDispatchGraph(hss.getHydroStation().getHsSpec().getId(),dg);
	}

}
