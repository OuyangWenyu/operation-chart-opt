package me.owenyy.drawmethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.factory.topology.Series;
import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;
import com.wenyu.service.InitialContainer;
import com.wenyu.service.TimeSeqCurveManager;
import com.wenyu.service.excelutil.ExcelTool;
import com.wenyu.service.excelutil.fromdbway1.ExcelOutputToSpecialFormat;
import com.wenyu.service.excelutil.fromdbway1.OutputSerialize;
import com.wenyu.service.excelutil.fromdbway1.TryFile;

import me.owenyy.divideperiod.FixedDividePeriod;
import me.owenyy.divideperiod.MakeSureByOutput;
import me.owenyy.divideperiod.helper.EveryYearByDivideResult;
import me.owenyy.divideperiod.helper.RegulationYear;
import me.owenyy.drawmethods.helper.MultiFomulaForDispatch0;
import me.owenyy.drawmethods.helper.pso.DGInputParasPlus;
import me.owenyy.drawmethods.helper.pso.InitialForDG;
import me.owenyy.drawmethods.helper.pso.MultiStationTimeOpe;
import me.owenyy.drawmethods.helper.pso.PsoDrawDG;
import me.owenyy.optimal.pso.PSO;
import me.owenyy.optimal.pso.Particle;

/**
 * 一种优化思想的调度图绘制，这是我个人构思的一种方法，详见论文
 * 
 * @author OwenYY
 *
 */
public class MethodOptimalDrawing implements DispatchGraphBehavior {
	private List<HydroStation> stations;

	public List<HydroStation> getStations() {
		return stations;
	}

	public void setStations(List<HydroStation> stations) {
		this.stations = stations;
	}

	public MethodOptimalDrawing() {
		super();
	}

	public void makeDispatchGraph(PowerControlHStation hStation, DispatchInputParas input) {
		PSO pso = new PSO();
		/*
		 * InitialIndividual initial=new Initial(); pso.setInitial(initial); int
		 * dims=3; double[] pLowers=new double[dims]; double[] pUppers=new
		 * double[dims]; double[] vLowers=new double[dims]; double[] vUppers=new
		 * double[dims]; for(int i=0;i<3;i++){ pLowers[i]=-10; pUppers[i]=10;
		 * vLowers[i]=-20; vUppers[i]=20; }
		 * pso.init(50,2,2,0.80,3,pLowers,pUppers,vLowers,vUppers);
		 * pso.run(500); pso.showresult();
		 */
		InitialForDG initial = new InitialForDG(hStation, input, stations);
		pso.setInitial(initial);
		int pNums = 20;
		int dims = 10;
		double c1 = 2;
		double c2 = 2;
		double w = 0.8;

		/*
		 * 粒子各维分别代表: 调度图绘制中选取的典型年径流序列个数 , 供水期选择的数个典型年的频率中位数, 蓄水期选择的数个典型年的频率中位数,
		 * 供水期最大加大出力值, 蓄水期最大加大出力值, 加大出力线个数, 供水期最小降低出力值, 蓄水期最小降低出力值, 降低出力线个数，蓄供水期保证出力之比
		 */

		double[] pLowers = new double[dims];
		double[] pUppers = new double[dims];
		double[] vLowers = new double[dims];
		double[] vUppers = new double[dims];

		pLowers[0] = 2;//调度图绘制中选取的典型年径流序列个数 
		pLowers[1] = 0.85;//供水期选择的数个典型年的频率中位数
		pLowers[2] = 0.02;//蓄水期选择的数个典型年的频率中位数
		pLowers[3] = hStation.getHydroStation().getHsSpec().getOutputGuaranteed();//供水期最大出力下限
		// 供水期最大加大出力值

		HStationSpec hsSpec = hStation.getHydroStation().getHsSpec();
		/* 先把调节年度的数据生成 */
		int periodstart = hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue();// 时段号，1表示第一个时段
		String tbType = input.getTbType();
		if (tbType == "DECAD") {
			periodstart = (hStation.getHydroStation().getHsStates().get(0).getTimeStart().getMonthValue() - 1) * 3 + 1;
		}

		TimeSeqCurveManager tscm = InitialContainer.getContext().getBean(TimeSeqCurveManager.class);
		TimeSeqCurve runoffData = tscm.createTimeSeqCurve(hsSpec.getId(), 1000, tbType,
				hStation.getHydroStation().getHsStates().size());

		MakeSureByOutput divideDeliveryAndStorage = new MakeSureByOutput();
		divideDeliveryAndStorage.setServeTime(new FixedDividePeriod(runoffData.getDates().getStartDateTime().getYear(),
				runoffData.getDates().getStartDateTime().getMonthValue()));
		divideDeliveryAndStorage.setAssuranceRate(input.getAssuranceRate());
		divideDeliveryAndStorage.setCurve(hStation.getHydroStation().getStationCurves());
		divideDeliveryAndStorage.setHsSpec(hsSpec);

		MultiFomulaForDispatch0 mffd0 = new MultiFomulaForDispatch0();
		mffd0.setCurve(hStation.getHydroStation().getStationCurves());
		mffd0.setFixedHeadLoss(1);
		mffd0.setHsSpec(hsSpec);
		divideDeliveryAndStorage.setMfm(mffd0);

		int[] divideResult = divideDeliveryAndStorage.getProvideSaveTimeFinal(runoffData.getDatas().getArray(),
				hStation.getHydroStation().getHsSpec().getStorageRegulating(), tbType,
				hStation.getHydroStation().getHsStates().get(0).getTimeStart().getYear(), periodstart);

		int stoPerMonLength = 0;
		for (int i = 0; i < 12; i++) {
			if (divideResult[i] < 0) {
				stoPerMonLength++;
			}
		}
		int[] stoPerMon = new int[stoPerMonLength];
		for (int i = 0; i < stoPerMonLength; i++) {
			stoPerMon[i] = 6 + i;
		}
		input.setStoragePeriodMonth(stoPerMon);
		
		EveryYearByDivideResult generateRegulationYear = new EveryYearByDivideResult();

		List<RegulationYear> regulationYears = new ArrayList<RegulationYear>();
		regulationYears = generateRegulationYear.generateRegulationYearsBeginWithStorage(runoffData, hsSpec,
				divideResult);// 一个生成regulationYears的方法

		double dWarrantedOutput = divideDeliveryAndStorage.getGuaranteedOutputSearch();//设置保证出力入口
		hsSpec.setOutputGuaranteed(dWarrantedOutput);
		initial.setDivideDeliveryAndStorage(divideDeliveryAndStorage);
		initial.setRegulationYears(regulationYears);
		double sWarrantedOutput=divideDeliveryAndStorage.getsGuaranteedOutputSearch();//蓄水期平均出力
		
		pLowers[4] = sWarrantedOutput;//蓄水期最大加大出力值下限
		pLowers[5] = 1;//加大出力线个数
		//供水期最小降低出力值
		pLowers[6] = divideDeliveryAndStorage.getDef().get(divideDeliveryAndStorage.getDef().size() - 1).getValue();// 等流量调节计算所得的最小出力值
		pLowers[7] = pLowers[6];// 蓄水期最小降低出力值与供水期设置一致
		pLowers[8] = 1;//降低出力线个数
		pLowers[9] = 1;//蓄水期保证出力与供水期保证出力之比
		
		//调度图绘制中选取的典型年径流序列个数 
		pUppers[0] = 20;//hStation.getHydroStation().getHsStates().get(hStation.getHydroStation().getHsStates().size() - 1).getTimeEnd().getYear() - hStation.getHydroStation().getHsStates().get(0).getTimeStart().getYear();// 径流序列总数
		pUppers[1] = 0.99;//供水期选择的数个典型年的频率中位数
		pUppers[2] = 0.99;//蓄水期选择的数个典型年的频率中位数
		pUppers[3] = hStation.getHydroStation().getHsSpec().getPowerInstalled();// 供水期最大加大出力值
		pUppers[4] = pUppers[3];// 蓄水期最大加大出力值
		pUppers[5] = 4.9999999;//加大出力线个数
		
		pUppers[6] = dWarrantedOutput;//供水期最小降低出力值上限
		pUppers[7] = dWarrantedOutput;//蓄水期最小降低出力值上限
		pUppers[8] = 4.9999999;//降低出力线个数
		pUppers[9] = sWarrantedOutput/dWarrantedOutput;//蓄水期保证出力与供水期保证出力之比最大值暂时用蓄水期平均出力与供水期平均出力之比表示 
		
		vLowers[0] = -2;//调度图绘制中选取的典型年径流序列个数 
		vLowers[1] = -0.1;//供水期选择的数个典型年的频率中位数
		vLowers[2] = -0.5;//蓄水期选择的数个典型年的频率中位数
		vLowers[3] = -5;//-0.5;// 供水期最大加大出力值
		vLowers[4] = vLowers[3];// 蓄水期最大加大出力值
		vLowers[5] = -2;//加大出力线个数
		vLowers[6] = -5;//-0.5;//供水期最小降低出力值上限
		vLowers[7] = vLowers[6];//蓄水期最小降低出力值上限
		vLowers[8] = -2;//降低出力线个数
		vLowers[9] = -0.5;//蓄水期保证出力与供水期保证出力之比最大值暂时用蓄水期平均出力与供水期平均出力之比表示 
		
		vUppers[0] = 2;//调度图绘制中选取的典型年径流序列个数 
		vUppers[1] = 0.1;//供水期选择的数个典型年的频率中位数
		vUppers[2] = 0.5;//蓄水期选择的数个典型年的频率中位数
		vUppers[3] = 5;//0.5;// 供水期最大加大出力值
		vUppers[4] = vUppers[3];// 蓄水期最大加大出力值
		vUppers[5] = 2;//加大出力线个数
		vUppers[6] = 5;//0.5;//供水期最小降低出力值上限
		vUppers[7] = vUppers[6];//蓄水期最小降低出力值上限
		vUppers[8] = 2;//降低出力线个数
		vUppers[9] = 0.5;//蓄水期保证出力与供水期保证出力之比最大值暂时用蓄水期平均出力与供水期平均出力之比表示 
		
		int runtimes=500;
		pso.init(pNums, c1, c2, w, dims, pLowers, pUppers, vLowers, vUppers);
		pso.run(runtimes);
		for(int i=1;i<runtimes+1;i++)
			pso.showresult(i);
		pso.showresult();
		
		Object[][] allGeneRusult=new Object[PSO.allGeneRusults.size()][dims+1];
		for(int i=0;i<allGeneRusult.length;i++){
			for(int j=0;j<dims+1;j++){
				allGeneRusult[i][j]=(PSO.allGeneRusults.get(i+1))[j];
			}
		}
		try {
			ExcelTool.reWrite07Excel("C:/Users/asus/Desktop/所有迭代结果.xlsx", "各代结果", allGeneRusult);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 最后运行完毕后把调度图结果和模拟调度结果都输出出来，看看效果
		DGInputParasPlus inputPlus = new DGInputParasPlus();
		inputPlus.setInput(input);
		input.setTypicalYearsNum((int) Particle.gbest[0]);
		double dRepresFre = Particle.gbest[1];
		double sRepresFre = Particle.gbest[2];
		inputPlus.setdRepresFre(dRepresFre);
		inputPlus.setsRepresFre(sRepresFre);
		double dOutputMax = Particle.gbest[3];
		double sOutputMax = Particle.gbest[4];
		input.setAugmentPwerLineNum((int) Particle.gbest[5]);
		double dOutputMin = Particle.gbest[6];
		double sOutputMin = Particle.gbest[7];
		input.setReducePwerLineNum((int) Particle.gbest[8]);

		double[] dArgumentMultiples = new double[input.getAugmentPwerLineNum()];
		double[] dReduceMultiples = new double[input.getReducePwerLineNum()];
		double[] sArgumentMultiples = new double[input.getAugmentPwerLineNum()];
		double[] sReduceMultiples = new double[input.getReducePwerLineNum()];

		double dGuaranteedOutput = hStation.getHydroStation().getHsSpec().getOutputGuaranteed();
		double sGuaranteedOutput = Particle.gbest[9]*dGuaranteedOutput;
		for (int i = 0; i < input.getAugmentPwerLineNum(); i++) {
			dArgumentMultiples[i] = (dGuaranteedOutput
					+ (dOutputMax - dGuaranteedOutput) / input.getAugmentPwerLineNum() * (i + 1)) / dGuaranteedOutput;
			sArgumentMultiples[i] = (sGuaranteedOutput
					+ (sOutputMax - sGuaranteedOutput) / input.getAugmentPwerLineNum() * (i + 1)) / sGuaranteedOutput;
		}
		for (int i = 0; i < input.getReducePwerLineNum(); i++) {
			dReduceMultiples[i] = (dGuaranteedOutput
					- (dGuaranteedOutput - dOutputMin) / input.getReducePwerLineNum() * (i + 1)) / dGuaranteedOutput;
			sReduceMultiples[i] = (sGuaranteedOutput
					- (sGuaranteedOutput - sOutputMin) / input.getReducePwerLineNum() * (i + 1)) / sGuaranteedOutput;
		}

		inputPlus.setdArgumentMultiples(dArgumentMultiples);
		inputPlus.setdReduceMultiples(dReduceMultiples);
		inputPlus.setsArgumentMultiples(sArgumentMultiples);
		inputPlus.setsReduceMultiples(sReduceMultiples);

		DispatchGraph dispatchGraph = new DispatchGraph();
		PsoDrawDG psodd = new PsoDrawDG(dGuaranteedOutput,sGuaranteedOutput);
		psodd.makeDG(hStation, dispatchGraph, inputPlus, divideDeliveryAndStorage, regulationYears);
		// 绘制完毕调度图，开始模拟调度
		Series series = new Series();
		series.setLeadStation(hStation);
		series.setStations(stations);

		MultiStationTimeOpe msto = new MultiStationTimeOpe();
		msto.seriesSimuOpera(series);// 模拟运行完毕

		// 进行完模拟调度后，进行发电量，保证率等计算
		
		stations.add(0, hStation.getHydroStation());
		
		OutputSerialize os=new OutputSerialize(stations);
		String filefolder="C:/Users/asus/Desktop/长系列结果";
		TryFile.createFileFolder(filefolder);//文件夹建好
		ExcelOutputToSpecialFormat eotsf=new ExcelOutputToSpecialFormat(stations, 
				stations.get(0).getHsStates().get(0).getLevelMax(),
				stations.get(0).getHsStates().get(0).getLevelMin(),
				filefolder);
		eotsf.output1();
		os.toExcelMethod1(filefolder);
		
	}
}
