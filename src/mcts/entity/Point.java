package mcts.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 点对象
 * @author Jervis
 *
 */
public class Point {

	private int x;	// 横坐标
	private int y; 	// 纵坐标
	// 所有点的map，key为'x,y'，value为point对象
	private static Map<String, Point> map = new HashMap<>();
	
	// 每次创建点，都从map中找一边，如果有就直接返回，如果没有则创建并且放到map中（实现单例对象）
	public static Point newInstance(int x, int y) {
		Point point = map.get(x + "," + y);
		if (point == null) {
			point = new Point(x, y);
			map.put(x + "," + y, point);
		}
		return point;
	}
	
	private Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
	
}
