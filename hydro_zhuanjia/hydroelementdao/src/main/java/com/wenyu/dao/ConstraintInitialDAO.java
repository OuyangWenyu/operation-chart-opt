package com.wenyu.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.wenyu.entity.ConstraintInitial;

@Component("constraintInitialDAO")
public class ConstraintInitialDAO {
	private HibernateTemplate hibernateTemplate;

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	@Resource
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	/**
	 * @param belongto
	 *            属于哪个电站
	 * @param type
	 *            属于什么约束类型
	 * @param month
	 *            所属月份
	 * @return
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public List<ConstraintInitial> loadConstraintsByBelongtoAndTypeAndMonth(int belongto, int type, int month)
			throws DataAccessException {
		return (List<ConstraintInitial>) this.hibernateTemplate.find(
				"from com.wenyu.entity.ConstraintInitial constraintInitial"
						+ " where constraintInitial.belongto=? and constraintInitial.type=?"
						+ " and constraintInitial.startmonth<=? and constraintInitial.endmonth>=?",
				belongto, type, month, month);
	}

	@SuppressWarnings("unchecked")
	public List<ConstraintInitial> loadConstraintsByBelongto(int belongto) throws DataAccessException {
		return (List<ConstraintInitial>) this.hibernateTemplate.find(
				"from com.wenyu.entity.ConstraintInitial constraintInitial where constraintInitial.belongto=?",
				belongto);
	}
}
