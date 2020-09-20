package me.owenyy.divideperiod;


import java.io.IOException;

import org.junit.Test;

import com.wenyu.service.excelutil.ExcelTool;

public class ServeTimeTest {

	@Test
	public void testGetProvideSaveTimeFinal() {
		double[][] inflowdataArr = null;
		try {
			inflowdataArr = ExcelTool.readDoubleFrom07Excel("C:/Users/asus/Desktop/"+"如美"+".xlsx", "自然年历史月径流");
		} catch (IOException e) {
			e.printStackTrace();
		}
		double V_benifit = 24.698;//兴利库容，亿m³
		int rowNumTemp = inflowdataArr.length;
		int colNumTemp = inflowdataArr[0].length;
		int yearPeriods = colNumTemp;//12个月
		int allperiods = rowNumTemp*colNumTemp;
		double[] inflowdataSeries = new double[allperiods];
		for(int i=0; i<rowNumTemp; i++){
			for(int j=0; j<colNumTemp; j++){
				inflowdataSeries[j+i*colNumTemp] = inflowdataArr[i][j];				
			}
		}
		String tbtype="MONTH";
		DivideDeliveryAndStorage serveTime=new ServeTime();
		int[] providestateFinal = serveTime.getProvideSaveTimeFinal(inflowdataSeries, V_benifit, tbtype,1953,6);
		double[][] providestatetemp = new double[rowNumTemp][colNumTemp];
		for(int i=0; i<rowNumTemp; i++){
			for(int j=0; j<colNumTemp; j++){
				providestatetemp[i][j] = providestateFinal[j+yearPeriods*i];
			}
		}
		
		Object[][] providestate=new Object[providestatetemp.length][providestatetemp[0].length];
		for(int i=0;i<providestatetemp.length;i++)
		{
			for(int j=0;j<providestatetemp[0].length;j++)
			{
				providestate[i][j]= providestatetemp[i][j];
			}
		}
		try {
			ExcelTool.reWrite03Excel("C:/Users/asus/Desktop/供蓄水期划分结果.xls", "供蓄水期划分", providestate);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
