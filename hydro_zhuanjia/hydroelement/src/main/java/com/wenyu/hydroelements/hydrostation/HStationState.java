package com.wenyu.hydroelements.hydrostation;

import java.time.LocalDateTime;

/**
 * 借鉴306的TimeBean
 * 水电站整体的时段变量（pojo型bean），日期采用java8的日期
 * @author OwenYY
 */
public class HStationState{
	
	//**************************基本信息*********************************
	/**起始时间*/
	private LocalDateTime timeStart;
	/**终止时间*/
	private LocalDateTime timeEnd;
	/**时段长度(按秒)*/
	private int timeLength;
		
	//**************************输入条件*********************************
	/**（输入）预报来水**/
	private double inflowForecast;
	/**（输入）还原径流*/
	private double inflowOrigin;
	/**（输入）还现径流*/
	private double inflowPresent;
	/**（输入）实际来水（参与运算的来水）**/
	private double inflowReal;
	/**（输入）区间来水**/
	private double inflowRange;
	
	///****************约束，约束的廊道一定要先确定好，在外面初始化状态量时必须把硬约束确定住********************
	/**（输入）最大下泄能力**/
	private double outflowMax;
	/**（输入）最小下泄要求**/
	private double outflowMin;
	/**
	 * （输入）最大发电流量
	 */
	private double outflowGeneMax;
	/**
	 * （输入）最小发电要求流量
	 */
	private double outflowGeneMin;
	/**（输入）最大出力**/
	private double outputMax;
	/**（输入）最小出力**/
	private double outputMin;
	/**（输入）最高下游水位**/
	private double levelDownMax;
	/**（输入）最低下游水位**/
	private double levelDownMin;
	/**（输入）时段末最大水位**/
	@Deprecated
	private double levelMax;//！！！！！这是个旧版，需要新的版本！！有时候时段初和时段末的约束不一致！！有些时候，计算会采用逆序计算，只设置时段末水位约束对于有些程序而言不易编写，所以设置初末水位约束都很重要
	/**（输入）时段末最小水位**/
	@Deprecated
	private double levelMin;//时段末的水位范围，模拟运行查调度图时才会得到，初始设定为正常蓄水位和死水位
	/**（输入）最大水头**/
	private double headMax;
	/**（输入）最小水头**/
	private double headMin;//时段末的水位范围，模拟运行查调度图时才会得到，初始设定为正常蓄水位和死水位
	
	//**************************调度结果*********************************
	/**（输出）初水位**/
	private double levelBegin;
	/**（输出）末水位**/
	private double levelEnd;
	/**（输出）下游水位**/
	private double levelDown;
	/**（输出）下泄流量**/
	private double outflow;//某时刻（段(初)）水库（平均）出流
	/**（输出）实际出力**/
	private double output;//某时刻（段）电站的（平均）出力
	/**（输出）净水头**/
	private double headPure;//某时刻（段）电站的（平均）净水头
	/**（输出）弃水流量**/
	private double outflowDesert;//某时刻（段）电站的（平均）弃水流量
	/**（输出）发电量**/
	private double generation;//某时刻（段）电站的（平均）发电量
	/**（输出）发电流量**/
	private double outflowGeneration;//某时刻（段）电站的（平均）引用发电流量
	/**（输出）毛水头**/
	private double headGross;//某时刻（段）电站的（平均）毛水头
	/**（输出）水头损失**/
	private double headLoss;//某时刻（段）电站的（平均）水头损失
	/**（输出）水量耗水率**/
	private double waterRate;
	
	public LocalDateTime getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(LocalDateTime timeStart) {
		this.timeStart = timeStart;
	}
	public LocalDateTime getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(LocalDateTime timeEnd) {
		this.timeEnd = timeEnd;
	}
	public int getTimeLength() {
		return timeLength;
	}
	public void setTimeLength(int timeLength) {
		this.timeLength = timeLength;
	}
	
	/**
	 * @return the inflowForecast
	 */
	public double getInflowForecast() {
		return inflowForecast;
	}
	/**
	 * @param inflowForecast the inflowForecast to set
	 */
	public void setInflowForecast(double inflowForecast) {
		this.inflowForecast = inflowForecast;
	}
	/**
	 * @return the inflowOrigin
	 */
	public double getInflowOrigin() {
		return inflowOrigin;
	}
	/**
	 * @param inflowOrigin the inflowOrigin to set
	 */
	public void setInflowOrigin(double inflowOrigin) {
		this.inflowOrigin = inflowOrigin;
	}
	/**
	 * @return the inflowPresent
	 */
	public double getInflowPresent() {
		return inflowPresent;
	}
	/**
	 * @param inflowPresent the inflowPresent to set
	 */
	public void setInflowPresent(double inflowPresent) {
		this.inflowPresent = inflowPresent;
	}
	public double getInflowReal() {
		return inflowReal;
	}

	public double getLevelDownMax() {
		return levelDownMax;
	}
	public void setLevelDownMax(double levelDownMax) {
		this.levelDownMax = levelDownMax;
	}
	public double getLevelDownMin() {
		return levelDownMin;
	}

	public void setLevelDownMin(double levelDownMin) {
		this.levelDownMin = levelDownMin;
	}

	public void setInflowReal(double inflowReal) {
		this.inflowReal = inflowReal;
	}

	public double getInflowRange() {
		return inflowRange;
	}
	public void setInflowRange(double inflowRange) {
		this.inflowRange = inflowRange;
	}
	public double getOutflowMax() {
		return outflowMax;
	}
	public void setOutflowMax(double outflowMax) {
		this.outflowMax = outflowMax;
	}
	public double getOutflowMin() {
		return outflowMin;
	}
	public void setOutflowMin(double outflowMin) {
		this.outflowMin = outflowMin;
	}
	/**
	 * @return the outflowGeneMax
	 */
	public double getOutflowGeneMax() {
		return outflowGeneMax;
	}
	/**
	 * @param outflowGeneMax the outflowGeneMax to set
	 */
	public void setOutflowGeneMax(double outflowGeneMax) {
		this.outflowGeneMax = outflowGeneMax;
	}
	/**
	 * @return the outflowGeneMin
	 */
	public double getOutflowGeneMin() {
		return outflowGeneMin;
	}
	/**
	 * @param outflowGeneMin the outflowGeneMin to set
	 */
	public void setOutflowGeneMin(double outflowGeneMin) {
		this.outflowGeneMin = outflowGeneMin;
	}
	public double getOutputMax() {
		return outputMax;
	}
	public void setOutputMax(double outputMax) {
		this.outputMax = outputMax;
	}
	
	public double getOutputMin() {
		return outputMin;
	}

	public void setOutputMin(double outputMin) {
		this.outputMin = outputMin;
	}

	public double getLevelMax() {
		return levelMax;
	}
	public void setLevelMax(double levelMax) {
		this.levelMax = levelMax;
	}
	public double getLevelMin() {
		return levelMin;
	}
	public void setLevelMin(double levelMin) {
		this.levelMin = levelMin;
	}
	public double getHeadMax() {
		return headMax;
	}

	public void setHeadMax(double headMax) {
		this.headMax = headMax;
	}

	public double getHeadMin() {
		return headMin;
	}

	public void setHeadMin(double headMin) {
		this.headMin = headMin;
	}

	public double getLevelBegin() {
		return levelBegin;
	}
	public void setLevelBegin(double levelBegin) {
		this.levelBegin = levelBegin;
	}
	public double getLevelEnd() {
		return levelEnd;
	}
	public void setLevelEnd(double levelEnd) {
		this.levelEnd = levelEnd;
	}
	public double getLevelDown() {
		return levelDown;
	}
	public void setLevelDown(double levelDown) {
		this.levelDown = levelDown;
	}
	public double getOutflow() {
		return outflow;
	}
	public void setOutflow(double outflow) {
		this.outflow = outflow;
	}
	public double getOutput() {
		return output;
	}
	public void setOutput(double output) {
		this.output = output;
	}
	public double getHeadPure() {
		return headPure;
	}
	public void setHeadPure(double pureHead) {
		this.headPure = pureHead;
	}
	public double getOutflowDesert() {
		return outflowDesert;
	}
	public void setOutflowDesert(double outflowDesert) {
		this.outflowDesert = outflowDesert;
	}
	public double getGeneration() {
		return generation;
	}
	public void setGeneration(double generation) {
		this.generation = generation;
	}
	public double getOutflowGeneration() {
		return outflowGeneration;
	}
	public void setOutflowGeneration(double outflowGeneration) {
		this.outflowGeneration = outflowGeneration;
	}
	public double getHeadGross() {
		return headGross;
	}
	public void setHeadGross(double headGross) {
		this.headGross = headGross;
	}
	public double getHeadLoss() {
		return headLoss;
	}
	public void setHeadLoss(double headLoss) {
		this.headLoss = headLoss;
	}
	public double getWaterRate() {
		return waterRate;
	}
	public void setWaterRate(double waterRate) {
		this.waterRate = waterRate;
	} 
}
