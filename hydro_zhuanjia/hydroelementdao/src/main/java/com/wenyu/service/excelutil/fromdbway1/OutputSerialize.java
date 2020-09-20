package com.wenyu.service.excelutil.fromdbway1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.hydrostation.HydroStation;
import com.wenyu.hydroelements.operation.statistics.HsStatesStatistics;
import com.wenyu.hydroelements.operation.statistics.OutputVarsType;


/**
 * 把一些对象输入到持久层或数据层的方法
 *
 */
public class OutputSerialize {
	private List<HydroStation> cascadeStations;

	public List<HydroStation> getCascadeStations() {
		return cascadeStations;
	}

	public void setCascadeStations(List<HydroStation> cascadeStations) {
		this.cascadeStations = cascadeStations;
	}
	
	public OutputSerialize(List<HydroStation> cascadeStations) {
		super();
		this.cascadeStations = cascadeStations;
	}

	
	/**
	 *  把梯级的各电站的状态值输入到datamodel对象持久层中
	 */
	public List<OutputState> toStateDataModel()
	{
		List<OutputState> outputs=new ArrayList<OutputState>();
		for(int i=0;i<cascadeStations.size();i++)
		{
			for(int j=0;j<cascadeStations.get(i).getHsStates().size();j++)
			{
				OutputState temp=stateToOutput(cascadeStations.get(i).getHsSpec().getName(),
						cascadeStations.get(i).getHsStates().get(j));
				outputs.add(temp);
			}
		}
		return outputs;
		
	}
	
	private OutputState stateToOutput(String name,HStationState hStationState)
	{
		OutputState output=new OutputState();
		output.setEndtime(hStationState.getTimeEnd());
		output.setGeneration(hStationState.getGeneration());
		output.setHeadnet(hStationState.getHeadPure());
		output.setInflowreal(hStationState.getInflowReal());
		output.setLevelbegin(hStationState.getLevelBegin());
		output.setLeveldown(hStationState.getLevelDown());
		output.setLevelend(hStationState.getLevelEnd());
		output.setLevelupavg((hStationState.getLevelBegin()+hStationState.getLevelEnd())/2);
		output.setOutflow(hStationState.getOutflow());
		output.setOutflowgenerate(hStationState.getOutflowGeneration());
		output.setOutput(hStationState.getOutput());
		output.setOutflowsurplus(hStationState.getOutflowDesert());
		output.setStarttime(hStationState.getTimeStart());
		output.setStationname(name);
		return output;
		
	}
	
	public void toExcelMethod1(String filefolder)
	{
		
		TryFile.createFileFolder(filefolder);//文件夹建好
		
		OutputStation[] outputs=new OutputStation[cascadeStations.size()];
		String[] fileNames=new String[outputs.length];
		String[] indicators={"发电量（亿千瓦时）","枯期发电量","丰期发电量","丰枯电量比","统计保证出力"};
		List<OutputVarsType> vartypes=new ArrayList<OutputVarsType>();
		vartypes.add(OutputVarsType.INFLOW);
		vartypes.add(OutputVarsType.LEVEL_BEGIN);
		vartypes.add(OutputVarsType.LEVEL_END);
		vartypes.add(OutputVarsType.LEVEL_UP);
		vartypes.add(OutputVarsType.LEVEL_DOWN);
		vartypes.add(OutputVarsType.OUTPUT);
		vartypes.add(OutputVarsType.OUTFLOW);
		vartypes.add(OutputVarsType.OUTFLOW_SURPLUS);
		vartypes.add(OutputVarsType.HEAD_NET);
		vartypes.add(OutputVarsType.GENERATION);
		vartypes.add(OutputVarsType.OUTFLOW_GENERATION);
		
		List<double[]> allstationsoutput=new ArrayList<double[]>();//各电站出力值，统计梯级保证出力用
		
		for(int i=0;i<cascadeStations.size();i++)
		{
			fileNames[i]=cascadeStations.get(i).getHsSpec().getName();
			HsStatesStatistics statistics=new HsStatesStatistics(
					cascadeStations.get(i).getHsSpec().getName(),
					cascadeStations.get(i).getHsStates());
			allstationsoutput.add(statistics.choseOneVar(OutputVarsType.OUTPUT));
			
			outputs[i]=new OutputStation();
			outputs[i].setGenerationYear(statistics.calMulYearsAvgPower(12));
			int[] wetPeriodNums={6,7,8,9,10,11};
			int[] dryPeriodNums={12,1,2,3,4,5};
			outputs[i].setGenerationHighflow(statistics.calSomePeriodsYearsAvgPower(12, wetPeriodNums));
			outputs[i].setGenerationLowflow(statistics.calSomePeriodsYearsAvgPower(12, dryPeriodNums));
			outputs[i].setRadioHighLow(outputs[i].getGenerationHighflow()/outputs[i].getGenerationLowflow());
			
			outputs[i].setOutputWarrantedStatistics(statistics.statisticsWarrantedOutputMonth(0.9));
			
			double[] outputstationshow=new double[indicators.length];
			outputstationshow[0]=outputs[i].getGenerationYear();
			outputstationshow[1]=outputs[i].getGenerationLowflow();
			outputstationshow[2]=outputs[i].getGenerationHighflow();
			outputstationshow[3]=outputs[i].getRadioHighLow();
			outputstationshow[4]=outputs[i].getOutputWarrantedStatistics();
			OutputHandle.writeExcel(filefolder+"/"+fileNames[i]+".xlsx", "电站结果统计", 
					OutputHandle.outputForm(indicators, outputstationshow));
			
			OutputHandle.writeExcel(filefolder+"/"+fileNames[i]+".xlsx", "各指标各时段多年平均统计", 
					OutputHandle.outputAvg(vartypes,  statistics.getAvgs()));
			LocalDateTime startTime=cascadeStations.get(0).getHsStates().get(0).getTimeStart();
			LocalDateTime endTime=cascadeStations.get(0).getHsStates().get(cascadeStations.get(0).getHsStates().size()-1).getTimeEnd();
			//此处的年数计算，如果不是从一月份开始的，那么年数就应该是endTime.getYear()-startTime.getYear()，如果是从一月份开始，那么还要再加1
			String[] rowNames=new String[endTime.getYear()-startTime.getYear()+1+1];
			if(startTime.getMonthValue()>1)
				rowNames=new String[endTime.getYear()-startTime.getYear()+1];
			String[] colNames=new String[12+1];
			for(int j=0;j<rowNames.length;j++)
			{
				if(j==0)
					rowNames[j]="时段";
				else 
					rowNames[j]=(startTime.getYear()+j-1)+"年";
			}
			int startMonth=startTime.getMonthValue()-1;
			for(int j=0;j<colNames.length;j++)
			{
				if(j==0)
					colNames[j]="时段";
				else 
					colNames[j]=((startMonth+j-1)%12+1)+"月";
			}
			
			for(int j=0;j<vartypes.size();j++)
			{
				OutputHandle.writeExcel(filefolder+"/"+fileNames[i]+".xlsx", 
						vartypes.get(j).getVarName(),
						OutputHandle.outputFormWithHead(
						rowNames, colNames, statistics.showYearsData(12,vartypes.get(j))));
			}
			
		}
		
		OutputCascade output=new OutputCascade();
		output=OutputHandle.calOutputCascade(outputs);
		System.out.println("梯级整体结果输出：");
		System.out.println("年电量（亿千瓦时） :"+output.getGenerationYear());
		System.out.println("枯期电量 :"+output.getGenerationLowflow());
		System.out.println("丰水期电量 :"+output.getGenerationHighflow());
		System.out.println("丰枯电量比 :"+output.getRadioHighLow());
		System.out.println("输出完毕");
		double[] cascadeputput=new double[4];
		cascadeputput[0]=output.getGenerationYear();
		cascadeputput[1]=output.getGenerationLowflow();
		cascadeputput[2]=output.getGenerationHighflow();
		cascadeputput[3]=output.getRadioHighLow();
		String[] indis={"年电量（亿千瓦时） :","枯期电量 :","丰水期电量 :","丰枯电量比 :"};
		
		double caswar=OutputHandle.calOutputCascadeWarrantedPutput(allstationsoutput, 0.9);
		System.out.println(caswar);
		OutputHandle.writeExcel(filefolder+"/梯级.xlsx", "结果统计", 
				OutputHandle.outputForm(indis, cascadeputput));
	}
	
}
