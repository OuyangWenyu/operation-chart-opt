package me.owenyy.drawmethods.helper.pso;

import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.StatUtils;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.factory.topology.Series;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;
import com.wenyu.hydroelements.operation.statistics.HsStatesStatistics;
import com.wenyu.hydroelements.operation.statistics.OutputVarsType;

import me.owenyy.divideperiod.MakeSureByOutput;
import me.owenyy.divideperiod.helper.RegulationYear;
import me.owenyy.optimal.InitialIndividual;

public class InitialForDG implements InitialIndividual{
	public static Random rnd;
	private PowerControlHStation hStation;
	private List<HydroStation> stations;
	private DispatchInputParas input;
	private MakeSureByOutput divideDeliveryAndStorage;
	private List<RegulationYear> regulationYears;
	/**
	 * @return the hStation
	 */
	public PowerControlHStation gethStation() {
		return hStation;
	}

	/**
	 * @param hStation the hStation to set
	 */
	public void sethStation(PowerControlHStation hStation) {
		this.hStation = hStation;
	}

	public List<HydroStation> getStations() {
		return stations;
	}

	public void setStations(List<HydroStation> stations) {
		this.stations = stations;
	}

	/**
	 * @return the input
	 */
	public DispatchInputParas getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(DispatchInputParas input) {
		this.input = input;
	}
	public MakeSureByOutput getDivideDeliveryAndStorage() {
		return divideDeliveryAndStorage;
	}

	public void setDivideDeliveryAndStorage(MakeSureByOutput divideDeliveryAndStorage) {
		this.divideDeliveryAndStorage = divideDeliveryAndStorage;
	}
	public List<RegulationYear> getRegulationYears() {
		return regulationYears;
	}

	public void setRegulationYears(List<RegulationYear> regulationYears) {
		this.regulationYears = regulationYears;
	}

	public InitialForDG(PowerControlHStation hStation, DispatchInputParas input,List<HydroStation> stations) {
		super();
		this.hStation = hStation;
		this.input = input;
		this.stations=stations;
	}

	/**
	 * 返回low—uper之间的数
	 * 
	 * @param low
	 *            下限
	 * @param uper
	 *            上限
	 * @return 返回low—uper之间的数
	 */
	double rand(double low, double uper) {
		rnd = new Random();
		return rnd.nextDouble() * (uper - low) + low;
	}

	/**
	 * pos的各维参数分别是：调度图绘制中选取的典型年径流序列个数 , 
	 * 供水期选择的数个典型年的频率中位数, 蓄水期选择的数个典型年的频率中位数,
	 * 供水期最大加大出力值, 蓄水期最大加大出力值, 
	 * 加大出力线个数, 供水期最小降低出力值, 
	 * 蓄水期最小降低出力值, 降低出力线个数，蓄水期保证出力与供水期保证出力之比
	 * @see me.owenyy.optimal.InitialIndividual#calFitness(double[])
	 */
	public double calFitness(double[] pos) {
		// TODO Auto-generated method stub
		DGInputParasPlus inputPlus=new DGInputParasPlus();
		inputPlus.setInput(input);
		input.setTypicalYearsNum((int)pos[0]);
		double dRepresFre=pos[1];
		double sRepresFre=pos[2];
		inputPlus.setdRepresFre(dRepresFre);
		inputPlus.setsRepresFre(sRepresFre);
		double dOutputMax=pos[3];
		double sOutputMax=pos[4];
		input.setAugmentPwerLineNum((int)pos[5]);
		double dOutputMin=pos[6];
		double sOutputMin=pos[7];
		input.setReducePwerLineNum((int)pos[8]);
		
		double[] dArgumentMultiples=new double[input.getAugmentPwerLineNum()];
		double[] dReduceMultiples=new double[input.getReducePwerLineNum()];
		double[] sArgumentMultiples=new double[input.getAugmentPwerLineNum()];
		double[] sReduceMultiples=new double[input.getReducePwerLineNum()];
		
		double dGuaranteedOutput=hStation.getHydroStation().getHsSpec().getOutputGuaranteed();
		double sGuaranteedOutput=dGuaranteedOutput*pos[9];//divideDeliveryAndStorage.getStorageMinOutputSearch();
		for(int i=0;i<input.getAugmentPwerLineNum();i++){
			dArgumentMultiples[i]=(dGuaranteedOutput+(dOutputMax-dGuaranteedOutput)/input.getAugmentPwerLineNum()*(i+1))/dGuaranteedOutput;
			sArgumentMultiples[i]=(sGuaranteedOutput+(sOutputMax-sGuaranteedOutput)/input.getAugmentPwerLineNum()*(i+1))/sGuaranteedOutput;
		}
		for(int i=0;i<input.getReducePwerLineNum();i++){
			dReduceMultiples[i]=(dGuaranteedOutput-(dGuaranteedOutput-dOutputMin)/input.getReducePwerLineNum()*(i+1))/dGuaranteedOutput;
			sReduceMultiples[i]=(sGuaranteedOutput-(sGuaranteedOutput-sOutputMin)/input.getReducePwerLineNum()*(i+1))/sGuaranteedOutput;
		}
		
		inputPlus.setdArgumentMultiples(dArgumentMultiples);
		inputPlus.setdReduceMultiples(dReduceMultiples);
		inputPlus.setsArgumentMultiples(sArgumentMultiples);
		inputPlus.setsReduceMultiples(sReduceMultiples);
		
		DispatchGraph dispatchGraph = new DispatchGraph();
		PsoDrawDG pso=new PsoDrawDG(dGuaranteedOutput,sGuaranteedOutput);
		pso.makeDG(hStation, dispatchGraph, inputPlus, divideDeliveryAndStorage, regulationYears);
		//绘制完毕调度图，开始模拟调度
		Series series=new Series();
		series.setLeadStation(hStation);
		series.setStations(stations);
		
		MultiStationTimeOpe msto=new MultiStationTimeOpe();
		msto.seriesSimuOpera(series);//模拟运行完毕
		
		//进行完模拟调度后，进行发电量，保证率等计算
		HsStatesStatistics hss=new HsStatesStatistics(hStation.getHydroStation().getHsSpec().getName(),hStation.getHydroStation().getHsStates());
		double firstStationGene=hss.calMulYearsAvg(OutputVarsType.GENERATION, 12);
		
		//统计如美水电站的出力保证率，这里统计对应保证率的保证出力，如果小于规定的保证出力，在后面加惩罚
		double nowGuarnteedOutput=hss.statisticsWarrantedOutputMonth(input.getAssuranceRate());
		//还有一个蓄水期末是否蓄满，统计蓄水期末为蓄满的次数，对次数也进行惩罚
		int notFilledCounts=0;//未蓄满次数
		for(int i=0;i<hStation.getHydroStation().getHsStates().size();i++){
			if(hStation.getHydroStation().getHsStates().get(i).getTimeEnd().getMonthValue()==(input.getStoragePeriodMonth())[input.getStoragePeriodMonth().length-1]
					&& hStation.getHydroStation().getHsStates().get(i).getLevelEnd()<hStation.getHydroStation().getHsSpec().getLevelNormal()-0.1)
				notFilledCounts++;
				
		}
		//然后考察弃水量，对弃水量是否惩罚？
		
		double[] elseStationsGene=new double[stations.size()];
		for(int i=0;i<stations.size();i++){
			hss=new HsStatesStatistics(stations.get(i).getHsSpec().getName(),stations.get(i).getHsStates());	
			elseStationsGene[i]=hss.calMulYearsAvgPower(12);
		}
		double cascadeGene=StatUtils.sum(elseStationsGene) +firstStationGene/10000;
		
		double fitness=0;
		double guaranteedOutputDif=hStation.getHydroStation().getHsSpec().getOutputGuaranteed()-nowGuarnteedOutput;
		double allMinOutput=hss.statisticsMinOutputMonth();
		/*if(guaranteedOutputDif>20)
			System.out.print("");*/
		fitness=cascadeGene-50*(guaranteedOutputDif>0?guaranteedOutputDif:0)-10*notFilledCounts
				;//-1*(hStation.getHydroStation().getHsSpec().getOutputGuaranteed()-allMinOutput);
		/*double energyTemp=0;
		if(output[0]<hsSpec.getWarrantedOutput()|| output[1]<hsSpec.getWarrantedOutput())
		{
			continue;
		}
		for(int j=0;j<2;j++)
		{
			energyTemp=energyTemp+output[j]*dts[stages[j].getNum()]/3600;
		}*/
		
		/*if(output[0]<hsSpec.getWarrantedOutput()){        //惩罚
			energyTemp=energyTemp-20*(hsSpec.getWarrantedOutput()-output[0]);
		}
		if(output[1]<hsSpec.getWarrantedOutput()){
			energyTemp=energyTemp-10*(hsSpec.getWarrantedOutput()-output[1]);
		}*///有无如美典型年的时候用惩罚函数
		//得到梯级多年平均发电量之后，即得到了fitness值，考虑蓄水期末的蓄满情况以及发电保证率的问题，采用惩罚函数去写
		
		return fitness;
	}

	public void initial(int dim, double[] pos, double[] v, double[] pbest,double[] pLowers,
			double[] pUppers,double[] vLowers,double[] vUppers) {
		// TODO Auto-generated method stub
		// 因为粒子各维的取值有不同，有的值是离散值，因此需要特殊的初始化和更新
		
		for (int i = 0; i < dim; ++i) {
			pos[i] = rand(pLowers[i], pUppers[i]);
			pbest[i] = pos[i];
			v[i] = rand(vLowers[i], vUppers[i]);
		}
	}

}
