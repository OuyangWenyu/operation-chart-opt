package com.wenyu.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.wenyu.entity.CurveThreeDimen;
@Component("curveThreeDimenDAO")
public class CurveThreeDimenDAO {
	private HibernateTemplate hibernateTemplate;
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}
	@Resource
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
	@SuppressWarnings("unchecked")
	public List<CurveThreeDimen> loadCurvesByBelongtoAndType(int belongto,int type) throws DataAccessException {
        return (List<CurveThreeDimen>) this.hibernateTemplate.find(
        		"from com.wenyu.entity.CurveThreeDimen curveThreeDimen"
        		+ " where curveThreeDimen.belongto=? and curveThreeDimen.type=?", belongto, type);
    }
}
