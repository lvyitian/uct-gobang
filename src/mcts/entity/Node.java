package mcts.entity;

import java.util.ArrayList;
import java.util.List;

import mcts.exception.IllegalPointException;

/**
 * 节点对象
 * @author Jervis
 *
 */
public class Node {

	private Node parent;		// 父节点
	private int n;				// 模拟了多少次
	private double value;		// 分数
	private List<Node> childs;	// 所有子节点
	private Point point;		// 点
	private Gobang gobang;		// 五子棋
	private Observation<int[][]> observation; // 行动后的描述
	
	public void addOneCount() {
		this.n ++;
	}
	
	public boolean hasChild() {
		return this.childs != null && this.childs.size() > 0;
	}
	
	public boolean hasParent() {
		return this.parent != null;
	}
	
	public boolean isInitialization() {
		return this.n == 0;
	}

	public void addChild(Node node) {
		if (this.childs == null) {
			this.childs = new ArrayList<>();
		}
		this.childs.add(node);
	}
	
	public void addChild() {
		if (this.childs == null) {
			this.childs = new ArrayList<>();
		}
		this.childs.add(new Node(this));
	}
	
	public Node(Node parent, Point point, Gobang gobang) {
		super();
		this.parent = parent;
		this.point = point;
		this.gobang = gobang;
	}

	public Node() {
		
	}
	
	public Node(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public List<Node> getChilds() {
		return childs;
	}

	public void setChilds(List<Node> childs) {
		this.childs = childs;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Gobang getGobang() {
		return gobang;
	}

	public void setGobang(Gobang gobang) {
		this.gobang = gobang;
	}
	
	public Observation<int[][]> getObservation() {
		return observation;
	}

	public void setObservation(Observation<int[][]> observation) {
		this.observation = observation;
	}
	
	public boolean isDone() {
		return this.observation.isDone();
	}
	
	public int who() {
		return this.gobang.getBroad()[this.point.getX()][this.point.getY()];
	}
	
	public Node copy() {
		Node node = new Node();
		node.childs = this.childs == null ? null : new ArrayList<>(this.childs);
		node.gobang = (Gobang) this.gobang.copy();
		node.observation = this.observation;
		node.parent = this.parent;
		node.n = this.n;
		node.value = this.value;
		node.point = this.point;
		return node;
	}
	
	public Node copySon() {
		Node node = new Node();
		node.childs = this.childs == null ? null : new ArrayList<>(this.childs);
		node.gobang = (Gobang) this.gobang.copy();
		node.observation = this.observation;
		node.parent = this;
		node.n = this.n;
		node.value = this.value;
		node.point = this.point;
		return node;
	}
	
	public Node copy(Point point) throws IllegalPointException {
		Node node = new Node();
		node.childs = this.childs == null ? null : new ArrayList<>(this.childs);
		node.gobang = (Gobang) this.gobang.copy();
		node.observation = node.gobang.action(point, this.who() == Gobang.POINT_AI ? Gobang.POINT_PLAYER : Gobang.POINT_AI);
		node.parent = this.parent;
		node.n = this.n;
		node.value = this.value;
		node.point = this.point;
		return node;
	}
}
