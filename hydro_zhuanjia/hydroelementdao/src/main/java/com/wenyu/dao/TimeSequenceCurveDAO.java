package com.wenyu.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.wenyu.entity.TimeSequenceCurve;
import com.wenyu.entity.TimeSequenceCurveTemp;

@Component("timeSequenceCurveDAO")
public class TimeSequenceCurveDAO {
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
	 * @param type
	 * @param tbtype
	 * @param tbNums
	 * @return 所有符合belongto，type，tbtype的数据，取最前面tbNums个
	 * @throws DataAccessException
	 */
	public List<TimeSequenceCurve> loadSomeCurvesByBelongtoAndTypeAndTbtype(int belongto, int type, String tbtype,int tbNums)
			throws DataAccessException {
		List<TimeSequenceCurve> allresult=(List<TimeSequenceCurve>)this.hibernateTemplate
				.find("from com.wenyu.entity.TimeSequenceCurve timeSequenceCurve"
						+ " where timeSequenceCurve.belongto=? and timeSequenceCurve.type=?"
						+ " and timeSequenceCurve.timebuckettype=?", belongto, type, tbtype);
		List<TimeSequenceCurve> result=new ArrayList<TimeSequenceCurve>();
		for(int i=0;i<tbNums;i++)
			result.add(allresult.get(i));
		return  result;
	}

	@SuppressWarnings("unchecked")
	public List<TimeSequenceCurve> loadCurvesByBelongtoAndTypeAndTbtypeAndTime(int belongto, int type, String tbtype,
			LocalDateTime start, LocalDateTime end) throws DataAccessException {
		return (List<TimeSequenceCurve>) this.hibernateTemplate.find(
				"from com.wenyu.entity.TimeSequenceCurve timeSequenceCurve"
						+ " where timeSequenceCurve.belongto=? and timeSequenceCurve.type=?"
						+ " and timeSequenceCurve.timebuckettype=? and timeSequenceCurve.starttime>=? and timeSequenceCurve.endtime<=?",
				belongto, type, tbtype, start, end);
	}

	/**
	 * @param belongto
	 * @param type
	 * @param tbtype
	 * @param start
	 * @param end
	 * @return 从TimeSequenceCurveTemp表格中取相应条件的数据
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public List<TimeSequenceCurveTemp> loadDatasTempByBelongtoAndTypeAndTbtypeAndTime(int belongto, int type,
			String tbtype, LocalDateTime start, LocalDateTime end) throws DataAccessException {
		return (List<TimeSequenceCurveTemp>) this.hibernateTemplate.find(
				"from com.wenyu.entity.TimeSequenceCurveTemp timeSequenceCurve"
						+ " where timeSequenceCurve.belongto=? and timeSequenceCurve.type=?"
						+ " and timeSequenceCurve.timebuckettype=? and timeSequenceCurve.starttime>=? and timeSequenceCurve.endtime<=?",
				belongto, type, tbtype, start, end);
	}

	/**
	 * @param entity
	 */
	public void save(TimeSequenceCurveTemp entity) {
		hibernateTemplate.saveOrUpdate(entity);
	}

	/**
	 * @param entity
	 */
	public void update(TimeSequenceCurveTemp entity) {
		hibernateTemplate.update(entity);
	}

	/**
	 * @param entity
	 */
	public void save(TimeSequenceCurve entity) {
		hibernateTemplate.saveOrUpdate(entity);
	}

	/**
	 * @param tscs  批量插入数据，重写hibernate的execute里的doInHibernate
	 */
	public void insertTimeSequenceCurveBatch(final List<TimeSequenceCurve> tscs) {
		this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Object>() {
			public Object doInHibernate(Session session) throws HibernateException {
				int batchSize = 25;
				for (int i = 0; i < tscs.size(); i++) {
					session.save(tscs.get(i));
					if (i > 0 && i % batchSize == 0) {
						session.flush();
						session.clear();
					}
				}
				return null;
			}
		});
	}

	public void update(TimeSequenceCurve entity) {
		// TODO Auto-generated method stub
		hibernateTemplate.update(entity);
	}

	/**
	 * 检测是否存在实体，检测到了就覆盖？hibernate保存的时候是不是自动覆盖的，这个需要试一下
	 */
	public TimeSequenceCurveTemp exsits(int belongto, int type, String tbtype, LocalDateTime start) {
		@SuppressWarnings("unchecked")
		List<TimeSequenceCurveTemp> tscts = (List<TimeSequenceCurveTemp>) hibernateTemplate
				.find("from com.wenyu.entity.TimeSequenceCurveTemp tsct where tsct.belongto =? and "
						+ "tsct.type=? and tsct.timebuckettype=? and tsct.starttime=?", belongto, type, tbtype, start);
		if (tscts != null && tscts.size() > 0) {
			return tscts.get(0);
		}
		return null;
	}

	/**
	 * @param belongto
	 * @param tbType
	 * @return 对某个电站的某种时段类型，它的TimeSequenceCurveTemp表格里面有几种类型的数据（这个还没写好 不太对）
	 */
	public int[] distinctTypes(final int belongto, final String tbType) {
		@SuppressWarnings("unchecked")
		List<TimeSequenceCurveTemp> tscts = (List<TimeSequenceCurveTemp>) hibernateTemplate
				.execute(new HibernateCallback<Object>() {
					public Object doInHibernate(Session session) throws HibernateException {
						String queryString = "select distinct type from com.wenyu.entity.TimeSequenceCurveTemp tsct where tsct.belongto =? and tsct.timebuckettype=?";
						Query queryObject = session.createQuery(queryString);
						queryObject.setParameter(0, belongto);
						queryObject.setParameter(1, tbType);
						Object lst = queryObject.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();
						return lst;
					}
				});
		int[] types = new int[tscts.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = tscts.get(i).getType();
		}
		return types;

	}

	/**
	 * 往TimeSequenceCurve表格里面插入新的资料数据
	 * 
	 * @param entity
	 */
	public void insert(TimeSequenceCurve entity) {
		// if(!exist(entity.getBelongto(),entity.getType(),entity.getTimebuckettype(),entity.getStarttime()))
		hibernateTemplate.saveOrUpdate(entity);
	}

	/**
	 * 检测是否存在实体，存在就返回真，否则返回假
	 */
	public boolean exist(int belongto, int type, String tbtype, LocalDateTime start) {
		@SuppressWarnings("unchecked")
		List<TimeSequenceCurve> tscts = (List<TimeSequenceCurve>) hibernateTemplate
				.find("from com.wenyu.entity.TimeSequenceCurve tsct where tsct.belongto =? and "
						+ "tsct.type=? and tsct.timebuckettype=? and tsct.starttime=?", belongto, type, tbtype, start);
		if (tscts != null && tscts.size() > 0) {
			return true;
		}
		return false;
	}

}
