package com.wenyu.factory;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.service.DispatchGraphManager;
@Component("powerControlStationConstructor")
public class PowerControlStationConstructor {
	private HydroStationConstructor hsc;
	private DispatchGraphManager dispatchGraphManager;
	/**
	 * @return the hsc
	 */
	public HydroStationConstructor getHsc() {
		return hsc;
	}
	/**
	 * @param hsc the hsc to set
	 */
	@Resource
	public void setHsc(HydroStationConstructor hsc) {
		this.hsc = hsc;
	}
	/**
	 * @return the dispatchGraphManager
	 */
	public DispatchGraphManager getDispatchGraphManager() {
		return dispatchGraphManager;
	}
	/**
	 * @param dispatchGraphManager the dispatchGraphManager to set
	 */
	@Resource
	public void setDispatchGraphManager(DispatchGraphManager dispatchGraphManager) {
		this.dispatchGraphManager = dispatchGraphManager;
	}
	
	public PowerControlHStation constructStation(EngineerBureau eb) {
		HydroStation hs=hsc.constructStation(eb);
		PowerControlHStation pchs=new PowerControlHStation();
		pchs.setHydroStation(hs);
		pchs.setDispatchGraph(dispatchGraphManager.createDispatchGraph(eb.getStationId(), eb.getTbType()));
		return pchs;
	}
	
	public PowerControlHStation constructStation(EngineerBureau eb, int floodControlBeginPeriod, int floodControlEndPeriod) {
		HydroStation hs=hsc.constructStation(eb, floodControlBeginPeriod, floodControlEndPeriod);
		PowerControlHStation pchs=new PowerControlHStation();
		pchs.setHydroStation(hs);
		pchs.setDispatchGraph(dispatchGraphManager.createDispatchGraph(eb.getStationId(), eb.getTbType()));
		return pchs;
	}
}
