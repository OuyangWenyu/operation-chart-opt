package me.owenyy.servicedispatchgraph;

import com.wenyu.factory.station.PowerControlHStation;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchGraph;
import com.wenyu.hydroelements.hydrostation.dispatchgraph.DispatchInputParas;

import me.owenyy.drawmethods.DispatchGraphBehavior;
import me.owenyy.drawmethods.MethodTrial;

/**
 * 配置各种具体的实现方法
 * @author  OwenYY
 *
 */
public class MakeDispatchGraph {
	PowerControlHStation hss;
	DispatchGraphBehavior makeMethod;//后面测试了策略模式的spring注入后再使用
	public MakeDispatchGraph(PowerControlHStation hss) {
		super();
		this.hss = hss;
	}
	public DispatchGraph makeDispatchGraph(){
		makeMethod=new MethodTrial();//MethodDifferentOutput();
		DispatchInputParas dispatchInputParas=new DispatchInputParas("DECAD",0.95, 3, 1, 5);//外部输入绘制调度图所需的数据
		makeMethod.makeDispatchGraph(hss,dispatchInputParas);
		return hss.getDispatchGraph();
	}
}
