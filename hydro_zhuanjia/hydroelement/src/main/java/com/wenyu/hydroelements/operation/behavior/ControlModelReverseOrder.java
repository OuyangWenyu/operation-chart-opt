package com.wenyu.hydroelements.operation.behavior;

import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;
import com.wenyu.hydroelements.operation.basic.BasicWaterResourceCal;
import com.wenyu.hydroelements.operation.basic.CheckConstraint;
import com.wenyu.hydroelements.operation.basic.MultiFomulaMode;

/**
 * 单时段逆时序计算
 * 
 * @author OwenYY
 *
 */
public class ControlModelReverseOrder {
	private MultiFomulaMode fomulaMode;// 各种计算方式

	/**
	 * @return the fomulaMode
	 */
	public MultiFomulaMode getFomulaMode() {
		return fomulaMode;
	}

	/**
	 * @param fomulaMode
	 *            the fomulaMode to set
	 */
	public void setFomulaMode(MultiFomulaMode fomulaMode) {
		this.fomulaMode = fomulaMode;
	}

	/**
	 * 逆时序出力控制计算
	 * 
	 * @param hsState
	 * @param hsSpec
	 * @param stationCurve
	 */
	public void powerControl(HStationState hsState, HStationSpec hsSpec, StationCurve curves) {
		double K = hsSpec.getOutputCoefficient();
		double levelMax =hsState.getLevelMax();
		double levelMin =hsState.getLevelMin();
		double Q_out = 0;
		int timelength_reverse=hsState.getTimeLength();
		double Q_in_reverse=hsState.getInflowReal();
		double N=hsState.getOutput();
		
		double Z1 = 0;// 时段平均水位

		double[] Z1_temp = new double[2];// 时段初末水位
		Z1_temp[0] = hsState.getLevelEnd();//时段末水位

		for (int j = 0; j <= (levelMax - Z1_temp[0]<=0?0:(levelMax - Z1_temp[0])); j++) {// 以时段末水位去试，直至出力与N相同为止
			Z1_temp[1] = Z1_temp[0] + (double) j;// 时段初水位
			Z1 = (Z1_temp[0] + Z1_temp[1]) / 2;// 时段平均水位
			double V2 = curves.getCapacityByLevel(Z1_temp[0]);//末库容
			double V1 = curves.getCapacityByLevel(Z1_temp[1]);//初库容
			double V_delta = V1 - V2;
			Q_out = V_delta * 1e8 / timelength_reverse + Q_in_reverse;
			double Z_down = curves.getLeveldownByOutflow(Q_out);
			double H = Z1 - Z_down;
			H = fomulaMode.calHeadPure(H);
			double N_temp = K * Q_out * H / 10000;
			if (N_temp < N) {// 如果计算的该水位对应的出力小于N，说明要增加时段初水位，以增大N_temp
				if (j == Math.floor(levelMax - Z1_temp[0])) {// 如果已经计算到正常蓄水位，就停止计算
					Z1_temp[1] = levelMax;
					Z1 = levelMax;
					break;
				}
				continue;
			} else {/// 如果计算的该水位对应的出力大于N，说明要降低时段初水位，以减小N_temp
				if (j == 0) {//出力最小了，还不满足，说明初水位应该是低于末水位的，如果此时是死水位，接下来会进行出力，让时段初水位仍然是死水位
					for (int k = 0; k <= (Z1_temp[0] - levelMin); k++) {//以时段末水位去试，直至出力与N相同为止
						Z1_temp[1] = Z1_temp[0] - (double) k;
						Z1 = (Z1_temp[1] + Z1_temp[0]) / 2;
						V2 = curves.getCapacityByLevel(Z1_temp[0]);
						V1 = curves.getCapacityByLevel(Z1_temp[1]);
						V_delta = V1 - V2;
						Q_out = V_delta * 1e8 / timelength_reverse + Q_in_reverse;
						Z_down = curves.getLeveldownByOutflow(Q_out);
						H = Z1 - Z_down;
						H=fomulaMode.calHeadPure(H);
						N_temp = K * Q_out * H / 10000;
						if (N_temp > N) {//如果计算的该水位对应的出力大于N，说明要降低时段初水位，以减小N_temp
							if (k == Math.floor(Z1_temp[0] - levelMin)) {//如果已经计算到死水位，就停止计算
								Z1_temp[1] = levelMin;
								Z1 = levelMin;
								break;
							}
							continue;
						}
						else {
							if (k == 0) {//出力最大了还达不到N，就维持水位
								/*// 加入汛期水位约束（先不考虑）
								if (Z1_temp[i+1] > hStation.getTimeData()
										.get(retainxun[retainxun.length - 1 - i])
										.getLevelMax()) {
									Z1_temp[i + 1] = hStation.getTimeData()
											.get(retainxun[retainxun.length - 1 - i])
											.getLevelMax();
									Z1[i] = (Z1_temp[i]+Z1_temp[i+1])/2.0;
								}*/
								break;//应该不会有这一步了
							} 
							else {
								double Z_max = Z1_temp[1] + 1;
								double Z_min = Z1_temp[1];
								while (Math.abs(N - N_temp) > 0.001) {
									Z1_temp[1] = (Z_max + Z_min) / 2;
									Z1= (Z1_temp[1] + Z1_temp[0]) / 2;
									V2 =curves.getCapacityByLevel(Z1_temp[0]);
									V1 = curves.getCapacityByLevel(Z1_temp[1]);
									V_delta = V1 - V2;
									Q_out = V_delta * 1e8 / timelength_reverse + Q_in_reverse;
									Z_down = curves.getLeveldownByOutflow(Q_out);
									H = Z1 - Z_down;
									H=fomulaMode.calHeadPure(H);
									N_temp = K * Q_out * H / 10000;
									if (N_temp < N) {
										Z_min = Z1_temp[1];
									} else {
										Z_max = Z1_temp[1];
									}
								}
								// 判断有无弃水，有弃水就表示完全下泄，维持水位
								double N_max=Math.min(hsSpec.getPowerInstalled(), K*H*hsSpec.getGenerateInflowMax()/10000);
								/* 暂时没有水头预想出力曲线，用当前时段水头*最大引用流量*出力系数代替,
								 * 在预想出力和最大出力中选择一个更加严格的约束
								 */
								if (N_temp > N_max)
								{
									//Z1 = Z1_temp[0];
									//Z1_temp[1] = Z1_temp[0];//这个也应该不会有
									//System.out.println("在BasicWaterResourceCal里面也加上最大发电引用流量的约束");
								}
								// 加入汛期水位约束（暂不考虑）
								/*if (Z1[i] > hStation.getTimeData()
										.get(retainxun[retainxun.length - 1 - i])
										.getLevelMax()) {
									Z1[i] = hStation.getTimeData()
											.get(retainxun[retainxun.length - 1 - i])
											.getLevelMax();
									Z1_temp[i + 1] = Z1[i];
								}*/

								break;

							}

						}//if (N_temp > N)对应的else的结束
					}
					break;
				} else {// 加1m的水位加多了，因此加小于1m的，慢慢地迭代计算，1m内的迭代
					double Z_max = Z1_temp[1];
					double Z_min = Z1_temp[1] - 1;
					while (Math.abs(N - N_temp) > 0.001) {
						Z1_temp[1] = (Z_max + Z_min) / 2;
						Z1 = (Z1_temp[0] + Z1_temp[1]) / 2;
						V2 = curves.getCapacityByLevel(Z1_temp[0]);
						V1 = curves.getCapacityByLevel(Z1_temp[1]);
						V_delta = V1 - V2;
						Q_out = V_delta * 1e8 / timelength_reverse + Q_in_reverse;
						Z_down = curves.getLeveldownByOutflow(Q_out);
						H = Z1 - Z_down;
						H = fomulaMode.calHeadPure(H);
						N_temp = K * Q_out * H / 10000;
						if (N_temp < N) {
							Z_min = Z1_temp[1];
						} else {
							Z_max = Z1_temp[1];
						}
					}
					break;
				}

			} // if (N_temp < N)对应的else的结束

		}

		// 改为水位点（非平均水位），首末水位
		double[] Z = new double[Z1_temp.length];

		for (int k = 0; k < Z.length; k++) {
			Z[k] = Z1_temp[Z1_temp.length - 1 - k];
		}
		
			try {
				if(Z[0]<=0 || Z[1]<=0) 
					throw new Exception("水位为非正数！计算有误！");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		hsState.setLevelBegin(Z[0]);
		hsState.setLevelEnd(Z[1]);
	}

	
	/**
	 * 应该先判断该种水位控制方式是否可行，然后再进行水位控制调度，逆时序水位控制模拟运行与顺时序水位控制并没有区别，
	 * 均为确定时段初末水位，然后进行计算
	 * @param hsState
	 *            当前时段的电站状态
	 * @param hsSpec
	 *            电站的相关特征量
	 * @param stationCurve
	 *            电站整体的特性曲线
	 * @return info 0 计算成功,1末水位不能满足硬约束,21 流量偏大,22 流量偏小,31 水头偏大,32 水头偏小
	 */
	public int levelControl(HStationState hsState, HStationSpec hsSpec, StationCurve stationCurve) {
		int info = -1;
		// ********定义变量*********
		double inflow;// 时段入库流量
		double levelbegin;// 初水位
		double levelend;// 末水位

		double outflow;// 时段下泄流量(包含弃水和发电引用流量)
		double DeltaWater;// 时段弃水流量
		double downlevel = 0;// 下游水位
		double waterHead;// 水头
		double Power;// 时段出力
		double power_max;// 预想出力;
		int timeLength;// 时段长度
		double waterHeadMao;// 毛水头
		double generation;// 时段发电量
		double outflow_gereration;// 发电引用流量

		// ********************计算过程*****************************
		// 初始量赋值
		levelbegin = hsState.getLevelBegin();
		levelend = hsState.getLevelEnd();
		inflow = hsState.getInflowReal();
		timeLength = hsState.getTimeLength();// 获取时段长度

		// 首先判断水位满足当前时段的硬约束，如果考虑水位变幅约束应该把变幅约束考虑进来
		if (levelend > hsState.getLevelMax() || levelend < hsState.getLevelMin()) {
			// 不满足水位约束，直接返回一个值
			info = 1;
			return info;
		}

		// 计算出库流量
		outflow = BasicWaterResourceCal.GetFluxFromDeltaWaterLevel(stationCurve, levelbegin, levelend, timeLength);// 出入库水量平衡
		outflow = inflow + outflow;
		// 下面一个if语句块是判断出流是否可能的语句！！！！！！！！！！！！！！！！（！表示后来加的，需要考虑是否需要委托出去防止代码冗余）
		if (outflow < 0) {
			outflow = 0;
			double v0temp = stationCurve.getCapacityByLevel(levelbegin);
			double vttemp = 0;
			try {
				vttemp = BasicWaterResourceCal.waterBalanceCalculate(v0temp, inflow, outflow, timeLength, 2);
			} catch (Exception e) {
				e.printStackTrace();
			}

			double levelMax = stationCurve.getLevelByCapacity(vttemp);
			hsState.setLevelMax(levelMax);
			levelend = levelMax;// 把末水位也修正到最大水位
			hsState.setLevelEnd(levelMax);
		}

		// 计算出流量后，应该判断是否可以执行此下泄流量
		int temp = CheckConstraint.CheckFluxLimit(hsState.getOutflowMin(), hsState.getOutflowMax(), outflow);
		if (temp == 0) // 如果流量偏小，将流量修正至最小下泄流量
		{
			info = 22;
			// 将末水位修正到正常流量
			double Content_deta;
			double Content_begin;
			double Content_end;
			double outflow_min;// 最小下泄

			outflow_min = hsState.getOutflowMin();
			outflow = outflow_min;
			Content_deta = (inflow - outflow_min) * timeLength / 1e8;// 单位亿立方米

			Content_begin = stationCurve.getCapacityByLevel(levelbegin);
			Content_end = Content_begin + Content_deta;
			levelend = stationCurve.getLevelByCapacity(Content_end);

			/**
			 * zhch添加的为死水位，这里改为当前状态的最小水位，
			 * 如果最小下泄流量却还是导致水位低于最小水位，那么这时候选择以水位控制为主的方式进行调度
			 **/
			if (levelend < hsState.getLevelMin()) {
				// 若末水位低于死水位，则忽略最小下泄约束
				levelend = hsState.getLevelMin();
				outflow = BasicWaterResourceCal.GetFluxFromDeltaWaterLevel(stationCurve, levelbegin, levelend,
						timeLength);// 出入库水量平衡
				outflow = inflow + outflow;
			}
			hsState.setLevelMax(levelend);// 如果流量偏小，说明最高水位初始设置有误差，修正最高水位

		} else if (temp == 1) // 流量偏大
		{
			info = 21;
			// 将末水位修正到正常流量
			double Content_deta;
			double Content_begin;
			double Content_end;
			double outflow_max;// 最大下泄

			outflow_max = hsState.getOutflowMax();
			outflow = outflow_max;
			Content_deta = (inflow - outflow_max) * timeLength / 1e8;// 单位亿立方米

			Content_begin = stationCurve.getCapacityByLevel(levelbegin);
			Content_end = Content_begin + Content_deta;
			levelend = stationCurve.getLevelByCapacity(Content_end);

			hsState.setLevelMin(levelend);// 如果流量偏大，说明最低水位初始设置有误差，修正最低水位
		}

		if (hsState.getLevelMin() > hsState.getLevelMax()) {//出现这个情况表示升不到约束要求的水位，这时要做一些处理，不应让最低水位还这么高，先处理为设置成死水位
			try {
				hsState.setLevelMin(hsSpec.getLevelDead());
				throw new Exception("当前时段的水位约束满足不了");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		downlevel = fomulaMode.tailLevelByOutflow(outflow);

		waterHeadMao = (levelbegin + levelend) / 2 - downlevel;// 计算毛水头
		waterHead = fomulaMode.calHeadPure(waterHeadMao);// 计算净水头

		power_max = fomulaMode.maxPowerState(waterHead);// hsSpec.getPowerInstalled();//暂时没有水头预想出力曲线，用装机容量代替
		// 确定当前时段的最大出力后，应该修正时段的最大出力
		hsState.setOutputMax(power_max);
		if(hsState.getOutflowGeneMax()<1){
			hsState.setOutflowGeneMax((hsSpec.getGenerateInflowMax()>1)?hsSpec.getGenerateInflowMax():10000);
		}
		// 最后要判断出力是否合理，出力肯定是装机容量和计算得到的出力里较小的那一个
		Power = Math.min(power_max,
				hsSpec.getOutputCoefficient() * waterHead * Math.min(outflow, hsState.getOutflowGeneMax()) / 10000);// 单位万kW

		outflow_gereration = Power * 10000 / hsSpec.getOutputCoefficient() / waterHead;
		DeltaWater = outflow - outflow_gereration;// hsSpec.getGenerateInflowMax();//
		if (DeltaWater > 0.001) {
			// Power = power_max;
		} else
			DeltaWater = 0;

		// outflow_gereration = outflow - DeltaWater;
		generation = Power * timeLength / 3600;// 单位万kWh

		hsState.setLevelEnd(levelend);// 设置末水位
		hsState.setLevelDown(downlevel);// 设置下游水位
		hsState.setOutput(Power);// 设置出力
		hsState.setOutflow(outflow);// 设置流量
		hsState.setHeadPure(waterHead);// 设置净水头
		hsState.setOutflowDesert(DeltaWater);// 设置弃水
		hsState.setGeneration(generation);// 设置发电量
		hsState.setOutflowGeneration(outflow_gereration);// 设置发电引用流量

		if (info != 22 && info != 21) {
			info = 0;
		}
		return info;
	}
	
}
