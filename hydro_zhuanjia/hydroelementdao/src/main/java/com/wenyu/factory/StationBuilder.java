package com.wenyu.factory;

import com.wenyu.factory.station.PowerControlHStation;

/**
 * 实在是想不到更好的方法来构造梯级电站，暂时还是使用抽象工厂模式
 * @author  OwenYY
 *
 */
public interface StationBuilder {
	/**
	 * @param eb
	 * @return  构造水库的抽象方法
	 */
	public PowerControlHStation constructStation(EngineerBureau eb);
}
