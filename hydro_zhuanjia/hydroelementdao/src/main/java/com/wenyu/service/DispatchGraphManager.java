package com.wenyu.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.dao.AppointIdNameDAO;
import com.wenyu.dao.DispatchGraphDAO;
import com.wenyu.entity.DispatchGraphEntity;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraphFactory;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchLine;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchLineItem;
import com.wenyu.hydroelements.operation.basic.HydroDateUtil;
import com.wenyu.service.excelutil.ExcelTool;

@Component("dispatchGraphManager")
public class DispatchGraphManager {
	private DispatchGraphDAO dispatchGraphDAO;
	private AppointIdNameDAO appointIdNameDAO;

	public DispatchGraphDAO getDispatchGraphDAO() {
		return dispatchGraphDAO;
	}

	@Resource
	public void setDispatchGraphDAO(DispatchGraphDAO dispatchGraphDAO) {
		this.dispatchGraphDAO = dispatchGraphDAO;
	}

	public AppointIdNameDAO getAppointIdNameDAO() {
		return appointIdNameDAO;
	}

	@Resource
	public void setAppointIdNameDAO(AppointIdNameDAO appointIdNameDAO) {
		this.appointIdNameDAO = appointIdNameDAO;
	}

	/**
	 * @param stationId
	 * @param tbType
	 * @return 从数据库中读取stationId的属于tbType类型的调度图，生成DispatchGraph对象
	 */
	public DispatchGraph createDispatchGraph(int stationId, String tbType) {
		List<DispatchGraphEntity> dispatchGraphEntity = dispatchGraphDAO.loadGraphByBelongtoAndTbtype(stationId,
				tbType);
		if(dispatchGraphEntity.size()<10) return null;//至少要有一条线，一条线一般情况下也有12个月，因此如果小于10，必有问题
		DispatchGraph dispatchGraph = new DispatchGraph();
		int[] lineNums = new int[dispatchGraphEntity.size()];
		List<Integer> lineNum = new ArrayList<Integer>();
		lineNum.add(dispatchGraphEntity.get(0).getLinenum());
		for (int i = 0; i < dispatchGraphEntity.size(); i++) {
			lineNums[i] = dispatchGraphEntity.get(i).getLinenum();
			if (i > 0) {
				if (lineNums[i] != lineNums[i - 1]) {
					lineNum.add(lineNums[i]);
				}
			}
		}
		List<DispatchLine> line = new ArrayList<DispatchLine>();
		for (int i = 0; i < lineNum.size(); i++) {
			List<DispatchLineItem> items = new ArrayList<DispatchLineItem>();
			List<DispatchGraphEntity> entities = dispatchGraphDAO.loadLineByBelongtoAndTbtypeAndId(stationId, tbType,
					lineNum.get(i));

			for (int j = 0; j < entities.size(); j++) {

				DispatchLineItem item = DispatchGraphFactory.createDispatchLineItem(tbType,
						entities.get(j).getTimenum(), entities.get(j).getWaterlevelbegin(),
						entities.get(j).getWaterlevelend(), entities.get(j).getOutput());
				items.add(item);
			}
			DispatchLine temp = DispatchGraphFactory.createDispatchLine(items, lineNum.get(i));
			line.add(temp);
		}
		dispatchGraph.setDispacthline(line);
		dispatchGraph.setStationName(appointIdNameDAO.loadNameByNumber(stationId).get(0).getName());
		return dispatchGraph;
	}

	/**
	 * 把对象里的数据写进数据库
	 * 
	 * @param dispatchGraph
	 */
	public void writeDispatchGraph(int stationId, DispatchGraph dispatchGraph) {
		List<DispatchGraphEntity> dispatchGraphEntity = new ArrayList<DispatchGraphEntity>();
		String timebuckettype = "MONTH";
		if (dispatchGraph.getDispacthline().get(0).getWaterLevel().get(0).getBeginTime()
				.until(dispatchGraph.getDispacthline().get(0).getWaterLevel().get(0).getEndTime()).getDays() < 20) {
			timebuckettype = "DECAD";
		}
		for (int i = 0; i < dispatchGraph.getDispacthline().size(); i++) {
			for (int j = 0; j < dispatchGraph.getDispacthline().get(i).getWaterLevel().size(); j++) {
				DispatchGraphEntity dge = new DispatchGraphEntity();
				dge.setBelongto(stationId);
				dge.setLinenum(dispatchGraph.getDispacthline().get(i).getId());
				dge.setOutput(dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j).getOutput());
				dge.setTimebuckettype(timebuckettype);
				int timenum = dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j).getBeginTime()
						.getMonthValue();
				if (timebuckettype == "DECAD")
					timenum = HydroDateUtil.getDecad(dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j).getBeginTime());
				dge.setTimenum(timenum);
				dge.setWaterlevelbegin(dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j).getLevelBegin());
				dge.setWaterlevelend(dispatchGraph.getDispacthline().get(i).getWaterLevel().get(j).getLevelEnd());
				dispatchGraphEntity.add(dge);
			}
		}
		for (int i = 0; i < dispatchGraphEntity.size(); i++) {
			dispatchGraphDAO.saveAValue(dispatchGraphEntity.get(i));
		}
	}
	
	/**
	 * 根据stationId和tbType从DB中读取数据并写入excel中，格式为
	 * @param stationId
	 * @param tbType
	 * @param sIndex 蓄水期时段编号，从开始到结束  比如[6 7 8 9 10]，如果tbType是MONTH，那么分别表示6月到10月
	 * @param dIndex 供水期时段编号，从开始到结束 比如[11 12 1 2 3 4 5]，如果tbType是MONTH，那么分别表示11月到5月，这个顺序要严格按照调度图时间顺序，它会作为读取一条线的各个量的排序参考
	 */
	public void writeToExcelFromDB(String path,String sheetName,int stationId, String tbType,final int[] sIndex,final int[] dIndex){
		List<DispatchGraphEntity> dispatchGraphEntity = dispatchGraphDAO.loadGraphByBelongtoAndTbtype(stationId,
				tbType);
		List<DispatchGraphEntity> sHighs=new ArrayList<DispatchGraphEntity>();
		List<DispatchGraphEntity> sUWars=new ArrayList<DispatchGraphEntity>();
		List<DispatchGraphEntity> sLWars=new ArrayList<DispatchGraphEntity>();
		List<DispatchGraphEntity> sLows=new ArrayList<DispatchGraphEntity>();
		List<DispatchGraphEntity> dHighs=new ArrayList<DispatchGraphEntity>();
		List<DispatchGraphEntity> dUWars=new ArrayList<DispatchGraphEntity>();
		List<DispatchGraphEntity> dLWars=new ArrayList<DispatchGraphEntity>();
		List<DispatchGraphEntity> dLows=new ArrayList<DispatchGraphEntity>();
		for(int i=0;i<dispatchGraphEntity.size();i++){
			if(dispatchGraphEntity.get(i).getLinenum()/10==10012)//属于蓄水期加大出力线
				sHighs.add(dispatchGraphEntity.get(i));
			else if(dispatchGraphEntity.get(i).getLinenum()/10==10011)//属于供水期加大出力线 
				dHighs.add(dispatchGraphEntity.get(i));
			else if(dispatchGraphEntity.get(i).getLinenum()==100009)//属于蓄水期保证出力上限 
				sUWars.add(dispatchGraphEntity.get(i));
			else if(dispatchGraphEntity.get(i).getLinenum()==100008)//属于供水期保证出力上限 
				dUWars.add(dispatchGraphEntity.get(i));
			else if(dispatchGraphEntity.get(i).getLinenum()==100013)//属于蓄水期保证出力下限
				sLWars.add(dispatchGraphEntity.get(i));
			else if(dispatchGraphEntity.get(i).getLinenum()==100012)//属于供水期保证出力下限
				dLWars.add(dispatchGraphEntity.get(i));
			else if(dispatchGraphEntity.get(i).getLinenum()/10==10022)//属于蓄水期降低出力线
				sLows.add(dispatchGraphEntity.get(i));
			else if(dispatchGraphEntity.get(i).getLinenum()/10==10021)//属于供水期降低出力线
				dLows.add(dispatchGraphEntity.get(i));
		}
		//构造一个比较器，对各个List进行排序以便把数据输出到excel
		Comparator<DispatchGraphEntity> dgeComparator=new Comparator<DispatchGraphEntity>(){
			public int compare(DispatchGraphEntity o1, DispatchGraphEntity o2) {
				int com=0;
				if(o1.getOutput()>o2.getOutput())
					com=-1;
				else if(o1.getOutput()==o2.getOutput()){
					int o1index=-1;
					int o2index=-1;
					for(int j=0;j<sIndex.length;j++){
						if(o1.getTimenum()==sIndex[j])
							o1index=j;
						if(o2.getTimenum()==sIndex[j])
							o2index=j;
					}
					for(int j=0;j<dIndex.length;j++){
						if(o1.getTimenum()==dIndex[j])
							o1index=j;
						if(o2.getTimenum()==dIndex[j])
							o2index=j;
					}
					if(o1index>o2index)
						com=1;
					else if(o1index==o2index)
						com=0;
					else 
						com=-1;
				}else
					com=1;
					
				return com;
			}
		};
		sHighs.sort(dgeComparator);
		sUWars.sort(dgeComparator);
		sLWars.sort(dgeComparator);
		sLows.sort(dgeComparator);
		dHighs.sort(dgeComparator);
		dUWars.sort(dgeComparator);
		dLWars.sort(dgeComparator);
		dLows.sort(dgeComparator);
		
		int highNum=sHighs.size()/sIndex.length;
		int lowNum=sLows.size()/sIndex.length;
		double[][] outputs=new double[2][highNum+2+lowNum];
		
		double[][] highLines=new double[highNum][sIndex.length+dIndex.length];
		for(int i=0;i<highNum;i++){
			int j=0;
			for(;j<sIndex.length;j++)
				highLines[i][j]=sHighs.get(i*sIndex.length+j).getWaterlevelbegin();
			for(;j<sIndex.length+dIndex.length;j++)
				highLines[i][j]=dHighs.get(i*dIndex.length+j-sIndex.length).getWaterlevelbegin();
			outputs[0][i]=sHighs.get(i*sIndex.length).getOutput();
			outputs[1][i]=dHighs.get(i*dIndex.length+j-1-sIndex.length).getOutput();
		}
		
		double[][] warLines=new double[2][sIndex.length+dIndex.length];
		for(int j=0;j<sIndex.length;j++){
			warLines[0][j]=sUWars.get(j).getWaterlevelbegin();
			warLines[1][j]=sLWars.get(j).getWaterlevelbegin();
		}
		outputs[0][highNum]=sUWars.get(0).getOutput();
		outputs[0][highNum+1]=sLWars.get(0).getOutput();
		for(int j=sIndex.length;j<sIndex.length+dIndex.length;j++){
			warLines[0][j]=dUWars.get(j-sIndex.length).getWaterlevelbegin();
			warLines[1][j]=dLWars.get(j-sIndex.length).getWaterlevelbegin();
		}
		outputs[1][highNum]=dUWars.get(0).getOutput();
		outputs[1][highNum+1]=dLWars.get(0).getOutput();
		double[][] lowLines=new double[lowNum][sIndex.length+dIndex.length];
		for(int i=0;i<lowNum;i++){
			int j=0;
			for(;j<sIndex.length;j++)
				lowLines[i][j]=sLows.get(i*sIndex.length+j).getWaterlevelbegin();
			for(;j<sIndex.length+dIndex.length;j++)
				lowLines[i][j]=dLows.get(i*dIndex.length+j-sIndex.length).getWaterlevelbegin();
			outputs[0][i+highNum+2]=sLows.get(i*sIndex.length).getOutput();
			outputs[1][i+highNum+2]=dLows.get(i*dIndex.length+j-1-sIndex.length).getOutput();
		}
		
		Object[][] lines=new Object[highNum+2+lowNum+2][sIndex.length+dIndex.length];
		for(int i=0;i<lines.length;i++){
			if(i<highNum){
				for(int j=0;j<highLines[i].length;j++){
					lines[i][j]=highLines[i][j];
				}
			}
			else if(i<highNum+2){
				for(int j=0;j<warLines[i-highNum].length;j++){
					lines[i][j]=warLines[i-highNum][j];
				}
			}
			else if(i<highNum+2+lowNum){
				for(int j=0;j<lowLines[i-highNum-2].length;j++){
					lines[i][j]=lowLines[i-highNum-2][j];
				}
			}
			else{
				for(int j=0;j<outputs[i-highNum-2-lowNum].length;j++){
					lines[i][j]=outputs[i-highNum-2-lowNum][j];
				}
			}
				
		}
		try {
			ExcelTool.reWrite07Excel(path, sheetName, lines);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
