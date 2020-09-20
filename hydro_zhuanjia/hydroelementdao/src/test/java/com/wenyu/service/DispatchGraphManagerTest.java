package com.wenyu.service;


import org.junit.Before;
import org.junit.Test;

import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;

public class DispatchGraphManagerTest {
	static DispatchGraphManager dgm;
	@Before
	public void before(){
		dgm=InitialContainer.getContext().getBean(DispatchGraphManager.class);
		
	}
	
	@Test
	public void testCreateDispatchGraph() {
		DispatchGraph dg=dgm.createDispatchGraph(12010100, "MONTH");
		System.out.print(dg);
	}
	
	@Test
	public void testWriteToExcelFromDB() {
		int[] sIndex={6,7,8,9,10};
		int[] dIndex={11,12,1,2,3,4,5};
		String sheet="Sheet1";
		String path="C:/Users/asus/Desktop/dispatchgraph.xlsx";
		dgm.writeToExcelFromDB(path,sheet,12010100, "MONTH", sIndex, dIndex);
		
	}
}
