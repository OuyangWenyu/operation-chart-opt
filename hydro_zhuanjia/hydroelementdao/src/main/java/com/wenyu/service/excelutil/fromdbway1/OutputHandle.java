package com.wenyu.service.excelutil.fromdbway1;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;

import com.wenyu.hydroelements.operation.statistics.OutputPeriodAvg;
import com.wenyu.hydroelements.operation.statistics.OutputVarsType;
import com.wenyu.service.excelutil.ExcelTool;



/**
 * 本项目专用，处理数据输出到excel表，数据直接从内存读进excel不是一个好的面向对象的编程方式
 *
 */
public class OutputHandle {
	public static OutputCascade calOutputCascade(OutputStation[] outputs)
	{
		OutputCascade output=new OutputCascade();
		double[] generationsYears=new double[outputs.length];//年电量
		double[] generationsLowflow=new double[outputs.length];//枯期电量
		double[] generationsHighflow=new double[outputs.length];//丰期电量
		for(int i=0;i<outputs.length;i++)
		{
			generationsHighflow[i]=outputs[i].getGenerationHighflow();
			generationsLowflow[i]=outputs[i].getGenerationLowflow();
			generationsYears[i]=outputs[i].getGenerationYear();
		}
		double geneyear=StatUtils.sum(generationsYears);
		double genewet=StatUtils.sum(generationsHighflow);
		double genedry=StatUtils.sum(generationsLowflow);
		double rate=genewet/genedry;
		
		output.setGenerationHighflow(genewet);
		output.setGenerationLowflow(genedry);
		output.setGenerationYear(geneyear);
		output.setRadioHighLow(rate);
		return output;
	}
	
	/**
	 * @param outputs  各电站每年各时段的出力值
	 * @return
	 */
	public static double calOutputCascadeWarrantedPutput(List<double[]> outputs,double assurance)
	{
		double[] casout=new double[outputs.get(0).length];
		for(int i=0;i<casout.length;i++)
		{
			for(int j=0;j<outputs.size();j++)
			{
				casout[i]=casout[i]+(outputs.get(j))[i];
			}
		}
		double result=BasicMathMethods.calFrequencyValue(casout, assurance);
		return result;
		
	}
	
	
	public static void writeDoubleExcel(String path, String sheetName, double[][] data)
	{
		File temp = new File(path);
        Object[][] inputdata=new Object[data.length][data[0].length];
        for (int i=0;i<data.length;i++) {
        	for (int j=0;j<data[0].length;j++) {
        		inputdata[i][j] = (Object) data[i][j];
        	}
        }
		try {
			if (temp.getName().endsWith(".xls")) {
				ExcelTool.reWrite03Excel(path, sheetName,inputdata);
			}
			else if (temp.getName().endsWith(".xlsx")) {
				ExcelTool.reWrite07Excel(path, sheetName,inputdata);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void writeExcel(String path, String sheetName, Object[][] inputdata)
	{
		File temp = new File(path);
        
		try {
			if (temp.getName().endsWith(".xls")) {
				ExcelTool.reWrite03Excel(path, sheetName,inputdata);
			}
			else if (temp.getName().endsWith(".xlsx")) {
				ExcelTool.reWrite07Excel(path, sheetName,inputdata);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * @param output  电站结果类
	 * @param indicators  指标
	 * @param data 数据
	 * @return  输出到excel表的数据形式，指标在一列，数据在一列，两者关系对应
	 */
	public static Object[][] outputForm(String[] indicators,double[] data)
	{
		Object[][] outputs=new Object[data.length][2];
		for(int i=0;i<outputs.length;i++)
		{
			outputs[i][0]=(Object)indicators[i];
			outputs[i][1]=(Object)data[i];
		}
		return outputs;
		
	}
	
	/**
	 * @param rowNames 行名（每行的开头）
	 * @param colNames  列名
	 * @param data 数据
	 * @return  输出带表头的数据
	 */
	public static Object[][] outputFormWithHeadDay(String[] rowNames,String[] colNames,
			double[][] data)
	{
		Object[][] outputs=new Object[data.length+1][];
		for(int i=0;i<outputs.length;i++)
		{
			
			if(i==0)
			{
				outputs[i]=new Object[32];
				for(int j=0;j<outputs[0].length;j++)
				{
					outputs[i][j]=(Object)colNames[j];
				}
			}
			else
			{
				outputs[i]=new Object[data[i-1].length+1];
				for(int j=0;j<outputs[i].length;j++)
				{
					if(j==0)
					{
						outputs[i][0]=rowNames[i];
					}
					else
					{
						
						outputs[i][j]=data[i-1][j-1];
					}
				}
			}
			
		}
		return outputs;
		
	}
	/**
	 * @param rowNames 行名（每行的开头）
	 * @param colNames  列名
	 * @param data 数据
	 * @return  输出带表头的数据
	 */
	public static Object[][] outputFormWithHead(String[] rowNames,String[] colNames,
			double[][] data)
	{
		Object[][] outputs=new Object[data.length+1][data[0].length+1];
		for(int i=0;i<outputs.length;i++)
		{
			
			if(i==0)
			{
				for(int j=0;j<outputs[0].length;j++)
				{
					outputs[i][j]=(Object)colNames[j];
				}
			}
			else
			{
				for(int j=0;j<outputs[0].length;j++)
				{
					if(j==0)
					{
						outputs[i][0]=rowNames[i];
					}
					else
					{
						
						outputs[i][j]=data[i-1][j-1];
					}
				}
			}
			
		}
		return outputs;
		
	}
	
	
	public static Object[][] outputAvg(List<OutputVarsType> vartypes,OutputPeriodAvg avg)
	{
		//avg.getAvgs().get(vartypes.get(0)).length+1表示时段长加1，加的1是为字符留出来的
		Object[][] outputs=new Object[vartypes.size()][avg.getAvgs().get(vartypes.get(0)).length+1];
		for(int i=0;i<vartypes.size();i++)
		{
			outputs[i][0]=vartypes.get(i).getVarName()+vartypes.get(i).getUnit();
			for(int j=0;j<outputs[0].length-1;j++)
			{
				outputs[i][j+1]=(avg.getAvgs().get(vartypes.get(i)))[j];
			}
		}
		Object[][] results=new Object[outputs.length+1][outputs[0].length];
		for(int i=0;i<results.length;i++)
		{
			if(i==0)
			{
				for(int j=0;j<results[0].length;j++)
				{
					if(j==0)
					{
						results[i][j]="时段";	
					}
					else
					{
						results[i][j]=((5+j-1)%12+1)+"月";
					}
				}
			}
			else
			{
				for(int j=0;j<results[0].length;j++)
				{
					results[i][j]=outputs[i-1][j];
				}	
			}
		}
		return results;
	}
}
