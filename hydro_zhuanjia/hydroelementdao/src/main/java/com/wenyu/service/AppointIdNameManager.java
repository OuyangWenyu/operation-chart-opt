package com.wenyu.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.dao.AppointIdNameDAO;
import com.wenyu.entity.AppointIdName;

@Component("appointIdNameManager")
public class AppointIdNameManager {
	private AppointIdNameDAO appointIdNameDAO;

	public AppointIdNameDAO getAppointIdNameDAO() {
		return appointIdNameDAO;
	}

	@Resource
	public void setAppointIdNameDAO(AppointIdNameDAO appointIdNameDAO) {
		this.appointIdNameDAO = appointIdNameDAO;
	}

	public void saveAppointIdName(AppointIdName entity) {
		try {
			if (!appointIdNameDAO.exists(entity.getName()))
				appointIdNameDAO.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String loadNameByNumber(int number){
		List<AppointIdName> ain=appointIdNameDAO.loadNameByNumber(number);
		String name=ain.get(0).getName();
		return name;
	}
}
