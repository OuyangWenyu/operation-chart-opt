package com.wenyu.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="dispatchgraph")
public class DispatchGraphEntity {
	private int id;
	private int belongto;//属于哪一个电站
	private int linenum;//调度线编号
	private double output;//出力值
	private String timebuckettype;//时段类型
	private int timenum;//时段编号，就从1开始一直到12或者36或者其它表示从一年的第一个时段开始 具体是多少，取多少看使用情况
	private double waterlevelbegin;//水位值
	private double waterlevelend;//水位值
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
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
	 * @return the linenum
	 */
	public int getLinenum() {
		return linenum;
	}
	/**
	 * @param linenum the linenum to set
	 */
	public void setLinenum(int linenum) {
		this.linenum = linenum;
	}
	/**
	 * @return the output
	 */
	public double getOutput() {
		return output;
	}
	/**
	 * @param output the output to set
	 */
	public void setOutput(double output) {
		this.output = output;
	}
	public String getTimebuckettype() {
		return timebuckettype;
	}
	public void setTimebuckettype(String timebuckettype) {
		this.timebuckettype = timebuckettype;
	}
	/**
	 * @return the timenum
	 */
	public int getTimenum() {
		return timenum;
	}
	/**
	 * @param timenum the timenum to set
	 */
	public void setTimenum(int timenum) {
		this.timenum = timenum;
	}
	/**
	 * @return the waterlevel
	 */
	public double getWaterlevelbegin() {
		return waterlevelbegin;
	}
	/**
	 * @param waterlevel the waterlevel to set
	 */
	public void setWaterlevelbegin(double waterlevelbegin) {
		this.waterlevelbegin = waterlevelbegin;
	}
	public double getWaterlevelend() {
		return waterlevelend;
	}
	public void setWaterlevelend(double waterlevelend) {
		this.waterlevelend = waterlevelend;
	}

}
