package me.owenyy.drawmethods.helper;

import com.wenyu.hydroelements.hydrostation.HStationSpec;
import com.wenyu.hydroelements.hydrostation.characcurves.StationCurve;
import com.wenyu.hydroelements.operation.basic.MultiFomulaMode;

/**
 * 固定水头损失+无顶托下游水位流量+水头预想出力控制最大出力
 * @author  OwenYY
 *
 */
public class MultiFomulaForDispatch1 implements MultiFomulaMode {
	private double fixedHeadLoss;
	private StationCurve curve;
	private HStationSpec hsSpec;

	public double calHeadPure(double headGross) {
		double headPure = headGross - fixedHeadLoss;
		return headPure;
	}

	public double tailLevelByOutflow(double outflow) {
		double level = curve.getLeveldownByOutflow(outflow);
		return level;
	}

	public double maxPowerState(double Hnet) {
		double value=curve.getExpectPowerByHead(Hnet);
		return value;
	}

	/**
	 * @return the fixedHeadLoss
	 */
	public double getFixedHeadLoss() {
		return fixedHeadLoss;
	}

	/**
	 * @param fixedHeadLoss the fixedHeadLoss to set
	 */
	public void setFixedHeadLoss(double fixedHeadLoss) {
		this.fixedHeadLoss = fixedHeadLoss;
	}

	/**
	 * @return the curve
	 */
	public StationCurve getCurve() {
		return curve;
	}

	/**
	 * @param curve the curve to set
	 */
	public void setCurve(StationCurve curve) {
		this.curve = curve;
	}

	/**
	 * @return the hsSpec
	 */
	public HStationSpec getHsSpec() {
		return hsSpec;
	}

	/**
	 * @param hsSpec the hsSpec to set
	 */
	public void setHsSpec(HStationSpec hsSpec) {
		this.hsSpec = hsSpec;
	}

}
