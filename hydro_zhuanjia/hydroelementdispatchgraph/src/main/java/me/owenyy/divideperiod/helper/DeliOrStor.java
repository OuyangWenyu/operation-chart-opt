package me.owenyy.divideperiod.helper;

public enum DeliOrStor {
	DELIVERY(1,"供水期"),
	STORAGE(-1,"蓄水期"),
	NOT_DELI_OR_STOR(0,"不蓄不供期"),
	/*CANNOT_DELI(10,"不能供水期"),
	CANNOT_STOR(-10,"不能蓄水期"),//不能供水期和不能蓄水期都是不蓄不供期，不能供水期是水位到死水位不能再降，
*/	ALL_PERIODS(-101,"调节年度");
	
	private int ID;
	private String chineseName;
	
	DeliOrStor(int ID,String chineseName)
	{
		this.ID=ID;
		this.chineseName=chineseName;
	}

	public int getID() {
		return ID;
	}

	public String getChineseName() {
		return chineseName;
	}
	
	
}
