package me.owenyy.drawmethods.helper.pso;

import java.util.Random;

import me.owenyy.optimal.InitialIndividual;

/**
 * 测试接口用的一个类，求解函数 f(x)=x1^2+(x2-x3)^2 的最大值
 * @author  OwenYY
 *
 */
public class Initial implements InitialIndividual{
	public static Random rnd;
	 /**
     * 返回low—uper之间的数
     * @param low 下限
     * @param uper 上限
     * @return 返回low—uper之间的数
     */
    double rand(double low, double uper) {
        rnd = new Random();
        return rnd.nextDouble() * (uper - low) + low;
    }

	public double calFitness(double[] pos) {
		// TODO Auto-generated method stub
		return pos[0] * pos[0] + (pos[1] - pos[2]) * (pos[1] - pos[2]); 
	}
	public void initial(int dim, double[] pos, double[] v, double[] pbest,double[] pLowers,
			double[] pUppers,double[] vLowers,double[] vUppers) {
		// TODO Auto-generated method stub
		for (int i = 0; i < dim; ++i) {
            pos[i] = rand(-10, 10);
            pbest[i] = pos[i];
            v[i] = rand(-20, 20);
           
        }
	}

}
