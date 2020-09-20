package com.wenyu.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="constraintinitial")
public class ConstraintInitial {
	private int id;
	private int belongto;
	private int type;
	private int startmonth;
	private int endmonth;
	private double valuemin;
	private double valuemax;
	/**
	 * @return the id
	 */
	@Id
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
	 * @return the startmonth
	 */
	public int getStartmonth() {
		return startmonth;
	}
	/**
	 * @param startmonth the startmonth to set
	 */
	public void setStartmonth(int startmonth) {
		this.startmonth = startmonth;
	}
	/**
	 * @return the endmonth
	 */
	public int getEndmonth() {
		return endmonth;
	}
	/**
	 * @param endmonth the endmonth to set
	 */
	public void setEndmonth(int endmonth) {
		this.endmonth = endmonth;
	}
	/**
	 * @return the valuemin
	 */
	public double getValuemin() {
		return valuemin;
	}
	/**
	 * @param valuemin the valuemin to set
	 */
	public void setValuemin(double valuemin) {
		this.valuemin = valuemin;
	}
	/**
	 * @return the valuemax
	 */
	public double getValuemax() {
		return valuemax;
	}
	/**
	 * @param valuemax the valuemax to set
	 */
	public void setValuemax(double valuemax) {
		this.valuemax = valuemax;
	}

}
