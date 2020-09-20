package com.wenyu.service.excelutil.fromdbway1;

/**
 * 输出到excel时用的类
 *
 */
public class OutputStation {
	private String stationname;
	private double levelNormal;
	private double levelDeath;
	private double levelDrawdown;//消落水位
	private double capacityNormal;
	private double capacityDead;
	private double capacityRegulation;//调节库容
	private double installedCapacity;//装机容量
	private double headMax;
	private double headMin;
	private double generationYear;//年电量
	private double generationLowflow;//枯期电量
	private double generationHighflow;//丰期电量
	private double radioHighLow;//丰枯电量比
	private double hoursUse;//年利用小时数
	private double outputWarrantedStatistics;//模拟运行统计保证出力
	/*
	 * 加上每个时段的各个指标的结果，也输出 
	 */
	
	
	/**
	 * @return the stationname
	 */
	public String getStationname() {
		return stationname;
	}


	/**
	 * @param stationname the stationname to set
	 */
	public void setStationname(String stationname) {
		this.stationname = stationname;
	}


	/**
	 * @return the levelNormal
	 */
	public double getLevelNormal() {
		return levelNormal;
	}


	/**
	 * @param levelNormal the levelNormal to set
	 */
	public void setLevelNormal(double levelNormal) {
		this.levelNormal = levelNormal;
	}


	/**
	 * @return the levelDeath
	 */
	public double getLevelDeath() {
		return levelDeath;
	}


	/**
	 * @param levelDeath the levelDeath to set
	 */
	public void setLevelDeath(double levelDeath) {
		this.levelDeath = levelDeath;
	}


	/**
	 * @return the levelDrawdown
	 */
	public double getLevelDrawdown() {
		return levelDrawdown;
	}


	/**
	 * @param levelDrawdown the levelDrawdown to set
	 */
	public void setLevelDrawdown(double levelDrawdown) {
		this.levelDrawdown = levelDrawdown;
	}


	/**
	 * @return the capacityNormal
	 */
	public double getCapacityNormal() {
		return capacityNormal;
	}


	/**
	 * @param capacityNormal the capacityNormal to set
	 */
	public void setCapacityNormal(double capacityNormal) {
		this.capacityNormal = capacityNormal;
	}


	/**
	 * @return the capacityDead
	 */
	public double getCapacityDead() {
		return capacityDead;
	}


	/**
	 * @param capacityDead the capacityDead to set
	 */
	public void setCapacityDead(double capacityDead) {
		this.capacityDead = capacityDead;
	}


	/**
	 * @return the capacityRegulation
	 */
	public double getCapacityRegulation() {
		return capacityRegulation;
	}


	/**
	 * @param capacityRegulation the capacityRegulation to set
	 */
	public void setCapacityRegulation(double capacityRegulation) {
		this.capacityRegulation = capacityRegulation;
	}


	/**
	 * @return the installedCapacity
	 */
	public double getInstalledCapacity() {
		return installedCapacity;
	}


	/**
	 * @param installedCapacity the installedCapacity to set
	 */
	public void setInstalledCapacity(double installedCapacity) {
		this.installedCapacity = installedCapacity;
	}


	/**
	 * @return the headMax
	 */
	public double getHeadMax() {
		return headMax;
	}


	/**
	 * @param headMax the headMax to set
	 */
	public void setHeadMax(double headMax) {
		this.headMax = headMax;
	}


	/**
	 * @return the headMin
	 */
	public double getHeadMin() {
		return headMin;
	}


	/**
	 * @param headMin the headMin to set
	 */
	public void setHeadMin(double headMin) {
		this.headMin = headMin;
	}


	/**
	 * @return the generationYear
	 */
	public double getGenerationYear() {
		return generationYear;
	}


	/**
	 * @param generationYear the generationYear to set
	 */
	public void setGenerationYear(double generationYear) {
		this.generationYear = generationYear;
	}


	/**
	 * @return the generationLowflow
	 */
	public double getGenerationLowflow() {
		return generationLowflow;
	}


	/**
	 * @param generationLowflow the generationLowflow to set
	 */
	public void setGenerationLowflow(double generationLowflow) {
		this.generationLowflow = generationLowflow;
	}


	/**
	 * @return the generationHighflow
	 */
	public double getGenerationHighflow() {
		return generationHighflow;
	}


	/**
	 * @param generationHighflow the generationHighflow to set
	 */
	public void setGenerationHighflow(double generationHighflow) {
		this.generationHighflow = generationHighflow;
	}


	/**
	 * @return the radioHighLow
	 */
	public double getRadioHighLow() {
		return radioHighLow;
	}


	/**
	 * @param radioHighLow the radioHighLow to set
	 */
	public void setRadioHighLow(double radioHighLow) {
		this.radioHighLow = radioHighLow;
	}


	/**
	 * @return the hoursUse
	 */
	public double getHoursUse() {
		return hoursUse;
	}


	/**
	 * @param hoursUse the hoursUse to set
	 */
	public void setHoursUse(double hoursUse) {
		this.hoursUse = hoursUse;
	}


	/**
	 * @return the outputWarrantedStatistics
	 */
	public double getOutputWarrantedStatistics() {
		return outputWarrantedStatistics;
	}


	/**
	 * @param outputWarrantedStatistics the outputWarrantedStatistics to set
	 */
	public void setOutputWarrantedStatistics(double outputWarrantedStatistics) {
		this.outputWarrantedStatistics = outputWarrantedStatistics;
	}


	public OutputStation() {
		// TODO Auto-generated constructor stub
	}


	
}
