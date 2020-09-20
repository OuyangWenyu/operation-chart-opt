package com.wenyu.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InitialContainer {
	private static ApplicationContext context;
	static {
		setContext(new ClassPathXmlApplicationContext("beans.xml"));
	}
	public static ApplicationContext getContext() {
		return context;
	}
	public static void setContext(ApplicationContext context) {
		InitialContainer.context = context;
	}
	
}
