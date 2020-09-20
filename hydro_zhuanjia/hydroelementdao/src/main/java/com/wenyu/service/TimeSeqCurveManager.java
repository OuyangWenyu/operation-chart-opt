package com.wenyu.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.dao.TimeSequenceCurveDAO;
import com.wenyu.entity.TimeSequenceCurve;
import com.wenyu.entity.TimeSequenceCurveTemp;
import com.wenyu.hydroelements.curve.TimeSeqCurve;
import com.wenyu.hydroelements.curve.TimeSeqCurveFactory;
import com.wenyu.hydroelements.hydrostation.HStationState;
import com.wenyu.hydroelements.operation.basic.HydroDateUtil;
import com.wenyu.service.util.HsStateToTimeSeq;

@Component("timeSeqCurveManager")
public class TimeSeqCurveManager {
	private TimeSequenceCurveDAO timeSeqCurveDAO;

	public TimeSequenceCurveDAO getTimeSeqCurveDAO() {
		return timeSeqCurveDAO;
	}

	@Resource
	public void setTimeSeqCurveDAO(TimeSequenceCurveDAO timeSeqCurveDAO) {
		this.timeSeqCurveDAO = timeSeqCurveDAO;
	}

	/**
	 * @param belongto
	 *            曲线数据属于哪个对象，是电站是机组还是什么
	 * @param type
	 *            曲线的类型是什么，是径流，是负荷还是什么
	 * @param tbType
	 *            时段类型
	 * @param tbNums
	 *            总共有多少个时段
	 * @return  每个时段都是一个单元的
	 */
	public TimeSeqCurve createTimeSeqCurve(int belongto, int type, String tbType,int tbNums) {
		TimeSeqCurve timeSeqCurve = new TimeSeqCurve();
		List<TimeSequenceCurve> tsc = timeSeqCurveDAO.loadSomeCurvesByBelongtoAndTypeAndTbtype(belongto, type, tbType,tbNums);
		LocalDateTime startTime = tsc.get(0).getStarttime();
		int num = tsc.size();
		double[] data = new double[tsc.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = tsc.get(i).getValueavg();
		}
		TimeSeqCurveFactory timeSeqCurveFactory = new TimeSeqCurveFactory(tbType, 1, startTime, num, data);
		timeSeqCurve = timeSeqCurveFactory.curvePlotting();
		return timeSeqCurve;

	}

	/**
	 * @param belongto
	 *            曲线数据属于哪个对象，是电站是机组还是什么
	 * @param type
	 *            曲线的类型是什么，是径流，是负荷还是什么
	 * @param tbType
	 *            时段类型
	 * @param unitNums
	 *            单时段的时段个数 例如一个时段时几小时
	 * @param startTimeString
	 *            开始时间
	 * @param endTimeString
	 *            结束时间
	 * @return
	 */
	public TimeSeqCurve createTimeSeqCurve(int belongto, int type, String tbType, int unitNums, LocalDateTime startTime,
			LocalDateTime endTime) {
		TimeSeqCurve timeSeqCurve = new TimeSeqCurve();
		
		List<TimeSequenceCurve> tsc = timeSeqCurveDAO.loadCurvesByBelongtoAndTypeAndTbtypeAndTime(belongto, type,
				tbType, startTime, endTime);
		if(tsc==null || tsc.size()<1)
			return null;
		int num = tsc.size();
		double[] data = new double[tsc.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = tsc.get(i).getValueavg();
		}
		TimeSeqCurveFactory timeSeqCurveFactory = new TimeSeqCurveFactory(tbType, unitNums, startTime, num, data);
		timeSeqCurve = timeSeqCurveFactory.curvePlotting();
		return timeSeqCurve;

	}
	
		
	
	/**
	 * 往数据库里面写时序数据，写进temp的表格里面，而先不直接写进TimeSeq的表格里面
	 * @param belongto
	 * @param types
	 * @param states 
	 */
	public void writeTimeSeqCurve(int belongto,int[] types,List<HStationState> states){
		//创建TimeSeq同名的temp表格
		List<TimeSequenceCurveTemp> tscts=HsStateToTimeSeq.hsStatesToTimeSequenceCurve(belongto, types, states);
		//把相应的数据写进temp表
		for(int i=0;i<tscts.size();i++){
			TimeSequenceCurveTemp temp=timeSeqCurveDAO.exsits(belongto, tscts.get(i).getType(), tscts.get(i).getTimebuckettype(), tscts.get(i).getStarttime());
			if(temp!=null){
				temp.setValueavg(tscts.get(i).getValueavg());
				if(tscts.get(i).getValueavg()!=temp.getValueavg())
					timeSeqCurveDAO.update(temp);
			}
			else
				timeSeqCurveDAO.save(tscts.get(i));
		}
	}
	
	
	/**
	 * 根据径流数据更新数据库表格数据，首先根据belongto和数据类型以及时段类型把对应的全部数据取出来，
	 * 然后根据输入的数据，依次更新对象的值，注意取值的时候要按时间顺序取值
	 */
	public void updateTimeSeqCurve(int belongto,int type, String tbType, int unitNums,
			String startTime, double[] runoffData){
		LocalDateTime startDateTime = LocalDateTime.parse(startTime);
		LocalDateTime endDateTime = HydroDateUtil.calEndTime(startDateTime, tbType, unitNums, runoffData.length);

		List<TimeSequenceCurve> tscs=timeSeqCurveDAO.loadCurvesByBelongtoAndTypeAndTbtypeAndTime(belongto, type, tbType, startDateTime, endDateTime);
		
		for(int i=0;i<tscs.size();i++){
			boolean temp=timeSeqCurveDAO.exist(belongto, tscs.get(i).getType(), tscs.get(i).getTimebuckettype(), tscs.get(i).getStarttime());
			if(temp){
				tscs.get(i).setValueavg(runoffData[i]);
				timeSeqCurveDAO.update(tscs.get(i));
			}
			else
				timeSeqCurveDAO.save(tscs.get(i));
		}
	}
	
	/**
	 * 把runoffData中的数据插入到数据库之中
	 * 然后根据输入的数据，依次更新对象的值，注意插入的时候要按时间顺序取值
	 */
	public void insertTimeSeqCurve(int belongto,int type, String tbType, int unitNums,
			String startTime, double[] runoffData){
		LocalDateTime startDateTime = LocalDateTime.parse(startTime);

		List<TimeSequenceCurve> tscs=createTimeSequenceCurveByNewRunoff(belongto, type, tbType, unitNums,startDateTime, runoffData);
		for(int i=0;i<tscs.size();i++){
			boolean temp=timeSeqCurveDAO.exist(belongto, tscs.get(i).getType(), tscs.get(i).getTimebuckettype(), tscs.get(i).getStarttime());
			if(!temp)
				timeSeqCurveDAO.save(tscs.get(i));
		}
	}
	/**
	 * 把runoffData中的数据批量插入到数据库之中
	 * 然后根据输入的数据，依次更新对象的值，注意插入的时候要按时间顺序取值
	 */
	public void insertTimeSeqCurveBatch(int belongto,int type, String tbType, int unitNums,
			String startTime, double[] runoffData){
		LocalDateTime startDateTime = LocalDateTime.parse(startTime);
		List<TimeSequenceCurve> tscs=createTimeSequenceCurveByNewRunoff(belongto, type, tbType, unitNums,startDateTime, runoffData);
		timeSeqCurveDAO.insertTimeSequenceCurveBatch(tscs);
	}

	/**
	 * @param belongto
	 * @param type
	 * @param tbType
	 * @param startDateTime
	 * @param runoffData  外部输入的一组数据
	 * @return  为把数据插入数据库中，生成一组对象数据
	 */
	private List<TimeSequenceCurve> createTimeSequenceCurveByNewRunoff(int belongto, int type, String tbType,int unitNums,
			LocalDateTime startDateTime, double[] runoffData) {
		// TODO Auto-generated method stub
		List<TimeSequenceCurve> tscs=new ArrayList<TimeSequenceCurve>();
		for(int i=0;i<runoffData.length;i++){
			TimeSequenceCurve timeSequence=new TimeSequenceCurve();
			timeSequence.setBelongto(belongto);
			LocalDateTime end = HydroDateUtil.calEndTime(startDateTime, tbType, unitNums, i+1).minusSeconds(1);
			timeSequence.setEndtime(end);
			LocalDateTime start = HydroDateUtil.calEndTime(startDateTime, tbType, unitNums, i);
			timeSequence.setStarttime(start);
			timeSequence.setTimebuckettype(tbType);
			timeSequence.setTimelength((int) Duration.between(start, end).getSeconds()+1);
			timeSequence.setType(type);
			timeSequence.setValueavg(runoffData[i]);
			tscs.add(timeSequence);
		}
		return tscs;
	}

}
