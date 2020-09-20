package com.wenyu.hydroelements.hydrostation;

/**
 * 水库的特性数据，不包括曲线数据和防洪相关，后面需要直接把这个类作为一个组成部分
 * 
 * @author OwenYY
 */
public class HStationSpec {
	private int id;
	private String name;
	/** 校核洪水位 */
	private double levelFloodCheck;
	/** 设计洪水位 */
	private double levelFloodDesign;
	/** 防洪高水位 */
	private double levelFloodControl;
	/** 正常蓄水位 */
	private double levelNormal;
	/** 防洪限制水位 */
	private double levelFloodLimiting;
	/** 死水位 */
	private double levelDead;

	/** 总库容(亿立方) */
	private double storageTotal;
	/** 调洪库容 */
	private double storageControl;
	/** 防洪库容 */
	private double storageProtect;
	/** 调节库容 */
	private double storageRegulating;
	/** 死库容 */
	private double storageDead;

	/** 多年平均发电量(亿度) **/
	private double powerProductionMeanAnnual;
	/** 装机容量(万千瓦) */
	private double powerInstalled;
	/** 保证出力(万千瓦) */
	private double outputGuaranteed;
	/** 平均出力系数 **/
	private double outputCoefficient;
	/** 机组类型数量 */
	private int unitTypeNum;
	/** 机组台数 */
	private int unitNum;
	/** 最大发电引用流量 */
	private double generateInflowMax;
	/** 溢洪道底高程 */
	private double elevationSpillwayBottom;
	/** 极限最大下泄能力 */
	private double dischargeAbilityMax;
	/** 要求最小下泄流量 */
	private double dischargeDemandMin;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the levelFloodCheck
	 */
	public double getLevelFloodCheck() {
		return levelFloodCheck;
	}

	/**
	 * @param levelFloodCheck
	 *            the levelFloodCheck to set
	 */
	public void setLevelFloodCheck(double levelFloodCheck) {
		this.levelFloodCheck = levelFloodCheck;
	}

	/**
	 * @return the levelFloodDesign
	 */
	public double getLevelFloodDesign() {
		return levelFloodDesign;
	}

	/**
	 * @param levelFloodDesign
	 *            the levelFloodDesign to set
	 */
	public void setLevelFloodDesign(double levelFloodDesign) {
		this.levelFloodDesign = levelFloodDesign;
	}

	/**
	 * @return the levelFloodControl
	 */
	public double getLevelFloodControl() {
		return levelFloodControl;
	}

	/**
	 * @param levelFloodControl
	 *            the levelFloodControl to set
	 */
	public void setLevelFloodControl(double levelFloodControl) {
		this.levelFloodControl = levelFloodControl;
	}

	/**
	 * @return the levelNormal
	 */
	public double getLevelNormal() {
		return levelNormal;
	}

	/**
	 * @param levelNormal
	 *            the levelNormal to set
	 */
	public void setLevelNormal(double levelNormal) {
		this.levelNormal = levelNormal;
	}

	/**
	 * @return the levelFloodLimiting
	 */
	public double getLevelFloodLimiting() {
		return levelFloodLimiting;
	}

	/**
	 * @param levelFloodLimiting
	 *            the levelFloodLimiting to set
	 */
	public void setLevelFloodLimiting(double levelFloodLimiting) {
		this.levelFloodLimiting = levelFloodLimiting;
	}

	/**
	 * @return the levelDead
	 */
	public double getLevelDead() {
		return levelDead;
	}

	/**
	 * @param levelDead
	 *            the levelDead to set
	 */
	public void setLevelDead(double levelDead) {
		this.levelDead = levelDead;
	}

	/**
	 * @return the storageTotal
	 */
	public double getStorageTotal() {
		return storageTotal;
	}

	/**
	 * @param storageTotal
	 *            the storageTotal to set
	 */
	public void setStorageTotal(double storageTotal) {
		this.storageTotal = storageTotal;
	}

	/**
	 * @return the storageControl
	 */
	public double getStorageControl() {
		return storageControl;
	}

	/**
	 * @param storageControl
	 *            the storageControl to set
	 */
	public void setStorageControl(double storageControl) {
		this.storageControl = storageControl;
	}

	/**
	 * @return the storageProtect
	 */
	public double getStorageProtect() {
		return storageProtect;
	}

	/**
	 * @param storageProtect
	 *            the storageProtect to set
	 */
	public void setStorageProtect(double storageProtect) {
		this.storageProtect = storageProtect;
	}

	/**
	 * @return the storageRegulating
	 */
	public double getStorageRegulating() {
		return storageRegulating;
	}

	/**
	 * @param storageRegulating
	 *            the storageRegulating to set
	 */
	public void setStorageRegulating(double storageRegulating) {
		this.storageRegulating = storageRegulating;
	}

	/**
	 * @return the storageDead
	 */
	public double getStorageDead() {
		return storageDead;
	}

	/**
	 * @param storageDead
	 *            the storageDead to set
	 */
	public void setStorageDead(double storageDead) {
		this.storageDead = storageDead;
	}

	/**
	 * @return the powerProductionMeanAnnual
	 */
	public double getPowerProductionMeanAnnual() {
		return powerProductionMeanAnnual;
	}

	/**
	 * @param powerProductionMeanAnnual
	 *            the powerProductionMeanAnnual to set
	 */
	public void setPowerProductionMeanAnnual(double powerProductionMeanAnnual) {
		this.powerProductionMeanAnnual = powerProductionMeanAnnual;
	}

	/**
	 * @return the powerInstalled
	 */
	public double getPowerInstalled() {
		return powerInstalled;
	}

	/**
	 * @param powerInstalled
	 *            the powerInstalled to set
	 */
	public void setPowerInstalled(double powerInstalled) {
		this.powerInstalled = powerInstalled;
	}

	/**
	 * @return the outputGuaranteed
	 */
	public double getOutputGuaranteed() {
		return outputGuaranteed;
	}

	/**
	 * @param outputGuaranteed
	 *            the outputGuaranteed to set
	 */
	public void setOutputGuaranteed(double outputGuaranteed) {
		this.outputGuaranteed = outputGuaranteed;
	}

	/**
	 * @return the outputCoefficient
	 */
	public double getOutputCoefficient() {
		return outputCoefficient;
	}

	/**
	 * @param outputCoefficient
	 *            the outputCoefficient to set
	 */
	public void setOutputCoefficient(double outputCoefficient) {
		this.outputCoefficient = outputCoefficient;
	}

	/**
	 * @return the unitTypeNum
	 */
	public int getUnitTypeNum() {
		return unitTypeNum;
	}

	/**
	 * @param unitTypeNum
	 *            the unitTypeNum to set
	 */
	public void setUnitTypeNum(int unitTypeNum) {
		this.unitTypeNum = unitTypeNum;
	}

	/**
	 * @return the unitNum
	 */
	public int getUnitNum() {
		return unitNum;
	}

	/**
	 * @param unitNum
	 *            the unitNum to set
	 */
	public void setUnitNum(int unitNum) {
		this.unitNum = unitNum;
	}

	/**
	 * @return the generateInflowMax
	 */
	public double getGenerateInflowMax() {
		return generateInflowMax;
	}

	/**
	 * @param generateInflowMax
	 *            the generateInflowMax to set
	 */
	public void setGenerateInflowMax(double generateInflowMax) {
		this.generateInflowMax = generateInflowMax;
	}

	public double getElevationSpillwayBottom() {
		return elevationSpillwayBottom;
	}

	public void setElevationSpillwayBottom(double elevationSpillwayBottom) {
		this.elevationSpillwayBottom = elevationSpillwayBottom;
	}

	public double getDischargeAbilityMax() {
		return dischargeAbilityMax;
	}

	public void setDischargeAbilityMax(double dischargeAbilityMax) {
		this.dischargeAbilityMax = dischargeAbilityMax;
	}

	public double getDischargeDemandMin() {
		return dischargeDemandMin;
	}

	public void setDischargeDemandMin(double dischargeDemandMin) {
		this.dischargeDemandMin = dischargeDemandMin;
	}

}
