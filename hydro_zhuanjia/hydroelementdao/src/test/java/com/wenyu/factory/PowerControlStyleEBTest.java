package com.wenyu.factory;

import org.junit.Test;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.service.InitialContainer;

public class PowerControlStyleEBTest {

	@Test
	public void testCreateStation() {
		PowerControlStationConstructor hsof = InitialContainer.getContext().getBean(PowerControlStationConstructor.class);
		EngineerBureau eb=new EngineerBureau(17060100, new int[]{10001,10002,10003}, new int[]{101,102,201,301}, "DECAD", 1, "1956-01-01T00:00", 1944);
		PowerControlHStation hss = hsof.constructStation(eb,16,25);
		System.out.print(hss);
	}

}
