package mcts.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mcts.abs.Environment;
import mcts.exception.IllegalPointException;

/**
 * @author Jervis
 *
 *         五子棋环境
 */
public class Gobang implements Environment<int[][]> {

	/**
	 * 初始化点值，即没有子的点值
	 */
	public final static int POINT_INITAL = 0;
	/**
	 * AI点值
	 */
	public final static int POINT_AI = 1;
	/**
	 * 玩家点值
	 */
	public final static int POINT_PLAYER = 2;

	/**
	 * 获胜者AI
	 */
	public final static String WINNER_AI = "0";
	/**
	 * 获胜者玩家
	 */
	public final static String WINNER_PLAYER = "1";
	/**
	 * 没有获胜者
	 */
	public final static String NO_WINNER = "2";

	/**
	 * 棋局没有结束，或者没有任何胜负相关判断的返回值
	 */
	public final static double NORMAL_WINNER_REWARD = 0;

	/**
	 * AI有3个子连一起，并且没有一边被挡住获得返回值
	 */
	private static final double AI_THREE = 0.2;
	/**
	 * AI有4个子连一起，有一边被挡住获得返回值
	 */
	private static final double AI_FOUR_BETWEEN = 0.2;
	/**
	 * AI有4个字连一起，并且没有一边被挡住获得返回值
	 */
	private static final double AI_FOUR_NO_BETWEEN = 0.8;
	/**
	 * AI有5个字连一起获得返回值
	 */
	private static final double AI_FIVE = 1;
	/**
	 * 当前棋局，玩家下一步之后，有3个子连一起，并且没有一边被挡住获得返回值
	 */
	private static final double PLAYER_NEXT_THREE = -0.25;
	/**
	 * 玩家3个子连一起，并且没有一边被挡住获得返回值
	 */
	private static final double PLAYER_THREE = -0.8;
	/**
	 * 当前棋局，玩家下一步之后，有4个子连一起，有一边被挡住获得返回值
	 */
	private static final double PLAYER_NEXT_FOUR_BETWEEN = -0.25;
	/**
	 * 玩家4个子连一起，有一边被挡住获得返回值
	 */
	private static final double PLAYER_FOUR_BETWEEN = -0.8;
	/**
	 * 玩家4个子连一起，并且没有一边被挡住获得返回值
	 */
	private static final double PLAYER_FOUR_NO_BETWEEN = -0.8;
	/**
	 * 玩家有5个字连一起获得返回值
	 */
	private static final double PLAYER_FIVE = -0.8;

	/**
	 * 棋盘行数
	 */
	private int row;
	/**
	 * 棋盘列数
	 */
	private int col;
	/**
	 * 当前棋盘
	 */
	private static Gobang gobang;

	/**
	 * 棋盘使用二位数组
	 */
	private int[][] broad;

	private Gobang(int row, int col) {
		this.row = row;
		this.col = col;
		this.broad = new int[row][col];
	}

	private Gobang(int[][] board, int row, int col) {
		this.row = row;
		this.col = col;
		this.broad = board;
	}

	/**
	 * 环境做成单例，获得实例的方法
	 * 
	 * @param row
	 *            行数
	 * @param col
	 *            列数
	 * @return 单例对象
	 */
	public static Gobang getGobang(int row, int col) {
		if (gobang == null) {
			gobang = new Gobang(row, col);
		}
		return gobang;
	}

	/**
	 * 在环境中判断，参数点 是否合法
	 * 
	 * @param point
	 *            点对象
	 * @return
	 */
	@Override
	public boolean isIllegal(Point point) {
		if (point == null) return false;
		return isIllegal(point.getX(), point.getY());
	}

	/**
	 * 在环境中判断，参数点 是否合法
	 * 
	 * @param x
	 *            坐标x
	 * @param y
	 *            坐标y
	 * @return
	 */
	@Override
	public boolean isIllegal(int x, int y) {
		return this.broad[x][y] == Gobang.POINT_INITAL;
	}

	/**
	 * 重置整个环境
	 */
	@Override
	public int[][] reset() {
		this.broad = new int[this.row][this.col];
		return this.broad;
	}

	/**
	 * 打印环境在控制台
	 */
	@Override
	public void render() {
		System.out.println(this.toString());
	}

	/**
	 * 行动方法
	 * 
	 * @param point
	 *            点对象
	 * @param who
	 *            玩家 or AI
	 * @return 返回对象包括是否结束，环境评分等信息
	 * @throws IllegalPointException
	 *             当前点不合法则会抛出此异常
	 */
	@Override
	public Observation<int[][]> action(Point point, int who) throws IllegalPointException {
		// 不合法点判断，抛出异常
		if (!isIllegal(point))
			throw new IllegalPointException(String.format("illegal point %s at board %s", point, this));
		// 不非法点值判断，抛出异常
		if (who != Gobang.POINT_AI && who != Gobang.POINT_PLAYER)
			throw new IllegalPointException(String.format("illegal point value %s", who));
		// 修改二维数组中的点值
		this.broad[point.getX()][point.getY()] = who;
		// 判断当前下法是否结束
		boolean done = isDone(point);
		// 拼装返回对象
		if (done)
			return new Observation<int[][]>(
					(done ? who == Gobang.POINT_AI ? Gobang.AI_FIVE : Gobang.PLAYER_FIVE : Gobang.NORMAL_WINNER_REWARD),
					((Gobang) this.copy()).broad, done, "winner",
					who == Gobang.POINT_AI ? Gobang.WINNER_AI : Gobang.WINNER_PLAYER);
		return new Observation<int[][]>(((Gobang) this.copy()).broad);
	}

	/**
	 * 行动方法
	 * 
	 * @param x
	 *            坐标x
	 * @param y
	 *            坐标y
	 * @param who
	 *            玩家 or AI
	 * @return 返回对象包括是否结束，环境评分等信息
	 * @throws IllegalPointException
	 *             当前点不合法则会抛出此异常
	 */
	@Override
	public Observation<int[][]> action(int x, int y, int who) throws IllegalPointException {
		return this.action(Point.newInstance(x, y), who);
	}

	/**
	 * 深度复制整个环境
	 * 
	 * @return 本环境的复制
	 */
	@Override
	public Environment<int[][]> copy() {
		int[][] copy = new int[this.row][this.col];
		for (int i = 0; i < this.broad.length; i++) {
			System.arraycopy(this.broad[i], 0, copy[i], 0, this.broad[i].length);
		}
		return new Gobang(copy, this.row, this.col);
	}

	/**
	 * 判断当前下法是否会结束游戏
	 * 
	 * @param point
	 *            下法
	 * @return
	 */
	private boolean isDone(Point point) {
		int x = point.getX(), y = point.getY(), count = 0;
		for (int i = y - 1; i >= 0 && this.broad[x][y] == this.broad[x][i]; i--)
			count++;
		for (int i = y + 1; i < this.row && this.broad[x][y] == this.broad[x][i]; i++)
			count++;
		if (count >= 4)
			return true;
		count = 0;
		for (int i = x - 1; i >= 0 && this.broad[x][y] == this.broad[i][y]; i--)
			count++;
		for (int i = x + 1; i < this.col && this.broad[x][y] == this.broad[i][y]; i++)
			count++;
		if (count >= 4)
			return true;
		count = 0;
		for (int i = x - 1, j = y - 1; i >= 0 && j >= 0 && this.broad[x][y] == this.broad[i][j]; i--, j--)
			count++;
		for (int i = x + 1, j = y + 1; i < this.col && j < this.row && this.broad[x][y] == this.broad[i][j]; i++, j++)
			count++;
		if (count >= 4)
			return true;
		count = 0;
		for (int i = x - 1, j = y + 1; i >= 0 && j < this.row && this.broad[x][y] == this.broad[i][j]; i--, j++)
			count++;
		for (int i = x + 1, j = y - 1; i < this.col && j >= 0 && this.broad[x][y] == this.broad[i][j]; i++, j--)
			count++;
		if (count >= 4)
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() 当前只将AI打印成白棋，玩家打印为黑棋
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.broad.length; i++) {
			if (i == 0) {
				sb.append(
						(i < 10 ? "0" + i : i) + (this.broad[i][0] == 0 ? "┌ " : this.broad[i][0] == 1 ? "○ " : "● "));
			} else if (i == this.broad.length - 1) {
				sb.append(
						(i < 10 ? "0" + i : i) + (this.broad[i][0] == 0 ? "└ " : this.broad[i][0] == 1 ? "○ " : "● "));
			} else {
				sb.append(
						(i < 10 ? "0" + i : i) + (this.broad[i][0] == 0 ? "├ " : this.broad[i][0] == 1 ? "○ " : "● "));
			}
			for (int j = 1; j < this.broad[i].length - 1; j++) {
				if (i == 0) {
					sb.append(this.broad[i][j] == 0 ? " ┬ " : this.broad[i][j] == 1 ? " ○ " : " ● ");
				} else if (i == this.broad.length - 1) {
					sb.append(this.broad[i][j] == 0 ? " ┴ " : this.broad[i][j] == 1 ? " ○ " : " ● ");
				} else {
					sb.append(this.broad[i][j] == 0 ? " ┼ " : this.broad[i][j] == 1 ? " ○ " : " ● ");
				}
			}
			if (i == 0) {
				sb.append(this.broad[i][this.col - 1] == 0 ? " ┐❂\r\n"
						: this.broad[i][this.col - 1] == 1 ? "○❂\r\n" : "●❂\r\n");
			} else if (i == this.broad.length - 1) {
				sb.append(this.broad[i][this.col - 1] == 0 ? " ┘❂\r\n"
						: this.broad[i][this.col - 1] == 1 ? "○❂\r\n" : "●❂\r\n");
			} else {
				sb.append(this.broad[i][this.col - 1] == 0 ? " ┤❂\r\n"
						: this.broad[i][this.col - 1] == 1 ? "○❂\r\n" : "●❂\r\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 获得当前情况下，每个已下子周围一格的点
	 * 
	 * @return 前情况下，每个已下子周围一格的所有点
	 */
	public List<Point> illegalPoints() {
		Set<Point> points = new HashSet<>();
		for (int i = 0; i < this.broad.length; i++) {
			for (int j = 0; j < this.broad[i].length; j++) {
				if (this.broad[i][j] != Gobang.POINT_INITAL) {
					if (i < this.row - 1 && this.broad[i + 1][j] == Gobang.POINT_INITAL)
						points.add(Point.newInstance(i + 1, j));
					if (i > 0 && this.broad[i - 1][j] == Gobang.POINT_INITAL)
						points.add(Point.newInstance(i - 1, j));
					if (j < this.col - 1 && this.broad[i][j + 1] == Gobang.POINT_INITAL)
						points.add(Point.newInstance(i, j + 1));
					if (j > 0 && this.broad[i][j - 1] == Gobang.POINT_INITAL)
						points.add(Point.newInstance(i, j - 1));
					if (i < this.row - 1 && j < this.col - 1 && this.broad[i + 1][j + 1] == Gobang.POINT_INITAL)
						points.add(Point.newInstance(i + 1, j + 1));
					if (i > 0 && j > 0 && this.broad[i - 1][j - 1] == Gobang.POINT_INITAL)
						points.add(Point.newInstance(i - 1, j - 1));
					if (i < this.row - 1 && j > 0 && this.broad[i + 1][j - 1] == Gobang.POINT_INITAL)
						points.add(Point.newInstance(i + 1, j - 1));
					if (i > 0 && j < this.col - 1 && this.broad[i - 1][j + 1] == Gobang.POINT_INITAL)
						points.add(Point.newInstance(i - 1, j + 1));
				}
			}
		}
		return new ArrayList<>(points);
	}

	/**
	 * 形势判断
	 * 
	 * @return 判断当前局势得分，依据以上已经定好的常量
	 */
	public Observation<int[][]> formalJudgment() {
		// 判断玩家当前局势得分
		double value = judgmentDetail(Gobang.POINT_PLAYER, Gobang.POINT_PLAYER);
		// 判断玩家所有下一步情况局势得分
		value += judgmentDetail(Gobang.POINT_INITAL, Gobang.POINT_PLAYER);
		// 判断白棋当前情况得分
		value += judgmentDetail(Gobang.POINT_AI, Gobang.POINT_AI);
		// 组装返回值
		return new Observation<int[][]>(value, this.broad, value == Gobang.NORMAL_WINNER_REWARD ? false : true, null);
	}

	/**
	 * 形式判断通用方法
	 * 
	 * @param who
	 *            找出哪些值的点
	 * @param pointWho
	 *            匹配值
	 * @return
	 */
	private double judgmentDetail(int who, int pointWho) {
		// 所有需要判断的点，集合
		List<Point> playerPoint = new ArrayList<>();
		// 所有已经被判断过的点集合，后面判断时会跳过这些点
		List<Point> judgedPoint = new ArrayList<>();
		// 循环添加需要判断的点
		for (int i = 0; i < broad.length; i++) {
			for (int j = 0; j < broad[i].length; j++) {
				if (broad[i][j] == who) {
					playerPoint.add(Point.newInstance(i, j));
				}
			}
		}
		double value = 0;
		// 循环判断
		for (int i = 0; i < playerPoint.size(); i++) {
			Point point = playerPoint.get(i);
			// 如果已经判断过当前点 ，则跳过此次判断
			if (judgedPoint.contains(point)) {
				continue;
			}
			// 将当前点添加到已经判断的集合中
			judgedPoint.add(point);
			// 获得返回值
			value += pointJudgment(point.getX(), point.getY(), judgedPoint, pointWho, who);
		}
		// 返回
		return value;
	}

	/**
	 * 判断一个点，它的价值 这个方法与判断是否结束方法很像，只是多一步给指定连接子数量给分的操作
	 * 
	 * @param x
	 *            横坐标
	 * @param y
	 *            纵坐标
	 * @param judgedPoint
	 *            已经判断过的点集合，每次判断点之后都添加到这个集合中，减少判断次数
	 * @param pointWho
	 *            判断周围点，是谁的才加分
	 * @return
	 */
	private double pointJudgment(int x, int y, List<Point> judgedPoint, int pointWho, int who) {
		double reward = 0;
		int count = 0;
		Point firstPoint = Point.newInstance(x, y), lastPoint = Point.newInstance(x, y);
		for (int i = y - 1; i >= 0 && pointWho == this.broad[x][i]; i--) {
			count++;
			firstPoint = Point.newInstance(x, i);
			judgedPoint.add(firstPoint);
		}
		for (int i = y + 1; i < this.row && pointWho == this.broad[x][i]; i++) {
			count++;
			lastPoint = Point.newInstance(x, i);
			judgedPoint.add(lastPoint);
		}
		if (count >= 2 && firstPoint.getY() > 0 && lastPoint.getY() < this.row - 1) {
			reward += countJudgment(count, firstPoint.getX(), firstPoint.getY() - 1, lastPoint.getX(),
					lastPoint.getY() + 1, who);
		}
		count = 0;
		firstPoint = Point.newInstance(x, y);
		lastPoint = Point.newInstance(x, y);
		for (int i = x - 1; i >= 0 && pointWho == this.broad[i][y]; i--) {
			count++;
			firstPoint = Point.newInstance(i, y);
			judgedPoint.add(firstPoint);
		}
		for (int i = x + 1; i < this.col && pointWho == this.broad[i][y]; i++) {
			count++;
			lastPoint = Point.newInstance(i, y);
			judgedPoint.add(lastPoint);
		}
		if (count >= 2 && firstPoint.getX() > 0 && lastPoint.getX() < this.col - 1) {
			reward += countJudgment(count, firstPoint.getX() - 1, firstPoint.getY(), lastPoint.getX() + 1,
					lastPoint.getY(), who);
		}
		;
		count = 0;
		firstPoint = Point.newInstance(x, y);
		lastPoint = Point.newInstance(x, y);
		for (int i = x - 1, j = y - 1; i >= 0 && j >= 0 && pointWho == this.broad[i][j]; i--, j--) {
			count++;
			firstPoint = Point.newInstance(i, j);
			judgedPoint.add(firstPoint);
		}
		for (int i = x + 1, j = y + 1; i < this.col && j < this.row && pointWho == this.broad[i][j]; i++, j++) {
			count++;
			lastPoint = Point.newInstance(i, j);
			judgedPoint.add(lastPoint);
		}
		if (count >= 2 && firstPoint.getX() > 0 && firstPoint.getY() > 0 && lastPoint.getX() < this.col - 1
				&& lastPoint.getY() < this.row - 1) {
			reward += countJudgment(count, firstPoint.getX() - 1, firstPoint.getY() - 1, lastPoint.getX() + 1,
					lastPoint.getY() + 1, who);
		}
		count = 0;
		firstPoint = Point.newInstance(x, y);
		lastPoint = Point.newInstance(x, y);
		for (int i = x - 1, j = y + 1; i >= 0 && j < this.row && pointWho == this.broad[i][j]; i--, j++) {
			count++;
			firstPoint = Point.newInstance(i, j);
			judgedPoint.add(firstPoint);
		}
		for (int i = x + 1, j = y - 1; i < this.col && j >= 0 && pointWho == this.broad[i][j]; i++, j--) {
			count++;
			lastPoint = Point.newInstance(i, j);
			judgedPoint.add(lastPoint);
		}
		if (count >= 2 && firstPoint.getX() > 0 && firstPoint.getY() < this.row - 1 && lastPoint.getX() < this.col - 1
				&& lastPoint.getY() > 0) {
			reward += countJudgment(count, firstPoint.getX() - 1, firstPoint.getY() + 1, lastPoint.getX() + 1,
					lastPoint.getY() - 1, who);
		}
		return reward;
	}

	/**
	 * 根据count（连子数）返回对应的分数
	 * 
	 * @param count
	 *            连子数
	 * @param firstPointX
	 *            最前面一个子的横坐标
	 * @param firstPointY
	 *            最前面一个子的纵坐标
	 * @param lastPointX
	 *            最后面一个子的横坐标
	 * @param lastPointY
	 *            最后面一个子的纵坐标
	 * @param pointWho
	 *            给分时区分AI与玩家的判断值
	 * @return
	 */
	private double countJudgment(int count, int firstPointX, int firstPointY, int lastPointX, int lastPointY,
			int pointWho) {
		double reward = 0.0;
		// 三连子
		if (count == 2 && this.broad[firstPointX][firstPointY] == Gobang.POINT_INITAL
				&& this.broad[lastPointX][lastPointY] == Gobang.POINT_INITAL) {
			if (pointWho == Gobang.POINT_INITAL) {
				reward += Gobang.PLAYER_NEXT_THREE;
			} else {
				reward += pointWho == Gobang.POINT_AI ? Gobang.AI_THREE : Gobang.PLAYER_THREE;
			}
			// 四连子
		} else if (count == 3 && this.broad[firstPointX][firstPointY] == Gobang.POINT_INITAL
				&& this.broad[lastPointX][lastPointY] == Gobang.POINT_INITAL) {
			reward += pointWho == Gobang.POINT_AI ? Gobang.AI_FOUR_NO_BETWEEN : Gobang.PLAYER_FOUR_NO_BETWEEN;
			// 四连子 有一边被挡住
		} else if (count == 3 && (this.broad[firstPointX][firstPointY] == Gobang.POINT_INITAL
				|| this.broad[lastPointX][lastPointY] == Gobang.POINT_INITAL)) {
			if (pointWho == Gobang.POINT_INITAL) {
				reward += Gobang.PLAYER_NEXT_FOUR_BETWEEN;
			} else {
				reward += pointWho == Gobang.POINT_AI ? Gobang.AI_FOUR_BETWEEN : Gobang.PLAYER_FOUR_BETWEEN;
			}
			// 五连子
		} else if (count >= 4) {
			reward += pointWho == Gobang.POINT_AI ? Gobang.AI_FIVE : Gobang.PLAYER_FIVE;
		}
		return reward;
	}
	
	
	public Point speedPoint() throws IllegalPointException {
		List<Point> illegalPoints = this.illegalPoints();
		for (int i = 0; i < illegalPoints.size(); i ++) {
			Point point = illegalPoints.get(i);
			Observation<int[][]> action = this.copy().action(point, Gobang.POINT_AI);
			if (action.isDone()) return point;
			action = this.copy().action(point, Gobang.POINT_PLAYER);
			if (action.isDone()) return point;
		}
		return null;
	}

	public int[][] getBroad() {
		return broad;
	}

	public void setBroad(int[][] broad) {
		this.broad = broad;
	}
	
	public static void main(String[] args) throws IllegalPointException {
		Gobang bang = Gobang.getGobang(19, 19);
		bang.broad = new int[][] 
			{{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

		// System.out.println(bang.isDone(Point.newInstance(2, 3)));
		// System.out.println(bang);
		// bang.render();
		// Gobang copy = (Gobang) bang.copy();
		// Observation<int[][]> action = copy.action(Point.newInstance(0, 0),
		// POINT_PLAYER);
		// System.out.println(action);
		// copy.render();
		// System.out.println(copy);
		System.out.println(bang.formalJudgment().getReward());
	}
}
