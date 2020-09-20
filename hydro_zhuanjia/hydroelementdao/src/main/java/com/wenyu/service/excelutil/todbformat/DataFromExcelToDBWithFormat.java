package com.wenyu.service.excelutil.todbformat;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.wenyu.dao.TimeSequenceCurveDAO;
import com.wenyu.entity.TimeSequenceCurve;
import com.wenyu.hydroelements.operation.basic.HydroDateUtil;
import com.wenyu.service.InitialContainer;
import com.wenyu.service.excelutil.ExcelTool;

public class DataFromExcelToDBWithFormat {
	/**
	 * 从标准化的表格里输入中长期的径流数据到数据库中，主要是旬的，月的和日的直接在excel里就能拖数据了，然后导到数据库就行了
	 */
	public static void longDurationRunoffInput(String path, String sheet) {
		Object[][] originData = null;
		try {
			originData = ExcelTool.read07Excel(path, sheet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		TimeSequenceCurve[] result = new TimeSequenceCurve[originData.length];

		for (int i = 0; i < originData.length; i++) {
			result[i] = lineToLineWithFormat(originData[i]);
			TimeSequenceCurveDAO tscm=InitialContainer.getContext().getBean(TimeSequenceCurveDAO.class);
			tscm.insert(result[i]);
		}

		
	}

	private static TimeSequenceCurve lineToLineWithFormat(Object[] objects) {
		TimeSequenceCurve result = new TimeSequenceCurve();
		LocalDateTime starttime = HydroDateUtil.getLocalDateByYearAndDecad(Double.valueOf(objects[3].toString()).intValue(),
				Double.valueOf(objects[4].toString()).intValue()).atStartOfDay();
		LocalDateTime endtime = HydroDateUtil.calEndTime(starttime, "DECAD", 1, 1).minusSeconds(1);
		if (objects[2].toString().equals("MONTH")) {
			starttime = LocalDate.of(Double.valueOf(objects[3].toString()).intValue(), Double.valueOf(objects[4].toString()).intValue(), 1)
					.atStartOfDay();
			endtime = starttime.plusMonths(1).minusSeconds(1);
		}
		result.setBelongto(Double.valueOf(objects[0].toString()).intValue());
		result.setEndtime(endtime);
		result.setStarttime(starttime);
		result.setTimebuckettype(objects[2].toString());
		result.setType(Double.valueOf(objects[1].toString()).intValue());
		result.setValueavg(Double.valueOf(objects[5].toString()));
		result.setTimelength((int) Duration.between(starttime, endtime).getSeconds()+1);
		return result;
	}
}
