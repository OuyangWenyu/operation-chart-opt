package com.wenyu.hydroelements.hydrostation;

import java.util.List;

import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;

/**
 * 最基本的一个水电站的类
 * @author  OwenYY
 *
 */
public class HydroStation {
	private HStationSpec hsSpec;
	private List<HStationState> hsStates;
	private StationCurve stationCurves;
	
	public HStationSpec getHsSpec() {
		return hsSpec;
	}

	public void setHsSpec(HStationSpec hsSpec) {
		this.hsSpec = hsSpec;
	}

	public List<HStationState> getHsStates() {
		return hsStates;
	}

	public void setHsStates(List<HStationState> hsStates) {
		this.hsStates = hsStates;
	}

	


	public StationCurve getStationCurves() {
		return stationCurves;
	}

	public void setStationCurves(StationCurve stationCurves) {
		this.stationCurves = stationCurves;
	}
}
