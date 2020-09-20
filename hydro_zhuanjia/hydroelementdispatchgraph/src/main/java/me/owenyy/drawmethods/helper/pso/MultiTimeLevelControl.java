package me.owenyy.drawmethods.helper.pso;

import java.util.List;

import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.hydroelements.operation.basic.MultiFomulaMode;
import com.wenyu.hydroelements.operation.behavior.ControlModelSingleTime;

/**
 * 多时段均采用径流式电站的运行方式
 * 
 * @author OwenYY
 *
 */
public class MultiTimeLevelControl {
	private double level_Begin;
	private HydroStation hydroStation;
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
	 * @return the hydroStation
	 */
	public HydroStation getHydroStation() {
		return hydroStation;
	}

	/**
	 * @param hydroStation
	 *            the hydroStation to set
	 */
	public void setHydroStation(HydroStation hydroStation) {
		this.hydroStation = hydroStation;
	}

	public MultiFomulaMode getCalMethods() {
		return calMethods;
	}

	public void setCalMethods(MultiFomulaMode calMethods) {
		this.calMethods = calMethods;
	}

	/**
	 * 径流式电站维持水位
	 */
	public void simuOperation() {
		List<HStationState> hsstates = hydroStation.getHsStates();// 可以先取出变量进行计算，因为这是继承的域，直接取数据要get，改成protected的没玩过
		// HStationSpec hsspec=hydroStation.getHsSpec();

		double[] levels_begin;// 时段初水位
		double[] levels_end;// 时段末水位
		double[] levels_down;// 下游水位
		double[] outflows;// 时段平均出库流量
		double[] powers;// 时段平均出力
		double[] waterheads;// 时段平均水头
		double[] desertflows;// 时段平均弃水
		double[] generations;// 时段发电量
		double[] outflow_gereration;// 发电引用流量

		int timeCount = hsstates.size();// 调度总时长
		levels_begin = new double[timeCount];
		levels_end = new double[timeCount];
		levels_down = new double[timeCount];
		outflows = new double[timeCount];
		powers = new double[timeCount];
		waterheads = new double[timeCount];
		desertflows = new double[timeCount];
		generations = new double[timeCount];
		outflow_gereration = new double[timeCount];

		// 获取初水位
		hydroStation.getHsStates().get(0).setLevelBegin(level_Begin);
		levels_begin[0] = hydroStation.getHsStates().get(0).getLevelBegin();
		ControlModelSingleTime cmst = new ControlModelSingleTime(calMethods);
		for (int i = 0; i < timeCount; i++) {
			HStationState input = new HStationState();
			// SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd
			// HH:mm:ss");
			// 获取初水位
			if (i == 0) {
				levels_begin[i] = hydroStation.getHsStates().get(0).getLevelBegin();
			}
			hsstates.get(i).setLevelBegin(levels_begin[i]);
			hsstates.get(i).setLevelEnd(levels_begin[i]);

			// 针对汛限水位，写一段判断的代码，暂时的补充！！！！！！
			if (hydroStation.getHsSpec().getLevelFloodLimiting() > 1.0) {
				if ((hsstates.get(i).getTimeStart().getMonthValue() <= 9)
						&& (hsstates.get(i).getTimeStart().getMonthValue() >= 5)) {
					if((hsstates.get(i).getTimeStart().getMonthValue() == 5))
						System.out.print("");
					hsstates.get(i).setLevelEnd(hydroStation.getHsSpec().getLevelFloodLimiting());
				} else
					hsstates.get(i).setLevelEnd(hydroStation.getHsSpec().getLevelNormal());
			}			
			
			cmst.levelControl(hsstates.get(i), hydroStation.getHsSpec(), hydroStation.getStationCurves());
			if (i == timeCount - 1 && i>0) {
				hydroStation.getHsStates().get(i).setLevelEnd(level_Begin);
				cmst.levelControl(hsstates.get(i), hydroStation.getHsSpec(), hydroStation.getStationCurves());
			}
			ControlModelSingleTime.valueCopy(input, hydroStation.getHsStates().get(i));
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
			hsstates.get(j).setLevelBegin(levels_begin[j]);
			hsstates.get(j).setLevelEnd(levels_end[j]);
			hsstates.get(j).setLevelDown(levels_down[j]);
			hsstates.get(j).setOutput(powers[j]);
			hsstates.get(j).setOutflow(outflows[j]);
			hsstates.get(j).setOutflowDesert(desertflows[j]);
			hsstates.get(j).setHeadPure(waterheads[j]);
			hsstates.get(j).setGeneration(generations[j]);
			hsstates.get(j).setOutflowGeneration(outflow_gereration[j]);
		}

		hydroStation.setHsStates(hsstates);// 计算完毕再写入数据
		System.out.println(hydroStation.getHsSpec().getName() + "电站" + "多时段出力控制模拟运行完毕！");

	}
	/**
	 * 控制水位
	 */
	public void simuOperationNotRunoff(double[] levels_end) {
		List<HStationState> hsstates = hydroStation.getHsStates();// 可以先取出变量进行计算，因为这是继承的域，直接取数据要get，改成protected的没玩过
		// HStationSpec hsspec=hydroStation.getHsSpec();

		double[] levels_begin;// 时段初水位
		double[] levels_down;// 下游水位
		double[] outflows;// 时段平均出库流量
		double[] powers;// 时段平均出力
		double[] waterheads;// 时段平均水头
		double[] desertflows;// 时段平均弃水
		double[] generations;// 时段发电量
		double[] outflow_gereration;// 发电引用流量

		int timeCount = hsstates.size();// 调度总时长
		levels_begin = new double[timeCount];
		levels_down = new double[timeCount];
		outflows = new double[timeCount];
		powers = new double[timeCount];
		waterheads = new double[timeCount];
		desertflows = new double[timeCount];
		generations = new double[timeCount];
		outflow_gereration = new double[timeCount];

		// 获取初水位
		hydroStation.getHsStates().get(0).setLevelBegin(level_Begin);
		levels_begin[0] = hydroStation.getHsStates().get(0).getLevelBegin();
		ControlModelSingleTime cmst = new ControlModelSingleTime(calMethods);
		for (int i = 0; i < timeCount; i++) {
			HStationState input = new HStationState();
			// SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd
			// HH:mm:ss");
			// 获取初水位
			if (i == 0) {
				levels_begin[i] = hydroStation.getHsStates().get(0).getLevelBegin();
			}
			hsstates.get(i).setLevelBegin(levels_begin[i]);
			hsstates.get(i).setLevelEnd(levels_end[i]);

			// 针对汛限水位，写一段判断的代码，暂时的补充！！！！！！
			if (hydroStation.getHsSpec().getLevelFloodLimiting() > 1.0) {
				if ((hsstates.get(i).getTimeStart().getMonthValue() <= 9)
						&& (hsstates.get(i).getTimeStart().getMonthValue() >= 5)) {
					hsstates.get(i).setLevelEnd(hydroStation.getHsSpec().getLevelFloodLimiting());
				} else
					hsstates.get(i).setLevelEnd(hydroStation.getHsSpec().getLevelNormal());
			}			
			
			cmst.levelControl(hsstates.get(i), hydroStation.getHsSpec(), hydroStation.getStationCurves());
			if (i == timeCount - 1 && i>0) {
				hydroStation.getHsStates().get(i).setLevelEnd(level_Begin);
				cmst.levelControl(hsstates.get(i), hydroStation.getHsSpec(), hydroStation.getStationCurves());
			}
			ControlModelSingleTime.valueCopy(input, hydroStation.getHsStates().get(i));
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
			hsstates.get(j).setLevelBegin(levels_begin[j]);
			hsstates.get(j).setLevelEnd(levels_end[j]);
			hsstates.get(j).setLevelDown(levels_down[j]);
			hsstates.get(j).setOutput(powers[j]);
			hsstates.get(j).setOutflow(outflows[j]);
			hsstates.get(j).setOutflowDesert(desertflows[j]);
			hsstates.get(j).setHeadPure(waterheads[j]);
			hsstates.get(j).setGeneration(generations[j]);
			hsstates.get(j).setOutflowGeneration(outflow_gereration[j]);
		}

		hydroStation.setHsStates(hsstates);// 计算完毕再写入数据
		System.out.println(hydroStation.getHsSpec().getName() + "电站" + "多时段出力控制模拟运行完毕！");

	}
}
