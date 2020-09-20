package com.wenyu.factory;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.factory.state.HsStateOriginFactory;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.hydroelements.hydrostation.constraint.BuildConstraint;
import com.wenyu.service.AppointIdNameManager;
import com.wenyu.service.HsSpecManager;
import com.wenyu.service.StationCurveManager;

/**
 * 构造最基本的电站参数，以后可以针对机组写一个类似的只针对机组的基本的构造类
 * 
 * @author OwenYY
 *
 */
@Component("hydroStationConstructor")
public class HydroStationConstructor {
	private HsStateOriginFactory hsStateFactory;
	private HsSpecManager hsSpecManager;
	private StationCurveManager stationCurveManager;
	private AppointIdNameManager appointIdNameManager;

	public HsStateOriginFactory getHsStateFactory() {
		return hsStateFactory;
	}

	@Resource
	public void setHsStateFactory(HsStateOriginFactory hsStateFactory) {
		this.hsStateFactory = hsStateFactory;
	}

	public HsSpecManager getHsSpecManager() {
		return hsSpecManager;
	}

	@Resource
	public void setHsSpecManager(HsSpecManager hsSpecManager) {
		this.hsSpecManager = hsSpecManager;
	}

	public StationCurveManager getStationCurveManager() {
		return stationCurveManager;
	}

	@Resource
	public void setStationCurveManager(StationCurveManager stationCurveManager) {
		this.stationCurveManager = stationCurveManager;
	}

	public AppointIdNameManager getAppointIdNameManager() {
		return appointIdNameManager;
	}

	@Resource
	public void setAppointIdNameManager(AppointIdNameManager appointIdNameManager) {
		this.appointIdNameManager = appointIdNameManager;
	}

	/**
	 * @param eb
	 * @return  构造普通电站
	 */
	public HydroStation constructStation(EngineerBureau eb) {
		HydroStation station = new HydroStation();
		int stationid = eb.getStationId();
		String tbtype = eb.getTbType();
		station.setHsSpec(hsSpecManager.createHsSpec(stationid));
		station.getHsSpec().setName(appointIdNameManager.loadNameByNumber(stationid));
		station.setHsStates(hsStateFactory.createHsStates(stationid, eb.getConstraintTypes(), tbtype, eb.getUnitNums(),
				eb.getStartTime(), eb.getTbNums()));//此处设置状态值的时候，如果在数据库中已经给定一些月份有不同于其他月份的约束，也会初始化进来，比如汛限水位约束
		station.setStationCurves(stationCurveManager.createStationCurve(stationid / 10 * 10, eb.getCurveTypes()));
		station.getHsSpec()
				.setStorageDead(station.getStationCurves().getCapacityByLevel(station.getHsSpec().getLevelDead()));
		station.getHsSpec()
				.setStorageRegulating(
						station.getHsSpec().getStorageRegulating() <= 0
								? station.getStationCurves().getCapacityByLevel(station.getHsSpec().getLevelNormal())
										- station.getHsSpec().getStorageDead()
								: station.getHsSpec().getStorageRegulating());
		return station;
	}

	/**
	 * @param eb
	 * @param floodControlBeginPeriod
	 * @param floodControlEndPeriod
	 * @return  构造带汛限水位的电站
	 */
	public HydroStation constructStation(EngineerBureau eb, int floodLimitBeginPeriod, int floodLimitEndPeriod) {
		HydroStation station = constructStation(eb);
		List<HStationState> states = station.getHsStates();
		BuildConstraint.valueFloodLimitLevelForStates(eb.getTbType(), states,
				station.getHsSpec().getLevelFloodLimiting(), floodLimitBeginPeriod, floodLimitEndPeriod);
		return station;
	}
}
