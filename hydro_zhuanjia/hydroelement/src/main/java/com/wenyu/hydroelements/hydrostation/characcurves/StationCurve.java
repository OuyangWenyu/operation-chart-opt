package com.wenyu.hydroelements.hydrostation.characcurves;

import java.io.Serializable;

import com.wenyu.hydroelements.curve.DoubleCurve;
import com.wenyu.hydroelements.curve.TripleCurve;


/**
 * 电站的基本特性曲线
 *
 */
public class StationCurve implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 6649501994266344067L;

	private int stationID;

	/**水位库容 按照水位从小到大排列*/
	private DoubleCurve levelCapacityCurve;
	/**下泄流量下游水位*/
	private DoubleCurve flowLeveldownCurve;
	/**下泄流量下游水位（有下游电站顶托的水库使用）*/
	private TripleCurve threeFlowLeveldownCurve;
	/**下泄能力水位*/
	private DoubleCurve dischargelevelCurve;
	/**下游水位流量关系*/
	private DoubleCurve levelflowCurve;
	/**水头预想出力*/
	private DoubleCurve headMaxpowerCurve;
	/**全站NHQ曲线*/
	private TripleCurve totalNHQ;


	/****************通用查询方法****************/

	/**
	 * 水位查库容
	 *
	 * @param waterLevel 水位 单位米
	 * @return 库容 单位百万立方米
	 */
	public  double getCapacityByLevel(double waterLevel) {
		return getLevel_Capacity().getV0ByV1(waterLevel);
	}

	/**
	 * 库容查水位
	 *
	 * @param capacity 库容
	 * @return 水位
	 */
	public  double getLevelByCapacity(double capacity) {
		return getLevel_Capacity().getV1ByV0(capacity);
	}

	/**
	 * 水位查下泄能力
	 * @param level 水位
	 * @return 下泄能力
	 */
	public  double getDischargeByLevel(double level) {
		return getDischarge_Level().getV1ByV0(level);
	}
	/**
	 * 下游水位查下泄流量
	 * @param level 水位
	 * @return 流量
	 */
	public  double getflowByLevel(double level) {
		return getflow_Level().getV0ByV1(level);
	}

	/**
	 * 下泄流量查下游水位(适用有顶托水库)
	 *
	 * @param downstreamlevel 下游水位
	 * @param outflow 下泄流量
	 * @return
	 */
	public  double getLeveldownByOutflow(double downstreamlevel, double outflow) {

		return getThreeFlowLeveldownCurve().getV2ByV0V1(downstreamlevel,outflow);
	}

	/**
	 * 下泄流量查下游水位(适用无顶托水库)
	 *
	 * @param outflow 下泄流量
	 * @return
	 */
	public  double getLeveldownByOutflow(double outflow) {
		return getFlow_LevelDown().getV1ByV0(outflow);
	}

	/**
	 * 通过水头查询机组预想出力
	 *
	 * @param H 水头
	 * @return
	 */
	public  double getExpectPowerByHead(double H) {
		return headMaxpowerCurve.getV1ByV0(H);
	}


	/**
	 * (全站)获取全站最大下泄流量
	 * @param head
	 * @return
	 */
	public double getMaxFlowByHead(double head)
	{
		double[] powers= getTotalNHQ().getV1sByV0(head);
		return powers[powers.length-1];
	}

	/**
	 * (全站)根据毛水头和发电流量查出力
	 * @param head
	 * @param flow
	 * @return
	 */
	public double getPowerByHeadFlow(double head,double flow)
	{
		return getTotalNHQ().getV2ByV0V1(head, flow);
	}

	/**
	 * (全站)根据毛水头和出力查发电流量
	 * @param head
	 * @param power
	 * @return
	 */
	public double getFlowByHeadPower(double head,double power)
	{
		return getTotalNHQ().getV1ByV0V2(head, power);
	}


	private synchronized DoubleCurve getLevel_Capacity() {
		return levelCapacityCurve;
	}

	private synchronized DoubleCurve getDischarge_Level() {
		return dischargelevelCurve;
	}
	private synchronized DoubleCurve getflow_Level() {
		return levelflowCurve;
	}
	public synchronized TripleCurve getThreeFlowLeveldownCurve() {
		return threeFlowLeveldownCurve;
	}

	public void setThreeFlowLeveldownCurve(TripleCurve threeFlowLeveldownCurve) {
		this.threeFlowLeveldownCurve = threeFlowLeveldownCurve;
	}

	private synchronized DoubleCurve getFlow_LevelDown() {
		return flowLeveldownCurve;
	}


	


	/**
	 * @return the headMaxpowerCurve
	 */
	public DoubleCurve getHeadMaxpowerCurve() {
		return headMaxpowerCurve;
	}

	/**
	 * @param headMaxpowerCurve the headMaxpowerCurve to set
	 */
	public void setHeadMaxpowerCurve(DoubleCurve headMaxpowerCurve) {
		this.headMaxpowerCurve = headMaxpowerCurve;
	}

	/**
	 * 获取机组的NHQ曲线,一台机组则为为0
	 * @param num
	 * @return
	 */
	public synchronized TripleCurve getTotalNHQ() {
		return totalNHQ;
	}


	public int getStationID() {
		return stationID;
	}

	public void setStationID(int stationID) {
		this.stationID = stationID;
	}

	public  void clean(){
		flowLeveldownCurve = null;
		headMaxpowerCurve = null;
		levelCapacityCurve = null;
		totalNHQ = null;
	}

	public DoubleCurve getLevelCapacityCurve() {
		return levelCapacityCurve;
	}

	public void setLevelCapacityCurve(DoubleCurve levelCapacityCurve) {
		this.levelCapacityCurve = levelCapacityCurve;
	}

	public DoubleCurve getFlowLeveldownCurve() {
		return flowLeveldownCurve;
	}

	public void setFlowLeveldownCurve(DoubleCurve flowLeveldownCurve) {
		this.flowLeveldownCurve = flowLeveldownCurve;
	}

	public DoubleCurve getDischargelevelCurve() {
		return dischargelevelCurve;
	}

	public void setDischargelevelCurve(DoubleCurve dischargelevelCurve) {
		this.dischargelevelCurve = dischargelevelCurve;
	}
	public DoubleCurve getLevelflowCurve() {
		return levelflowCurve;
	}

	public void setLevelflowCurve(DoubleCurve levelflowCurve) {
		this.levelflowCurve = levelflowCurve;
	}


	public void setTotalNHQ(TripleCurve totalNHQ) {
		this.totalNHQ = totalNHQ;
	}

}
