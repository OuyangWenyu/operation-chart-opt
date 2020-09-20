package com.wenyu.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.wenyu.entity.AppointIdName;

@Component("appointIdNameDAO")
public class AppointIdNameDAO {
	private HibernateTemplate hibernateTemplate;

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	@Resource
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public void save(AppointIdName entity) {
		hibernateTemplate.save(entity);
	}

	public boolean exists(String name) throws Exception {
		@SuppressWarnings("unchecked")
		List<AppointIdName> users = (List<AppointIdName>) hibernateTemplate
				.find("from com.wenyu.entity.AppointIdName ain where ain.name = '" + name + "'");
		if (users != null && users.size() > 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<AppointIdName> loadNameByNumber(int number) throws DataAccessException {
		return (List<AppointIdName>) this.hibernateTemplate
				.find("from com.wenyu.entity.AppointIdName appointIdName where appointIdName.number=?", number);
	}
}
