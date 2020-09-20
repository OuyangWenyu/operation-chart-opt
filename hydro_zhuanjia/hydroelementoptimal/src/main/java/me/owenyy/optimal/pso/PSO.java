package me.owenyy.optimal.pso;

import java.util.HashMap;

import me.owenyy.optimal.InitialIndividual;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *粒子群类
 * @author FashionXu作,owenyy改动一点
 */
public class PSO {
    /**
     * 粒子群
     */
    Particle[] pars;
    double global_best;//全局最优解
    /**粒子的数量*/
    int pcount;
	private InitialIndividual initial;
	
	/**保存每一代的全局最优解，以判断粒子的收敛情况*/
	public static HashMap<Integer,double[]> allGeneRusults;//double[]数组最后一个数是第key代global_best值，前面是粒子个维取值
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
	/**
     * 粒子群初始化
     * @param n 粒子的数量
     * @param c1 粒子自身认知能力 系数
     * @param c2 粒子通过社会信息学习的能力 系数
     * @param w  
     * @param dims
     */
    public void init(int n,double c1,double c2,double w,int dims,double[] pLowers,
    		double[] pUppers,double[] vLowers,double[] vUppers) {
        pcount = n;
        global_best = -1e6;//初始化
        int index = -1;
        pars = new Particle[pcount];
        //类的静态成员的初始化
        Particle.c1 = c1;
        Particle.c2 = c2;
        Particle.w = w;
        Particle.dims = dims;
        Particle.pLowers=pLowers;
        Particle.pUppers=pUppers;
        Particle.vLowers=vLowers;
        Particle.vUppers=vUppers;
       
        for (int i = 0; i < pcount; ++i) {
            pars[i] = new Particle();
            pars[i].setInitial(initial);
            pars[i].initial(dims);
            pars[i].evaluate();
            if (global_best < pars[i].fitness) {
                global_best = pars[i].fitness;
                index = i;
            }
        }
        Particle.gbest = new double[Particle.dims];
        for (int i = 0; i < dims; ++i) {
            Particle.gbest[i] = pars[index].pos[i];
        }
        
        allGeneRusults=new HashMap<Integer,double[]>();
    }
    /**
     * 粒子群的运行
     */
    public void run(int runtimes) {
        int index;
        int allRunTimes=runtimes;
        boolean converge=false;
        while (runtimes > 0) {
            index = -1;//能更新全局最优值的粒子在当前种群中的位置
            double lastGlobalFitness=global_best;
            //每个粒子更新位置和适应值
            for (int i = 0; i < pcount; ++i) {
                pars[i].update(allRunTimes,allRunTimes-runtimes);
                pars[i].evaluate();
                if (global_best < pars[i].fitness) {
                    global_best = pars[i].fitness;
                    index = i;
                }
            }
            if(global_best-lastGlobalFitness<0.0001 && runtimes<1){
            	converge=true;
            }
            //发现更好的解
            if (index != -1) {
                for (int i = 0; i < pars[i].pos.length; ++i) {
                    Particle.gbest[i] = pars[index].pos[i];
                }
            }
            if(converge)
            	break;
            --runtimes;
            double[] nowBestResult=new double[Particle.dims+1];
            for(int i=0;i<nowBestResult.length-1;i++){
            	nowBestResult[i]=Particle.gbest[i];
            }
            nowBestResult[nowBestResult.length-1]=global_best;
            allGeneRusults.put(allRunTimes-runtimes, nowBestResult);
        }
    }
    /**
     * 显示程序每次迭代的最终结果
     */
    public void showresult(int generationNum) {
        System.out.println("程序第"+generationNum+"代求得的最优解是" + (allGeneRusults.get(generationNum))[allGeneRusults.get(generationNum).length-1]);
        System.out.println("每一维的值是");
        for (int i = 0; i < Particle.dims; ++i) {
            System.out.println((allGeneRusults.get(generationNum))[i]);
        }
    }
    /**
     * 显示程序求解结果
     */
    public void showresult() {
        System.out.println("程序求得的最优解是" + global_best);
        System.out.println("每一维的值是");
        for (int i = 0; i < Particle.dims; ++i) {
            System.out.println(Particle.gbest[i]);
        }
    }
}

