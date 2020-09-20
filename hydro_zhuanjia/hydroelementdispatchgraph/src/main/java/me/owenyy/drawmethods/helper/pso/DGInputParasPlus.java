package me.owenyy.drawmethods.helper.pso;

import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;

public class DGInputParasPlus {
	private DispatchInputParas input;
	private double dRepresFre;//
	private double sRepresFre;//
	private double[] dArgumentMultiples;//从小到大，加大出力线倍数（=出力线出力/保证出力） 
	private double[] dReduceMultiples;//从小到大，降低出力线倍数（=出力线出力/保证出力）
	private double[] sArgumentMultiples;//从小到大，加大出力线倍数（=出力线出力/保证出力） 
	private double[] sReduceMultiples;//从小到大，降低出力线倍数（=出力线出力/保证出力）
	public DispatchInputParas getInput() {
		return input;
	}

	public void setInput(DispatchInputParas input) {
		this.input = input;
	}

	public double getdRepresFre() {
		return dRepresFre;
	}

	public void setdRepresFre(double dRepresFre) {
		this.dRepresFre = dRepresFre;
	}

	public double getsRepresFre() {
		return sRepresFre;
	}

	public void setsRepresFre(double sRepresFre) {
		this.sRepresFre = sRepresFre;
	}

	public double[] getdArgumentMultiples() {
		return dArgumentMultiples;
	}

	public void setdArgumentMultiples(double[] dArgumentMultiples) {
		this.dArgumentMultiples = dArgumentMultiples;
	}

	public double[] getdReduceMultiples() {
		return dReduceMultiples;
	}

	public void setdReduceMultiples(double[] dReduceMultiples) {
		this.dReduceMultiples = dReduceMultiples;
	}

	public double[] getsArgumentMultiples() {
		return sArgumentMultiples;
	}

	public void setsArgumentMultiples(double[] sArgumentMultiples) {
		this.sArgumentMultiples = sArgumentMultiples;
	}

	public double[] getsReduceMultiples() {
		return sReduceMultiples;
	}

	public void setsReduceMultiples(double[] sReduceMultiples) {
		this.sReduceMultiples = sReduceMultiples;
	}
}
