package mcts.entity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mcts.abs.UCT;
import mcts.exception.IllegalPointException;

/**
 * @author Jervis
 * 
 *         实现五子棋UCT方法的类
 *
 */
public class GobangUCT implements UCT<int[][]> {

	/**
	 * 常量C，UCB公式里用到的，通常为2的开方
	 */
	public static final double C = 0.5;
	/**
	 * 多少次模拟后，当前节点拓展新节点
	 */
	public static final int EXPANSION_N = 40;

	/**
	 * 选择
	 * 
	 * @param node
	 *            根节点
	 * @return 最符合条件的叶节点
	 */
	@Override
	public Node selection(Node node) {
		// 获得所有子节点
		List<Node> nodes = node.getChilds();
		// 如果有子节点是从来没有被模拟过的，则返回该节点
		for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
			Node child = iterator.next();
			if (child.isInitialization()) {
				return child;
			}
		}
		// 根据UCB公式进行排序
		Collections.sort(nodes, (node1, node2) -> GobangUCT.ucb(node1) > GobangUCT.ucb(node2) ? 0 : GobangUCT.ucb(node1) > GobangUCT.ucb(node2) ? 1 : -1);
		// 如果下一步是玩家下，则需要取UCB值最小的节点；否则取最大的节点
		Node base = node.who() == Gobang.POINT_AI ? nodes.get(0) : nodes.get(nodes.size() - 1);
		// 如果选择到的节点不是叶节点，继续往下寻找；否则返回选择的节点
		if (base.hasChild()) {
			return selection(base);
		} else {
			return base;
		}
	}

	/**
	 * 扩展 （此处和UCT的扩展有点不一样，这里一次扩展会直接将所有可下点全部扩展）
	 * 
	 * @param node
	 *            根节点
	 * @return 扩展之后根节点
	 * @throws IllegalPointException
	 *             当前点不合法则会抛出此异常
	 */
	@Override
	public Node expansion(Node node) throws IllegalPointException {
		// 如果节点判断已经结束 ，直接返回，不做扩展
		if (node.getObservation() != null && node.isDone()) {
			return node;
		}
		// 获得所有可以被下的点，此处控制这些点都距离已下点的周围一格
		List<Point> illegalPoints = node.getGobang().illegalPoints();
		// 循环添加这些点成为节点的子节点
		for (Iterator<Point> iterator = illegalPoints.iterator(); iterator.hasNext();) {
			Point point = iterator.next();
			Gobang gobang = (Gobang) node.getGobang().copy();
			Observation<int[][]> action = gobang.action(point,
					node.who() == Gobang.POINT_AI ? Gobang.POINT_PLAYER : Gobang.POINT_AI);
			Node child = new Node(node, point, gobang);
			child.setObservation(action);
			node.addChild(child);
		}
		return node;
	}

	/**
	 * 模拟
	 * 
	 * @param node
	 *            根节点
	 * @return 模拟最后一次的环境描述
	 */
	@Override
	public Observation<int[][]> simulation(Node node) {
		// 选择最适合的子节点
		Node child = selection(node);
		// 如果当前选择的节点没有子节点，并且已经超过需要扩展的模拟次数，进行扩展，并且选择出当前节点最好的子节点
		if (!child.hasChild() && child.getN() > GobangUCT.EXPANSION_N) {
			try {
				expansion(child);
				// 如果扩展之后有子节点，则选择子节点
				if (child.hasChild()) {
					child = selection(child);
				}
			} catch (IllegalPointException e) {
				e.printStackTrace();
			}
		}
		// 深度复制节点，以免模拟的情况，影响到树结构
		Node copyNode = child.copy();
		// 当作没有任何叶节点是已经结束的
		int who = copyNode.who();
		// 第一次进来直接运行形势判断
		Observation<int[][]> observation = copyNode.getGobang().formalJudgment();
		try {
			// 如果形势判断已经有结果 则 直接不进行模拟
			while (!observation.isDone()) {
				// 如果节点已经是结束状态，也不进行模拟，并且用节点的状态进行传播
				if (copyNode.isDone()) {
					observation = copyNode.getObservation();
					break;
				}
				// 如果节点没有子节点，扩展节点
				if (!copyNode.hasChild()) {
					expansion(copyNode);
					// 如果扩展之后还没有子节点，重新模拟
					if (!copyNode.hasChild()) {
						copyNode = child.copy();
						observation = copyNode.getGobang().formalJudgment();
						continue;
					}
				}
				who = who == Gobang.POINT_AI ? Gobang.POINT_PLAYER : Gobang.POINT_AI;
				int index = (int) (Math.random() * copyNode.getChilds().size());
				// 随机获得一个子节点去模拟对局
				Node selection = copyNode.getChilds().get(index);
				observation = copyNode.getGobang().action(selection.getPoint(), who);
				// 如果当前下的子是AI同时没有结束，则进行棋盘的形势判断
				if (who == Gobang.POINT_AI && !observation.isDone()) {
					observation = copyNode.getGobang().formalJudgment();
				}
				copyNode = selection;
			}
			// 将模拟对局的 得分向上传播
			double reward = observation.getReward();
			backPropagation(child, reward);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return observation;
	}

	/**
	 * 传播
	 * 向父节点传播，直到根节点
	 * @param node	叶节点
	 * @param value	向上传播的得分
	 */
	@Override
	public void backPropagation(Node node, double value) {
		backUpOne(node, value);
		if (node.hasParent()) {
			backPropagation(node.getParent(), value);
		}
	}
	
	
	/**
	 * 单节点传播方法
	 * @param node	本次节点
	 * @param value 本次获得的分数
	 */
	private void backUpOne(Node node, double value) {
		double oldValueTotal = node.getValue() * node.getN();
		node.addOneCount();
		node.setValue((oldValueTotal + value) / node.getN());
	}

	/**
	 * UCB公式
	 * @param node	节点
	 * @return		UCB分数
	 */
	private static double ucb(Node node) {
		return node.getValue() + GobangUCT.C * Math.sqrt(2 * Math.log(node.getParent().getN()) / node.getN());
	}

}
