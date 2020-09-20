package com.wenyu.hydroelements.hydrostation.dispatchgraph;

import java.util.List;

public class DispatchGraph{
	//水库名称
	private String stationName;
	//调度图的多条调度线
	private List<DispatchLine> dispacthline;
	
	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public List<DispatchLine> getDispacthline() {
		return dispacthline;
	}

	public void setDispacthline(List<DispatchLine> dispacthline) {
		this.dispacthline = dispacthline;
	}


}
