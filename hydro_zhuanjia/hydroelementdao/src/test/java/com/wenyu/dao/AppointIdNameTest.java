package com.wenyu.dao;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.wenyu.entity.AppointIdName;

public class AppointIdNameTest {

	@Test
	public void test() {
		HibernateTemplate hibernateTemplate = new HibernateTemplate();
		AppointIdName entity = new AppointIdName();
		AppointIdNameDAO dao = new AppointIdNameDAO();
		String name = "lantsangMainstreamStationNuozhadu";
		int number = 12011300;
		entity.setName(name);
		entity.setNumber(number);
		dao.save(entity);
	}



}
