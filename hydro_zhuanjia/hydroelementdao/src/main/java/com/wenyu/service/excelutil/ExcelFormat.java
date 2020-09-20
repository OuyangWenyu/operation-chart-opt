package com.wenyu.service.excelutil;

import java.io.IOException;

/**
 * Excel格式的一些处理
 * @author  OwenYY
 *
 */
public class ExcelFormat {
	/**
	 * @param data
	 * @param heads
	 * @return 给二维的数组加一行表头
	 */
	public static Object[][] addFormHead(Object[][] data, String[] heads) {
		Object[][] outputs = new Object[data.length + 1][data[0].length];
		for (int i = 0; i < outputs.length; i++) {

			if (i == 0) {
				for (int j = 0; j < outputs[0].length; j++) {
					outputs[i][j] = (Object) heads[j];
				}
			} else {
				for (int j = 0; j < outputs[0].length; j++) {
					outputs[i][j] = data[i - 1][j];
				}
			}

		}
		return outputs;
	}
	
	/**
	 * @param left
	 * @param right
	 * @return 两个二维数组 左右组合成一个新的二维数组
	 */
	public static Object[][] leftRightCombine(Object[][] left,Object[][] right){
		Object[][] result=new Object[Math.max(left.length, right.length)][left[0].length+right[0].length];
		for(int i=0;i<result.length;i++){
			for(int j=0;j<result[0].length;j++){
				if(j<left[0].length){
					result[i][j]=i<left.length?left[i][j]:null;
				}
				else{
					result[i][j]=right[i][j-left[0].length];
				}
			}
		}
		return result;
	}
	
	/**
	 * @param up
	 * @param down
	 * @return 上下结合成新的Object[][]
	 */
	public static Object[][] updownCombine(Object[][] up,Object[][] down){
		Object[][] result=new Object[up.length+down.length][Math.max(up[0].length, down[0].length)];
		for(int i=0;i<result.length;i++){
			for(int j=0;j<result[0].length;j++){
				if(i<up.length){
					result[i][j]=j<up[0].length?up[i][j]:null;
				}
				else{
					result[i][j]=down[i-up.length][j];
				}
			}
		}
		return result;
		
	}
	
	/**
	 * 把三维的values的每个二维数组写到对应的sheetName里面去
	 * @param values
	 * @param path
	 * @param sheetsName
	 */
	public static void writeMultiSheets(Object[][][] values,String path,String[] sheetsName){
		for(int i=0;i<values.length;i++){
			try {
				ExcelTool.reWrite07Excel(path, sheetsName[i], values[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
