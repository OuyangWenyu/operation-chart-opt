package com.wenyu.factory;

/**
 * 本来想把它当工厂的基本类，然后觉得还是写成接口好，所以这个类就退化成了一个提供构造电站参数的类
 * 主要是这些参数：电站ID-stationId;电站曲线类型curveTypes;
	电站约束类型constraintTypes;
	运算的时段类型tbType;
	单位时段的该尺度时段个数unitNums;
	开始时间startTime;
	时段个数tbNums;
 * @author  OwenYY
 *
 */
public class EngineerBureau {
	private int stationId;
	private int[] curveTypes;
	private int[] constraintTypes;
	private String tbType;
	private int unitNums;
	private String startTime;
	private int tbNums;
	/**
	 * @return the stationId
	 */
	public int getStationId() {
		return stationId;
	}
	/**
	 * @param stationId the stationId to set
	 */
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	/**
	 * @return the curveType
	 */
	public int[] getCurveTypes() {
		return curveTypes;
	}
	/**
	 * @param curveType the curveType to set
	 */
	public void setCurveTypes(int[] curveTypes) {
		this.curveTypes = curveTypes;
	}
	/**
	 * @return the constraintTypes
	 */
	public int[] getConstraintTypes() {
		return constraintTypes;
	}
	/**
	 * @param constraintTypes the constraintTypes to set
	 */
	public void setConstraintTypes(int[] constraintTypes) {
		this.constraintTypes = constraintTypes;
	}
	/**
	 * @return the tbType
	 */
	public String getTbType() {
		return tbType;
	}
	/**
	 * @param tbType the tbType to set
	 */
	public void setTbType(String tbType) {
		this.tbType = tbType;
	}
	/**
	 * @return the unitNums
	 */
	public int getUnitNums() {
		return unitNums;
	}
	/**
	 * @param unitNums the unitNums to set
	 */
	public void setUnitNums(int unitNums) {
		this.unitNums = unitNums;
	}
	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the tbNums
	 */
	public int getTbNums() {
		return tbNums;
	}
	/**
	 * @param tbNums the tbNums to set
	 */
	public void setTbNums(int tbNums) {
		this.tbNums = tbNums;
	}
	
	/**
	 * @param stationId  电站编号
	 * @param curveTypes 曲线类型
	 * @param constraintTypes 约束类型
	 * @param tbType 时段类型
	 * @param unitNums 单位时段包含的时段个数
	 * @param startTime 起始时间
	 * @param tbNums 总共有多少个单位时段
	 */
	public EngineerBureau(int stationId, int[] curveTypes, int[] constraintTypes, String tbType, int unitNums,
			String startTime, int tbNums) {
		super();
		this.stationId = stationId;
		this.curveTypes = curveTypes;
		this.constraintTypes = constraintTypes;
		this.tbType = tbType;
		this.unitNums = unitNums;
		this.startTime = startTime;
		this.tbNums = tbNums;
	}
	public void initialize() {
        // do some initialization work
    }
}
