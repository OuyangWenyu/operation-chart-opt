package com.wenyu.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wenyu.dao.ConstraintInitialDAO;
import com.wenyu.entity.ConstraintInitial;
import com.wenyu.hydroelements.hydrostation.constraint.Constraint;
import com.wenyu.hydroelements.hydrostation.constraint.ConstraintItem;
import com.wenyu.hydroelements.hydrostation.constraint.TimeSeqConstraint;
import com.wenyu.hydroelements.hydrostation.constraint.TimeSeqConstraintFactory;
import com.wenyu.hydroelements.timebucket.TimeBucketSequence;
import com.wenyu.hydroelements.timebucket.TimeBucketType;

@Component("constraintInitialManager")
public class TimeSeqConstraintManager {
	private ConstraintInitialDAO constraintInitialDAO;
	private HsSpecManager hsSpecManager;

	public ConstraintInitialDAO getConstraintInitialDAO() {
		return constraintInitialDAO;
	}

	@Resource
	public void setConstraintInitialDAO(ConstraintInitialDAO constraintInitialDAO) {
		this.constraintInitialDAO = constraintInitialDAO;
	}

	public HsSpecManager getHsSpecManager() {
		return hsSpecManager;
	}

	@Resource
	public void setHsSpecManager(HsSpecManager hsSpecManager) {
		this.hsSpecManager = hsSpecManager;
	}

	/**
	 * @param belongto
	 *            曲线数据属于哪个对象，是电站是机组还是什么
	 * @param types
	 *            约束的类型都有什么，是径流，是负荷还是什么
	 * @param tbtype
	 *            时段类型
	 * @param unitNums
	 *            单时段单元时段个数
	 * @param startDateTime
	 *            开始时间
	 * @param tbNums
	 *            时段个数
	 * @return 根据数据库的初始约束数据初始化各时段约束（这段代码的效率太低！）
	 */
	public TimeSeqConstraint createTimeSeqCurve(int belongto, int[] types, String tbType, int unitNums,
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
		List<ConstraintInitial> test = constraintInitialDAO.loadConstraintsByBelongto(belongto);
		if (test == null || test.size() < 1) {// 表格里没有初始化相应的数据，就从其它地方读取数据进行初始化构造
			TimeSeqConstraintFactory timeSeqConstraintFactory = new TimeSeqConstraintFactory(hsSpecManager.createHsSpec(belongto));
			timeSeqConstraint = timeSeqConstraintFactory.constructByHsSpec(types, tbType, 1, startDateTime, tbNums);
			return timeSeqConstraint;
		}
		//把约束提前取出来，再对每个时段进行初始化约束，这样效率会更高一些 20170327加代码
		List<Constraint> constraintsEveryMonth=new ArrayList<Constraint>();
		for(int i=1;i<13;i++){//12个月
			int month = i;
			List<ConstraintItem> cis = new ArrayList<ConstraintItem>();
			for (int j = 0; j < types.length; j++) {
				List<ConstraintInitial> cil = constraintInitialDAO.loadConstraintsByBelongtoAndTypeAndMonth(belongto,
						types[j], month);//取出一组约束数据，list中只有一个ConstraintInitial对象元素
				ConstraintItem ci = new ConstraintItem();
				double[] range = new double[2];
				range[0] = cil.get(0).getValuemax();
				range[1] = cil.get(0).getValuemin();
				ci.setType(types[j]);
				ci.setValueMax(range[0]);
				ci.setValueMin(range[1]);
				cis.add(ci);
			}
			Constraint c = new Constraint();
			c.setConstraintItems(cis);
			constraintsEveryMonth.add(c);
		}
		for (int i = 0; i < tbNums; i++) {
			int month = tbs.getTbSeq().get(i).getStartDateTime().getMonthValue();
			cs.add(constraintsEveryMonth.get(month-1));
		}
		/*for (int i = 0; i < tbNums; i++) {
			int month = tbs.getTbSeq().get(i).getStartDateTime().getMonthValue();
			List<ConstraintItem> cis = new ArrayList<>();
			for (int j = 0; j < types.length; j++) {
				List<ConstraintInitial> cil = constraintInitialDAO.loadConstraintsByBelongtoAndTypeAndMonth(belongto,
						types[j], month);
				ConstraintItem ci = new ConstraintItem();
				double[] range = new double[2];
				range[0] = cil.get(0).getValuemax();
				range[1] = cil.get(0).getValuemin();
				ci.setType(types[j]);
				ci.setValueMax(range[0]);
				ci.setValueMin(range[1]);
				cis.add(ci);
			}
			Constraint c = new Constraint();
			c.setConstraintItems(cis);
			cs.add(c);
		}*/

		timeSeqConstraint.setConstraints(cs);
		timeSeqConstraint.setDates(tbs);
		return timeSeqConstraint;

	}

}
