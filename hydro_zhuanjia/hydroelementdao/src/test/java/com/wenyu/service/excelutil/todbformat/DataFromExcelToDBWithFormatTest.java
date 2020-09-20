package com.wenyu.service.excelutil.todbformat;


import org.junit.Test;

public class DataFromExcelToDBWithFormatTest {

	@Test
	public void testLongDurationRunoffInput() {
		DataFromExcelToDBWithFormat.longDurationRunoffInput("C:/Users/asus/Desktop/jsjrunoff.xlsx", "Sheet1");
	}

}
