package com.wenyu.dao;


import org.junit.Test;

import com.wenyu.service.InitialContainer;

public class TimeSequenceCurveDAOTest {

	@Test
	public void testDistinctTypes() {
		TimeSequenceCurveDAO timeSeqCurveDAO=InitialContainer.getContext().getBean(TimeSequenceCurveDAO.class);
		int belongto=12010100;
		String tbType="MONTH";
		int[] varTypes = timeSeqCurveDAO.distinctTypes(belongto, tbType);
		System.out.print(varTypes);
	}

}
