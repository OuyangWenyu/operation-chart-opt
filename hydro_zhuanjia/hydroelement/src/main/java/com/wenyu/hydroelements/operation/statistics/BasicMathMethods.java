package com.wenyu.hydroelements.operation.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicMathMethods 
{
	/*纯数学方法*/

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
	
	public static double[] mixTwoArray(double[] x, double[] y, double a){
		double[] output = new double[x.length];
		for(int i=0;i<x.length;i++){
			output[i] = (x[i] * (1-a)) + (y[i] * a);
		}
		return output;
	}
	
	
	
	/**
	 * @param x  给定一个一维数组
	 * @return   求其所有元素加和
	 */
	public static double sumArray(double[] x)
	{
		double sum=0;
		for(int i=0;i<x.length;i++)
		{
			sum=sum+x[i];
		}
		return sum;
	}
	/**
	 * @param x  给定一个一维数组
	 * @return   求其所有元素加和
	 */
	public static long sumArray(long[] x)
	{
		long sum=0;
		for(int i=0;i<x.length;i++)
		{
			sum=sum+x[i];
		}
		return sum;
	}
	/**
	 * @param x  给定一个一维数组
	 * @return   求其所有元素均值
	 */
	public static double avgArray(double[] x)
	{
		double avg=0;
		avg=sumArray(x)/x.length;
		return avg;
	}
	
	
	/**
	 * @param a  二维数组
	 * @param b  同a的数组长度一样的二维数组
	 * @return  定义数组和一个数相乘，即数组中每个数都乘以number
	 */
	public static double[][] arrayPlusArray2D(double[][] a,double[][] b)
	{
		double[][] c=new double[a.length][a[0].length];
		for(int i=0;i<a.length;i++)
		{
			for(int j=0;j<a[0].length;j++)
			{
				c[i][j]=a[i][j]+b[i][j];
			}
			
		}
		return c;
	}
	
	
	/**
	 * @return  定义数组和一个数相乘，即数组中每个数都乘以number
	 */
	public static double[] arrayMultiplyNumber(double[] a,double number)
	{
		for(int i=0;i<a.length;i++)
		{
			a[i]=a[i]*number;
		}
		return a;
	}
	
	/**
	 * @return  定义数组和一个数组相乘，即数组中每个数都乘以对应序号的另一个数组中的数
	 */
	public static double[] arrayMultiplyArray(double[] a,double[] b)
	{
		double[] c=new double[a.length];
		for(int i=0;i<a.length;i++)
		{
			c[i]=a[i]*b[i];
		}
		return c;
	}
	/**
	 * @return  定义数组和一个数组相乘，即数组中每个数都乘以对应序号的另一个数组中的数
	 */
	public static double[] arrayMultiplyArray(double[] a,long[] b)
	{
		double[] c=new double[a.length];
		for(int i=0;i<a.length;i++)
		{
			c[i]=(double) (a[i]*b[i]);
		}
		return c;
	}
	
	
	/**
	 * @param a   给定一维数组
	 * @return    求其最小、最大值  存入Interval实例里
	 *//*
	public static <T extends Comparable> Interval<T> minmax(T[] a)
	{
		if(a==null || a.length==0) return null;
		T min=a[0];
		T max=a[0];
		for(int i=1;i<a.length;i++)
		{
			if(min.compareTo(a[i])>0) min=a[i];
			if(max.compareTo(a[i])<0) max=a[i];
		}
		return new Interval<>(min,max);
	}*/
	
	
	/**
	 * @param a 存放结果的数组
	 * @param max   
	 * @param min
	 * @param num  要在max和min之间内插值的个数
	 * @return  double[num+2]  按比例线性插值的结果  如1  4之间插2值   结果是【2,3】  组成数组[1 2 3 4]
	 */
	public static double[] linearInterpolation(double[] a, double max,double min,int num)
	{
		a[0]=min;
		a[a.length-1]=max;
		for(int i=1;i<num+1;i++){
			a[i]=i*(max-min)/(num+1)+min;
		}
		return a;
	}
	
	
	/**
	 * @param max   
	 * @param min
	 * @param num  要在max和min之间内插值的个数
	 * @return  double[num+2]  按比例线性插值的结果  如1  4之间插2值   结果是【2,3】  组成数组[1 2 3 4]
	 */
	public static double[] linearInterpolation(double max,double min,int num)
	{
		double[] a=new double[num+2];
		a[0]=min;
		a[a.length-1]=max;
		for(int i=1;i<num+1;i++){
			a[i]=i*(max-min)/(num+1)+min;
		}
		return a;
	}
	
	
	/**
	 * @param a 存放结果的数组
	 * @param max   
	 * @param min
	 * @param num  要在max和min之间内插值的个数
	 * @return  double[num]  逆序按比例线性插值的结果  如4  1之间插2值   结果是【3,2】  组成数组[4 3 2 1]
	 */
	public static double[] linearInterpolationInverse(double max,double min,int num)
	{
		double[] a=new double[num+2];
		a[0]=max;
		a[a.length-1]=min;
		for(int i=1;i<num+1;i++){
			a[i]=i*(min-max)/(num+1)+max;
		}
		return a;
	}
	
	/**
	 * @param max
	 * @param min
	 * @param num 要在max和min之间内插值的个数
	 * @return 比如 4  1之间查5个数   结果是4 3 2 1 2 3 4
	 */
	public static double[] linearInterpolationBothSide(double max,double min,int num)
	{
		double[] a=new double[num+2];
		a[0]=max;
		a[num+1]=max;
		if((num+2)%2==0)
		{
			a[num/2]=min;
			a[num/2+1]=min;
			for(int i=1;i<num/2;i++)
				{
					a[i]=max-(max-min)/(num/2)*i;
					a[num+1-i]=max-(max-min)/(num/2)*i;
				}
		}
		else if((num+2)%2!=0)
		{
			a[(num+1)/2]=min;
			for(int i=1;i<(num+1)/2;i++)
				{
					a[i]=max-(max-min)/((num+1)/2)*i;
					a[num+1-i]=max-(max-min)/((num+1)/2)*i;
				}
			
		}
		return a;
		
	}
	
	/**
	 * @param p    要进行转置的二维数组
	 * @return     转置好的二维数组
	 */
	public static int[][] transpose(int[][] p)
	{
		int[][] pt=new int[p[0].length][p.length];
		for(int i=0;i<p[0].length;i++)
		{
			for(int j=0;j<p.length;j++)
			{
				pt[i][j]=p[j][i];
			}
		}
		return pt;
	}
	
	/**
	 * @param p    要进行转置的二维数组
	 * @return     转置后的二维数组
	 */
	public static double[][] transpose(double[][] p)//重载函数
	{
		double[][] pt=new double[p[0].length][p.length];
		for(int i=0;i<p[0].length;i++)
		{
			for(int j=0;j<p.length;j++)
			{
				pt[i][j]=p[j][i];
			}
		}
		return pt;
	}
	
	
	
	/**对于一个数组a【】  x在其中出现几次  计数几次*/
	public static int numOfSameElements(int[] a,int x)
	{
		int count=0;
		for(int i=0;i<a.length;i++)
		{
			if(a[i]==x) count+=1;
		} 
		return count;
	}
	
	/**对于一个数组a【】  x在其中出现的位置*/
	public static int[] indexOfSameElements(int[] a,int x)
	{
		int[] indexs=new int[numOfSameElements(a,x)];
		int count=0;
		for(int i=0;i<a.length;i++)
		{
			if(a[i]==x)
			{ 
				indexs[count]=i;
				count=count+1;
			}
		} 
		return indexs;
	}
	
	/**
	 * @param a  给定数组是一个平稳递增的数列 ，如【 3 4 5 6】 所有元素+1递增  当出现形如如【0 1 2 3 4 5 6 11 12】  前一部分所有元素+1递增  中间有跳变时 返回第一次出现跳变的元素的索引
	 * 
	 * 【3 4 5 6】  无跳变  返回-1   
	 * 【0 1 2 3 4 5 6 11 12】有跳变  返回11的索引号 7  
	 */
	public static int indexOfJumpPointInArray(int[] a)
	{
		int index=-1;
		for(int i=1;i<a.length;i++){
			if((a[i]-a[i-1])!=1) 
			{
				index=i;
				break;
			}			
		}
		return index;
	}
	
	
	/**
	 * @param a  给定数组是一个平稳递增的数列 ，如【 3 4 5 6】 所有元素+1递增  当出现形如如【0 1 2 3 4 5 6 11 12】  前一部分所有元素+1递增  中间有跳变时 返回第一次出现跳变的元素的索引
	 * 
	 * 【3 4 5 6】  无跳变  返回-1   
	 * 【0 1 2 3 4 5 6 11 12】有一次跳变  返回1
	 * [1 1 1 1 1 -1 -1 -1 -1 -1 1 1]  有两次跳变返回2
	 */
	public static int numsOfJumpPointInArray(int[] a)
	{
		int index=-1;
		int[] interval=new int[a.length-1];
		for(int i=1;i<a.length;i++){
			interval[i-1]=a[i]-a[i-1];			
		}
		
		List<Integer> jumpIndex=new ArrayList<>();
		for(int i=1;i<interval.length;i++)
		{
			if((interval[i]-interval[i-1])!=0) 
			{
				jumpIndex.add(i);
			}
		}
		
		if(jumpIndex.size()%2==0)//这里对各连续部分进行计算才是合理的，暂时这样写应该是够用了
		{
			index=jumpIndex.size()/2;
		}
		else
		{
			index=((int)(jumpIndex.size()/2))+1;
		}
		return index;
	}
	
	
	/**
	 * @param a
	 * @return   一维数组从index=0开始，依次取一定数目成为一行，最后变成二维的   例如{1,2}变为{{1}，{2}}
	 */
	public static double[] array2DTo1D(double[][] a)
	{
		double[] x = new double[a.length * a[0].length] ;
		for(int i=0;i<x.length;i++)
		{
			x[i]=a[(int) i/a[0].length][i%a[0].length];
		}
		return x;
	}
	
	/**
	 * @param a
	 * @return   一维数组从index=0开始，依次取一定数目成为一行，最后变成二维的   例如{1,2}变为{{1}，{2}}
	 */
	public static int[] array2DTo1D(int[][] a)
	{
		int[] x = new int[a.length * a[0].length] ;
		for(int i=0;i<x.length;i++)
		{
			x[i]=a[(int) i/a[0].length][i%a[0].length];
		}
		return x;
	}
	
	/**
	 * @param a
	 * @param rowNums 一行的元素个数
	 * @return   二维数组一行加在一行后 变成一维的   例如{{1}，{2}}变为{1,2}
	 */
	public static double[][] array1DTo2D(double[] a,int rowNums)
	{
		double[][] x = new double[a.length/rowNums][rowNums] ;
		for(int i=0;i<x.length;i++)
		{
			for(int j=0;j<x[0].length;j++)
			{
				x[i][j]=a[i*rowNums+j];
			}
		}
		return x;
	}
	
	/**
	 * @param a
	 * @param rowNums 一行的元素个数
	 * @return   二维数组一行加在一行后 变成一维的   例如{{1}，{2}}变为{1,2}
	 */
	public static int[][] array1DTo2D(int[] a,int rowNums)
	{
		int[][] x = new int[a.length/rowNums][rowNums] ;
		for(int i=0;i<x.length;i++)
		{
			for(int j=0;j<x[0].length;j++)
			{
				x[i][j]=a[i*rowNums+j];
			}
		}
		return x;
	}
	
	
	/**
	 * 与ArrayList的addAll函数的功能类似
	 * 
	 * @param a
	 * @param b
	 * @return   将b中的元素全部接到a的最后  
	 */
	public static void addAllBetweenArrays(double[] a,double[] b)
	{
		
	}
	
	/**
	 * @param a  
	 * @param index  
	 * @return [1 2 3]变为[3 2 1]
	 */
	public static double[] reverseArray(double[] a)
	{
		double[] b=new double[a.length];
		for(int i=0;i<a.length;i++)
		{
			b[i] = a[a.length - 1 - i];
		}
		return b;
	}
	
	/**
	 * @param a  
	 * @param index  
	 * @return [1 2 3]变为[3 2 1]
	 */
	public static long[] reverseArray(long[] a)
	{
		long[] b=new long[a.length];
		for(int i=0;i<a.length;i++)
		{
			b[i] = a[a.length - 1 - i];
		}
		return b;
	}
	
	/**
	 * @param a  
	 * @param index  
	 * @return [1 2 3]变为[3 2 1]
	 */
	public static int[] reverseArray(int[] a)
	{
		int[] b=new int[a.length];
		for(int i=0;i<a.length;i++)
		{
			b[i] = a[a.length - 1 - i];
		}
		return b;
	}
	
	/**
	 * @param a  
	 * @param index  
	 * 
	 * 例a=[1 2 3 4 5 6 7 8 9 10]   index=2  将函数顺序 从第三个数那里开始 变为[3 4 5 6 7 8 9 10 1 2]
	 * 此函数使用Arrays的copyOf函数和上面的addAllBetweenArrays很容易完成
	 */
	public static double[] reverseArray(double[] a,int index)
	{
		double[] b=new double[a.length];
		int indexend=a.length-index;
		for(int i=0;i<a.length;i++)
		{
			b[(i+indexend)%(a.length)]=a[i];
		}
		return b;
	}
	
	/**
	 * @param a  
	 * @param index  
	 * 
	 * 例a=[1 2 3 4 5 6 7 8 9 10]   index=2  将函数顺序 从第三个数那里开始 变为[3 4 5 6 7 8 9 10 1 2]
	 * 此函数使用Arrays的copyOf函数和上面的addAllBetweenArrays很容易完成
	 */
	public static int[] reverseArray(int[] a,int index)
	{
		int[] b=new int[a.length];
		int indexend=a.length-index;
		for(int i=0;i<a.length;i++)
		{
			b[(i+indexend)%(a.length)]=a[i];
		}
		return b;
	}
	
	/**
	 * @param a  
	 * @param index  
	 * 
	 * 例a=[1 2 3 4 5 6 7 8 9 10]   index=2  将函数顺序 从第三个数那里开始 变为[3 4 5 6 7 8 9 10 1 2]
	 * 此函数使用Arrays的copyOf函数和上面的addAllBetweenArrays很容易完成
	 */
	public static long[] reverseArray(long[] a,int index)
	{
		long[] b=new long[a.length];
		int indexend=a.length-index;
		for(int i=0;i<a.length;i++)
		{
			b[(i+indexend)%(a.length)]=a[i];
		}
		return b;
	}
	
	
	/**
	 * @param a
	 * @param rowMalposition   行错位
	 * @param columnIndex  列交换
	 * 
	 * 例：{10 20 30 40 50}						  {40 50 10 20 30}				  
	 *    {11 21 31 41 51}   列交换=2  表示从第三列开始变 变为     {41 51 11 21 31}   行错位=1 数组会变为{40 50 11 21 31} 
	 *    
	 *    
	 */
	public static double[][] reverse2DArray(double[][] a,int rowMalposition,int columnIndex)
	{
		double[][] b=null;
		if(rowMalposition==0)
		{
			b = new double[a.length][];
		}
		else if(rowMalposition==1)
		{
			b = new double[a.length-1][];
			double[] a_temp=array2DTo1D(a);
			double[] x=Arrays.copyOfRange(a_temp, columnIndex, (a.length)*(a[0].length)-(a[0].length-columnIndex));
			b=array1DTo2D(x,a[0].length);
		}
		else
		{
			
		}
		return b;
	}
	
	
	public static int[][] reverse2DArray(int[][] a, int rowMalposition, int columnIndex) {
		// TODO Auto-generated method stub
		int[][] b=null;
		if(rowMalposition==0)
		{
			b = new int[a.length][];
		}
		else if(rowMalposition==1)
		{
			b = new int[a.length-1][];
			int[] a_temp=array2DTo1D(a);
			int[] x=Arrays.copyOfRange(a_temp, columnIndex, (a.length)*(a[0].length)-(a[0].length-columnIndex));
			b=array1DTo2D(x,a[0].length);
		}
		else
		{
			
		}
		return b;
	}
	
	public static double[] listToDouble(List<Double> a)
	{
		double[] b=new double[a.size()];
		Object[] arr1 = a.toArray();
        for (int i = 0; i < arr1.length; i++) {
            b[i] = (double) arr1[i];
        }
        return b;
	}
	public static long[] listToLong(List<Long> a)
	{
		long[] b=new long[a.size()];
		Object[] arr1 = a.toArray();
        for (int i = 0; i < arr1.length; i++) {
            b[i] = (long) arr1[i];
        }
        return b;
	}
	
	
	/**
	 * @param arr   给定一数组
	 * @return      数组中的最大元素
	 */
	public static double maxOf1DArray(double[] arr)
	{
		double max=arr[0];
		for (int i = 1; i < arr.length; i++) 
		{
	       if (arr[i] > max)  max = arr[i];
	    }
		return max;
	}
	
	/**
	 * @param arr   给定一数组
	 * @return      数组中的最大元素
	 */
	public static int maxOf1DArray(int[] arr)
	{
		int max=arr[0];
		for (int i = 1; i < arr.length; i++) 
		{
	       if (arr[i] > max)  max = arr[i];
	    }
		return max;
	}
	
	/**
	 * @param arr   给定一数组
	 * @return      数组中的最大元素
	 */
	public static int indexOf1DArrayMax(double[] arr)
	{
		double max=arr[0];
		int index=0;
		for (int i = 1; i < arr.length; i++) 
		{
	       if (arr[i] > max) 
	       {
	    	   max = arr[i];
	    	   index=i;
	       }
	    }
		return index;
	}
	
	/**
	 * @param arr   给定一数组
	 * @return      数组中的最小元素
	 */
	public static int indexOf1DArrayMin(double[] arr)
	{
		double min=arr[0];
		int index=0;
		for (int i = 1; i < arr.length; i++) 
		{
	       if (arr[i] < min) 
	       {
	    	   min = arr[i];
	    	   index=i;
	       }
	    }
		return index;
	}
	
	/**
	 * @param arr   给定一数组
	 * @return      数组中的最小元素
	 */
	public static double minOf1DArray(double[] arr)
	{
		double min=arr[0];
		for (int i = 1; i < arr.length; i++) 
		{
	       if (arr[i] < min)  min = arr[i];
	    }
		return min;
	}
	
	/**
	 * @param arr   给定一数组
	 * @return      数组中的最小元素
	 */
	public static int minOf1DArray(int[] arr)
	{
		int min=arr[0];
		for (int i = 1; i < arr.length; i++) 
		{
	       if (arr[i] < min)  min = arr[i];
	    }
		return min;
	}


	/**
	 * @param arr  
	 * @return   一个数组首尾两个数哪个大返回哪个的index 
	 */
	public static int indexOf1DArrayFLMax(double[] arr) {
		// TODO Auto-generated method stub
		int index=0;
		if(arr[0]<arr[arr.length-1])
		{
			index=arr.length-1;
		}
		return index;
	}
	
	/**
	 * @param arr  
	 * @return   一个数组首尾两个数哪个小返回哪个的index 
	 */
	public static int indexOf1DArrayFLMin(double[] arr) {
		// TODO Auto-generated method stub
		int index=0;
		if(arr[0]>arr[arr.length-1])
		{
			index=arr.length-1;
		}
		return index;
	}
	
	/**
	 * @param a
	 * @param b
	 * @return  a和b合起来，b放在a之后  [1 2]  [3 4]  合成 [1 2 3 4]
	 */
	public static double[] mergeArray(double[] a,double[] b)
	{
		double[] c=new double[a.length+b.length];
		for(int i=0;i<a.length;i++)
		{
			c[i]=a[i];
		}
		for(int j=0;j<b.length;j++)
		{
			c[a.length+j]=b[j];
		}
		return c;
	}
	
	/**
	 * @param a
	 * @param b
	 * @return  a和b合起来，b放在a之后  [1 2]  [3 4]  合成 [1 2 3 4]
	 */
	public static int[] mergeArray(int[] a,int[] b)
	{
		int[] c=new int[a.length+b.length];
		for(int i=0;i<a.length;i++)
		{
			c[i]=a[i];
		}
		for(int j=0;j<b.length;j++)
		{
			c[a.length+j]=b[j];
		}
		return c;
	}
	
	
	/**
	 * @param arr
	 * @param key
	 * @return   类似arraylist的contains的函数，判断key是否存在于arr中，若是 返回true，否则，返回false
	 */
	public static boolean contains(int[] arr,int key)
	{
		boolean exist=false;
		for(int i=0;i<arr.length;i++)
		{
			if(arr[i]==key)
			{
				exist=true;
				break;
			}
		}
		return exist;
		
	}
	
	/**
	 * @param arr
	 * @param key
	 * @return   类似arraylist的contains的函数，判断key是否存在于arr中，若是 返回key第一次出现时在arr中的index，
	 * 否则，返回-1
	 */
	public static int containsIndex(int[] arr,int key)
	{
		int exist=-1;
		for(int i=0;i<arr.length;i++)
		{
			if(arr[i]==key)
			{
				exist=i;
				break;
			}
		}
		return exist;
		
	}
	
	/**
	 * @param arraytemp
	 * @param percent
	 * @return   一个数组，先降序排序，然后计算经验频率对应的值
	 */
	public static double calFrequencyValue(double[] arraytemp, double percent){
		Arrays.sort(arraytemp);
		double[] array=reverseArray(arraytemp);
		double percentNumTemp = (array.length+1)*percent;
		if(percentNumTemp > array.length){
			percentNumTemp = array.length;
		}
		if(percentNumTemp < 1){
			percentNumTemp = 1;
		}
		BigDecimal percentNum = new BigDecimal(percentNumTemp).setScale(0, BigDecimal.ROUND_HALF_UP);//四舍五入
//		System.out.println("***"+percentNum);
		int percentId = percentNum.intValue();
		double arrdata = array[percentId-1];
		return arrdata;
	}
	
	/**
	 * @param temp
	 * @return  Object[][] 转 double[][]
	 */
	public static double[][] objectToDouble(Object[][] temp)
	{
		double[][] data = new double[temp.length][temp[0].length];
	    for (int i=0;i<temp.length;i++) {
	    	for (int j=0;j<temp[0].length;j++) {
	    		data[i][j] = (double) temp[i][j];
	    	}
	    }
	    return data;
	}
	
	
	/**
	 * 给定两个区间，求他们的交集
	 * @param range1  两个数 range1[0]是较小的值，range1[1]是大值
	 * @param range2 同上
	 * @return
	 * @throws Exception 
	 */
	public static double[] rangeIntersection(double[] range1,double[] range2) throws Exception
	{
		double[] range=new double[2];
		if(range1[1]<range2[0] || range2[1]<range1[0] || range1[0]>range1[1]||range2[0]>range2[1])
		{
			throw new Exception("WTF 没交集");
		}
		else if(range1[1]==range2[0])
		{
			range[0]=range2[0];
			range[1]=range2[0];
		}
		else if(range2[1]==range1[0])
		{
			range[0]=range1[0];
			range[1]=range1[0];
		}
		else if((range1[1]<=range2[1])  && (range1[0]<=range2[0]))
		{
			range[0]=range2[0];
			range[1]=range1[1];
		}
		else if((range2[1]<=range1[1])  && (range2[0]<=range1[0]))
		{
			range[0]=range1[0];
			range[1]=range2[1];
		}
		else if((range2[1]>=range1[1])  && (range2[0]<=range1[0]))
		{
			range[0]=range1[0];
			range[1]=range1[1];
		}
		else if((range2[1]<=range1[1])  && (range2[0]>=range1[0]))
		{
			range[0]=range2[0];
			range[1]=range2[1];
		}
		
		return range;
	}
	
	
	
	
	/*水库方法，单拿出来写，主要是为了统一单位，其实运行速度肯定没有内联的函数速度快*/
	/**
	 *	库容水位曲线、下泄流量水位曲线是一定的，时段类型给定，时段初水位、时段末水位、时段平均入流、出流四者知3得1，水量平衡公式
	 */
	/**
	 * @param a 
	 * @param b
	 * @param c  四个量中的三个
	 * @param vs 库容水位曲线
	 * @param ds 流量水位曲线
	 * @param dt 时段类型  对应枚举类的一个常量
	 * @param choice a、b、c三个数按顺序：1是时段初库容、时段末库容、时段平均入流  求出流   
	 * 2是时段初库容、时段平均入流、出流  求时段末库容 
	 * 3是时段平均入流、时段平均出流、时段末库容，求时段初库容 
	 *  4是时段平均出流、时段末库容、入流，求时段初库容
	 * @return  计算中所有的数据都不能为小于0的数。
	 * @throws Exception 
	 */
	public static double waterBalanceCalculate(double a,double b,double c,double dt,int choice) throws Exception
	{
		double d=0;
		double v0,vt,inflow,outflow;
		switch(choice)
		{
		case 1:
			v0=a;
			vt=b;
			inflow=c;
			d=inflow-(vt-v0)*1e8/dt;
			break;
		case 2:
			v0=a;
			inflow=b;
			outflow=c;
			d=(a*1e8+(b-outflow)*dt)/1e8;
			break;
		case 3:
			vt=c;
			inflow=a;
			outflow=b;
			d=vt-(inflow-outflow)*dt/1e8;
			break;
		case 4:
			break;
		}
		/*if(d<0) 
		{
			System.out.println("抛出一个异常:");
			throw new Exception("--------!水量平衡不能满足!--------");
		}*/
		return d;
	}
}
