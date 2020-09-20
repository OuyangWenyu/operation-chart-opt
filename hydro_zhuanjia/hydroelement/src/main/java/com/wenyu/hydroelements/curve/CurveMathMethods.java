package com.wenyu.hydroelements.curve;

public class CurveMathMethods 
{
	/*用于曲线查询的各个数学方法*/

	/**
	 * 折半查找法
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
		
		if(xa[min] == xa[max])
			return xy[min];
		else			
			result= xy[min] +(xy[max]-xy[min])*(x-xa[min])/(xa[max]-xa[min]);
		
		return result;
	}
	
	public static double halfSearch(double x ,BaseStatistics xpack, BaseStatistics ypack){
		return halfSearch(x, xpack.getArray(), ypack.getArray());
	}
	
	public static int halfLocation(double x, double[] xpack){
		int start = 0;
		int end = xpack.length;
		if(x < xpack[0])return -1;
		if(x > xpack[end - 1])return -2;
		do{
			int current = (start + end) / 2;
			if(current == start)return current;
			if(xpack[current] > x)end = current;
			else if(xpack[current] < x)start = current;
			else return current;
		}while(true);
	}
	
	public static int halfLocation(double x, BaseStatistics xpack){
		return halfLocation(x, xpack.getArray());
	}
	
	public static double normalized(double value, double lower, double upper){
		return (value - lower) / (upper - lower);
	}
	
	public static double unnormalized(double times, double lower, double upper){
		return (lower * (1-times)) + (upper * times);
	}
	
	
}
