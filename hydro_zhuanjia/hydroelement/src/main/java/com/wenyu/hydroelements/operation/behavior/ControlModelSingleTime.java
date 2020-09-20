package com.wenyu.hydroelements.operation.behavior;

import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;
import com.wenyu.hydroelements.operation.basic.BasicWaterResourceCal;
import com.wenyu.hydroelements.operation.basic.CheckConstraint;
import com.wenyu.hydroelements.operation.basic.MultiFomulaMode;


/**
 * 单时段控制模式运行，出力控制的权限是最高的，水位控制模式是最低层的运算方式，采用水位控制和流量控制方式的时候就表明不需要出力控制。
 * 
 * @author OwenYY
 *
 */
public class ControlModelSingleTime {
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

	public ControlModelSingleTime(MultiFomulaMode fomulaMode) {
		super();
		this.fomulaMode = fomulaMode;
	}

	/**
	 * 应该先判断该种水位控制方式是否可行，然后再进行水位控制调度
	 * 函数对必须破坏约束的情况没有考虑全面，需要进一步完善！！！！！！！！！！！！！！！！！！！！
	 * ！！！！！！！！！！！！！！！！！！！！！！！！
	 * ！！！！！！！！！！！！！！！！！！！！！
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
			try {
				//hsState.setLevelMin(hsSpec.getLevelDead());
				throw new Exception("当前时段的水位约束满足不了");
				
			} catch (Exception e) {
				e.printStackTrace();}
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
				//hsState.setLevelMin(hsSpec.getLevelDead());
				throw new Exception("当前时段的水位约束满足不了");
				
			} catch (Exception e) {
				e.printStackTrace();
				hsState.setLevelMin(hsState.getLevelBegin());
				hsState.setOutput(hsSpec.getOutputGuaranteed());
				powerControl(hsState, hsSpec, stationCurve);
			}
		}

		// 考虑顶托和不考虑顶托怎么区分？
		/*
		 * if (hStation.getTailleveJackingEnable()==HydroStation.ENABLE) { if
		 * (hStation.getDownstreamStation().getType()==HydroStation.RESERVOIR) {
		 * downlevel=stationCurve.getLeveldownByOutflow(hStation.
		 * getDownstreamStation(). getTimeData().get(timeCount).getLevelBegin(),
		 * outflow); } else {
		 * downlevel=stationCurve.getLeveldownByOutflow(hStation.
		 * getDownstreamStation(). getLevelNormal(), outflow); }
		 * 
		 * }else { downlevel=stationCurve.getLeveldownByOutflow(outflow); }
		 */
		downlevel = fomulaMode.tailLevelByOutflow(outflow);

		waterHeadMao = (levelbegin + levelend) / 2 - downlevel;// 计算毛水头
		waterHead = fomulaMode.calHeadPure(waterHeadMao);// 计算净水头

		// 把水头检查的先忽略，搞不清楚为啥要判断水头
		/*
		 * boolean isHeadOK =
		 * CheckConstraint.CheckWaterhead(hsState.getHeadMin(),
		 * hsState.getHeadMax(), waterHead);// 检查水头的函数
		 * 
		 * if (isHeadOK == false) { // 不满足水头约束
		 * System.out.print("水头不满足,难道每次都是这个:" + waterHead);
		 * System.out.println("*************"); System.out.println("初水位:" +
		 * levelbegin); System.out.println("末水位:" + levelend);
		 * System.out.println("来水:" + inflow); System.out.println("下泄流量:" +
		 * outflow); System.out.println("下游水位:" + downlevel);
		 * System.out.println("*************"); if (waterHead >
		 * hsState.getOutputMax()) { System.out.println("水头修正到最大边界"); waterHead
		 * = hsState.getHeadMax(); }
		 * 
		 * if (waterHead < hsState.getOutputMin()) {
		 * System.out.println("水头修正到最小边界"); waterHead = hsState.getHeadMin(); }
		 * }
		 */

		// power_max = stationCurve.getPowerMaxByHead(timeCount, waterHead);
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

	/**
	 * 应该先判断该种水位控制方式是否可行，然后再进行水位控制调度
	 * 
	 * @param hsState
	 *            当前时段的电站状态
	 * @param hsSpec
	 *            电站的相关特征量
	 * @param stationCurve
	 *            电站整体的特性曲线
	 * @return info 0 计算成功,1末水位不能满足硬约束,21 流量偏大,22 流量偏小,31 水头偏大,32 水头偏小
	 * @throws CloneNotSupportedException
	 */
	public int levelControlCheck(HStationState hsState, HStationSpec hsSpec, StationCurve stationCurve)
	{
		int info = levelControl(hsState, hsSpec, stationCurve);
		double DeltaWater = hsState.getOutflowDesert();
		double levelend = hsState.getLevelEnd();
		double levelmax=hsState.getLevelBegin();
		double levelmin=levelend;
		if (DeltaWater > 0.01 && (levelend < hsState.getLevelBegin() - 0.01)) // 出现弃水的时候应该判断当前水位控制方式是否合理，若电站有弃水水位还下降，说明运行指令有误
		{
			// 处理不合理情况，
			// 思路：修正到没有弃水的情况或者最高水位的情况（两者取一个对应发电量较大的值）---此思路不可取应该是慢慢升高末水位直至没有弃水或达到初始水位????
			//貌似有时候还是会有下降弃水的情况，有时候这种情况下才能到达最大 出力。。。不知道咋整了
			int N;// 离散点数
			double e = 0.01;// 水位离散的精度
			double[] levels;// 离散的水位点数
			HStationState tempReasonalInput = null;

			if ((levelmax - levelmin) < 1e-2) // 最高水位小于或等于最低水位
				return 1;

			// 初始化离散点数
			N = (int) ((levelmax - levelmin) / e);
			levels = new double[N + 1];
			for (int i = 0; i < levels.length; i++) {
				levels[i] = i * e + levelmin;
			}
			if (Math.abs((N * e + levelmin) - levelmax) != 0) {
				levels[N] = levelmax;
			}

			for (int i = 0; i < levels.length; i++) {
				HStationState temp = new HStationState();
				hsState.setLevelEnd(levels[i]);
				levelControl(hsState, hsSpec, stationCurve);
				valueCopy(temp, hsState);

				if (temp.getOutflowDesert() < 0.01) {
					tempReasonalInput = temp;
					break;

				} 
			}
			valueCopy(hsState, tempReasonalInput);

		}

		if (info != 22 && info != 21) {
			info = 0;
		}
		return info;
	}

	/**
	 * @param hsState
	 * @param hsSpec
	 * @param stationCurve
	 * @return info 流量控制模式运行之前的检测，0 可行,11给定下泄流量偏大，12流量偏小,21 水位偏大,22 水位偏小。
	 *         不管哪种情况，都将约束和指令修正到合理范围。
	 * @throws CloneNotSupportedException
	 */
	public int flowControl(HStationState hsState, HStationSpec hsSpec, StationCurve stationCurve) {
		int info = -1;
		double outflow = hsState.getOutflow();// 获取出库流量
		double inflow = hsState.getInflowReal();// 获取入库流量
		double level_begin = hsState.getLevelBegin();// 获取初水位
		int timeLength = hsState.getTimeLength();// 获取时段长度

		double Content_deta;// 库容变化
		double Content_begin;// 初始库容
		double Content_end;// 末库容

		/* 首先应该判断流量约束是否满足 */
		if (outflow > hsState.getOutflowMax()) {
			// 不满足流量约束，直接返回一个值
			info = 11;
			hsState.setOutflow(hsState.getOutflowMax());

		} else if (outflow < hsState.getOutflowMin()) {
			info = 12;
			hsState.setOutflow(hsState.getOutflowMin());
		}

		// 计算末水位
		double level_end = 0;

		Content_deta = (inflow - outflow) * timeLength / 1e8;// 单位亿立方米
		double Content_max;
		double Content_min;

		Content_begin = stationCurve.getCapacityByLevel(level_begin);
		Content_end = Content_begin + Content_deta;
		Content_max = stationCurve.getCapacityByLevel(hsState.getLevelMax());
		Content_min = stationCurve.getCapacityByLevel(hsState.getLevelMin());
		level_end = stationCurve.getLevelByCapacity(Content_end);

		// *********注意:此处千万别用末水位比较,因为库容太大了容易查不出末水位********
		// 流量约束处理
		if (Content_end - Content_max > 0) // 此处需用0,否则容易出现水位越界的问题
		{
			// 流量偏小导致末水位超出上届,此时应该加大流量，并且这说明了最小流量设置有误
			level_end = hsState.getLevelMax();
			info = 22;
			double outflowMin = BasicWaterResourceCal.GetFluxFromDeltaWaterLevel(stationCurve, level_begin, level_end,
					timeLength);// 出入库水量平衡
			outflowMin = inflow + outflowMin;
			hsState.setOutflowMin(outflowMin);
		} else if (Content_end - Content_min < 0) {
			// 流量偏大导致末水位低于下届,应该减小流量
			level_end = hsState.getLevelMin();
			info = 21;
			double outflowMax = BasicWaterResourceCal.GetFluxFromDeltaWaterLevel(stationCurve, level_begin, level_end,
					timeLength);// 出入库水量平衡
			outflowMax = inflow + outflowMax;
			hsState.setOutflowMax(outflowMax);
		} else {
			info = 0;
		}

		hsState.setLevelEnd(level_end);
		info = levelControl(hsState, hsSpec, stationCurve);
		return info;
	}

	/**
	 * 
	 * @return info 0 计算成功,1 出力太大,2 出力太小.
	 * @throws CloneNotSupportedException
	 */
	public int powerControl(HStationState hsState, HStationSpec hsSpec, StationCurve stationCurve)
	{
		int info = -1;
		double thePower = hsState.getOutput();

		// 1.获取当前工况下的最小出力（最小最大出力）
		/*
		 * 最小出力的获取应该以高水位比低水位好为标准，末水位最高时候的最大出力就是最小的最大出力（小出力可以通过机组的开停来实现出力的调节），
		 * 即如果水位上升还能满足出力要求，那干嘛还要下降水位呢？
		 * 只要出力小于这个最小最大出力，就水位levelControl，如果levelControl求得的出力大于给定的出力，
		 * 那么应该通过电站内部调节，即增加弃水，这里默认就将出力修正为这个求得的出力；如果levelControl求得的出力小于给定的出力，
		 * 那么应该迭代降低水位直至求得的出力能达到给定的出力。
		 */
		double level_max_temp = hsState.getLevelMax();// 获取末水位上限
		// HS_max_levelEnd名字的意思是水电站状态（HS）最高末水位
		HStationState HS_max_levelEnd = new HStationState();
		valueCopy(HS_max_levelEnd, hsState);
		HS_max_levelEnd.setLevelEnd(level_max_temp);
		int info_max_levelEnd = levelControl(HS_max_levelEnd, hsSpec, stationCurve);

		if (info_max_levelEnd == 22)
		// 上升到最高水位无法满足最小下泄流量的要求,则按照最小下泄流量计算
		{
			HS_max_levelEnd.setOutflow(HS_max_levelEnd.getOutflowMin());
			flowControl(HS_max_levelEnd, hsSpec, stationCurve);
			level_max_temp = HS_max_levelEnd.getLevelEnd();
			HS_max_levelEnd.setLevelMax(level_max_temp);
		}
		// 计算完了最高水位对应的出力情况，就知道了实际上最高水位只能达到多少了，因此在这里修正hsState的levelMax为最新的值，实际上最小下泄也能同步修正了（待定）
		hsState.setLevelMax(HS_max_levelEnd.getLevelMax());
		level_max_temp = hsState.getLevelMax();
		if(hsState.getLevelMax()<hsState.getLevelMin()){//出现这种情况表示最低水位满足不了，不要强行设置水位约束了
			hsState.setLevelMin(hsSpec.getLevelDead());
		}
		/* 2.求最大出力 */
		double power_max = 0;
		HStationState input_maxpower = new HStationState();

		double level_min_temp = hsState.getLevelMin();// 获取末水位下限
		HStationState HS_min_levelEnd = new HStationState();
		valueCopy(HS_min_levelEnd, hsState);
		HS_min_levelEnd.setLevelEnd(level_min_temp);
		levelControl(HS_min_levelEnd, hsSpec, stationCurve);
		// 执行完上面的过程后，最新的符合约束的末水位下限也计算出来了，应该将下限值重新设置
		HS_min_levelEnd.setLevelMin(HS_min_levelEnd.getLevelEnd());
		hsState.setLevelMin(HS_min_levelEnd.getLevelMin());
		level_min_temp = hsState.getLevelMin();

		int identifier = 1;// 标识是非弃水期还是弃水期
		// *****************************判断弃水期,非弃水期*****************************
		if (HS_min_levelEnd.getOutflowDesert() == 0)
		// 下降到最低水位没有弃水,说明所有可能的末水位情况都在非弃水期
		// ****非弃水期,出力和末水位成反比关系，即末水位越低出力越大
		{
			power_max = HS_min_levelEnd.getOutput();
			input_maxpower = HS_min_levelEnd;
			identifier = 1;// 反比关系
		} else if (HS_max_levelEnd.getOutflowDesert() > 0)
		// 上升到最高水位还有弃水,说明所有可能的末水位情况都在弃水期
		// ****弃水期,出力和末水位成正比关系
		{
			power_max = HS_max_levelEnd.getOutput();
			input_maxpower = HS_max_levelEnd;
			identifier = -1;// 正比关系

			// 整个处在弃水期,按照运行到最高水位出力
			// hsState = (HStationState) input_maxpower.clone();
			valueCopy(hsState, input_maxpower);// 如果改变时段约束的话，此处没有把最低水位约束加进来，在下面加上
			hsState.setLevelMin(HS_min_levelEnd.getLevelMin());
			return 2;

		} else
		// 可能的末水位情况有可能是弃水,也有可能是非弃水,最大出力在最高水位和最低水位之间
		{
			// 寻求最大出力的点
			double levelmax = HS_max_levelEnd.getLevelEnd();// 最高水位
			double levelmin = HS_min_levelEnd.getLevelEnd();// 最低水位
			/*
			 * double maxlevel_power = HS_max_levelEnd.getOutput();// 非弃水端最小出力
			 * double minlevel_power = HS_min_levelEnd.getOutput();// 弃水端最小
			 * 
			 * double maxpower_level;
			 */

			HStationState findMaxpower = new HStationState();
			valueCopy(findMaxpower, hsState);
			findMaxpower.setLevelEnd(levelmin);
			findMaxPower(levelmax, levelmin, findMaxpower, hsSpec, stationCurve);

			power_max = findMaxpower.getOutput();// 最大出力
			input_maxpower = findMaxpower;// 最大出力的输入条件
			// maxpower_level = findMaxpower.getLevelEnd();// 最大出力时的末水位
		}

		/* 3.出力约束处理 */
		if (thePower < HS_max_levelEnd.getOutput()) {
			/*
			 * 如果levelControl求得的出力大于给定的出力， 那么理论上一定要精准控制出力，应该通过电站内部调节，即增加弃水；
			 * 这里直接将出力修正为这个最低的出力
			 */
			// hsState = (HStationState) HS_max_levelEnd.clone();
			valueCopy(hsState, HS_max_levelEnd);
			return 2;
		} else if (thePower > power_max) {
			// hsState = (HStationState) input_maxpower.clone();
			valueCopy(hsState, input_maxpower);
			return 1;
		}

		// 4.迭代试算(运用水位迭代)（一维搜索法合理么？合理，但是需要正确运用才合理）********************************
		double Lmax = level_max_temp;
		double Lmin = input_maxpower.getLevelEnd();
		double e = 0.01; // 精度
		double tempN = 0;
		double tempLevel = 0;
		double counter = 0;
		HStationState input_iterative = new HStationState();
		valueCopy(input_iterative, hsState);
		
		while ((Math.abs(tempN - thePower) > e) && (Lmax - Lmin > 0)) {
			tempLevel = Lmin + 0.618 * (Lmax - Lmin);
			// 根据流量控制模式计算出力
			input_iterative.setLevelEnd(tempLevel);
			info = levelControl(input_iterative, hsSpec, stationCurve);

			tempN = input_iterative.getOutput();// 获取出力

			counter++;

			if (counter >= 1000)
				break;
			if (tempN == thePower)
				break;
			else if (tempN * identifier > identifier * thePower)
				Lmin = tempLevel;
			else if (tempN * identifier < thePower * identifier)
				Lmax = tempLevel;
		}
		// hsState= (HStationState) input_iterative.clone();
		/*
		 * hsState的值需要更新，但是不能clone值，因为clone了一个input_iterative引用的对象，
		 * 而input_iterative是一个局部对象， 出了powerControl函数会被清除的，即一个方法不能让对象参数引用一个新的对象，
		 * 因此为了将input_iterative的值赋给hsState，
		 * 必须将各属性的值直接赋值给hsState，不能改变hsState的引用对象。valueCopy的值可以通过反射写一个通用一点的函数，
		 * 先直接赋值吧
		 */
		valueCopy(hsState, input_iterative);
		return info;
	}

	private int findMaxPower(double levelmax, double levelmin, HStationState hsState, HStationSpec hsSpec,
			StationCurve stationCurve) {
		int info = -1;
		int N;// 离散点数
		double e = 0.01;// 水位离散的精度
		double[] levels;// 离散的水位点数
		double tempMaxPower = 0;
		double desertwater = 0;
		HStationState tempMaxInput = null;

		if ((levelmax - levelmin) < 1e-2) // 最高水位小于或等于最低水位
			return 1;

		// 初始化离散点数
		N = (int) ((levelmax - levelmin) / e);
		levels = new double[N + 1];
		for (int i = 0; i < levels.length; i++) {
			levels[i] = i * e + levelmin;
		}
		if (Math.abs((N * e + levelmin) - levelmax) != 0) {
			levels[N] = levelmax;
		}

		for (int i = 0; i < levels.length; i++) {
			HStationState temp = new HStationState();
			hsState.setLevelEnd(levels[i]);
			levelControl(hsState, hsSpec, stationCurve);
			valueCopy(temp, hsState);

			if (temp.getOutput() > tempMaxPower) {
				tempMaxPower = temp.getOutput();
				desertwater = temp.getOutflowDesert();
				tempMaxInput = temp;

			} else if (temp.getOutput() == tempMaxPower) {
				if (temp.getOutflowDesert() < desertwater) {
					tempMaxPower = temp.getOutput();
					desertwater = temp.getOutflowDesert();
					tempMaxInput = temp;
				}
			}

		}
		valueCopy(hsState, tempMaxInput);
		info = 0;
		return info;

	}

	/**
	 * origin各个属性值赋给copy，这个可以利用反射写一个通用的，深拷贝
	 * 
	 * @param copy
	 * @param origin
	 */
	public static void valueCopy(HStationState copy, HStationState origin) {
		copy.setGeneration(origin.getGeneration());
		copy.setHeadGross(origin.getHeadGross());
		copy.setHeadLoss(origin.getHeadLoss());
		copy.setHeadPure(origin.getHeadPure());
		copy.setHeadMax(origin.getHeadMax());
		copy.setHeadMin(origin.getHeadMin());
		copy.setInflowForecast(origin.getInflowForecast());
		copy.setInflowOrigin(origin.getInflowOrigin());
		copy.setInflowPresent(origin.getInflowPresent());
		copy.setInflowReal(origin.getInflowReal());
		copy.setInflowRange(origin.getInflowRange());
		copy.setLevelDownMin(origin.getLevelDownMin());
		copy.setLevelBegin(origin.getLevelBegin());
		copy.setLevelDown(origin.getLevelDown());
		copy.setLevelDownMax(origin.getLevelDownMax());
		copy.setLevelEnd(origin.getLevelEnd());
		copy.setLevelMax(origin.getLevelMax());
		copy.setLevelMin(origin.getLevelMin());
		// copy.setLoadGrid(origin.getLoadGrid());
		// copy.setLoadTypical(origin.getLoadTypical());
		// copy.setName(origin.getName());
		copy.setOutflow(origin.getOutflow());
		copy.setOutflowDesert(origin.getOutflowDesert());
		copy.setOutflowGeneration(origin.getOutflowGeneration());
		copy.setOutflowMax(origin.getOutflowMax());
		copy.setOutflowMin(origin.getOutflowMin());
		copy.setOutput(origin.getOutput());
		// copy.setOutputGiven(origin.getOutputGiven());
		copy.setOutputMax(origin.getOutputMax());
		copy.setOutputMin(origin.getOutputMin());
		copy.setTimeEnd(origin.getTimeEnd());
		copy.setTimeLength(origin.getTimeLength());
		copy.setTimeStart(origin.getTimeStart());
		copy.setWaterRate(origin.getWaterRate());

	}

}
