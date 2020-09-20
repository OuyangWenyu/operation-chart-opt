package me.owenyy.optimal;

/**
 * 计算适应度方法接口
 * @author  OwenYY
 *
 */
public interface InitialIndividual {
	//给出函数接口
	public double calFitness(double[] pos);
	void initial(int dim, double[] pos, double[] v, double[] pbest,double[] pLowers,
			double[] pUppers,double[] vLowers,double[] vUppers);
}
