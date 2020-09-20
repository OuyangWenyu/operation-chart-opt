package com.wenyu.hydroelements.hydrostation;

import com.wenyu.hydroelements.timebucket.TimeBucket;

public class BulidAState {
	/**
	 * 根据时序的径流数据，把时间方面的设置，径流数据设置好（历史数据和实际径流数据一致）
	 */
	public static void buildRunoff(TimeBucket timeBucket, double array, HStationState hsState) {
		hsState.setInflowOrigin(array);
		hsState.setInflowReal(array);
		hsState.setTimeEnd(timeBucket.getEndDateTime());
		hsState.setTimeLength(timeBucket.getTimeLength());
		hsState.setTimeStart(timeBucket.getStartDateTime());
	}
	
	/**
	 * 根据时序的区间径流数据，把时间方面的设置，区间径流数据设置好
	 */
	public static void buildRunoffRange(TimeBucket timeBucket, double array, HStationState hsState) {
		hsState.setInflowRange(array);
		hsState.setTimeEnd(timeBucket.getEndDateTime());
		hsState.setTimeLength(timeBucket.getTimeLength());
		hsState.setTimeStart(timeBucket.getStartDateTime());
	}
}
