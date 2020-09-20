package me.owenyy.optimal.pso;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Random;

import me.owenyy.optimal.InitialIndividual;
/**
 *粒子类
 * @author FashionXu作,owenyy改动一点
 */
public class Particle {
    public double[] pos;//粒子的位置，求解问题多少维，则此数组为多少维
    public double[] v;//粒子的速度，维数同位置
    public double fitness;//每个粒子的当前适应度
    public double[] pbest;//粒子的历史最好位置
    public static double[] gbest;//所有粒子找到的最好位置
    public static Random rnd = new Random();;
    public static int dims;//粒子的维数（速度和位置的维数一致）
    public static double w;//
    public static double c1;//
    public static double c2;//
    double pbest_fitness;//该粒子历史最优解
    
    private InitialIndividual initial;
    /**
	 * @return the initial
	 */
	public InitialIndividual getInitial() {
		return initial;
	}
	/**
	 * @param initial the initial to set
	 */
	public void setInitial(InitialIndividual initial) {
		this.initial = initial;
	}
	public static double[] vUppers;
	public static double[] vLowers;
	public static double[] pUppers;
	public static double[] pLowers;
    
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
    /**
     * 初始化粒子
     * @param dim 表示粒子的维数
     */
    public void initial(int dim) {
        pos = new double[dim];
        v = new double[dim];
        pbest = new double[dim];
        fitness = -1e6;
        pbest_fitness = -1e6;
        dims = dim;
        initial.initial(dim, pos, v, pbest,pLowers,
    			pUppers,vLowers,vUppers);

    }
    /**
     * 评估函数值,同时记录历史最优位置
     */
    public void evaluate() {
        fitness = initial.calFitness(pos);
        if (fitness > pbest_fitness) {
            for (int i = 0; i < dims; ++i) {
                pbest[i] = pos[i];
            }
            pbest_fitness=fitness;//当前代数该粒子适应度赋给该粒子的历史最优适应度值
        }
    }
    /**
     * 更新速度和位置，两个参数帮助确定惯性系数
     * @param allRunTimes  总迭代次数
     * @param runtime  当前迭代次数
     */
    public void update(int allRunTimes,int runtime) {
    	w=0.9-runtime/allRunTimes*0.5;
        for (int i = 0; i < dims; ++i) {
            v[i] = w * v[i] + c1 * rnd.nextDouble() * (pbest[i] - pos[i])
                    + c2 * rnd.nextDouble() * (gbest[i] - pos[i]);
            if (v[i] > vUppers[i]) {
                v[i] = vUppers[i];
            }
            if (v[i] < vLowers[i]) {
                v[i] = vLowers[i];
            }
            pos[i] = pos[i] + v[i];
            if (pos[i] > pUppers[i]) {
                pos[i] = pUppers[i];
            }
            if (pos[i] < pLowers[i]) {
                pos[i] = pLowers[i];
            }
        }
    }
}
