package me.owenyy.divideperiod;

import java.util.ArrayList;

import me.owenyy.divideperiod.helper.BasicMethod;
import me.owenyy.divideperiod.helper.DeliOrStor;

/**
 * 长系列历史径流资料整体判断供蓄水期
 * @author Yinyin Li
 */
public class ServeTime implements DivideDeliveryAndStorage {
	static int provideLabel = DeliOrStor.DELIVERY.getID();
	static int saveLabel = DeliOrStor.STORAGE.getID();
	static int notProvideSaveLabel = DeliOrStor.NOT_DELI_OR_STOR.getID();

	// 循环调整供蓄水期划分结果
	public int[] getProvideSaveTimeFinal(double[] inflowtemp, double V_benifit, String tbType, int startYear,
			int startPeriod) {
		int yearPeriods = 12;
		if (tbType == "DECAD") {
			yearPeriods = 36;
		}
		int allperiods = inflowtemp.length;
		int[] provideStateSeries = new int[allperiods];// 1表示供水，-1表示蓄水，0表示不蓄不供期
		for (int i = 0; i < allperiods; i++) {
			int periodIniId = i % yearPeriods;
			if (periodIniId < yearPeriods / 2) {
				provideStateSeries[i] = saveLabel;
			} else {
				provideStateSeries[i] = provideLabel;
			}
		}

		boolean boolflag = true;
		int[] providestate1 = new int[allperiods];
		for (int i = 0; i < allperiods; i++) {
			providestate1[i] = provideStateSeries[i];
		}

		int circleCount = 0;
		while (boolflag) {
			int[] providestate2 = getProvideSaveTimeSingle(inflowtemp, providestate1, V_benifit, yearPeriods, startYear,
					startPeriod);
			boolean boolflagtemp = true;
			for (int i = 0; i < providestate2.length; i++) {
				if (providestate2[i] != providestate1[i]) {
					boolflagtemp = false;
					break;
				}
			}
			if (boolflagtemp) {
				boolflag = false;
			} else {
				for (int i = 0; i < providestate2.length; i++) {
					providestate1[i] = providestate2[i];
				}
			}
			circleCount = circleCount + 1;
			System.out.println(circleCount);
		}
		return providestate1;
	}

	// 局部调整划分结果
	public int[] getProvideSaveTimeSingle(double[] inflowtemp, int[] providestate_ini, double V_benifit,
			int yearPeriods, int startYear, int startPeriod) {
		int startId = 0;// 供水期、蓄水期片段起点
		int periodSeriesLength = inflowtemp.length;
		int[] providestatetemp = new int[periodSeriesLength];
		for (int i = 0; i < periodSeriesLength; i++) {
			providestatetemp[i] = providestate_ini[i];
		}
		while (startId < periodSeriesLength) {// 从第一个时段起开始循环判断
			int endId = startId;

			/****************************** 判断蓄水期 **********************************/
			if (providestatetemp[startId] == saveLabel) {
				while (providestatetemp[endId] == saveLabel) {
					endId = endId + 1;
					if (endId == periodSeriesLength) {
						break;
					}
				}
				endId = endId - 1;// 因为while循环，多加了1

				// 第一步，首先判断当前片段是不是满足蓄水期判断条件，即：Q调节<当前片段包含所有时段入库流量，即为满足。其中Q调节=（当前片段所有时段入库水量-V兴利）/当前片段所有时段总时间
				// 如果某些时段不满足，返回不满足时段id
				ArrayList<Integer> nottruePeriodIds = BasicMethod.judgeSavePeriod(startId, endId, inflowtemp, V_benifit,
						yearPeriods, startYear, startPeriod);// 用于存储不满足判断条件时段id
				if (nottruePeriodIds.size() > 0) {
					for (int i = 0; i < nottruePeriodIds.size(); i++) {
						providestatetemp[nottruePeriodIds.get(i)] = notProvideSaveLabel;
					}
					ArrayList<ArrayList<Integer>> nottruePeriodIdKinds = new ArrayList<ArrayList<Integer>>();//
					ArrayList<Integer> nottruePeriodIdKindstemp = new ArrayList<Integer>();
					for (int i = startId; i <= endId; i++) {
						if (i == startId) {
							if (providestatetemp[i] == saveLabel) {
								nottruePeriodIdKindstemp = new ArrayList<Integer>();
								nottruePeriodIdKindstemp.add(i);
							} else if (providestatetemp[i] == notProvideSaveLabel) {

							}
						} else {
							if (providestatetemp[i] == saveLabel && providestatetemp[i - 1] == notProvideSaveLabel) {
								nottruePeriodIdKindstemp = new ArrayList<Integer>();
								nottruePeriodIdKindstemp.add(i);
								if (i == endId) {
									nottruePeriodIdKinds.add(nottruePeriodIdKindstemp);
								}
							} else if (providestatetemp[i] == saveLabel && providestatetemp[i - 1] == saveLabel) {
								nottruePeriodIdKindstemp.add(i);
								if (i == endId) {
									nottruePeriodIdKinds.add(nottruePeriodIdKindstemp);
								}
							} else if (providestatetemp[i] == notProvideSaveLabel
									&& providestatetemp[i - 1] == saveLabel) {
								nottruePeriodIdKinds.add(nottruePeriodIdKindstemp);
							} else if (providestatetemp[i] == notProvideSaveLabel
									&& providestatetemp[i - 1] == notProvideSaveLabel) {

							}
						}
					}
					int lengthtemp = -1;
					int maxLengthId = -1;
					if (nottruePeriodIdKinds.size() > 0) {
						for (int i = 0; i < nottruePeriodIdKinds.size(); i++) {
							if (nottruePeriodIdKinds.get(i).size() > lengthtemp) {
								maxLengthId = i;
								lengthtemp = nottruePeriodIdKinds.get(i).size();
							}
						}
						for (int i = startId; i <= endId; i++) {
							boolean bl = false;
							for (int j = 0; j < nottruePeriodIdKinds.get(maxLengthId).size(); j++) {
								if (i == nottruePeriodIdKinds.get(maxLengthId).get(j)) {
									bl = true;
									break;
								}
							}
							if (!bl) {
								providestatetemp[i] = notProvideSaveLabel;
							}

						}
					}

					for (int i = startId; i <= endId; i++) {
						if (providestatetemp[i] == saveLabel) {
							startId = i;
							break;
						}
					}
					int endIdtemp = endId - 1;
					endId = startId + 1;
					for (int i = startId; i <= endIdtemp; i++) {
						if (providestatetemp[i] == saveLabel && providestatetemp[i + 1] == saveLabel) {
							endId = i + 1;
						}
					}
				} else {
					// 第2步，往前推一个
					startId = startId - 1;
					if (startId >= 0) {
						int stateLabelBeforechange = providestatetemp[startId];
						providestatetemp[startId] = saveLabel;
						ArrayList<Integer> nottruePeriodIds_before = BasicMethod.judgeSavePeriod(startId, endId,
								inflowtemp, V_benifit, yearPeriods, startYear, startPeriod);// 用于存储不满足判断条件时段id
						if (nottruePeriodIds_before.size() > 0) {
							providestatetemp[startId] = stateLabelBeforechange;
							for (int i = startId; i <= endId; i++) {
								if (providestatetemp[i] == saveLabel) {
									startId = i;
									break;
								}
							}
							int endIdtemp = endId - 1;
							endId = startId;
							for (int i = startId; i <= endIdtemp; i++) {
								if (providestatetemp[i] == saveLabel && providestatetemp[i + 1] == saveLabel) {
									endId = i + 1;
								}
							}
						}
					} else {
						startId = startId + 1;
					}

					// 第3步，往后推一个
					endId = endId + 1;
					if (endId < periodSeriesLength) {
						providestatetemp[endId] = saveLabel;
						ArrayList<Integer> nottruePeriodIds_after = BasicMethod.judgeSavePeriod(startId, endId,
								inflowtemp, V_benifit, yearPeriods, startYear, startPeriod);// 用于存储不满足判断条件时段id
						if (nottruePeriodIds_after.size() > 0) {
							providestatetemp[endId] = notProvideSaveLabel;
							for (int i = startId; i <= endId; i++) {
								if (providestatetemp[i] == saveLabel) {
									startId = i;
									break;
								}
							}
							int endIdtemp = endId - 1;
							endId = startId;
							for (int i = startId; i <= endIdtemp; i++) {
								if (providestatetemp[i] == saveLabel && providestatetemp[i + 1] == saveLabel) {
									endId = i + 1;
								}
							}
						}
					} else {

					}
				}
				// 第4步，重新确定startId和endId
				startId = endId + 1;
			}

			/****************************** 判断供水期 **********************************/
			else if (providestatetemp[startId] == provideLabel) {
				while (providestatetemp[endId] == provideLabel) {
					endId = endId + 1;
					if (endId == periodSeriesLength) {
						break;
					}
				}
				endId = endId - 1;// 因为while循环，多加了1

				// 第一步，首先判断当前片段是不是满足供水期判断条件，即：Q调节>当前片段包含所有时段入库流量，即为满足。其中Q调节=（当前片段所有时段入库水量+V兴利）/当前片段所有时段总时间
				// 如果某些时段不满足，返回不满足时段id
				ArrayList<Integer> nottruePeriodIds = BasicMethod.judgeProvidePeriod(startId, endId, inflowtemp,
						V_benifit, yearPeriods, startYear, startPeriod);// 用于存储不满足判断条件时段id
				if (nottruePeriodIds.size() > 0) {
					for (int i = 0; i < nottruePeriodIds.size(); i++) {
						providestatetemp[nottruePeriodIds.get(i)] = notProvideSaveLabel;
					}

					ArrayList<ArrayList<Integer>> nottruePeriodIdKinds = new ArrayList<ArrayList<Integer>>();//
					ArrayList<Integer> nottruePeriodIdKindstemp = new ArrayList<Integer>();
					for (int i = startId; i <= endId; i++) {
						if (i == startId) {
							if (providestatetemp[i] == provideLabel) {
								nottruePeriodIdKindstemp = new ArrayList<Integer>();
								nottruePeriodIdKindstemp.add(i);
							} else if (providestatetemp[i] == notProvideSaveLabel) {

							}
						} else {
							if (providestatetemp[i] == provideLabel && providestatetemp[i - 1] == notProvideSaveLabel) {
								nottruePeriodIdKindstemp = new ArrayList<Integer>();
								nottruePeriodIdKindstemp.add(i);
								if (i == endId) {
									nottruePeriodIdKinds.add(nottruePeriodIdKindstemp);
								}
							} else if (providestatetemp[i] == provideLabel && providestatetemp[i - 1] == provideLabel) {
								nottruePeriodIdKindstemp.add(i);
								if (i == endId) {
									nottruePeriodIdKinds.add(nottruePeriodIdKindstemp);
								}
							} else if (providestatetemp[i] == notProvideSaveLabel
									&& providestatetemp[i - 1] == provideLabel) {
								nottruePeriodIdKinds.add(nottruePeriodIdKindstemp);
							} else if (providestatetemp[i] == notProvideSaveLabel
									&& providestatetemp[i - 1] == notProvideSaveLabel) {

							}
						}
					}
					int lengthtemp = -1;
					int maxLengthId = -1;
					if (nottruePeriodIdKinds.size() > 0) {
						for (int i = 0; i < nottruePeriodIdKinds.size(); i++) {
							if (nottruePeriodIdKinds.get(i).size() > lengthtemp) {
								maxLengthId = i;
								lengthtemp = nottruePeriodIdKinds.get(i).size();
							}
						}
						for (int i = startId; i <= endId; i++) {
							boolean bl = false;
							for (int j = 0; j < nottruePeriodIdKinds.get(maxLengthId).size(); j++) {
								if (i == nottruePeriodIdKinds.get(maxLengthId).get(j)) {
									bl = true;
									break;
								}
							}
							if (!bl) {
								providestatetemp[i] = notProvideSaveLabel;
							}
						}
					}

					for (int i = startId; i <= endId; i++) {
						if (providestatetemp[i] == provideLabel) {
							startId = i;
							break;
						}
					}
					int endIdtemp = endId - 1;
					endId = startId;
					for (int i = startId; i <= endIdtemp; i++) {
						if (providestatetemp[i] == provideLabel && providestatetemp[i + 1] == provideLabel) {
							endId = i + 1;
						}
					}
				} else {
					// 第2步，往前推一个
					startId = startId - 1;
					if (startId >= 0) {
						int stateLabelBeforechange = providestatetemp[startId];
						providestatetemp[startId] = provideLabel;
						ArrayList<Integer> nottruePeriodIds_before = BasicMethod.judgeProvidePeriod(startId, endId,
								inflowtemp, V_benifit, yearPeriods, startYear, startPeriod);// 用于存储不满足判断条件时段id
						if (nottruePeriodIds_before.size() > 0) {
							providestatetemp[startId] = stateLabelBeforechange;
							for (int i = startId; i <= endId; i++) {
								if (providestatetemp[i] == provideLabel) {
									startId = i;
									break;
								}
							}
							int endIdtemp = endId - 1;
							endId = startId;
							for (int i = startId; i <= endIdtemp; i++) {
								if (providestatetemp[i] == provideLabel && providestatetemp[i + 1] == provideLabel) {
									endId = i + 1;
								}
							}
						}
					} else {
						startId = startId + 1;
					}

					// 第3步，往后推一个
					endId = endId + 1;
					if (endId < periodSeriesLength) {
						providestatetemp[endId] = provideLabel;
						ArrayList<Integer> nottruePeriodIds_after = BasicMethod.judgeProvidePeriod(startId, endId,
								inflowtemp, V_benifit, yearPeriods, startYear, startPeriod);// 用于存储不满足判断条件时段id
						if (nottruePeriodIds_after.size() > 0) {
							providestatetemp[endId] = notProvideSaveLabel;
							for (int i = startId; i <= endId; i++) {
								if (providestatetemp[i] == provideLabel) {
									startId = i;
									break;
								}
							}
							int endIdtemp = endId - 1;
							endId = startId;
							for (int i = startId; i <= endIdtemp; i++) {
								if (providestatetemp[i] == provideLabel && providestatetemp[i + 1] == provideLabel) {
									endId = i + 1;
								}
							}
						}
					} else {

					}
				}
				// 第4步，重新确定startId和endId
				startId = endId + 1;

			}

			/******************************
			 * 如果是非供非蓄期，不处理，跳到下一个时段
			 **********************************/
			else if (providestatetemp[startId] == notProvideSaveLabel) {
				startId = endId + 1;
			}
		}

		return providestatetemp;

	}

}
