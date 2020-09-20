package com.wenyu.service;

import java.util.Iterator;
import java.util.TreeMap;

import org.junit.Test;

import com.wenyu.entity.AppointIdName;


public class AppointIdNameManagerTest {

	@Test
	public void test() {
		AppointIdNameManager ainm=InitialContainer.getContext().getBean(AppointIdNameManager.class);
		TreeMap<String, String> tm=ReadProperties.readAndSort();
		Iterator<String> it = tm.keySet().iterator();
		while(it.hasNext())
		{
			String key = it.next();
			String value=tm.get(key);
			int keyInt=Integer.parseInt(key);
			AppointIdName entity=new AppointIdName();
			entity.setNumber(keyInt);
			entity.setName(value);
			ainm.saveAppointIdName(entity);
		}
	}

}
