package me.owenyy.useregulation;


import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.service.DispatchGraphManager;
import com.wenyu.service.InitialContainer;

public class SchedulingRuleUseDGTest {
	static DispatchGraph dispatchGraph;
	static SchedulingRuleUseDG ssrud;
	@Before
	public void before(){
		DispatchGraphManager dgm=InitialContainer.getContext().getBean(DispatchGraphManager.class);
		dispatchGraph=dgm.createDispatchGraph(12010100, "MONTH");
		ssrud=new SchedulingRuleUseDG();
	}
	
	@Test
	public void testSearchOutput() {
		LocalDate date=LocalDate.now();
		double output=ssrud.searchOutput(date.plusMonths(2), 2815, dispatchGraph);
		System.out.print(output);
	}
	
	@Test
	public void testSearchLevelRestrict() {
		LocalDate date=LocalDate.now();		
		double[] levels=ssrud.searchLevelRestrict(date.plusMonths(6), 2895, dispatchGraph);
		System.out.print(levels);
	}

	
}
