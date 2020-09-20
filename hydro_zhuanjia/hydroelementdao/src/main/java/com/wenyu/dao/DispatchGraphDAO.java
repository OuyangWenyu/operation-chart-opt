package com.wenyu.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.wenyu.entity.DispatchGraphEntity;

@Component("dispatchGraphDAO")
public class DispatchGraphDAO {
	private HibernateTemplate hibernateTemplate;

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	@Resource
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	@SuppressWarnings("unchecked")
	public List<DispatchGraphEntity> loadGraphByBelongtoAndTbtype(int belongto, String tbtype)
			throws DataAccessException {
		return (List<DispatchGraphEntity>) this.hibernateTemplate.find(
				"from com.wenyu.entity.DispatchGraphEntity dispatchGraphEntity"
						+ " where dispatchGraphEntity.belongto=? and dispatchGraphEntity.timebuckettype=?",
				belongto, tbtype);
	}

	@SuppressWarnings("unchecked")
	public List<DispatchGraphEntity> loadLineByBelongtoAndTbtypeAndId(int belongto, String tbtype, int lineId)
			throws DataAccessException {
		return (List<DispatchGraphEntity>) this.hibernateTemplate.find(
				"from com.wenyu.entity.DispatchGraphEntity dispatchGraphEntity"
						+ " where dispatchGraphEntity.belongto=? and dispatchGraphEntity.timebuckettype=? and dispatchGraphEntity.linenum=?",
				belongto, tbtype, lineId);
	}

	public void saveAValue(DispatchGraphEntity dispatchGraphEntity) {
		if (!exists(dispatchGraphEntity.getLinenum(),dispatchGraphEntity.getOutput(), dispatchGraphEntity.getTimebuckettype(),
				dispatchGraphEntity.getTimenum(),dispatchGraphEntity.getWaterlevelbegin(),dispatchGraphEntity.getWaterlevelend()))
			hibernateTemplate.save(dispatchGraphEntity);
	}

	public boolean exists(int linenum,double output, String timebuckettype, int timenum,double waterlevelbegin,double waterlevelend) {
		@SuppressWarnings("unchecked")
		List<DispatchGraphEntity> dges = (List<DispatchGraphEntity>) hibernateTemplate.find(
				"from com.wenyu.entity.DispatchGraphEntity dge where dge.linenum = ? and dge.output = ? and dge.timebuckettype = ? and dge.timenum = ? and dge.waterlevelbegin = ? and dge.waterlevelend = ?",
				linenum,output, timebuckettype, timenum,waterlevelbegin,waterlevelend);
		if (dges != null && dges.size() > 0) {
			return true;
		}
		return false;
	}
}
