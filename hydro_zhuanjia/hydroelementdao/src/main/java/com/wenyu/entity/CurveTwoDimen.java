package com.wenyu.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="curvetwodimen")
public class CurveTwoDimen{
	private int id;
	private int belongto;//属于哪个对象是属于哪个机组还是属于哪个电站（电站和机组都采取统一的命名和编号形式）
	private int type;//曲线本身的类型id（也应统一曲线的编号形式）
	private double v0;
	private double v1;
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
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
	/**
	 * @return the v0
	 */
	public double getV0() {
		return v0;
	}
	/**
	 * @param v0 the v0 to set
	 */
	public void setV0(double v0) {
		this.v0 = v0;
	}
	/**
	 * @return the v1
	 */
	public double getV1() {
		return v1;
	}
	/**
	 * @param v1 the v1 to set
	 */
	public void setV1(double v1) {
		this.v1 = v1;
	}
	
	
}
