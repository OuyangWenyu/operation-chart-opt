package com.wenyu.hydroelements.hydrostation.constraint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 姘寸數绔欑浉鍏崇害鏉熺被
 *
 */
public class Constraint implements Serializable{
	private static final long serialVersionUID = -5484575761435544789L;

	private List<ConstraintItem> constraintItems;//鍚屼竴鏃舵锛岀害鏉熺被鍨嬩笉鍚岀殑鍑犱釜绾︽潫
	
	public Constraint() {
		constraintItems = new ArrayList<ConstraintItem>();
	}
	
	/**
	 * 缁戝畾绾︽潫鑷充竴缁存暟缁�
	 */
	public void bindConstraint(int constraintType,double[] range){
		//姘翠綅绾︽潫鐨勬湯姘翠綅
		double[] temp=new double[2];
		temp= getMaxMin(constraintType);
		range[0] =temp[0];
		range[1] = temp[1];
	}

	/**
	 * @param type
	 * @return  浠巆onstrain涓妸鍏蜂綋鐨勭害鏉熷ぇ灏忓�煎彇鍑烘潵锛�0瀵瑰簲max 1瀵瑰簲min
	 */
	public double[] getMaxMin(int type){

		double max = Double.MAX_VALUE;
		double min = -Double.MAX_VALUE;

		for(ConstraintItem cons:constraintItems){

			if(!(cons.getType() == type)) {
				continue;
			}
			
			if(max > cons.getValueMax())
				max = cons.getValueMax();

			if(min < cons.getValueMin())
				min = cons.getValueMin();
		}

		return new double[]{max,min};
	}



	/**
	 * get绾︽潫鐨勬椂鍊欙紝棣栧厛锛岀湅鏄笉鏄墍鏈夌殑绾︽潫閮藉湪List閲屼簡锛屽鏋滄病鏈夛紝杩欐椂鍊欏厛鎶婃病鐨勭害鏉熻ˉ涓婏紝浠oublede MAX鍜孧IN琛ワ紝
	 * 鐒跺悗鑷姩瀵圭害鏉熸寜鐓х害鏉熺被鍨嬭繘琛屽崌搴忔帓搴�.
	 * 杩欐牱鍐欎唬鐮佽矊浼煎奖鍝嶅悗闈㈢殑浣跨敤鏁堢巼銆傘�傘�傚厛鏀剧潃锛屽悗闈㈠啀鐪嬫斁鍝悎閫�
	 * @return 鎺掑ソ搴忕殑鎵�鏈夌害鏉熺被鍨嬮兘鏈夌殑锛岃繖鏍峰悗闈娇鐢ㄧ殑鏃跺�欐柟渚�
	 */
	public List<ConstraintItem> getConstraintItems() {
		
		Collections.sort(constraintItems,new Comparator<ConstraintItem>(){ 
			public int compare(ConstraintItem arg0, ConstraintItem arg1) {
				return (new Integer(arg0.getType())).compareTo(
						(new Integer(arg1.getType()))); 
			} 
        }); 
		return constraintItems;
	}

	public void setConstraintItems(List<ConstraintItem> constraintItems) {
		this.constraintItems = constraintItems;
	}
}
