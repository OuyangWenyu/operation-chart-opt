package com.wenyu.hydroelements.hydrostation.dispatchgraph;

public class CommonConsts {
	
	/*自然年的一些常量*/
	public static final int  SECONDS_PER_COMMONYEAR=31536000;
	public static final int  SECONDS_PER_LEAPYEAR=31622400;
	public static final int SECONDS_IN_MONTH_MULTIYEAR_AVG=2629800;//(3*365+366)/4/12*24*3600多年平均的平均每个月的估计秒数，估算多年平均值时用
	public static final int SECONDS_IN_DECAD_MULTIYEAR_AVG=876600;//多年平均的平均每个旬的估计秒数，估算多年平均值时用
	public static final int[] COMMONYEAR_DAYS_PER_DECAD={
			10,10,11,10,10,8,10,10,11,10,10,10,10,10,11,10,10,10,10,10,11,10,10,11,10,10,10,10,10,11,10,10,10,10,10,11};
	public static final int[] LEAPYEAR_DAYS_PER_DECAD={
			10,10,11,10,10,9,10,10,11,10,10,10,10,10,11,10,10,10,10,10,11,10,10,11,10,10,10,10,10,11,10,10,10,10,10,11};
	public static final int[] COMMONYEAR_DAYS_PER_MONTH={
			31,28,31,30,31,30,31,31,30,31,30,31};
	public static final int[] LEAPYEAR_DAYS_PER_MONTH={
			31,29,31,30,31,30,31,31,30,31,30,31};
	public static final int[] COMMONYEAR_SECONDS_PER_DECAD={
			864000,864000,950400,864000,864000,691200,864000,864000,950400,864000,864000,864000,864000,864000,950400,864000,864000,864000,864000,864000,950400,864000,864000,950400,864000,864000,864000,864000,864000,950400,864000,864000,864000,864000,864000,950400};
	public static final int[] LEAPYEAR_SECONDS_PER_DECAD={
			864000,864000,950400,864000,864000,777600,864000,864000,950400,864000,864000,864000,864000,864000,950400,864000,864000,864000,864000,864000,950400,864000,864000,950400,864000,864000,864000,864000,864000,950400,864000,864000,864000,864000,864000,950400};
	public static final int[] COMMONYEAR_SECONDS_PER_MONTH={
			2678400,2419200,2678400,2592000,2678400,2592000,2678400,2678400,2592000,2678400,2592000,2678400};
	public static final int[] LEAPYEAR_SECONDS_PER_MONTH={
			2678400,2505600,2678400,2592000,2678400,2592000,2678400,2678400,2592000,2678400,2592000,2678400};

	public static final int MONTHS_PER_YEAR=12;
	public static final int DECADS_PER_YEAR=36;
	
	public static final String[] STARTDATESTRING_EVERY_MONTH={//平闰年是一样的，只是结束的时间不一样
			"01-01","02-01","03-01","04-01","05-01","06-01",
			"07-01","08-01","09-01","10-01","11-01","12-01"};
	public static final String[] STARTDATESTRING_EVERY_DECAD={
			"01-01","01-11","01-21","02-01","02-11","02-21","03-01","03-11","03-21",
			"04-01","04-11","04-21","05-01","05-11","05-21","06-1","06-11","06-21",
			"07-01","07-11","07-21","08-01","08-11","08-21","09-01","09-11","09-21",
			"10-01","10-11","10-21","11-01","11-11","11-21","12-01","12-11","12-21"};
	public static final String[] ENDDATESTRING_EVERY_MONTH_COMMONYEAR={
			"01-31","02-28","03-31","04-30","05-31","06-30",
			"07-31","08-31","09-30","10-31","11-30","12-31"};
	public static final String[] ENDDATESTRING_EVERY_DECAD_COMMONYEAR={
			"01-10","01-20","01-31","02-10","02-20","02-28","03-10","03-20","03-31",
			"04-10","04-20","04-30","05-10","05-20","05-31","06-10","06-20","06-30",
			"07-10","07-20","07-31","08-10","08-20","08-31","09-10","09-20","09-30",
			"10-10","10-20","10-31","11-10","11-20","11-30","12-10","12-20","12-31"};

}
