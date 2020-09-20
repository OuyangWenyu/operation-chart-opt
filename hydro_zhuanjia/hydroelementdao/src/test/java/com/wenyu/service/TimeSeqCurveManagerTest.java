package com.wenyu.service;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wenyu.factory.state.HsStateOriginFactory;
import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.operation.statistics.BasicMathMethods;
import com.wenyu.service.excelutil.ExcelTool;

public class TimeSeqCurveManagerTest {
	TimeSeqCurveManager tscm;

	@Before
	public void before() {
		tscm = InitialContainer.getContext().getBean(TimeSeqCurveManager.class);
	}

	@Test
	public void testCreateTimeSeqCurve() {

		TimeSeqCurve hss = tscm.createTimeSeqCurve(12010100, 1000, "MONTH",636);
		System.out.print(hss);
	}

	@Test
	public void testWriteTimeSeqCurve() {
		HsStateOriginFactory hsof = InitialContainer.getContext().getBean(HsStateOriginFactory.class);
		int[] constrainttypes = { 101, 102, 201, 301 };
		List<HStationState> hss = hsof.createHsStates(12010100, constrainttypes, "MONTH", 1, "1953-06-01T00:00:00", 2);
		hss.get(0).setInflowReal(600);
		System.out.print(hss);
		tscm.writeTimeSeqCurve(12010100, new int[]{1000}, hss);
	}

	@Test
	public void testUpdateTimeSeqCurve() {
		double[][] data=null;
		try {
			data=ExcelTool.readDoubleFrom07Excel("C:/Users/asus/Desktop/各年历史径流/模拟径流/1954-2002.xlsx", "Sheet1");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*for(int i=0;i<data.length;i++){
			for(int j=0;j<data[0].length;j++){
				if(j>=5 && j<=10)
					data[i][j]=data[i][j]*0.868;
				else
					data[i][j]=data[i][j]*0.935;
			}
		}*/
		double[] transData=BasicMathMethods.array2DTo1D(data);
		/*for(int i=0;i<transData.length;i++){
			transData[i]=transData[i]*0.85;
		}*/
		tscm.updateTimeSeqCurve(12010100, 1000, "MONTH", 1, "1953-06-01T00:00:00", transData);
	}

	@Test
	public void testInsertTimeSeqCurve() {
		double[][][] data=new double[12][][];
		try {
			for(int i=0;i<12;i++){
				String sheet="Sheet"+(i+1);
				data[i]=ExcelTool.readDoubleFrom07Excel("E:/workspaceOctave/hydrology/MSAR/result20170314T090333.xlsx", sheet);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[][] transData=new double[12][];
		for(int i=0;i<12;i++){
			transData[i]=BasicMathMethods.array2DTo1D(data[i]);
			int belongto=12010100+i*100;
			if(belongto<12010101)
				tscm.insertTimeSeqCurve(belongto, 1000, "MONTH", 1, "2017-06-01T00:00:00", transData[i]);
			else
				tscm.insertTimeSeqCurve(belongto, 1001, "MONTH", 1, "2017-06-01T00:00:00", transData[i]);
		}

	}

	@Test
	public void testInsertTimeSeqCurveBatch() {
		double[][][] data=new double[7][][];
		try {
			for(int i=5;i<12;i++){
				String sheet="Sheet"+(i+1);
				data[i-5]=ExcelTool.readDoubleFrom07Excel("E:/workspaceOctave/hydrology/MSAR/result20170314T090333.xlsx", sheet);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[][] transData=new double[7][];
		for(int i=0;i<7;i++){
			transData[i]=BasicMathMethods.array2DTo1D(data[i]);
			int belongto=12010600+i*100;
			tscm.insertTimeSeqCurveBatch(belongto, 1001, "MONTH", 1, "2017-06-01T00:00:00", transData[i]);
		}
	}

	@Test
	public void nuzhaduInflowInsert(){
		double[][] inflow = null;
		try {
			inflow=ExcelTool.readDoubleFrom07Excel("C:/Users/YXL/Desktop/糯扎渡资料导入.xlsx", "sheet5");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[] inflow2 = BasicMathMethods.array2DTo1D(inflow);
		tscm.insertTimeSeqCurveBatch(12011300, 1001, "MONTH", 1, "1953-01-01T00:00:00", inflow2);
	}
}
