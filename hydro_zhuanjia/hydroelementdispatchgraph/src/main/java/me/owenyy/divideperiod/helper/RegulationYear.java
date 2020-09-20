package me.owenyy.divideperiod.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.operation.basic.HydroDateUtil;

/**
 * 从供水期初开始，调节年度
 *
 */
public class RegulationYear {
	private List<RegulationYearPeriod> regulationYearPeriods;// 各个调节时期
	private RegulationYearPeriod aRegulationYear;// 整个调节年

	public RegulationYear() {
	}

	public List<RegulationYearPeriod> getRegulationYearPeriods() {
		return regulationYearPeriods;
	}

	public void setRegulationYearPeriods(List<RegulationYearPeriod> regulationYearPeriods) {
		this.regulationYearPeriods = regulationYearPeriods;
	}

	public RegulationYearPeriod getaRegulationYear() {
		return aRegulationYear;
	}

	public void setaRegulationYear(RegulationYearPeriod aRegulationYear) {
		this.aRegulationYear = aRegulationYear;
	}

	/**
	 * @return 获取其中的供水期对应的RegulationYearPeriod对象
	 */
	public RegulationYearPeriod getDeliveryPeriod() {
		int index = 0;
		for (int i = 0; i < regulationYearPeriods.size(); i++) {
			if (regulationYearPeriods.get(i).getId().equals(DeliOrStor.DELIVERY)) {
				index = i;
				break;
			}
		}
		return regulationYearPeriods.get(index);

	}

	/**
	 * @return 获取其中的蓄水期对应的RegulationYearPeriod对象
	 */
	public RegulationYearPeriod getStoragePeriod() {

		int index = 0;
		for (int i = 0; i < regulationYearPeriods.size(); i++) {
			if (regulationYearPeriods.get(i).getId().equals(DeliOrStor.STORAGE)) {
				index = i;
				break;
			}
		}
		return regulationYearPeriods.get(index);

	}

	/**
	 * @param dividePeriod
	 *            分期情况
	 * @param runoff
	 *            径流数据
	 * @param StartTime
	 *            开始时间
	 * @return 生成一个调节年度的数据
	 */
	public static RegulationYear generateRegulationYear(int[] dividePeriod, double[] runoff, LocalDate startTime) {
		/* 先把整个调节年度的数据塞上 */
		RegulationYearPeriod aNewRegulationYear = new RegulationYearPeriod();
		aNewRegulationYear.initialAllData(DeliOrStor.ALL_PERIODS, runoff, startTime, dividePeriod.length);

		List<RegulationYearPeriod> newRegulationYearPeriods = new ArrayList<RegulationYearPeriod>();

		RegulationYearPeriod delivery = new RegulationYearPeriod();
		List<Integer> indexdeli = new ArrayList<Integer>();
		for (int i = 0; i < dividePeriod.length; i++) {
			if (dividePeriod[i] == DeliOrStor.DELIVERY.getID()) {
				indexdeli.add(i);
			}
		}
		double[] runoffdeli = new double[indexdeli.size()];
		for (int i = 0; i < runoffdeli.length; i++) {
			runoffdeli[i] = runoff[indexdeli.get(i)];
		}

		LocalDate[] delistartDate = new LocalDate[runoffdeli.length];
		for (int i = 0; i < delistartDate.length; i++) {
			if (dividePeriod.length == 12) {
				delistartDate[i] = startTime.plusMonths(indexdeli.get(i));
			} else if (dividePeriod.length == 36) {
				delistartDate[i] = HydroDateUtil.addDecad(startTime, indexdeli.get(i));
			}
		}
		

		delivery.initialAllData(DeliOrStor.DELIVERY, runoffdeli, delistartDate, dividePeriod.length);

		RegulationYearPeriod storage = new RegulationYearPeriod();
		List<Integer> indexstor = new ArrayList<Integer>();
		for (int i = 0; i < dividePeriod.length; i++) {
			if (dividePeriod[i] == DeliOrStor.STORAGE.getID()) {
				indexstor.add(i);
			}
		}
		double[] runoffstor = new double[indexstor.size()];
		for (int i = 0; i < runoffstor.length; i++) {
			runoffstor[i] = runoff[indexstor.get(i)];
		}

		LocalDate[] storstartDate =new LocalDate[runoffstor.length];
		for (int i = 0; i < storstartDate.length; i++) {
			if (dividePeriod.length == 12) {
				storstartDate[i] = startTime.plusMonths(indexstor.get(i));
			} else if (dividePeriod.length == 36) {
				storstartDate[i] = HydroDateUtil.addDecad(startTime, indexstor.get(i));
			}
		}
		

		storage.initialAllData(DeliOrStor.STORAGE, runoffstor, storstartDate, dividePeriod.length);

		RegulationYearPeriod not = new RegulationYearPeriod();
		List<Integer> indexnot = new ArrayList<Integer>();
		for (int i = 0; i < dividePeriod.length; i++) {
			if (dividePeriod[i] == DeliOrStor.NOT_DELI_OR_STOR.getID()) {
				indexnot.add(i);
			}
		}

		if (indexnot.size() > 0) {
			double[] runoffnot = new double[indexnot.size()];
			for (int i = 0; i < runoffnot.length; i++) {
				runoffnot[i] = runoff[indexnot.get(i)];
			}

			LocalDate[] notstartDate =new LocalDate[runoffnot.length];
			for (int i = 0; i < notstartDate.length; i++) {
				if (dividePeriod.length == 12) {
					notstartDate[i] = startTime.plusMonths(indexnot.get(i));
				} else if (dividePeriod.length == 36) {
					notstartDate[i] = HydroDateUtil.addDecad(startTime, indexnot.get(i));
				}
			}
			
			not.initialAllData(DeliOrStor.NOT_DELI_OR_STOR, runoffnot, notstartDate, dividePeriod.length);
			newRegulationYearPeriods.add(not);
		}

		newRegulationYearPeriods.add(delivery);
		newRegulationYearPeriods.add(storage);

		RegulationYear result = new RegulationYear();
		result.setaRegulationYear(aNewRegulationYear);
		result.setRegulationYearPeriods(newRegulationYearPeriods);
		return result;
	}

}
