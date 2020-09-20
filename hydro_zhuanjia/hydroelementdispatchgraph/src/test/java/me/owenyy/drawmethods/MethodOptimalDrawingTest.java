package me.owenyy.drawmethods;

import java.util.List;

import org.junit.Test;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.factory.topology.Series;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;
import com.wenyu.service.DispatchGraphManager;
import com.wenyu.service.InitialContainer;

public class MethodOptimalDrawingTest {

	@Test
	public void testMakeDispatchGraph() {
		MethodOptimalDrawing mod=new MethodOptimalDrawing();
		DispatchInputParas dispatchInputParas=new DispatchInputParas("MONTH",0.90, 2, 2, 5);//外部输入绘制调度图所需的数据
		Series series=new Series();
		int[] stationsId=new int[12];
		for(int i=0;i<12;i++){
			stationsId[i]=12010100+i*100;
		}
		series.constructSeries1N(stationsId, new int[] { 10001, 10002 },
				new int[] { 101, 102, 201, 301 }, "MONTH", 1,  "1953-06-01T00:00", 636);
		PowerControlHStation hss = series.getLeadStation();
		List<HydroStation> stations=series.getStations();
		mod.setStations(stations);
		mod.makeDispatchGraph(hss, dispatchInputParas);
		DispatchGraphManager dgm=InitialContainer.getContext().getBean(DispatchGraphManager.class);
		dgm.writeDispatchGraph(hss.getHydroStation().getHsSpec().getId(),hss.getDispatchGraph());
	}

}
