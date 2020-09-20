package com.wenyu.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.dao.CurveThreeDimenDAO;
import com.wenyu.dao.CurveTwoDimenDAO;
import com.wenyu.entity.CurveThreeDimen;
import com.wenyu.entity.CurveTwoDimen;
import com.wenyu.hydroelements.curve.DoubleCurve;
import com.wenyu.hydroelements.curve.TripleCurve;
import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;

@Component("stationCurveManager")
public class StationCurveManager {
	private CurveTwoDimenDAO curve2dDAO;
	private CurveThreeDimenDAO curve3dDAO;

	public CurveTwoDimenDAO getCurve2dDAO() {
		return curve2dDAO;
	}

	@Resource
	public void setCurve2dDAO(CurveTwoDimenDAO curve2dDAO) {
		this.curve2dDAO = curve2dDAO;
	}

	public CurveThreeDimenDAO getCurve3dDAO() {
		return curve3dDAO;
	}

	@Resource
	public void setCurve3dDAO(CurveThreeDimenDAO curve3dDAO) {
		this.curve3dDAO = curve3dDAO;
	}

	public DoubleCurve createDoubleCurve(int id, int curveType) {
		List<CurveTwoDimen> curve2d = curve2dDAO.loadCurvesByBelongtoAndType(id, curveType);
		// 先根据curve2d转换到DoubleCurve
		double[][] curve = new double[curve2d.size()][2];
		for (int i = 0; i < curve.length; i++) {
			curve[i][0] = curve2d.get(i).getV0();
			curve[i][1] = curve2d.get(i).getV1();
		}
		DoubleCurve dc = new DoubleCurve(curve);
		return dc;
	}

	public TripleCurve createTripleCurve(int id, int curveType) {
		List<CurveThreeDimen> curve3d = curve3dDAO.loadCurvesByBelongtoAndType(id, curveType);
		// 先根据curve3d转换到TripleCurve
		double[][] curve = new double[curve3d.size()][3];
		for (int i = 0; i < curve.length; i++) {
			curve[i][0] = curve3d.get(i).getV0();
			curve[i][1] = curve3d.get(i).getV1();
			curve[i][2] = curve3d.get(i).getV2();
		}
		TripleCurve tc = new TripleCurve(curve);
		return tc;
	}

	// 三维曲线和二维曲线的id值设置一个明显的区分界限，20000之后的是三维的曲线
	public StationCurve createStationCurve(int stationId, int[] curveIds) {
		StationCurve stationCurve = new StationCurve();
		for (int i = 0; i < curveIds.length; i++) {
			if (curveIds[i] < 20000) {
				DoubleCurve dc = createDoubleCurve(stationId, curveIds[i]);
				set2dCurve(stationCurve, dc, curveIds[i]);
			} else {
				TripleCurve tc = createTripleCurve(stationId, curveIds[i]);
				set3dCurve(stationCurve, tc, curveIds[i]);
			}
		}
		return stationCurve;
	}

	private void set3dCurve(StationCurve stationCurve, TripleCurve tc, int curveId) {
		switch (curveId) {
		case 20001:
			stationCurve.setThreeFlowLeveldownCurve(tc);
			break;

		}
	}

	private void set2dCurve(StationCurve stationCurve, DoubleCurve dc, int curveId) {
		switch (curveId) {
		case 10001:
			stationCurve.setLevelCapacityCurve(dc);
			break;
		case 10002:
			stationCurve.setFlowLeveldownCurve(dc);
			break;
		case 10003:
			stationCurve.setHeadMaxpowerCurve(dc);
			break;
		}
	}
}
