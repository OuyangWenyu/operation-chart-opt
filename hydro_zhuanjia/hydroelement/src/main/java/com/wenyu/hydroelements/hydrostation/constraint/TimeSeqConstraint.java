package com.wenyu.hydroelements.hydrostation.constraint;

import java.util.List;

import com.wenyu.hydroelements.timebucket.TimeBucketSequence;

public class TimeSeqConstraint {
	private TimeBucketSequence dates;//java8的日期类型的数据
	private List<Constraint> constraints;//各个时段的各种约束
	/**
	 * @return the dates
	 */
	public TimeBucketSequence getDates() {
		return dates;
	}
	/**
	 * @param dates the dates to set
	 */
	public void setDates(TimeBucketSequence dates) {
		this.dates = dates;
	}
	/**
	 * @return the constraints
	 */
	public List<Constraint> getConstraints() {
		return constraints;
	}
	/**
	 * @param constraints the constraints to set
	 */
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	
}
