package com.wenyu.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
/**
 * 这个实体类主要用来处理计算的得到的结果，对应的TimeSequenceCurve主要是一些原始数据
 * @author  OwenYY
 *
 */
@Entity
@Table(name="timesequencecurvetemp")
public class TimeSequenceCurveTemp {
	private String id;
	private int belongto;
	private int type;
	private String timebuckettype;
	private LocalDateTime starttime;
	private LocalDateTime endtime;
	private int timelength;
	private double valueavg;
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the belongto
	 */
	public int getBelongto() {
		return belongto;
	}
	/**
	 * @param belongto the belongto to set
	 */
	public void setBelongto(int belongto) {
		this.belongto = belongto;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	public String getTimebuckettype() {
		return timebuckettype;
	}
	public void setTimebuckettype(String timebuckettype) {
		this.timebuckettype = timebuckettype;
	}
	/**
	 * @return the starttime
	 */
	public LocalDateTime getStarttime() {
		return starttime;
	}
	/**
	 * @param starttime the starttime to set
	 */
	public void setStarttime(LocalDateTime starttime) {
		this.starttime = starttime;
	}
	/**
	 * @return the endtime
	 */
	public LocalDateTime getEndtime() {
		return endtime;
	}
	/**
	 * @param endtime the endtime to set
	 */
	public void setEndtime(LocalDateTime endtime) {
		this.endtime = endtime;
	}
	/**
	 * @return the timelength
	 */
	public int getTimelength() {
		return timelength;
	}
	/**
	 * @param timelength the timelength to set
	 */
	public void setTimelength(int timelength) {
		this.timelength = timelength;
	}
	/**
	 * @return the valueavg
	 */
	public double getValueavg() {
		return valueavg;
	}
	/**
	 * @param valueavg the valueavg to set
	 */
	public void setValueavg(double valueavg) {
		this.valueavg = valueavg;
	}

	
}
