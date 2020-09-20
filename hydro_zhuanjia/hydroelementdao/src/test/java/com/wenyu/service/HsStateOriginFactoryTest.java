package com.wenyu.service;

import java.util.List;

import org.junit.Test;

import com.wenyu.factory.state.HsStateOriginFactory;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.service.InitialContainer;

public class HsStateOriginFactoryTest {

	@Test
	public void testCreateHsStates() {
		HsStateOriginFactory hsof = InitialContainer.getContext().getBean(HsStateOriginFactory.class);
		int[] constrainttypes = { 101, 102, 201, 301 };
		List<HStationState> hss = hsof.createHsStates(12010100, constrainttypes, "MONTH", 1, "1953-06-01T00:00:00", 2);
		System.out.print(hss);
	}

}
