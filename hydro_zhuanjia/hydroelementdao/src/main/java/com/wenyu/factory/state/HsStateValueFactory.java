package com.wenyu.factory.state;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.curve.TimeSeqCurveFactory;
import com.wenyu.hydroelements.hydrostation.BulidAState;
import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.constraint.BuildConstraint;
import com.wenyu.hydroelements.hydrostation.constraint.TimeSeqConstraint;
import com.wenyu.hydroelements.hydrostation.constraint.TimeSeqConstraintFactory;
import com.wenyu.hydroelements.operation.basic.HydroDateUtil;

/**
 * 运算中需要根据提供的值构造HsState的时候使用，
 * 各种约束直接从hsSpec里面取相应数据
 * @author  OwenYY
 *
 */
public class HsStateValueFactory {
	private HStationSpec hsSpec;
	private String tbType;
	private int[] constrainttypes;
	private String startTime;
	private int tbNums;

	public HsStateValueFactory(HStationSpec hsSpec, String tbType, int[] constrainttypes, String startTime,
			int tbNums) {
		super();
		this.hsSpec = hsSpec;
		this.tbType = tbType;
		this.constrainttypes = constrainttypes;
		this.startTime = startTime;
		this.tbNums = tbNums;
	}
	public HStationSpec getHsSpec() {
		return hsSpec;
	}
	public void setHsSpec(HStationSpec hsSpec) {
		this.hsSpec = hsSpec;
	}
	public String getTbType() {
		return tbType;
	}
	public void setTbType(String tbType) {
		this.tbType = tbType;
	}
	public int[] getConstrainttypes() {
		return constrainttypes;
	}
	public void setConstrainttypes(int[] constrainttypes) {
		this.constrainttypes = constrainttypes;
	}
	/**
	 * @param inflows
	 * @return 根据输入的数据构造输入值，以后可以写成自行判断类型来生成相应状态的数据的
	 */
	public List<HStationState> createHsStates(double[] inflows) {
		LocalDateTime startDateTime = LocalDateTime.parse(startTime);
		TimeSeqCurveFactory timeSeqCurveFactory = new TimeSeqCurveFactory(tbType, 1, startDateTime, inflows.length, inflows);
		TimeSeqCurve runoffTsc = timeSeqCurveFactory.curvePlotting();
		TimeSeqConstraintFactory timeSeqConstraintFactory=new TimeSeqConstraintFactory(hsSpec);
		TimeSeqConstraint tsConstraint = timeSeqConstraintFactory.constructByHsSpec(constrainttypes, tbType, 1, startDateTime, tbNums);
		List<HStationState> hsStates = new ArrayList<HStationState>();
		for (int i = 0; i < tbNums; i++) {
			HStationState hsState = new HStationState();
			BulidAState.buildRunoff(runoffTsc.getDates().getTbSeq().get(i), (runoffTsc.getDatas().getArray())[i], hsState);
			BuildConstraint.buildConstraint(tsConstraint.getConstraints().get(i).getConstraintItems(), hsState);
			hsStates.add(hsState);
		}
		return hsStates;
	}
	/**
	 * @param q_in
	 * @param floodControlBegin
	 * @param floodControlEnd
	 * @return  根据输入的径流及汛限水位约束时间初末进行状态设置
	 */
	public List<HStationState> createHsStates(double[] inflows, LocalDate floodControlBegin, LocalDate floodControlEnd) {
		LocalDateTime startDateTime = LocalDateTime.parse(startTime);
		TimeSeqCurveFactory timeSeqCurveFactory = new TimeSeqCurveFactory(tbType, 1, startDateTime, inflows.length, inflows);
		TimeSeqCurve runoffTsc = timeSeqCurveFactory.curvePlotting();
		TimeSeqConstraintFactory timeSeqConstraintFactory=new TimeSeqConstraintFactory(hsSpec);
		TimeSeqConstraint tsConstraint = timeSeqConstraintFactory.constructByHsSpec(constrainttypes, tbType, 1, startDateTime, tbNums);
		List<HStationState> hsStates = new ArrayList<HStationState>();
		for (int i = 0; i < tbNums; i++) {
			HStationState hsState = new HStationState();
			BulidAState.buildRunoff(runoffTsc.getDates().getTbSeq().get(i), (runoffTsc.getDatas().getArray())[i], hsState);
			BuildConstraint.buildConstraint(tsConstraint.getConstraints().get(i).getConstraintItems(), hsState);
			hsStates.add(hsState);
		}
		if(tbType=="MONTH")
			BuildConstraint.valueFloodLimitLevelForStates(tbType, hsStates, hsSpec.getLevelFloodLimiting(), floodControlBegin.getMonthValue(), floodControlEnd.getMonthValue());
		else
			BuildConstraint.valueFloodLimitLevelForStates(tbType, hsStates, hsSpec.getLevelFloodLimiting(), HydroDateUtil.getDecad(floodControlBegin), HydroDateUtil.getDecad(floodControlEnd));
		return hsStates;
	}
}
