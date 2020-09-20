package com.wenyu.service.excelutil;

import java.util.List;

import com.wenyu.entity.TimeSequenceCurveTemp;

/**啊 还没有想好这个接口应该怎么设计，这个要根据excel的展示形式来进行总结判断。初步思路：数据和表头分离，
	 * 数据统一采用Object[][]作为输出形式，然后表格的第一排和第一列文字后面再加。
	 * 对于同一张sheet内，显示多个同样的表格这种情况，对应的程序写法是将加上表头的Object[][]对象 按行列位置进行拼接
 * @author  OwenYY
 *
 */
public interface IResultFromDBToExcel {
	/**
	 * 月的数据组织
	 * @param tscs  同一类型变量的一系列数据
	 * @return 
	 */
	public Object[][] dbMonthdataToExcel(List<TimeSequenceCurveTemp> tscs);
}
