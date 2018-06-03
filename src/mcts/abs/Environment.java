package mcts.abs;

import mcts.entity.Observation;
import mcts.entity.Point;
import mcts.exception.IllegalPointException;

/**
 * @author Jervis
 *
 *	定义环境的接口，初步定义以下几个必要方法
 *
 * @param <E> 环境类型（二维数组，一维数组等等）
 */
public interface Environment<E> {
	
	/**
	 * 在环境中判断，参数点 是否合法
	 * @param point	点对象
	 * @return
	 */
	boolean isIllegal(Point point);
	
	/**
	 * 在环境中判断，参数点 是否合法
	 * @param x 坐标x
	 * @param y 坐标y
	 * @return
	 */
	boolean isIllegal(int x, int y);
	
	/**
	 *	重置整个环境 
	 */
	E reset();
	
	/**
	 * 打印环境在控制台
	 */
	void render();
	
	/**
	 * 行动方法
	 * @param point	点对象
	 * @param who	玩家 or AI
	 * @return		返回对象包括是否结束，环境评分等信息
	 * @throws IllegalPointException	当前点不合法则会抛出此异常
	 */
	Observation<E> action(Point point, int who) throws IllegalPointException;
	
	/**
	 * 行动方法
	 * @param x 坐标x
	 * @param y 坐标y
	 * @param who	玩家 or AI
	 * @return		返回对象包括是否结束，环境评分等信息
	 * @throws IllegalPointException	当前点不合法则会抛出此异常
	 */
	Observation<E> action(int x, int y, int who) throws IllegalPointException;
	
	/**
	 * 深度复制整个环境
	 * @return 本环境的复制
	 */
	Environment<E> copy();
	
}
