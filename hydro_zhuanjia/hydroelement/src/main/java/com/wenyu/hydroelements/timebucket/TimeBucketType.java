package com.wenyu.hydroelements.timebucket;

/**
 * 时间尺度的类型就这么多
 * @author OwenYY
 *
 */
public enum TimeBucketType 
{
	YEAR("年"),
	MONTH("月"),
	DECAD("旬"),
	WEEK("周"),
	DAY("日"),
	HOUR("时"),
	MINUTE("分"),
	SECOND("秒");
	
	private String chinese;
	
	TimeBucketType(String chinese) {
		this.chinese = chinese;
	}
	
	public String getChinese() {
		return chinese;
	}
}
