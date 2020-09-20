package com.wenyu.service.excelutil.fromdbway1;

import java.io.IOException;
import java.util.List;

import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.service.excelutil.ExcelTool;


public class ExcelOutputToSpecialFormat {
	private List<HydroStation> riverReach;
	private double levelNormal;
	private double levelDead;
    private String path;
	
	public ExcelOutputToSpecialFormat(List<HydroStation> riverReach, double levelNormal, double levelDead,String path) {
		super();
		this.riverReach = riverReach;
		this.levelNormal = levelNormal;
		this.levelDead = levelDead;
		this.path=path;
	}


	/**
	 * @return the riverReach
	 */
	public List<HydroStation> getRiverReach() {
		return riverReach;
	}

	/**
	 * @param riverReach the riverReach to set
	 */
	public void setRiverReach(List<HydroStation> riverReach) {
		this.riverReach = riverReach;
	}

	/**
	 * @return the levelNormal
	 */
	public double getLevelNormal() {
		return levelNormal;
	}

	/**
	 * @param levelNormal the levelNormal to set
	 */
	public void setLevelNormal(double levelNormal) {
		this.levelNormal = levelNormal;
	}

	/**
	 * @return the levelDead
	 */
	public double getLevelDead() {
		return levelDead;
	}

	/**
	 * @param levelDead the levelDead to set
	 */
	public void setLevelDead(double levelDead) {
		this.levelDead = levelDead;
	}

	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}

	
	/**
	 * 一个电站计算一个水量利用率
	 */
	public void calRateOfUsingWater()
	{
		
	}
	
	/**
	 * 一个电站计算一个保证率
	 */
	public void calAssranceRate()
	{
		
	}
	
	/**
	 * 长序列输出结果到excel
	 */
	public void output1()
	{
		int withRumeiOrNot=0;
		
		String planname=path+"/梯级运行过程"+withRumeiOrNot;
		//输出相关结果变量，先定义相关变量
		Object[][] levelbegins=new Object[riverReach.size()][riverReach.get(0).getHsStates().size()];
		Object[][] inflows=new Object[riverReach.size()][riverReach.get(0).getHsStates().size()];
		Object[][] outputs=new Object[riverReach.size()][riverReach.get(0).getHsStates().size()];
		Object[][] outflows=new Object[riverReach.size()][riverReach.get(0).getHsStates().size()];
		Object[][] outflowsurplus=new Object[riverReach.size()][riverReach.get(0).getHsStates().size()];
		Object[][] heads=new Object[riverReach.size()][riverReach.get(0).getHsStates().size()];
		Object[][] generations=new Object[riverReach.size()][riverReach.get(0).getHsStates().size()];
		Object[][] outflowgenerations=new Object[riverReach.size()][riverReach.get(0).getHsStates().size()];
		
		
		for(int i=0;i<riverReach.size();i++)
		{
			for (int j = 0; j < riverReach.get(i).getHsStates().size(); j++) {
				
				inflows[i][j] =	riverReach.get(i).getHsStates().get(j).getInflowReal();
				outflows[i][j] =riverReach.get(i).getHsStates().get(j).getOutflow();
				outflowgenerations[i][j] =riverReach.get(i).getHsStates().get(j).getOutflowGeneration();
				outflowsurplus[i][j] =riverReach.get(i).getHsStates().get(j).getOutflowDesert();
				levelbegins[i][j] =	riverReach.get(i).getHsStates().get(j).getLevelBegin();
				heads[i][j] =riverReach.get(i).getHsStates().get(j).getHeadPure();
				generations[i][j] =riverReach.get(i).getHsStates().get(j).getGeneration();
				outputs[i][j] =riverReach.get(i).getHsStates().get(j).getOutput();
			}
		}
		
		try {
			
			ExcelTool.reWrite07Excel(planname+".xlsx", "入库流量", inflows);
			ExcelTool.reWrite07Excel(planname+".xlsx", "出库流量", outflows);
			ExcelTool.reWrite07Excel(planname+".xlsx", "发电流量", outflowgenerations);
			ExcelTool.reWrite07Excel(planname+".xlsx", "弃水流量", outflowsurplus);
			ExcelTool.reWrite07Excel(planname+".xlsx", "水位", levelbegins);
			ExcelTool.reWrite07Excel(planname+".xlsx", "水头", heads);
			ExcelTool.reWrite07Excel(planname+".xlsx", "发电量", generations);
			ExcelTool.reWrite07Excel(planname+".xlsx", "出力", outputs);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("计算过程没有编译错误");	
			
	}
	
	

	
	
}
