package me.owenyy.divideperiod.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 经验频率的类，一般是原序号、新序号、排序对象、补充的其它值和经验频率五个值组成
 *
 */
public class EmpiricalFrequency {
	private int serialNumOld;
	private int serialNumNew;
	private double value;
	private double valueelse;
	private double frequency;
	
	/**
	 * @param serialNumOld  原序列编号，可以是数字0 1 2 3  也可以是特定的 如年份
	 * @param value  要计算的数据
	 * @param valueelse  辅助数据
	 */
	public EmpiricalFrequency(int serialNumOld, double value ,double valueelse) {
		super();
		this.serialNumOld = serialNumOld;
		this.value = value;
		this.valueelse=valueelse;
	}
	/**
	 * @param serialNumOld  原序列编号，可以是数字0 1 2 3  也可以是特定的 如年份
	 * @param value  要计算的数据
	 */
	public EmpiricalFrequency(int serialNumOld, double value ) {
		super();
		this.serialNumOld = serialNumOld;
		this.value = value;
	}
	/**
	 * @param serialNumNew
	 * @param serialNumOld  原序列编号，可以是数字0 1 2 3  也可以是特定的 如年份
	 * @param value  要计算的数据
	 * @param frequency
	 */
	public EmpiricalFrequency(int serialNumNew, int serialNumOld,double value,double frequency ) {
		super();
		this.serialNumNew=serialNumNew;
		this.serialNumOld = serialNumOld;
		this.value = value;
		this.frequency=frequency;
	}
	/**
	 * @param serialNumNew
	 * @param serialNumOld  原序列编号，可以是数字0 1 2 3  也可以是特定的 如年份
	 * @param value  要计算的数据
	 * @param valueelse
	 * @param frequency
	 */
	public EmpiricalFrequency(int serialNumNew, int serialNumOld,double value,double valueelse,double frequency ) {
		super();
		this.serialNumNew=serialNumNew;
		this.serialNumOld = serialNumOld;
		this.value = value;
		this.valueelse=valueelse;
		this.frequency=frequency;
	}
	
	public int getSerialNumOld() {
		return serialNumOld;
	}
	public void setSerialNumOld(int serialNumOld) {
		this.serialNumOld = serialNumOld;
	}
	public int getSerialNumNew() {
		return serialNumNew;
	}
	public void setSerialNumNew(int serialNumNew) {
		this.serialNumNew = serialNumNew;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getValueelse() {
		return valueelse;
	}

	public void setValueelse(double valueelse) {
		this.valueelse = valueelse;
	}

	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	
	
	/**
	 * @param ef   ef已经创建，里面需已由构造函数将serialNumOld和value赋值，才能继续计算
	 * 进行简单的经验频率计算，并将结果存入ef中，并且将其中各EmpiricalFrequency对象按serialNumNew排好顺序
	 */
	public static List<EmpiricalFrequency> frequencyCal(List<EmpiricalFrequency> ef)
	{
		int num=ef.size();
		double[] P=new double[num];
		int[] oldserial=new int[num];
		int[] newserial=new int[num];
		double[] valueelse=new double[num];
		double[] values=new double[num];
		double[] arrToSort=new double[num];
		for(int i=0;i<num;i++)
		{
			values[i]=ef.get(i).getValue();
		}
		Arrays.sort(values);
		arrToSort=reverseArray(values);
		for(int i=0;i<num;i++)
		{
			P[i]=(double)(i+1)/(double)(num+1);
			newserial[i]=i;
		}
		
		double[] efvalue=new double[num];
		double[] efvalueelse=new double[num];
		for(int i=0;i<num;i++)
		{
			efvalue[i]=ef.get(i).getValue();
			efvalueelse[i]=ef.get(i).getValueelse();
		}
		for(int i=0;i<num;i++)
		{
			for(int j=0;j<num;j++)
			{
				if(arrToSort[i]==efvalue[j])
				{
					oldserial[i]=j;
					valueelse[i]=efvalueelse[j];
					break;
				}
			}
		}
		
		List<EmpiricalFrequency> efs=new ArrayList<EmpiricalFrequency>();
		for(int i=0;i<num;i++)
		{
			efs.add(new EmpiricalFrequency(i, oldserial[i], arrToSort[i], valueelse[i],P[i]));
		}//重新构造ef
		
		return efs;
	}
	
	/**
	 * @param a  
	 * @param index  
	 * @return [1 2 3]变为[3 2 1]
	 */
	private static double[] reverseArray(double[] a)
	{
		double[] b=new double[a.length];
		for(int i=0;i<a.length;i++)
		{
			b[i] = a[a.length - 1 - i];
		}
		return b;
	}
	
	
	/**
	 * @param efs  排好序的经验频率序列
	 * @param value 要查询的值
	 * @return  查询对应的频率
	 */
	public static double searchFreqByValue(List<EmpiricalFrequency> efs,double value)
	{
		double corrfreq=0;
		//把efs里的value和frequency作为两个数组拿出来，用halfsearch函数去寻找
		double[] arr1Temp=new double[efs.size()];
		double[] arr2Temp=new double[efs.size()];
		for(int i=0;i<arr1Temp.length;i++)
		{
			arr1Temp[i]=efs.get(i).getValue();
			arr2Temp[i]=efs.get(i).getFrequency();
		}
		double[] arr1=reverseArray(arr1Temp);
		double[] arr2=reverseArray(arr2Temp);
		corrfreq=halfSearch(value, arr1, arr2);
		return corrfreq;
		
	}
	/**
	 * @param efs  排好序的经验频率序列
	 * @param freq 要查询的频率
	 * @return  最接近该频率的一个数对应的newIndex
	 */
	public static int searchRoundIndexByFreq(List<EmpiricalFrequency> efs,double freq)
	{
		int corrValue=0;
		//把efs里的value和frequency作为两个数组拿出来，用halfsearch函数去寻找
		double[] arr1=new double[efs.size()];
		int[] arr2=new int[efs.size()];
		double[] difference=new double[efs.size()];
		for(int i=0;i<arr1.length;i++)
		{
			arr1[i]=efs.get(i).getFrequency();
			arr2[i]=efs.get(i).getSerialNumNew();
			difference[i]=Math.abs(arr1[i]-freq);
		}
		
		int minIndex=0;
		double temp=difference[0];
		for(int i=0;i<difference.length;i++){
			if(difference[i]<temp){
				temp=difference[i];
				minIndex=arr2[i];
			}			
		}
		corrValue=minIndex;
		return corrValue;
		
	}
	/**
	 * @param efs  排好序的经验频率序列
	 * @param freq 要查询的频率
	 * @return  对应频率的value
	 */
	public static double searchValueByFreq(List<EmpiricalFrequency> efs,double freq)
	{
		double corrValue=0;
		//把efs里的value和frequency作为两个数组拿出来，用halfsearch函数去寻找
		double[] arr1=new double[efs.size()];
		double[] arr2=new double[efs.size()];
		for(int i=0;i<arr1.length;i++)
		{
			arr1[i]=efs.get(i).getFrequency();
			arr2[i]=efs.get(i).getValue();
		}
		corrValue=halfSearch(freq, arr1, arr2);
		return corrValue;
		
	}
	
	/**
	 * 折半查找法，但是不插值，而是选取离目标更近的一个节点
	 * @param x 
	 * @param xa 由小到大排列
	 * @param xy
	 * @return -10000表示查找越界
	 */
	public static double halfSearch(double x, double[] xa, double[] xy){
		
		double result = -1;
		int min =0;
		int max = xa.length;
		int temp = max/2;
		if(max == 0)
			return -1;
		
		double order = 1;
		if(xa[0] > xa[max-1])
			order = -1;
		
		//判断是否越界,如果越界,则修正到边界
		if(order*x>order*xa[max-1] )
		{
//			logger.info("查询"+x+"低于最小值");
			return xy[max-1];
		}
		else if(order*x <order*xa[0])
		{
//			logger.info("查询"+x+"高于最大值");
			return xy[0];
		}

		
		//判断是否值在节点上
		for(int i=0;i<xa.length;i++){
			if(x == xa[i])return xy[i];
		}
		
		do
		{
			if(order*x>order*xa[temp])
				min = temp;
			else
				max = temp;
			temp = (max+min)/2;
		}while(min != temp);
		
		if(xa[min] == xa[max])//如果xa[min] == xa[max]，说明对应数据在节点上，否则就在两点之间
			return xy[min];
		else{
			double abs1=Math.abs(xa[min]-x);
			double abs2=Math.abs(xa[max]-x);
			if(abs1<abs2)
				result=xy[min];
			else
				result= xy[max];//xy[min] +(xy[max]-xy[min])*(x-xa[min])/(xa[max]-xa[min]);
		}		//数据在两点之间，不采用插值的方式，而是判断离两个数据哪个更近选哪个	
			
		
		return result;
	}
	
	
	
}
