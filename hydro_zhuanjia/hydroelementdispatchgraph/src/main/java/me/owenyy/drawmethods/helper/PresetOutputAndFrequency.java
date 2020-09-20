package me.owenyy.drawmethods.helper;

import java.util.List;

import me.owenyy.divideperiod.helper.EmpiricalFrequency;

/**
 * 给定出力值，对应经验频率值的函数类
 * 
 * @author OwenYY
 *
 */
public class PresetOutputAndFrequency {
	private double[] outputs;
	private double[] frequencies;

	/**
	 * @return the outputs
	 */
	public double[] getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs
	 *            the outputs to set
	 */
	public void setOutputs(double[] outputs) {
		this.outputs = outputs;
	}

	/**
	 * @return the frequencies
	 */
	public double[] getFrequencies() {
		return frequencies;
	}

	/**
	 * @param frequencies
	 *            the frequencies to set
	 */
	public void setFrequencies(double[] frequencies) {
		this.frequencies = frequencies;
	}

	/**
	 * 线性插值，根据efs内的出力值进行线性插值，频率也同样，包括两端的值
	 * 
	 * @param num
	 *            总共选择的出力的个数（最少是2 即最大和最小）
	 * @param efs
	 *            排好频的序列
	 * @param maxOutputIndex
	 *            最大出力对应efs内的序号
	 * @param minOutputIndex
	 *            最小出力对应efs内的序号
	 */
	public void linearInterpolation(int num, List<EmpiricalFrequency> efs, int maxOutputIndex, int minOutputIndex) {
		double maxOutput = efs.get(maxOutputIndex).getValue();// 把最大的出力值找出来
		double maxFrequency = efs.get(maxOutputIndex).getFrequency();

		double minOutput = efs.get(minOutputIndex).getValue();
		;// 最小出力是下限
		double minFrequency = efs.get(minOutputIndex).getFrequency();

		double[] allNs = new double[num];
		double[] allChosenFrequencies = new double[num];
		for (int k = 0; k < num; k++)// 先把数据线性插值出来
		{
			if (k == 0) {
				allNs[k] = maxOutput;
				allChosenFrequencies[k] = maxFrequency;
			} else if (k == num - 1) {
				allNs[k] = minOutput;
				allChosenFrequencies[k] = minFrequency;
			} else {
				allNs[k] = maxOutput - (maxOutput - minOutput) / (num - 1) * k;
				allChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(efs, allNs[k]);// 频率还是从供水期里对应出力寻找
			}
		}

		outputs = allNs.clone();
		frequencies = allChosenFrequencies.clone();
	}

	/**
	 * 根据指定的出力插值，不包括最小出力一端
	 * 
	 * @param num
	 * @param efs
	 * @param maxOutput
	 * @param minOutput
	 */
	public void linearInterpolation(int num, List<EmpiricalFrequency> efs, double maxOutput, double minOutput) {
		double maxFrequency = EmpiricalFrequency.searchFreqByValue(efs, maxOutput);

		double[] Ns = new double[num];
		double[] ChosenFrequencies = new double[num];
		for (int k = 0; k < num; k++)// 先把数据线性插值出来
		{
			if (k == 0) {
				Ns[k] = maxOutput;
				ChosenFrequencies[k] = maxFrequency;
			} else {
				Ns[k] = maxOutput - (maxOutput - minOutput) / num * k;
				ChosenFrequencies[k] = EmpiricalFrequency.searchFreqByValue(efs, Ns[k]);
			}
		}

		outputs = Ns.clone();
		frequencies = ChosenFrequencies.clone();
	}
}
