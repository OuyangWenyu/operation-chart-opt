package me.owenyy.divideperiod;

/**
 * 划分水利年度的供蓄水期方法接口
 * 
 * @author OwenYY
 *
 */
public interface DivideDeliveryAndStorage {
	/**
	 * 划分水文年的供蓄水期
	 * 
	 * @param inflowtemp
	 *            历史径流
	 * @param V_benifit
	 *            水库兴利库容
	 * @param tbType
	 *            时段类型
	 * @param startYear
	 *            开始的年度
	 * @param startPeriod
	 *            径流资料开始时段的编号
	 * @return 至少需要根据历史径流资料、兴利库容和一年的时段数，才可划分水文年的供蓄期
	 */
	public int[] getProvideSaveTimeFinal(double[] inflowtemp, double V_benifit, String tbType, int startYear,
			int startPeriod);
}
