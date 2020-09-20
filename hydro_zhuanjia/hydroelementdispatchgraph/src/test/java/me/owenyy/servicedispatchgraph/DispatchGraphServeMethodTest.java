package me.owenyy.servicedispatchgraph;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.service.DispatchGraphManager;
import com.wenyu.service.InitialContainer;

public class DispatchGraphServeMethodTest {
	static DispatchGraph dispatchGraph;
	@Before
	public void before(){
		DispatchGraphManager dgm=InitialContainer.getContext().getBean(DispatchGraphManager.class);
		dispatchGraph=dgm.createDispatchGraph(12010100, "MONTH");
	}
	@Test
	public void testCalStorageStartEnd() {
		LocalDate[] dates=DispatchGraphServeMethod.calStorageStartEnd(1999, dispatchGraph);
		System.out.println(dates[0]);
		System.out.println(dates[1]);
	}

}
