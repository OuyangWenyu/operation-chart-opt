package com.wenyu.hydroelements.hydrostation.constraint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.timebucket.TimeBucketSequence;
import com.wenyu.hydroelements.timebucket.TimeBucketType;

/**
 * 鏍规嵁鐜版湁鏁版嵁鏋勯�燭imeSeqConstraint瀵硅薄
 * 
 * @author OwenYY
 *
 */
public class TimeSeqConstraintFactory {
	private HStationSpec hsSpec;

	public TimeSeqConstraintFactory(HStationSpec hsSpec) {
		super();
		this.hsSpec = hsSpec;
	}

	public HStationSpec getHsSpec() {
		return hsSpec;
	}

	public void setHsSpec(HStationSpec hsSpec) {
		this.hsSpec = hsSpec;
	}

	/**
	 * 鏍规嵁HsSpec閲岀殑灞炴�ф瀯閫犵害鏉�
	 * 
	 * @param constrainttypes
	 * @return
	 */
	public TimeSeqConstraint constructByHsSpec(int[] constrainttypes, String tbType, int unitNums,
			LocalDateTime startDateTime, int tbNums) {
		TimeSeqConstraint timeSeqConstraint = new TimeSeqConstraint();
		TimeBucketType tbt = null;
		for (int i = 0; i < TimeBucketType.values().length; i++) {
			if (tbType.equals((TimeBucketType.values())[i].name())) {
				tbt = (TimeBucketType.values())[i];
				break;
			}
		}
		TimeBucketSequence tbs = new TimeBucketSequence(tbt, unitNums, startDateTime, tbNums);
		List<Constraint> cs = new ArrayList<Constraint>();
		for (int i = 0; i < tbNums; i++) {
			List<ConstraintItem> cis = new ArrayList<ConstraintItem>();
			for(int j=0;j<constrainttypes.length;j++){
				ConstraintItem ci=new ConstraintItem();
				ci.setType(constrainttypes[j]);
				cis.add(ci);
			}
			BuildConstraint.buildConstraint(cis, hsSpec);
			Constraint c=new Constraint();
			c.setConstraintItems(cis);
			cs.add(c);
		}
		timeSeqConstraint.setConstraints(cs);
		timeSeqConstraint.setDates(tbs);
		return timeSeqConstraint;

	}
}
