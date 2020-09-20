package com.wenyu.hydroelements.operation.statistics;

public enum OutputVarsType {
	LEVEL_BEGIN(0,"初水位","(米)"),
	LEVEL_END(1,"末水位","(米)"),
	LEVEL_UP(2,"上游水位","(米)"),
	LEVEL_DOWN(3,"下游水位","(米)"),
	
	INFLOW(4,"入流","(立方米/秒)"),
	OUTFLOW(5,"出流","(立方米/秒)"),
	OUTFLOW_GENERATION(6,"发电流量","(立方米/秒)"),
	OUTFLOW_SURPLUS(7,"弃水流量","(立方米/秒)"),
	
	OUTPUT(8,"出力","(万千瓦)"),//国内习惯用万千瓦
	GENERATION(9,"发电量","(万千瓦时)"),
	/*GENERATION_LOW_FLOW(13,"枯期发电量","(万千瓦时)"),
	GENERATION_HIGH_FLOW(14,"丰期发电量","(万千瓦时)"),
	RADIO_HIGH_LOW(15,"丰枯电量比","无量纲"),*/
	
	HEAD_GROSS(10,"毛水头","(米)"),
	HEAD_LOSS(11,"水头损失","(米)"),
	HEAD_NET(12,"净水头","(米)");
	
	private int flagBit;//标志位
	private String varName;//输出变量的名字
	private String unit;//常用单位
	
	OutputVarsType(int flagBit,String varName,String unit) {
		this.varName = varName;
		this.flagBit = flagBit;
		this.unit=unit;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public int getFlagBit() {
		return flagBit;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
}
