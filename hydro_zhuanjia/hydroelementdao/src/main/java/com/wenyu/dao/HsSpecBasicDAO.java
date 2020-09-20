package com.wenyu.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.wenyu.entity.HStationBasicSpec;

@Component("hsSpecBasicDAO")
public class HsSpecBasicDAO {
	private HibernateTemplate hibernateTemplate;

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	@Resource
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	/**
	 * @param id
	 *            电站的id标识
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HStationBasicSpec> loadSpecByBelongto(int belongto) throws DataAccessException {
		return (List<HStationBasicSpec>) this.hibernateTemplate.find(
				"from com.wenyu.entity.HStationBasicSpec hStationBasicSpec" + " where hStationBasicSpec.belongto=?",
				belongto);
	}

	/**
	 * @param id
	 *            电站的id标识
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HStationBasicSpec> loadSpecByBelongtoAndType(int belongto, int type) throws DataAccessException {
		return  (List<HStationBasicSpec>) this.hibernateTemplate
				.find("from com.wenyu.entity.HStationBasicSpec hStationBasicSpec"
						+ " where hStationBasicSpec.belongto=? and hStationBasicSpec.type=?", belongto, type);
	}
}
