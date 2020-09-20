package com.wenyu.service.excelutil.fromdbway1;


/**
 * 输出到excel时用的
 *
 */
public class OutputCascade {

	private double generationYear;//年电量
	private double generationLowflow;//枯期电量
	private double generationHighflow;//丰期电量
	private double radioHighLow;//丰枯电量比
	private double warrantedOutput;//梯级保证出力
	/**
	 * @return the generationYear
	 */
	public double getGenerationYear() {
		return generationYear;
	}

	/**
	 * @param generationYear the generationYear to set
	 */
	public void setGenerationYear(double generationYear) {
		this.generationYear = generationYear;
	}

	/**
	 * @return the generationLowflow
	 */
	public double getGenerationLowflow() {
		return generationLowflow;
	}

	/**
	 * @param generationLowflow the generationLowflow to set
	 */
	public void setGenerationLowflow(double generationLowflow) {
		this.generationLowflow = generationLowflow;
	}

	/**
	 * @return the generationHighflow
	 */
	public double getGenerationHighflow() {
		return generationHighflow;
	}

	/**
	 * @param generationHighflow the generationHighflow to set
	 */
	public void setGenerationHighflow(double generationHighflow) {
		this.generationHighflow = generationHighflow;
	}

	/**
	 * @return the radioHighLow
	 */
	public double getRadioHighLow() {
		return radioHighLow;
	}

	/**
	 * @param radioHighLow the radioHighLow to set
	 */
	public void setRadioHighLow(double radioHighLow) {
		this.radioHighLow = radioHighLow;
	}

	public double getWarrantedOutput() {
		return warrantedOutput;
	}

	public void setWarrantedOutput(double warrantedOutput) {
		this.warrantedOutput = warrantedOutput;
	}

	public OutputCascade() {
		// TODO Auto-generated constructor stub
	}

	
	
}
