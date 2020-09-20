package com.wenyu.service.util;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.dao.TimeSequenceCurveDAO;
import com.wenyu.entity.TimeSequenceCurveTemp;
import com.wenyu.service.AppointIdNameManager;
import com.wenyu.service.excelutil.ExcelFormat;
import com.wenyu.service.excelutil.IResultFromDBToExcel;

/**
 * 从数据库中读取数据，然后存入excel中，读取数据通过service层实现，装入TimeSeqCurve里，通过操作TimeSeqCurve对象，
 * 将数据写入excel
 * 
 * @author OwenYY
 *
 */
@Component("hsStatesFromDBToExcel")
public class HsStatesFromDBToExcel {
	private TimeSequenceCurveDAO timeSeqCurveDAO;
	private IResultFromDBToExcel dbToExcel;
	private AppointIdNameManager appointIdNameManager;

	public TimeSequenceCurveDAO getTimeSeqCurveDAO() {
		return timeSeqCurveDAO;
	}

	@Resource
	public void setTimeSeqCurveDAO(TimeSequenceCurveDAO timeSeqCurveDAO) {
		this.timeSeqCurveDAO = timeSeqCurveDAO;
	}

	public IResultFromDBToExcel getDbToExcel() {
		return dbToExcel;
	}

	public void setDbToExcel(IResultFromDBToExcel dbToExcel) {
		this.dbToExcel = dbToExcel;
	}

	public AppointIdNameManager getAppointIdNameManager() {
		return appointIdNameManager;
	}

	@Resource
	public void setAppointIdNameManager(AppointIdNameManager appointIdNameManager) {
		this.appointIdNameManager = appointIdNameManager;
	}

	/**
	 * @param belongto
	 * @param varId
	 * @param tbType
	 * @param start
	 * @param end
	 * @return  数据库中的数据读出来后转为Object[][]形式的数据以便于后面写入excel
	 */
	public Object[][] toexcel(int belongto, int varId, String tbType, LocalDateTime start, LocalDateTime end) {
		List<TimeSequenceCurveTemp> tscs = timeSeqCurveDAO.loadDatasTempByBelongtoAndTypeAndTbtypeAndTime(belongto, varId,
				tbType, start, end);
		Object[][] tscsExcel = dbToExcel.dbMonthdataToExcel(tscs);
		return tscsExcel;
	}

	/**
	 * @param belongto  所属电站
	 * @param tbType 时段类型
	 * @param varTypes 变量类型
	 * @param start 开始时间
	 * @param end 结束时间
	 * @param path 输出到的excel的存储路径
	 */
	public void writeToExcel(int belongto, String tbType, int[] varTypes,LocalDateTime start, LocalDateTime end, String path) {
		Object[][][] excels = new Object[varTypes.length][][];
		for (int i = 0; i < varTypes.length; i++) {
			Object[][] tscsExcel = toexcel(belongto, varTypes[i], tbType, start, end);
			excels[i] = tscsExcel.clone();//new Object[tscsExcel.length][tscsExcel[0].length];
		}
		String[] sheetNames = new String[varTypes.length];
		for (int i = 0; i < sheetNames.length; i++) {
			sheetNames[i] = appointIdNameManager.loadNameByNumber(varTypes[i]);
		}
		ExcelFormat.writeMultiSheets(excels, path, sheetNames);
	}
}
