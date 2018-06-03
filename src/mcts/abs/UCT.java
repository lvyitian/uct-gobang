package mcts.abs;

import mcts.entity.Node;
import mcts.entity.Observation;
import mcts.exception.IllegalPointException;

/**
 * @author Jervis
 *
 * 定义uct算法四个主要方法的接口
 *
 * @param <E> 环境类型（二维数组，一维数组等等）
 */
public interface UCT<E> {

	/**
	 * 选择
	 * @param node	根节点
	 * @return	最符合条件的叶节点
	 */
	Node selection(Node node);
	
	/**
	 * 扩展
	 * @param node 	根节点
	 * @return		扩展之后根节点
	 * @throws IllegalPointException	当前点不合法则会抛出此异常
	 */
	Node expansion(Node node) throws IllegalPointException;
	
	/**
	 * 模拟
	 * @param node	根节点
	 * @return		模拟最后一次的环境描述
	 */
	Observation<E> simulation(Node node);
	
	/**
	 * 传播
	 * @param node	叶节点
	 * @param value	向上传播的得分
	 */
	void backPropagation(Node node, double value);
	
}
