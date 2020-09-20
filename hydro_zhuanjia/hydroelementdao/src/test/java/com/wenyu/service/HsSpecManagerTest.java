package com.wenyu.service;

import org.junit.Test;

import com.wenyu.hydroelements.hydrostation.HStationSpec;

public class HsSpecManagerTest {

	@Test
	public void testCreateHsSpec() {
		HsSpecManager hsm=InitialContainer.getContext().getBean(HsSpecManager.class);
		HStationSpec hss=hsm.createHsSpec(12010100);
		System.out.print(hss);
		
	}

}
