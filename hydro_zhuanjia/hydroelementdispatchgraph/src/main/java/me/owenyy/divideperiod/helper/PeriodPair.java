package me.owenyy.divideperiod.helper;

import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.hydrostation.dispatchgraph.CommonConsts;
import com.wenyu.hydroelements.timebucket.TimeBucketType;


/**
 * highDigit、lowerDigit都是从0开始计数
 * 高位——低位 时间对  例如：年——月   年——旬  月——日 旬——日等
 *
 */
public class PeriodPair implements Comparable<Object> {  
    private int highDigit;  
    private int lowDigit; 
    
    private TimeBucketType ptHighDigit;//高位数的单位
    private TimeBucketType ptLowDigit;//低位数的单位
    
    /**
     * highDigit对应的总lowerDigit个数  
     * 如年月对numLowerDigit=12 年旬对36  月日对28、29、30或31  旬日对 8、9、10或11
     */
    private int numLowerDigit;
    
    public PeriodPair() {  
        
    }
    
    public PeriodPair(int highDigit, int lowerDigit, TimeBucketType ptHighDigit, TimeBucketType ptLowDigit) {  
        this.highDigit = highDigit;  
        this.lowDigit = lowerDigit;  
        this.ptHighDigit=ptHighDigit;
        this.ptLowDigit=ptLowDigit;
        if(ptHighDigit.equals(TimeBucketType.YEAR))
        {
        	if(ptLowDigit.equals(TimeBucketType.MONTH))
        	{
        		numLowerDigit=CommonConsts.MONTHS_PER_YEAR;
        	}
        	else if(ptLowDigit.equals(TimeBucketType.DECAD))
        	{
        		numLowerDigit=CommonConsts.DECADS_PER_YEAR;
        	}		
        }
        
    }
    
    public int gethighDigit() {
		return highDigit;
	}

	public void sethighDigit(int highDigit) {
		this.highDigit = highDigit;
	}

	public int getlowerDigit() {
		return lowDigit;
	}

	public void setlowerDigit(int lowerDigit) {
		this.lowDigit = lowerDigit;
	}

	public TimeBucketType getPtHighDigit() {
		return ptHighDigit;
	}

	public void setPtHighDigit(TimeBucketType ptHighDigit) {
		this.ptHighDigit = ptHighDigit;
	}

	public TimeBucketType getPtLowDigit() {
		return ptLowDigit;
	}

	public void setPtLowDigit(TimeBucketType ptLowDigit) {
		this.ptLowDigit = ptLowDigit;
	}

	public int getNumLowerDigit() {
		return numLowerDigit;
	}

	public void setNumLowerDigit(int numLowerDigit) {
		this.numLowerDigit = numLowerDigit;
	}

	/**
     * 年月（旬）在o前 结果小于0
     */
    public int compareTo(Object o) {  
        if (o instanceof PeriodPair) {  
            // int cmp = Double.compare(number, ((Pair) o).number);  
            int cmp = highDigit - ((PeriodPair) o).highDigit;  
            if (cmp!=0) {// highDigit是第一要比较的，即先比较年。如果相同再比较月或者旬  
                return cmp;  
            }  
            return lowDigit-(((PeriodPair) o).lowDigit);  
        }  
        throw new ClassCastException("Cannot compare Pair with "  
                + o.getClass().getName());  
    }
    
    /**
     * 定义PeriodPair的加法
     */
    public PeriodPair plus(Object o) {  
        if (o instanceof PeriodPair) {  
            // int cmp = Double.compare(number, ((Pair) o).number);  
            int val1 = ((PeriodPair) o).gethighDigit();  
            int val2 = ((PeriodPair) o).getlowerDigit();
            int temp1= highDigit + val1;
            int temp2= (lowDigit + val2) % numLowerDigit==0?numLowerDigit:(lowDigit + val2) % numLowerDigit;
            if(lowDigit + val2 > numLowerDigit)
            {
            	temp1=temp1+1;
            }
            return new PeriodPair(temp1,temp2,ptHighDigit,ptLowDigit);  
        }  
        else if(o instanceof Integer)
        {
        	int temp1= highDigit;
            int temp2= (lowDigit + (Integer)o) % numLowerDigit==0?numLowerDigit:(lowDigit + (Integer)o) % numLowerDigit;
            if(lowDigit + (Integer)o > numLowerDigit)
            {
            	temp1=temp1+1;
            }
            return new PeriodPair(temp1,temp2,ptHighDigit,ptLowDigit);
        }
        throw new ClassCastException("Cannot plus with "  
                + o.getClass().getName());  
    }
    
    /**
     * 定义PeriodPair的加法
     */
    public int plusInteger(Object o) {  
    	int sum=0;
        if (o instanceof PeriodPair) {  
            // int cmp = Double.compare(number, ((Pair) o).number);  
            int val1 = ((PeriodPair) o).gethighDigit();  
            int val2 = ((PeriodPair) o).getlowerDigit();
            int temp1= highDigit + val1;
            int temp2= (lowDigit + val2) % numLowerDigit==0?numLowerDigit:(lowDigit + val2) % numLowerDigit;
            if(lowDigit + val2 > numLowerDigit)
            {
            	temp1=temp1+1;
            }
            sum=temp1*numLowerDigit+temp2;
             
        }  
        else if(o instanceof Integer)
        {
        	int temp1= highDigit;
            int temp2= (lowDigit + (Integer)o) % numLowerDigit==0?numLowerDigit:(lowDigit + (Integer)o) % numLowerDigit;
            if(lowDigit + (Integer)o > numLowerDigit)
            {
            	temp1=temp1+1;
            }
            sum=temp1*numLowerDigit+temp2;
        }
        return sum; 
    }
    
    
    /**
     * 定义PeriodPair的减法
     */
    public PeriodPair minus(Object o) {  
        if (o instanceof PeriodPair) {  
            // int cmp = Double.compare(number, ((Pair) o).number);  
            int val1 = ((PeriodPair) o).gethighDigit();  
            int val2 = ((PeriodPair) o).getlowerDigit();
            int temp1= highDigit * numLowerDigit + lowDigit;
            int temp2= val1 * numLowerDigit + val2;
            int ytemp=0;
            int ptemp=0;
            if(temp1-temp2 < 0)
            {
            	System.out.println("被减数在减数之前！！");
            }
            else
            {
            	ptemp=(temp1-temp2)%numLowerDigit;
            	ytemp=(int)((temp1-temp2)/numLowerDigit);
            }
            return new PeriodPair(ytemp,ptemp,ptHighDigit,ptLowDigit);  
        }  
        else if(o instanceof Integer)//整数表示减去几个月
        {
        	int temp1= highDigit * numLowerDigit + lowDigit;
            int temp2= (Integer) o;
            int ytemp=0;
            int ptemp=0;
            if(temp1-temp2 < 0)
            {
            	System.out.println("减到公元前了！！");
            }
            else
            {
            	ptemp=(temp1-temp2)%numLowerDigit;
            	ytemp=(int)((temp1-temp2)/numLowerDigit);
            }
            return new PeriodPair(ytemp,ptemp,ptHighDigit,ptLowDigit);  
        }
        throw new ClassCastException("Cannot plus with "  
                + o.getClass().getName());  
    }
    
    /**
     * 定义PeriodPair的减法
     */
    public int minusInteger(Object o) {  
    	int difference=0;
        if (o instanceof PeriodPair) {  
            // int cmp = Double.compare(number, ((Pair) o).number);  
            int val1 = ((PeriodPair) o).gethighDigit();  
            int val2 = ((PeriodPair) o).getlowerDigit();
            int temp1= highDigit * numLowerDigit + lowDigit;
            int temp2= val1 * numLowerDigit + val2;
            difference=temp1-temp2;
            
        }  
        else if(o instanceof Integer)//整数表示减去几个月
        {
        	int temp1= highDigit * numLowerDigit + lowDigit;
            int temp2= (Integer) o;
            difference=temp1-temp2;
              
        }
        return difference;
    }
    
    
    /**
     * 简单排序 冒泡法   升序排序   
     * @param yps  
     */
    public static void sort(List<PeriodPair> yps)
    {
    	for(int i=0;i<yps.size();i++)
    	{
    		for(int j=i+1;j<yps.size();j++)
    		{
    			PeriodPair temp=new PeriodPair();
    			if(yps.get(i).compareTo(yps.get(j))>0)
    			{
    				 temp = yps.get(j);
    				 yps.set(j, yps.get(i));
    				 yps.set(i, temp);
    			}
    		}
    	}
    }
    
    /**
     * 简单排序 冒泡法   升序排序   
     * @param yps  对yps升序排序
     * @return 排序后，返回原来各数对应的index调整后的数组  如[3 2 1]的index是[0 1 2]，调整后index跟着新的顺序变成[2 1 0]
     */
    public static int[] sortindex(List<PeriodPair> yps)
    {
    	List<PeriodPair> ypsTemp=new ArrayList<PeriodPair>();
    	ypsTemp.addAll(yps);//不能直接赋值，因为那样会指向同一个对象
    	
    	for(int i=0;i<yps.size();i++)
    	{
    		for(int j=i+1;j<yps.size();j++)
    		{
    			PeriodPair temp=new PeriodPair();
    			if(yps.get(i).compareTo(yps.get(j))>0)
    			{
    				 temp = yps.get(j);
    				 yps.set(j, yps.get(i));
    				 yps.set(i, temp);
    			}
    		}
    	}
    	
    	int[] sortIndex=new int[yps.size()];
    	for(int i=0;i<yps.size();i++)
    	{
    		for(int j=0;j<yps.size();j++)
    		{
    			if(yps.get(i).compareTo(ypsTemp.get(j))==0)
    			{
    				sortIndex[i]=j;
    			}
    		}
    	}
    	
    	return sortIndex;
    }
    
    
} 
