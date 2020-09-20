package com.wenyu.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.wenyu.entity.CurveTwoDimen;

/**
 * 二维曲线的DAO
 * @author  OwenYY
 *
 */
@Component("curveTwoDimenDAO")
public class CurveTwoDimenDAO {
	private HibernateTemplate hibernateTemplate;
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}
	@Resource
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
	@SuppressWarnings("unchecked")
	public List<CurveTwoDimen> loadCurvesByBelongtoAndType(int belongto,int type) throws DataAccessException {
        return (List<CurveTwoDimen>) this.hibernateTemplate.find(
        		"from com.wenyu.entity.CurveTwoDimen curveTwoDimen"
        		+ " where curveTwoDimen.belongto=? and curveTwoDimen.type=?", belongto, type);
    }
}
