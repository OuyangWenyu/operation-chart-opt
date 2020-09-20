package me.owenyy.divideperiod;

import me.owenyy.divideperiod.helper.BasicMathMethods;

public class FixedDividePeriod implements DivideDeliveryAndStorage {
	int[] deliveryPeriod;
	int[] storagePeriod;
	int yearPeriodsNum = 12;
	int startYear = 1953;
	int startPeriod = 6;// 默认年月，6月
	int[] fixed_month;// 这个变量是从1月开始的
	int[] fixed_decad;// 这个变量是从1旬开始的

	public FixedDividePeriod(int startYear, int startPeriod) {
		super();
		this.startYear = startYear;
		this.startPeriod = startPeriod;

	}

	public FixedDividePeriod(int startYear, int startPeriod, int[] deliveryPeriod, int[] storagePeriod) {
		super();
		this.startYear = startYear;
		this.startPeriod = startPeriod;
		this.deliveryPeriod = deliveryPeriod;
		this.storagePeriod = storagePeriod;
		fixed_month = new int[yearPeriodsNum];
		for (int i = 0; i < deliveryPeriod.length; i++) {
			fixed_month[deliveryPeriod[i]] = 1;
			if (yearPeriodsNum > 12)
				fixed_decad[deliveryPeriod[i]] = 1;
		}
		for (int i = 0; i < storagePeriod.length; i++) {
			fixed_month[storagePeriod[i]] = -1;
			if (yearPeriodsNum > 12)
				fixed_decad[storagePeriod[i]] = -1;
		}
	}

	public int[] getProvideSaveTimeFinal(double[] inflowtemp, double V_benifit, String tbType, int startYear,
			int startPeriod) {
		// TODO Auto-generated method stub
		int yearPeriods = 12;
		if (tbType.equals("DECAD"))
			yearPeriods = 36;
		int[] fenqi = new int[inflowtemp.length];
		int[] fenqi_timecorrect = new int[yearPeriods];
		if (yearPeriods ==12 && fixed_month == null)// 默认的分期是6-10蓄 11-5供
		{
			fixed_month = new int[yearPeriods];

			for (int i = 5; i < 10; i++) {
				fixed_month[i] = -1;
			}
			for (int j = 0; j < 12; j++) {
				if (fixed_month[j] == 0)
					fixed_month[j] = 1;
			}

		}
		if (yearPeriods ==36 && fixed_decad == null) {//时期可以调整
			fixed_decad = new int[yearPeriods];

			for (int i = 18; i < 33; i++) {
				fixed_decad[i] = -1;
			}
			for (int j = 0; j < 36; j++) {
				if (fixed_decad[j] == 0)
					fixed_decad[j] = 1;
			}
		}
		if (yearPeriods == 12) {
			fenqi_timecorrect = BasicMathMethods.reverseArray(fixed_month, startPeriod - 1);
			for (int k = 0; k < inflowtemp.length / yearPeriods; k++) {
				for (int i = 0; i < 12; i++) {
					fenqi[k * yearPeriods + i] = fenqi_timecorrect[i];
				}
			}

		} else {
			fenqi_timecorrect = BasicMathMethods.reverseArray(fixed_decad, startPeriod-1);
			for (int k = 0; k < inflowtemp.length / yearPeriods; k++) {
				for (int i = 0; i < 36; i++) {
					fenqi[k * yearPeriods + i] = fenqi_timecorrect[i];
				}
			}
		}
		return fenqi;
	}

}
