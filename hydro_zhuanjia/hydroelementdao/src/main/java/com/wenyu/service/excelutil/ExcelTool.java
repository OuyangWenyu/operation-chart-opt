package com.wenyu.service.excelutil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 通用Excel操作方法，定制时请自行书写
 * @author Ferry
 *
 */
public class ExcelTool {


	public static void write07Excel(String path, String sheetName, Object[][] data) throws IOException{
		Workbook wb = new XSSFWorkbook();
	    Sheet sheet = wb.createSheet(sheetName);
	    fillSheet(wb, sheet, data);
	    FileOutputStream fileOut = new FileOutputStream(path);
	    wb.write(fileOut);
	    fileOut.close();
	}

	public static void reWrite07Excel(String path, String sheetName, Object[][] data) throws IOException{
		InputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (Exception e) {
		}
		Workbook wb;
		if(fis == null){
			wb = new XSSFWorkbook();
		}else {
			wb = new XSSFWorkbook(fis);
		}
	    Sheet sheet = wb.createSheet(sheetName);
	    fillSheet(wb, sheet, data);
	    FileOutputStream fileOut = new FileOutputStream(path);
	    wb.write(fileOut);
	    fileOut.close();
	}

	public static void write03Excel(String path, String sheetName, Object[][] data) throws IOException{
		Workbook wb = new HSSFWorkbook();
	    Sheet sheet = wb.createSheet(sheetName);
	    fillSheet(wb, sheet, data);
	    FileOutputStream fileOut = new FileOutputStream(path);
	    wb.write(fileOut);
	    fileOut.close();
	}

	/**
	 * 向已有文档中添加
	 * @param path
	 * @param sheetName
	 * @param data
	 * @throws IOException
	 */
	public static void reWrite03Excel(String path, String sheetName, Object[][] data) throws IOException{
		InputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (Exception e) {
		}
		Workbook wb;
		if(fis == null){
			wb = new HSSFWorkbook();
		}else {
			wb = new HSSFWorkbook(fis);
		}
	    Sheet sheet = wb.createSheet(sheetName);
	    fillSheet(wb, sheet, data);
	    FileOutputStream fileOut = new FileOutputStream(path);
	    wb.write(fileOut);
	    fileOut.close();
	}


	@SuppressWarnings("resource")
	public static double[][] readDoubleFrom07Excel(String path, String sheetName) throws IOException{
		InputStream fis = new FileInputStream(path);
		Workbook wb = new XSSFWorkbook(fis);
		Sheet sheet = wb.getSheet(sheetName);
		Object[][] temp = readExcel(sheet);
		double[][] data = new double[temp.length][temp[0].length];
        for (int i=0;i<temp.length;i++) {
        	for (int j=0;j<temp[0].length;j++) {
        		data[i][j] = (Double) temp[i][j];
        	}
        }
       return data;
	}

	@SuppressWarnings("resource")
	public static Object[][] read07Excel(String path, String sheetName) throws IOException{
		InputStream fis = new FileInputStream(path);
		Workbook wb = new XSSFWorkbook(fis);
		Sheet sheet = wb.getSheet(sheetName);
		return readExcel(sheet);
	}

	@SuppressWarnings("resource")
	public static Object[][] read07Excel(InputStream fis, String sheetName) throws IOException{
		Workbook wb = new XSSFWorkbook(fis);
		Sheet sheet = wb.getSheet(sheetName);
		return readExcel(sheet);
	}

	@SuppressWarnings("resource")
	public static Object[][] read03Excel(String path, String sheetName) throws IOException{
		InputStream fis = new FileInputStream(path);
		Workbook wb = new HSSFWorkbook(fis);
		Sheet sheet = wb.getSheet(sheetName);
		return readExcel(sheet);
	}

	@SuppressWarnings("resource")
	public static Object[][] read03Excel(InputStream fis, String sheetName) throws IOException{
		Workbook wb = new HSSFWorkbook(fis);
		Sheet sheet = wb.getSheet(sheetName);
		return readExcel(sheet);
	}

	protected static void fillSheet(Workbook wb, Sheet sheet, Object[][] data){
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

	    for(int i=0;i<data.length;i++){
	    	Row row = sheet.createRow(i);
	    	for(int j=0;j<data[i].length;j++){
	    		Cell cell = row.createCell(j);
	    		if(data[i][j] instanceof String){
	    			cell.setCellValue((String)data[i][j]);
	    		}
	    		else if(data[i][j] instanceof Double){
	    			cell.setCellValue((Double)data[i][j]);
	    		}
	    		else if(data[i][j] instanceof Integer){
	    			cell.setCellValue((Integer)data[i][j]);
	    		}
	    		else if(data[i][j] instanceof Date){
	    			cell.setCellValue((Date)data[i][j]);
	    			cell.setCellStyle(cellStyle);
	    		}
	    	}
	    }
	}

	protected static Object[][] readExcel(Sheet sheet){
		Object[][] output = null;
		int rowStart = sheet.getFirstRowNum();
		int rowEnd = sheet.getLastRowNum();
		output = new Object[rowEnd + 1][];
		for(int i=rowStart;i<=rowEnd;i++){
			Row row = sheet.getRow(i);
			if(row == null)continue;
			int cellStart = row.getFirstCellNum();
			int cellEnd = row.getLastCellNum();
			if(cellStart < 0)continue;
			output[i] = new Object[cellEnd];
			for(int j=cellStart;j<cellEnd;j++){
				Cell cell = row.getCell(j);
				if(cell == null)continue;
				int type = cell.getCellType();
				if(type == Cell.CELL_TYPE_STRING) output[i][j] = cell.getStringCellValue();
				else if(type == Cell.CELL_TYPE_NUMERIC){
					if(DateUtil.isCellDateFormatted(cell)) output[i][j] = cell.getDateCellValue();
					else output[i][j] = cell.getNumericCellValue();
				}
				else if(type == Cell.CELL_TYPE_BOOLEAN) output[i][j] = cell.getBooleanCellValue();
				else if(type == Cell.CELL_TYPE_BLANK) output[i][j] = null;
				else if(type == Cell.CELL_TYPE_FORMULA) output[i][j] = cell.getNumericCellValue();
				else if(type == Cell.CELL_TYPE_ERROR) output[i][j] = null;
			}
		}
		return output;
	}

}
