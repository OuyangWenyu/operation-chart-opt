package com.wenyu.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;

public class ReadProperties {
	public static TreeMap<String, String> readAndSort() {
		InputStream inStream = ReadProperties.class.getClassLoader().getResourceAsStream("appoint.properties");
		Properties prop = new Properties();
		TreeMap<String, String> tm=new TreeMap<String, String>(new Comparator<String>(){
			public int compare(String k1, String k2)
			{
				Integer i1=Integer.parseInt(k1);
				Integer i2=Integer.parseInt(k2);
				return i1.compareTo(i2);
			}
		});
		try {
			prop.load(inStream);
			Iterator<String> it = prop.stringPropertyNames().iterator();
			
			while (it.hasNext()) {
				String key = it.next();
				String value=prop.getProperty(key);
				tm.put(key, value);
			}
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tm;
	}

}
