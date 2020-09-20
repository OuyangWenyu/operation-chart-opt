package com.wenyu.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.dao.HsSpecBasicDAO;
import com.wenyu.entity.HStationBasicSpec;
import com.wenyu.hydroelements.hydrostation.HStationSpec;

/**
 * manager与DAO打交道，让DAO去与数据库连接，整个过程由spring通过DI管理。
 * 
 * @author OwenYY
 *
 */
@Component("hsSpecManager")
public class HsSpecManager {
	private HsSpecBasicDAO hsSpecBasicDao;// 有了spring之后，就不用再new对象了，直接注入
	// private CurveTwoDimenManager ctdManager;

	public HsSpecBasicDAO getHsSpecBasicDao() {
		return hsSpecBasicDao;
	}

	@Resource
	public void setHsSpecBasicDao(HsSpecBasicDAO hsSpecBasicDao) {
		this.hsSpecBasicDao = hsSpecBasicDao;
	}

	/**
	 * @param id
	 * @return 根据电站id号进行电站的初始化设置
	 */
	public HStationSpec createHsSpec(int stationId) {
		HStationSpec hsSpec = new HStationSpec();
		hsSpec.setId(stationId);
		
		List<HStationBasicSpec> basicspecs = hsSpecBasicDao.loadSpecByBelongto(stationId);
		for (int i = 0; i < basicspecs.size(); i++) {
			setValue(hsSpec, basicspecs.get(i).getType());
		}

		return hsSpec;
	}

	private void setValue(HStationSpec hsSpec, int type) {
		HStationBasicSpec hsbs = hsSpecBasicDao.loadSpecByBelongtoAndType(hsSpec.getId(), type).get(0);
		switch (type) {
		case 1000001:
			hsSpec.setLevelFloodCheck(hsbs.getValue());
			break;
		case 1000002:
			hsSpec.setLevelFloodDesign(hsbs.getValue());
			break;
		case 1000003:
			hsSpec.setLevelFloodControl(hsbs.getValue());
			break;
		case 1000004:
			hsSpec.setLevelNormal(hsbs.getValue());
			break;
		case 1000005:
			hsSpec.setLevelFloodLimiting(hsbs.getValue());
			break;
		case 1000006:
			hsSpec.setLevelDead(hsbs.getValue());
			break;
		case 1000007:
			hsSpec.setElevationSpillwayBottom(hsbs.getValue());
			break;
		case 1000101:
			hsSpec.setStorageTotal(hsbs.getValue());
			break;
		case 1000102:
			hsSpec.setStorageControl(hsbs.getValue());
			break;
		case 1000103:
			hsSpec.setStorageProtect(hsbs.getValue());
			break;
		case 1000104:
			hsSpec.setStorageRegulating(hsbs.getValue());
			break;
		case 1000105:
			hsSpec.setStorageDead(hsbs.getValue());
			break;
		case 1000201:
			hsSpec.setPowerProductionMeanAnnual(hsbs.getValue());
			break;
		case 1000202:
			hsSpec.setPowerInstalled(hsbs.getValue());
			break;
		case 1000203:
			hsSpec.setOutputGuaranteed(hsbs.getValue());
			break;
		case 1000204:
			hsSpec.setOutputCoefficient(hsbs.getValue());
			break;
		case 1000205:
			hsSpec.setUnitTypeNum((int) hsbs.getValue());
			break;
		case 1000206:
			hsSpec.setUnitNum((int) hsbs.getValue());
			break;
		case 1000207:
			hsSpec.setGenerateInflowMax(hsbs.getValue());
			break;
		case 1000301:
			hsSpec.setDischargeAbilityMax(hsbs.getValue());
			break;
		case 1000302:
			hsSpec.setDischargeDemandMin(hsbs.getValue());
			break;
		}
	}
}
