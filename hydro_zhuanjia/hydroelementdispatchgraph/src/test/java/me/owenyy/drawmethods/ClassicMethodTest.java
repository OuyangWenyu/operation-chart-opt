package me.owenyy.drawmethods;


import org.junit.Test;

import com.wenyu.factory.EngineerBureau;
import com.wenyu.factory.PowerControlStationConstructor;
import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;
import com.wenyu.service.DispatchGraphManager;
import com.wenyu.service.InitialContainer;

public class ClassicMethodTest {

//	@Test
//	public void testMakeDispatchGraph() {
//		ClassicMethod classicMethod= new ClassicMethod();
//		DispatchInputParas input=new DispatchInputParas("MONTH",0.90, 4, 3, 5);//外部输入绘制调度图所需的数据
//		double[] argumentMultiples = {1.8,1.6,1.4,1.2};//插值不行，不容易满足保证出力，因此采用倍数去计算，结果会好一点
//		input.setArgumentMultiples(argumentMultiples);
//		double[] reduceMultiples = {0.7,0.8,0.9};
//		input.setReduceMultiples(reduceMultiples);
//		PowerControlStationConstructor hsof = InitialContainer.getContext().getBean(PowerControlStationConstructor.class);
//		PowerControlHStation hStation=hsof.constructStation(new EngineerBureau(12010100, new int[] { 10001, 10002 },
//				new int[] { 101, 102, 201, 301 }, "MONTH", 1, "1953-06-01T00:00", 636));
//		classicMethod.makeDispatchGraph(hStation, input);
//		DispatchGraphManager dgm=InitialContainer.getContext().getBean(DispatchGraphManager.class);
//		dgm.writeDispatchGraph(hStation.getHydroStation().getHsSpec().getId(),hStation.getDispatchGraph());
//	}

	@Test
	public void testMakeDispatchGraph2() {
		ClassicMethod classicMethod= new ClassicMethod();
		DispatchInputParas input=new DispatchInputParas("MONTH",0.90, 4, 3, 5);//外部输入绘制调度图所需的数据
		double[] argumentMultiples = {1.8,1.6,1.4,1.2};//插值不行，不容易满足保证出力，因此采用倍数去计算，结果会好一点
		input.setArgumentMultiples(argumentMultiples);
		double[] reduceMultiples = {0.7,0.8,0.9};
		input.setReduceMultiples(reduceMultiples);
		PowerControlStationConstructor hsof = InitialContainer.getContext().getBean(PowerControlStationConstructor.class);
		PowerControlHStation hStation=hsof.constructStation(new EngineerBureau(12011300, new int[] { 10001, 10002 },
				new int[] { 101, 102, 201, 301 }, "MONTH", 1, "1953-01-01T00:00", 696));
		classicMethod.makeDispatchGraph(hStation, input);
		DispatchGraphManager dgm=InitialContainer.getContext().getBean(DispatchGraphManager.class);
		dgm.writeDispatchGraph(hStation.getHydroStation().getHsSpec().getId(),hStation.getDispatchGraph());
	}

}
