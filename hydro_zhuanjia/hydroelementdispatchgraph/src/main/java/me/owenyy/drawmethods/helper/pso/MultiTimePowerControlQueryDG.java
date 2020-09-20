package me.owenyy.drawmethods.helper.pso;


import java.time.LocalDate;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraphHowToUse;
import com.wenyu.hydroelements.operation.basic.MultiFomulaMode;
import com.wenyu.hydroelements.operation.behavior.ControlModelSingleTime;

import me.owenyy.servicedispatchgraph.DispatchGraphServeMethod;


/**
 * 多时段均采用查询调度图的出力控制方式
 * 
 * @author OwenYY
 *
 */
public class MultiTimePowerControlQueryDG {
	private double level_Begin;
	private PowerControlHStation powerHydroStation;
	private DispatchGraphHowToUse howToUseDG;
	private MultiFomulaMode calMethods;

	/**
	 * @return the level_Begin
	 */
	public double getLevel_Begin() {
		return level_Begin;
	}

	/**
	 * @param level_Begin
	 *            the level_Begin to set
	 */
	public void setLevel_Begin(double level_Begin) {
		this.level_Begin = level_Begin;
	}

	/**
	 * @return the powerHydroStation
	 */
	public PowerControlHStation getPowerHydroStation() {
		return powerHydroStation;
	}

	/**
	 * @param powerHydroStation
	 *            the powerHydroStation to set
	 */
	public void setPowerHydroStation(PowerControlHStation powerHydroStation) {
		this.powerHydroStation = powerHydroStation;
	}

	/**
	 * @return the howToUseDG
	 */
	public DispatchGraphHowToUse getHowToUseDG() {
		return howToUseDG;
	}

	/**
	 * @param howToUseDG
	 *            the howToUseDG to set
	 */
	public void setHowToUseDG(DispatchGraphHowToUse howToUseDG) {
		this.howToUseDG = howToUseDG;
	}

	public MultiFomulaMode getCalMethods() {
		return calMethods;
	}

	public void setCalMethods(MultiFomulaMode calMethods) {
		this.calMethods = calMethods;
	}

	/**
	 * 调度图为主进行出力控制模式运行，把蓄供水期标识出来，然后蓄水期采用出力控制时判断水位是否会下降，如果会的话，就注意降低给定出力运行，
	 * 以蓄水期最小出力进行出力控制， 这样在实际运行中其实是可以实现的，即如果水位有消落，那么考虑降低指令出力值
	 */
	public void simuOperation() {
		int timeCount = powerHydroStation.getHydroStation().getHsStates().size();// 调度总时长
		double[] levels_begin = new double[timeCount];// 时段初水位
		double[] levels_end = new double[timeCount];// 时段末水位
		double[] levels_down = new double[timeCount];// 下游水位
		double[] outflows = new double[timeCount];// 时段平均出库流量
		double[] powers = new double[timeCount];// 时段平均出力
		double[] waterheads = new double[timeCount];// 时段平均水头
		double[] desertflows = new double[timeCount];// 时段平均弃水
		double[] generations = new double[timeCount];// 时段发电量
		double[] outflow_gereration = new double[timeCount];// 发电引用流量

		// 获取初水位，起算水位从外部赋值进去
		powerHydroStation.getHydroStation().getHsStates().get(0).setLevelBegin(level_Begin);
		levels_begin[0] = powerHydroStation.getHydroStation().getHsStates().get(0).getLevelBegin();

		ControlModelSingleTime cmst = new ControlModelSingleTime(calMethods);
		for (int i = 0; i < timeCount; i++) {
			if (i==76) {
				System.out.print("！！");
			}
			HStationState input = new HStationState();
			// 获取初水位
			if (i == 0) {
				levels_begin[i] = powerHydroStation.getHydroStation().getHsStates().get(0).getLevelBegin();
			}
			powerHydroStation.getHydroStation().getHsStates().get(i).setLevelBegin(levels_begin[i]);

			double output = howToUseDG.searchOutput(powerHydroStation.getHydroStation().getHsStates().get(i).getTimeStart().toLocalDate(),
					levels_begin[i], powerHydroStation.getDispatchGraph());// 查询当前时间当前水位下调度图对应的出力值
			// 还需要检查水位约束
			double[] levelStrict = howToUseDG.searchLevelRestrict(
					powerHydroStation.getHydroStation().getHsStates().get(i).getTimeStart().toLocalDate(), levels_begin[i],
					powerHydroStation.getDispatchGraph());
			if (levelStrict[0] > 0 && levelStrict[1] > 0) {
				powerHydroStation.getHydroStation().getHsStates().get(i).setLevelMax(levelStrict[1]);
				powerHydroStation.getHydroStation().getHsStates().get(i).setLevelMin(levelStrict[0]);
			}

			if (output <= 0) {// 调度图查询出力不为正数，表示没有调度图或者调度图没有相应时段的出力指示，考虑采用流量控制或者水位控制的方式进行调度
				powerHydroStation.getHydroStation().getHsStates().get(i)
						.setLevelEnd(powerHydroStation.getHydroStation().getHsStates().get(i).getLevelBegin());
				cmst.levelControl(powerHydroStation.getHydroStation().getHsStates().get(i), powerHydroStation.getHydroStation().getHsSpec(),
						powerHydroStation.getHydroStation().getStationCurves());
			} else {
				powerHydroStation.getHydroStation().getHsStates().get(i).setOutput(output);
				cmst.powerControl(powerHydroStation.getHydroStation().getHsStates().get(i), powerHydroStation.getHydroStation().getHsSpec(),
						powerHydroStation.getHydroStation().getStationCurves());
			}

			// 蓄水期采用混合控制模式，如果出力控制导致水位下降（太多），那么考虑水位控制模式进行水库调度，维持水位不下降太多或者不变，或者换用供水期保证出力进行计算，作为双保证出力区；可以考虑给出一个最低水位线，防破坏的水位线
			LocalDate[] storageStartEnd = DispatchGraphServeMethod.calStorageStartEnd(
					powerHydroStation.getHydroStation().getHsStates().get(i).getTimeStart().getYear(),
					powerHydroStation.getDispatchGraph());
			if ((!powerHydroStation.getHydroStation().getHsStates().get(i).getTimeStart().toLocalDate().isBefore(storageStartEnd[0]))
					&& (!powerHydroStation.getHydroStation().getHsStates().get(i).getTimeStart().toLocalDate()
							.isAfter(storageStartEnd[1]))) {
				if (powerHydroStation.getHydroStation().getHsStates().get(i).getLevelEnd() < powerHydroStation.getHydroStation().getHsStates().get(i)
						.getLevelBegin()/*-15*/) {//因为水库水位越高越有利于发电，因此蓄水期原则上不应允许水位下降，如果有水位下降则维持水位进行调度

					powerHydroStation.getHydroStation().getHsStates().get(i)
							.setLevelEnd(powerHydroStation.getHydroStation().getHsStates().get(i).getLevelBegin());
					cmst.levelControl(powerHydroStation.getHydroStation().getHsStates().get(i), powerHydroStation.getHydroStation().getHsSpec(),
							powerHydroStation.getHydroStation().getStationCurves());
				}
			}

			ControlModelSingleTime.valueCopy(input, powerHydroStation.getHydroStation().getHsStates().get(i));
			levels_end[i] = input.getLevelEnd();
			if (i < timeCount - 1) {
				levels_begin[i + 1] = input.getLevelEnd();
			}
			levels_down[i] = input.getLevelDown();
			outflows[i] = input.getOutflow();
			powers[i] = input.getOutput();
			waterheads[i] = input.getHeadPure();
			desertflows[i] = input.getOutflowDesert();
			generations[i] = input.getGeneration();
			outflow_gereration[i] = input.getOutflowGeneration();
		}

		for (int j = 0; j < timeCount; j++) {
			powerHydroStation.getHydroStation().getHsStates().get(j).setLevelBegin(levels_begin[j]);
			powerHydroStation.getHydroStation().getHsStates().get(j).setLevelEnd(levels_end[j]);
			powerHydroStation.getHydroStation().getHsStates().get(j).setLevelDown(levels_down[j]);
			powerHydroStation.getHydroStation().getHsStates().get(j).setOutput(powers[j]);
			powerHydroStation.getHydroStation().getHsStates().get(j).setOutflow(outflows[j]);
			powerHydroStation.getHydroStation().getHsStates().get(j).setOutflowDesert(desertflows[j]);
			powerHydroStation.getHydroStation().getHsStates().get(j).setHeadPure(waterheads[j]);
			powerHydroStation.getHydroStation().getHsStates().get(j).setGeneration(generations[j]);
			powerHydroStation.getHydroStation().getHsStates().get(j).setOutflowGeneration(outflow_gereration[j]);
		}
		System.out.println(powerHydroStation.getHydroStation().getHsSpec().getName() + "电站" + "多时段出力控制模拟运行完毕！");

	}
}
