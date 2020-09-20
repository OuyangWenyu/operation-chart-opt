package me.owenyy.simuoperation;


import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.junit.Test;

import com.wenyu.factory.topology.Series;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.service.excelutil.fromdbway1.ExcelOutputToSpecialFormat;
import com.wenyu.service.excelutil.fromdbway1.OutputSerialize;
import com.wenyu.service.excelutil.fromdbway1.TryFile;

public class MultiStationTimeOpeTest {

	@Test
	public void testSeriesSimuOperation() {
		Series series=new Series();
		int[] stationsId=new int[12];
		for(int i=0;i<12;i++){
			stationsId[i]=12010100+i*100;
		}
		series.constructSeries1N(stationsId, new int[] { 10001, 10002 },
				new int[] { 101, 102, 201, 301 }, "MONTH", 1,  "1953-06-01T00:00", 636);
		//series.constructSeries("1953-06-01T00:00", 636);
		MultiStationTimeOpe msto=new MultiStationTimeOpe();
		msto.seriesSimuOpera(series);//有如美
		//msto.seriesRunoffStationsSimuOpera(series);//无如美，是按如美径流式去算的
		ArrayList<HydroStation> stations=new ArrayList<HydroStation>();
		for(int i=0;i<stationsId.length-1;i++)
			stations.add(series.getStations().get(i));
		stations.add(0, series.getLeadStation().getHydroStation());
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
	@Test
	public void testSeriesSimuOperation1Nxi() {
		Series series=new Series();
		int[] stationsId=new int[12];
		for(int i=0;i<12;i++){
			stationsId[i]=12010100+i*100;
		}
		Interval[] intervals=new Interval[1];
		intervals[0]=new Interval(6,9);
		series.constructSeries1Nxi(stationsId, new int[]{4,8}, new int[]{4}, intervals,
				new int[] { 10001, 10002 },	new int[] { 101, 102, 201, 301 }, "MONTH",
				1,  "1953-06-01T00:00", 636);
		//series.constructSeries("1953-06-01T00:00", 636);
		MultiStationTimeOpe msto=new MultiStationTimeOpe();
		double[] levelBegins={2815,2605,2525,2340,2230,1906,1818,1732,1586,1477,1408,1307};
		msto.seriesSimuOpera(series,new int[]{3,7},levelBegins);//有如美
//		msto.seriesRunoffStationsSimuOpera(series);//无如美，是按如美径流式去算的
		ArrayList<HydroStation> stations=new ArrayList<HydroStation>();
		for(int i=0;i<stationsId.length-1;i++)
			stations.add(series.getStations().get(i));
		stations.add(0, series.getLeadStation().getHydroStation());
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
	/*@Test
	public void testSeriesSimuOperationBatch() {//多年分成100份进行计算，还没写好
		Series series=new Series();
		int[] stationsId=new int[12];
		for(int i=0;i<12;i++){
			stationsId[i]=12010100+i*100;
		}
		for(int i=0;i<100;i++){
			series.constructSeries1N(stationsId, new int[] { 10001, 10002 },
					new int[] { 101, 102, 201, 301 }, "MONTH", 1,  "2017-06-01T00:00", 1200);
			MultiStationTimeOpe msto=new MultiStationTimeOpe();
			msto.seriesSimuOpera(series);//有如美
//			msto.seriesRunoffStationsSimuOpera(series);//无如美，是按如美径流式去算的
			ArrayList<HydroStation> stations=new ArrayList<>();
			for(int i=0;i<stationsId.length-1;i++)
				stations.add(series.getStations().get(i));
			stations.add(0, series.getLeadStation().getHydroStation());
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
		
	}*/
}
