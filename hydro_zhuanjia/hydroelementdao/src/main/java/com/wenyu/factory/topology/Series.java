package com.wenyu.factory.topology;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;

import com.wenyu.factory.EngineerBureau;
import com.wenyu.factory.HydroStationConstructor;
import com.wenyu.factory.PowerControlStationConstructor;
import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.hydroelements.operation.behavior.ControlModelSingleTime;
import com.wenyu.hydroelements.operation.statistics.BasicMathMethods;
import com.wenyu.service.InitialContainer;

/**
 * 应该给为树形拓扑，因为不太熟悉树的数据结构，因此暂时给出一个简易的串联结构，后面写了树形的再把这个归纳进去
 * 简单想法还是要对电站进行编号，然后所有代码按照统一的编号规则去运转，这也是符合算法的一种
 * 
 * @author OwenYY
 *
 */
public class Series {// 解决数据结构是个问题？？？怎么把不同类型的类放在一个统一的数据结构里呢？？？
	private PowerControlHStation leadStation;
	private List<HydroStation> stations;// 包括属于调节型电站
	private List<PowerControlHStation> reStations;// 下游电站中属于调节型的几个电站

	public PowerControlHStation getLeadStation() {
		return leadStation;
	}

	public void setLeadStation(PowerControlHStation leadStation) {
		this.leadStation = leadStation;
	}

	public List<HydroStation> getStations() {
		return stations;
	}

	public void setStations(List<HydroStation> stations) {
		this.stations = stations;
	}

	public List<PowerControlHStation> getReStations() {
		return reStations;
	}

	public void setReStations(List<PowerControlHStation> reStations) {
		this.reStations = reStations;
	}

	/**
	 * 构造一库N级式梯级电站，N级均为径流是电站，龙头水库为调节电站
	 * 
	 * @param stationsId
	 * @param startTime
	 * @param tbNums
	 */
	public void constructSeries1N(int[] stationsId, int[] curveTypes, int[] constraintTypes, String tbType,
			int unitNums, String startTime, int tbNums) {
		PowerControlStationConstructor hsof = InitialContainer.getContext()
				.getBean(PowerControlStationConstructor.class);
		setLeadStation(hsof.constructStation(
				new EngineerBureau(stationsId[0], curveTypes, constraintTypes, tbType, unitNums, startTime, tbNums)));
		HydroStationConstructor hsc = InitialContainer.getContext().getBean(HydroStationConstructor.class);
		ArrayList<HydroStation> stations = new ArrayList<HydroStation>();
		for (int i = 1; i < stationsId.length; i++) {
			HydroStation hydroStation = hsc.constructStation(new EngineerBureau(stationsId[i],
					new int[] { 10001, 10002 }, new int[] { 101, 102, 201, 301 }, "MONTH", 1, startTime, tbNums));
			stations.add(hydroStation);
		}
		setStations(stations);
	}

	/**
	 * 构造一库N级式梯级电站，除龙头水库为调节电站外，在下游电站中，还有若干电站x个是调节电站，它们的梯级拓扑编号是i
	 * 
	 * @param stationsId
	 *            所有电站的编号
	 * @param regulationId
	 *            其中属于调节型电站的编号，不包括龙头电站
	 * @param floodControlId
	 *            其中属于发电兼顾防洪调节型电站的编号，不包括龙头电站
	 * @param curveTypes
	 *            各个电站拥有的特性曲线的类型
	 * @param constraintTypes
	 *            各种约束类型
	 * @param tbType
	 *            时段类型
	 * @param unitNums
	 *            每个时段的单元个数
	 * @param startTime
	 *            起始调度时间
	 * @param tbNums
	 *            所有调度时段的个数
	 */
	public void constructSeries1Nxi(int[] stationsId, int[] regulationId, int[] floodControlId, Interval[] floodPeriods,
			int[] curveTypes, int[] constraintTypes, String tbType, int unitNums, String startTime, int tbNums) {
		PowerControlStationConstructor hsof = InitialContainer.getContext()
				.getBean(PowerControlStationConstructor.class);
		setLeadStation(hsof.constructStation(
				new EngineerBureau(stationsId[0], curveTypes, constraintTypes, tbType, unitNums, startTime, tbNums)));
		HydroStationConstructor hsc = InitialContainer.getContext().getBean(HydroStationConstructor.class);
		ArrayList<HydroStation> stations = new ArrayList<HydroStation>();
		ArrayList<PowerControlHStation> reStations = new ArrayList<PowerControlHStation>();
		for (int i = 1; i < stationsId.length; i++) {
			boolean isRe = false;
			for (int j = 0; j < regulationId.length; j++) {
				if (regulationId[j] == i) {
					boolean isFl = false;
					for (int k = 0; k < floodControlId.length; k++) {
						if (floodControlId[k] == i) {
							PowerControlHStation reStation = hsof.constructStation(
									new EngineerBureau(stationsId[i], curveTypes, constraintTypes, tbType, unitNums,
											startTime, tbNums),
									(int) floodPeriods[k].getInf(), (int) floodPeriods[k].getSup());
							stations.add(reStation.getHydroStation());
							reStations.add(reStation);
							isFl=true;
						}
					}
					if (!isFl) {
						HydroStation hydroStation = hsc.constructStation(new EngineerBureau(stationsId[i], curveTypes,
								constraintTypes, tbType, unitNums, startTime, tbNums));
						stations.add(hydroStation);
						PowerControlHStation reStation = new PowerControlHStation();
						reStation.setHydroStation(hydroStation);
						reStations.add(reStation);
						
					}
					isRe=true;
				}
			}
			if (!isRe) {
				HydroStation hydroStation = hsc.constructStation(new EngineerBureau(stationsId[i], curveTypes,
						constraintTypes, tbType, unitNums, startTime, tbNums));
				stations.add(hydroStation);
			}
		}
		setStations(stations);
		setReStations(reStations);
	}

	public void clean() {
		leadStation = null;
		stations = null;
	}

	/**
	 * @param timeIndex
	 * @return 根据时段编号，构造当前一个时段的梯级电站
	 */
	public Series get(int timeIndex){
		Series series=new Series();
		List<HydroStation> stationsNow=new ArrayList<HydroStation>();
		for(int i=0;i<stations.size();i++){
			HydroStation stationNow=new HydroStation();
			stationNow.setHsSpec(stations.get(i).getHsSpec());
			stationNow.setStationCurves(stations.get(i).getStationCurves());
			List<HStationState> hsStates=new ArrayList<HStationState> ();
			HStationState hs=new HStationState();
			ControlModelSingleTime.valueCopy(hs, stations.get(i).getHsStates().get(timeIndex));
			hsStates.add(hs);
			stationNow.setHsStates(hsStates);
			stationsNow.add(stationNow);
		}
		series.setStations(stationsNow);
		
		PowerControlHStation leadStationNow=new PowerControlHStation();
		HydroStation stationNow=new HydroStation();
		stationNow.setHsSpec(leadStation.getHydroStation().getHsSpec());
		stationNow.setStationCurves(leadStation.getHydroStation().getStationCurves());
		List<HStationState> hsStates=new ArrayList<HStationState> ();
		HStationState hs=new HStationState();
		ControlModelSingleTime.valueCopy(hs, leadStation.getHydroStation().getHsStates().get(timeIndex));
		hsStates.add(hs);
		stationNow.setHsStates(hsStates);
		leadStationNow.setHydroStation(stationNow);
		series.setLeadStation(leadStationNow);
		return series;
	}
	
	/**
	 * @param timeIndex
	 * @return 指定时段所有电站发电量之和
	 */
	public double getGeneration(int timeIndex){
		double gene=0;
		for(int i=0;i<stations.size();i++){
			gene=gene+stations.get(i).getHsStates().get(timeIndex).getGeneration();
		}
		gene=gene+leadStation.getHydroStation().getHsStates().get(timeIndex).getGeneration();
		return gene;
	}
	/**
	 * @param timeIndex
	 * @return 龙头电站时段出力
	 */
	public double getOutput(int timeIndex){
		double output=leadStation.getHydroStation().getHsStates().get(timeIndex).getOutput();
		return output;
	}
	/**
	 * @param timeIndex
	 * @return 龙头电站统计保证出力
	 */
	public double getOutputGuaranteed(double percent){
		double output=leadStation.getHydroStation().getHsSpec().getOutputGuaranteed();
		int size=leadStation.getHydroStation().getHsStates().size();
		double[] outputs=new double[size];
		for(int i=0;i<outputs.length;i++)
		{
			outputs[i]=leadStation.getHydroStation().getHsStates().get(i).getOutput();
		}
		double[][] outputs2D=new double[outputs.length/12][12];//二维的数据，每行代表一年，每列是一个月
		for(int i=0;i<outputs2D.length;i++){
			for(int j=0;j<12;j++){
				outputs2D[i][j]=outputs[i*12+j];
			}
		}
		double[] minEveryYear=new double[outputs2D.length];
		for(int i=0;i<outputs2D.length;i++){
			minEveryYear[i]=BasicMathMethods.minOf1DArray(outputs2D[i]);
		}
		output=BasicMathMethods.calFrequencyValue(minEveryYear, percent);
		return output;
	}
}
