package me.owenyy.simuoperation;


import org.junit.Test;

import com.wenyu.factory.EngineerBureau;
import com.wenyu.factory.PowerControlStationConstructor;
import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.service.InitialContainer;
import com.wenyu.service.TimeSeqCurveManager;
import com.wenyu.service.util.HsStatesFromDBToExcel;

import me.owenyy.drawmethods.helper.MultiFomulaForDispatch1;
import me.owenyy.useregulation.SchedulingRuleUseDG;

public class MultiTimePowerControlQueryDGTest {

	@Test
	public void testSimuOperation() {
		PowerControlStationConstructor hsof = InitialContainer.getContext().getBean(PowerControlStationConstructor.class);//构造器
		PowerControlHStation hss = hsof.constructStation(new EngineerBureau(17060100, new int[]{}, new int[]{}, "DECAD", 1, "1956-01-01T00:00", 1944), 16, 25);//建立电站
		MultiTimePowerControlQueryDG mtpcq=new MultiTimePowerControlQueryDG();//调度图的出力控制方式
		mtpcq.setPowerHydroStation(hss);//调度图电站对象
		mtpcq.setLevel_Begin(975);//初始水位
		SchedulingRuleUseDG howToUseDG=new SchedulingRuleUseDG();//调度图应用
		mtpcq.setHowToUseDG(howToUseDG);
		MultiFomulaForDispatch1 mffd0 = new MultiFomulaForDispatch1();//固定水头损失+无顶托下游水位流量+水头预想出力控制最大出力
		mffd0.setCurve(hss.getHydroStation().getStationCurves());//注入电站曲线
		mffd0.setFixedHeadLoss(1);//固定水头损失
		mffd0.setHsSpec(hss.getHydroStation().getHsSpec());//注入电站基础信息
		mtpcq.setCalMethods(mffd0);//设置计算方法
		mtpcq.simuOperation();//开始调度
		
		TimeSeqCurveManager tscm = InitialContainer.getContext().getBean(TimeSeqCurveManager.class);//存入数据库
		tscm.writeTimeSeqCurve(hss.getHydroStation().getHsSpec().getId(), new int[]{1002}, mtpcq.getPowerHydroStation().getHydroStation().getHsStates());
		
		HsStatesFromDBToExcel hsStatesFromDBToExcel=InitialContainer.getContext().getBean(HsStatesFromDBToExcel.class);
		String excelPath="C:/Users/asus/Desktop/testjsj.xlsx";
		hsStatesFromDBToExcel.writeToExcel(hss.getHydroStation().getHsSpec().getId(), "DECAD", new int[]{1002},mtpcq.getPowerHydroStation().getHydroStation().getHsStates().get(0).getTimeStart(), mtpcq.getPowerHydroStation().getHydroStation().getHsStates().get(mtpcq.getPowerHydroStation().getHydroStation().getHsStates().size()-1).getTimeEnd(), excelPath);
	}
}
