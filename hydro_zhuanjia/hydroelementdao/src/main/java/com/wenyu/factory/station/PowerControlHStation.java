package com.wenyu.factory.station;

import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;

/**
 * 有调蓄能力的发电水电站（可以被聚合，比如有防洪要求的水库可以有PowerControlHStation对象，然后再有防洪的要求）
 *
 */
public class PowerControlHStation{
	
	private HydroStation hydroStation;
	//都通过注入实现
	private DispatchGraph dispatchGraph;
	
	public HydroStation getHydroStation() {
		return hydroStation;
	}

	public void setHydroStation(HydroStation hydroStation) {
		this.hydroStation = hydroStation;
	}

	/**
	 * @return the dispatchGraph
	 */
	public DispatchGraph getDispatchGraph() {
		return dispatchGraph;
	}

	/**
	 * @param dispatchGraph the dispatchGraph to set
	 */
	public void setDispatchGraph(DispatchGraph dispatchGraph) {
		this.dispatchGraph = dispatchGraph;
	}

}
