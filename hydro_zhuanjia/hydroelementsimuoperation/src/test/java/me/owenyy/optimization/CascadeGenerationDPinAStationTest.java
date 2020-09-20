package me.owenyy.optimization;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.junit.Test;

import com.wenyu.factory.topology.Series;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.service.excelutil.fromdbway1.ExcelOutputToSpecialFormat;
import com.wenyu.service.excelutil.fromdbway1.OutputSerialize;
import com.wenyu.service.excelutil.fromdbway1.TryFile;

import me.owenyy.simuoperation.MultiStationTimeOpe;

public class CascadeGenerationDPinAStationTest {

	@Test
	public void testDp() {
		String startTime="4067-06-01T00:00";
		int stageNumber=12001; 
		Interval[] states_max_mins=new Interval[stageNumber];
		double[] precisions=new double[stageNumber];
		for(int i=0;i<stageNumber;i++){
			if(i%12==0)
				states_max_mins[i]=new Interval(2815, 2815);
			else
				states_max_mins[i]=new Interval(2815, 2895);
			precisions[i]=1;
		}
		Series series=new Series();
		int[] stationsId=new int[12];
		for(int i=0;i<12;i++){
			stationsId[i]=12010100+i*100;
		}
		series.constructSeries1N(stationsId, new int[] { 10001, 10002 },
				new int[] { 101, 102, 201, 301 }, "MONTH", 1,  startTime, stageNumber-1);
		//series.constructSeries("1953-06-01T00:00", 636);
		MultiStationTimeOpe msto=new MultiStationTimeOpe();
		double[] levelBegins={2815,2605,2525,2340,2245,1906,1818,1732,1619,1477,1408,1307};
		CascadeGenerationDPinAStation rmgDP=new CascadeGenerationDPinAStation(
				stageNumber,states_max_mins,precisions, -1, series, msto,levelBegins);
		rmgDP.dp();
		for(double i: rmgDP.getBestStateValuesChoosen()){
            System.out.println(i);
        }
		
		Series series1=new Series();
		series1.constructSeries1N(stationsId, new int[] { 10001, 10002 },
				new int[] { 101, 102, 201, 301 }, "MONTH", 1,  startTime, stageNumber-1);
		double[] levelEnd=new double[stageNumber-1];
		System.arraycopy(rmgDP.getBestStateValuesChoosen(), 1, levelEnd, 0, levelEnd.length);
		msto.seriesRunoffStationsSimuOpera(series1,levelBegins,levelEnd);//有如美
		ArrayList<HydroStation> stations=new ArrayList<HydroStation>();
		for(int i=0;i<stationsId.length-1;i++)
			stations.add(series1.getStations().get(i));
		stations.add(0, series1.getLeadStation().getHydroStation());
		OutputSerialize os=new OutputSerialize(stations);
		String filefolder="C:/Users/YXL/Desktop/长系列结果";
		TryFile.createFileFolder(filefolder);//文件夹建好
		ExcelOutputToSpecialFormat eotsf=new ExcelOutputToSpecialFormat(stations, 
				stations.get(0).getHsStates().get(0).getLevelMax(),
				stations.get(0).getHsStates().get(0).getLevelMin(),
				filefolder);
		//eotsf.output1();
		os.toExcelMethod1(filefolder);
	}

}
