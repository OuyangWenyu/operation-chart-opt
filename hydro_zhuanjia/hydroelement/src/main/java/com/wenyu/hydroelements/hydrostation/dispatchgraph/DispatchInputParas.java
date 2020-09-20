package com.wenyu.hydroelements.hydrostation.dispatchgraph;

/**
 * 绘制调度图的输入参数
 *
 */
public class DispatchInputParas {
	private String tbType;
	private double assuranceRate;//保证率
	private int augmentPwerLineNum;//加大出力线个数
	private int reducePwerLineNum;//降低出力线个数
	private double[] argumentMultiples;//从小到大，加大出力线倍数（=出力线出力/保证出力） 
	private double[] reduceMultiples;//从小到大，降低出力线倍数（=出力线出力/保证出力）

	private int typicalYearsNum;//选取的代表年个数（不包括典型年在内）

	private int[] deliveryPeriodMonth;//供水期编号
	private int[] storagePeriodMonth;//蓄水期编号
	
	private int[] deliveryPeriodDecad;//供水期编号
	private int[] storagePeriodDecad;//蓄水期编号
	
	private double argumentOutputMax;//最上面一根加大出力线与封顶的正常蓄水位之间的出力区的出力在绘制调度图的过程中是不会求得的，需要外设
	private double reduceOutputMin;//最下面一根降低出力线与封底的死蓄水位之间的出力区的出力在绘制调度图的过程中是不会求得的，需要外设
	public String getTbType() {
		return tbType;
	}

	public void setTbType(String tbType) {
		this.tbType = tbType;
	}

	/**
	 * @return the assuranceRate
	 */
	public double getAssuranceRate() {
		return assuranceRate;
	}

	/**
	 * @param assuranceRate the assuranceRate to set
	 */
	public void setAssuranceRate(double assuranceRate) {
		this.assuranceRate = assuranceRate;
	}

	/**
	 * @return the augmentPwerLineNum
	 */
	public int getAugmentPwerLineNum() {
		return augmentPwerLineNum;
	}

	/**
	 * @param augmentPwerLineNum the augmentPwerLineNum to set
	 */
	public void setAugmentPwerLineNum(int augmentPwerLineNum) {
		this.augmentPwerLineNum = augmentPwerLineNum;
	}

	/**
	 * @return the reducePwerLineNum
	 */
	public int getReducePwerLineNum() {
		return reducePwerLineNum;
	}

	/**
	 * @param reducePwerLineNum the reducePwerLineNum to set
	 */
	public void setReducePwerLineNum(int reducePwerLineNum) {
		this.reducePwerLineNum = reducePwerLineNum;
	}

	/**
	 * @return the argumentMultiples
	 */
	public double[] getArgumentMultiples() {
		return argumentMultiples;
	}

	/**
	 * @param argumentMultiples the argumentMultiples to set
	 */
	public void setArgumentMultiples(double[] argumentMultiples) {
		this.argumentMultiples = argumentMultiples;
	}

	/**
	 * @return the reduceMultiples
	 */
	public double[] getReduceMultiples() {
		return reduceMultiples;
	}

	/**
	 * @param reduceMultiples the reduceMultiples to set
	 */
	public void setReduceMultiples(double[] reduceMultiples) {
		this.reduceMultiples = reduceMultiples;
	}

	/**
	 * @return the typicalYearsNum
	 */
	public int getTypicalYearsNum() {
		return typicalYearsNum;
	}

	/**
	 * @param typicalYearsNum the typicalYearsNum to set
	 */
	public void setTypicalYearsNum(int typicalYearsNum) {
		this.typicalYearsNum = typicalYearsNum;
	}

	/**
	 * @return the deliveryPeriodMonth
	 */
	public int[] getDeliveryPeriodMonth() {
		return deliveryPeriodMonth;
	}

	/**
	 * @param deliveryPeriodMonth the deliveryPeriodMonth to set
	 */
	public void setDeliveryPeriodMonth(int[] deliveryPeriodMonth) {
		this.deliveryPeriodMonth = deliveryPeriodMonth;
	}

	/**
	 * @return the storagePeriodMonth
	 */
	public int[] getStoragePeriodMonth() {
		return storagePeriodMonth;
	}

	/**
	 * @param storagePeriodMonth the storagePeriodMonth to set
	 */
	public void setStoragePeriodMonth(int[] storagePeriodMonth) {
		this.storagePeriodMonth = storagePeriodMonth;
	}

	/**
	 * @return the deliveryPeriodDecad
	 */
	public int[] getDeliveryPeriodDecad() {
		return deliveryPeriodDecad;
	}

	/**
	 * @param deliveryPeriodDecad the deliveryPeriodDecad to set
	 */
	public void setDeliveryPeriodDecad(int[] deliveryPeriodDecad) {
		this.deliveryPeriodDecad = deliveryPeriodDecad;
	}

	/**
	 * @return the storagePeriodDecad
	 */
	public int[] getStoragePeriodDecad() {
		return storagePeriodDecad;
	}

	/**
	 * @param storagePeriodDecad the storagePeriodDecad to set
	 */
	public void setStoragePeriodDecad(int[] storagePeriodDecad) {
		this.storagePeriodDecad = storagePeriodDecad;
	}

	/**
	 * @return the argumentOutputMax
	 */
	public double getArgumentOutputMax() {
		return argumentOutputMax;
	}

	/**
	 * @param argumentOutputMax the argumentOutputMax to set
	 */
	public void setArgumentOutputMax(double argumentOutputMax) {
		this.argumentOutputMax = argumentOutputMax;
	}

	/**
	 * @return the reduceOutputMin
	 */
	public double getReduceOutputMin() {
		return reduceOutputMin;
	}

	/**
	 * @param reduceOutputMin the reduceOutputMin to set
	 */
	public void setReduceOutputMin(double reduceOutputMin) {
		this.reduceOutputMin = reduceOutputMin;
	}

	public DispatchInputParas() {
		super();
	}

	public DispatchInputParas(String tbType,double assuranceRate, int augmentPwerLineNum, int reducePwerLineNum,
			int typicalYearsNum) {
		super();
		this.tbType=tbType;
		this.assuranceRate = assuranceRate;
		this.augmentPwerLineNum = augmentPwerLineNum;
		this.reducePwerLineNum = reducePwerLineNum;
		this.typicalYearsNum = typicalYearsNum;
	}

	

}
