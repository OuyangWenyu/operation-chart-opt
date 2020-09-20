package com.wenyu.hydroelements.operation.basic;

public class MultiFomula {
	/**
	 * @param PowerInstalled
	 *            电站装机容量
	 * @return
	 */
	public static double calMaxPower(double PowerInstalled) {
		double Nmax = PowerInstalled;
		return Nmax;
	}

	/**
	 * @param PowerInstalled
	 *            电站装机容量
	 * @param Qin_gene__max
	 *            电站机组最大发电引用流量
	 * @return 最大引用流量和装机比较小的一个
	 */
	public static double calMaxPower(double PowerInstalled, double Qin_gene__max, double Hnet, double K) {
		double Nmax = PowerInstalled;
		double Ntemp = K * Hnet * Qin_gene__max;
		return Math.min(Nmax, Ntemp);
	}
}
