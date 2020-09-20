package com.wenyu.factory.state;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.hydrostation.BulidAState;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.constraint.BuildConstraint;
import com.wenyu.hydroelements.hydrostation.constraint.TimeSeqConstraint;
import com.wenyu.hydroelements.operation.basic.HydroDateUtil;
import com.wenyu.service.TimeSeqConstraintManager;
import com.wenyu.service.TimeSeqCurveManager;

/**
 * 完全根据数据接口的原始数据来初始化（相对于运算中根据上下游水力联系初始化hsStates而言，即天然状态下的径流数据），
 * 初始化所有时段的状态值（相对于优化而言，优化有时候只初始化开始时段的初始值，后面的靠实际数学运算）
 * 
 * @author OwenYY
 *
 */
@Component("hsStateOriginFactory")
public class HsStateOriginFactory {
	private TimeSeqCurveManager timeSeqCurveManager;
	private TimeSeqConstraintManager timeSeqConstraintManager;

	public List<HStationState> createHsStates(int stationId, int[] constraintTypes, String tbType, int unitNums,
			String startTime, int tbNums) {
		LocalDateTime startDateTime = LocalDateTime.parse(startTime);
		LocalDateTime endDateTime = HydroDateUtil.calEndTime(startDateTime, tbType, unitNums, tbNums);

		TimeSeqCurve runoffTsc = timeSeqCurveManager.createTimeSeqCurve(stationId / 10 * 10, 1000, tbType, unitNums,
				startDateTime, endDateTime);// 径流数据，除以10再乘以10是因为，设计上12010100编号的电站和12010101的电站只是水电站特征参数不一样，水库的特性参数，径流过程等都是一样的
		TimeSeqCurve runoffRangeTsc = timeSeqCurveManager.createTimeSeqCurve(stationId / 10 * 10, 1001, tbType,
				unitNums, startDateTime, endDateTime);// 径流数据，除以10再乘以10是因为，设计上12010100编号的电站和12010101的电站只是水电站特征参数不一样，水库的特性参数，径流过程等都是一样的

		TimeSeqConstraint tsConstraint = timeSeqConstraintManager.createTimeSeqCurve(stationId, constraintTypes, tbType,
				unitNums, startDateTime, tbNums);

		List<HStationState> hsStates = new ArrayList<HStationState>();
		for (int i = 0; i < tbNums; i++) {
			HStationState hsState = new HStationState();
			if (runoffTsc != null)
				BulidAState.buildRunoff(runoffTsc.getDates().getTbSeq().get(i), (runoffTsc.getDatas().getArray())[i],
						hsState);
			if (runoffRangeTsc != null)
				BulidAState.buildRunoffRange(runoffRangeTsc.getDates().getTbSeq().get(i),
						(runoffRangeTsc.getDatas().getArray())[i], hsState);
			BuildConstraint.buildConstraint(tsConstraint.getConstraints().get(i).getConstraintItems(), hsState);
			hsStates.add(hsState);
		}
		return hsStates;
	}

	/**
	 * @param stationId
	 * @param constraintTypes
	 * @param tbType
	 * @param unitNums
	 * @param startTime
	 * @param tbNums
	 * @param floodLimitLevel
	 * @param floodLimitStart
	 * @param floodLimitEnd
	 * @return 加上汛限水位约束
	 */
	public List<HStationState> createHsStatesWithFloodLimit(int stationId, int[] constraintTypes, String tbType,
			int unitNums, String startTime, int tbNums, double floodLimitLevel, int floodLimitStart,
			int floodLimitEnd) {
		List<HStationState> hsStates = createHsStates(stationId, constraintTypes, tbType, unitNums, startTime, tbNums);
		BuildConstraint.valueFloodLimitLevelForStates(tbType, hsStates, floodLimitLevel, floodLimitStart,
				floodLimitEnd);
		return hsStates;
	}

	/**
	 * @return the timeSeqCurveManager
	 */
	public TimeSeqCurveManager getTimeSeqCurveManager() {
		return timeSeqCurveManager;
	}

	/**
	 * @param timeSeqCurveManager
	 *            the timeSeqCurveManager to set
	 */
	@Resource
	public void setTimeSeqCurveManager(TimeSeqCurveManager timeSeqCurveManager) {
		this.timeSeqCurveManager = timeSeqCurveManager;
	}

	/**
	 * @return the timeSeqConstraintManager
	 */
	public TimeSeqConstraintManager getTimeSeqConstraintManager() {
		return timeSeqConstraintManager;
	}

	/**
	 * @param timeSeqConstraintManager
	 *            the timeSeqConstraintManager to set
	 */
	@Resource
	public void setTimeSeqConstraintManager(TimeSeqConstraintManager timeSeqConstraintManager) {
		this.timeSeqConstraintManager = timeSeqConstraintManager;
	}

}
