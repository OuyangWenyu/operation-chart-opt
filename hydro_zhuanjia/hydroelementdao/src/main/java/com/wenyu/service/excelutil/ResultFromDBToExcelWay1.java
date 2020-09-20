package com.wenyu.service.excelutil;

import java.util.Comparator;
import java.util.List;

import com.wenyu.entity.TimeSequenceCurveTemp;
import com.wenyu.hydroelements.operation.basic.HydroDateUtil;
import com.wenyu.hydroelements.timebucket.TimeBucketType;

/**
 * 如美的第一种格式
 * 
 * @author OwenYY
 *
 */
public class ResultFromDBToExcelWay1 implements IResultFromDBToExcel {

	public Object[][] dbMonthdataToExcel(List<TimeSequenceCurveTemp> tscs) {
		Object[][] result = null;
		int dataColumnLength = 12;
		if (tscs.get(0).getTimebuckettype().equals("DECAD"))
			dataColumnLength = 36;
		int dataRowLength = tscs.size() / dataColumnLength;
		result = new Object[dataRowLength][dataColumnLength];
		tscs.sort(new Comparator<TimeSequenceCurveTemp>() {
			public int compare(TimeSequenceCurveTemp o1, TimeSequenceCurveTemp o2) {
				return o1.getStarttime().compareTo(o2.getStarttime());
			}
		});
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				result[i][j] = tscs.get(i * dataColumnLength + j).getValueavg();
			}
		}

		Object[][] firstColumnOfResult = new Object[result.length][1];
		for (int i = 0; i < firstColumnOfResult.length; i++) {
			firstColumnOfResult[i][0] = tscs.get(0).getStarttime().getYear() + i;
		}
		Object[][] resultWithFirstColumn = ExcelFormat.leftRightCombine(firstColumnOfResult, result);

		Object[][] formHead = new Object[1][resultWithFirstColumn[0].length];
		formHead[0][0] = "年份";
		String monthordecad = "月";
		if (tscs.get(0).getTimebuckettype().equals("DECAD")) {
			monthordecad = "旬";
		}
		for (int i = 1; i < formHead[0].length; i++) {
			if (tscs.get(0).getTimebuckettype().equals("MONTH"))
				formHead[0][i] = HydroDateUtil.getPeriod(tscs.get(0).getStarttime().plusMonths(i - 1).toLocalDate(),
						TimeBucketType.valueOf(tscs.get(0).getTimebuckettype())) + monthordecad;

			else if (tscs.get(0).getTimebuckettype().equals("DECAD"))
				formHead[0][i] = HydroDateUtil.getPeriod(
						HydroDateUtil.addDecad(tscs.get(0).getStarttime().toLocalDate(), i - 1),
						TimeBucketType.valueOf(tscs.get(0).getTimebuckettype())) + monthordecad;
		}

		Object[][] results = ExcelFormat.updownCombine(formHead, resultWithFirstColumn);

		return results;
	}

}
