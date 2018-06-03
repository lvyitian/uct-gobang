package mcts.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mcts.entity.Gobang;
import mcts.entity.GobangUCT;
import mcts.entity.Node;
import mcts.entity.Observation;
import mcts.entity.Point;

/**
 * 用JavaFx做的一个五子棋UI界面
 * 
 * @author Jervis
 *
 */
public class GoBangUI extends Application {

	/**
	 * 模拟多少秒
	 */
	private final static long HOW_MANY_SECONDS = 5 * 1000;

	/**
	 * 黑棋图片
	 */
	private final static Background BLACK_POINT = new Background(
			new BackgroundImage(new Image(GoBangUI.class.getResourceAsStream("/b.png")), BackgroundRepeat.NO_REPEAT,
					BackgroundRepeat.NO_REPEAT, null, null));
	/**
	 * 白棋图片
	 */
	private final static Background WIHTE_POINT = new Background(
			new BackgroundImage(new Image(GoBangUI.class.getResourceAsStream("/w.png")), BackgroundRepeat.NO_REPEAT,
					BackgroundRepeat.NO_REPEAT, null, null));
	/**
	 * true 电脑黑棋， false 电脑白棋
	 */
	private final static boolean isBlack = true;
	/**
	 * 判断应该下黑棋还是白棋用的计数
	 */
	private int count;

	/**
	 * 多少行
	 */
	private final static int row = 19;
	/**
	 * 多少列
	 */
	private final static int col = 19;

	/**
	 * 所有界面上的labelMap，key：'y,x'，value：label
	 */
	private Map<String, Label> labels = new HashMap<>();

	@Override
	public void start(Stage primaryStage) throws Exception {
		int[][] is = new int[row][col];
		Pane root = new Pane();
		GobangUCT gobangUCT = new GobangUCT();
		Gobang gobang = Gobang.getGobang(row, col);
		for (int i = 0; i < is.length; i++) {
			int[] js = is[i];
			for (int j = 0; j < js.length; j++) {
				// 根据二维数组在界面上显示对应的透明label
				Label label = new Label("    ");
				label.setStyle("-fx-font-size:25px");
				label.setLayoutX(i * 24.6);
				label.setLayoutY(j * 25.2);
				label.setId(i + "," + j);
				// 将label放到map中
				labels.put(label.getId(), label);
				// 给label设置点击事件
				label.setOnMouseClicked(event -> {
					try {
						// 棋盘被点击获得点击的点
						Label l = (Label) event.getSource();
						// 判断黑棋还是白棋
						l.setBackground(count % 2 == 0 ? BLACK_POINT : WIHTE_POINT);
						count++;
						// 拿到坐标
						String[] split = l.getId().split(",");
						int x = Integer.parseInt(split[1]);
						int y = Integer.parseInt(split[0]);
						// 创建根节点
						Node node = new Node(null, Point.newInstance(x, y), gobang);
						Observation<int[][]> action = gobang.action(Point.newInstance(x, y), Gobang.POINT_PLAYER);
						// 如果结束了 ，就是玩家获胜
						if (action.isDone()) {
							System.out.println("PLAYER WIN!");
						}
						// 扩展根节点
						gobangUCT.expansion(node);
						// 根据快速走子方法，如果没有下法就进行模拟对局
						Point speedPoint = gobang.speedPoint();
						Node selection = new Node(node, speedPoint, gobang);
						if (selection.getPoint() == null) {
							// 拿到开始时间，一直模拟到设定的时间结束
							long start = System.currentTimeMillis();
							while (System.currentTimeMillis() - start < HOW_MANY_SECONDS)
								// 模拟对局
								gobangUCT.simulation(node);
							// 选择一个分数最大的子节点
							List<Node> childs = node.getChilds();
							Collections.sort(childs, (node1, node2) -> node1.getValue() == node2.getValue() ? 0
									: node1.getValue() > node2.getValue() ? 1 : -1);
							selection = childs.get(childs.size() - 1);
						} else {
							// 如果快速走子方法有下法，则拼装数据
							Gobang speedGobang = (Gobang) gobang.copy();
							Observation<int[][]> observation = speedGobang.action(speedPoint, Gobang.POINT_AI);
							selection.setGobang(speedGobang);
							selection.setObservation(observation);
						}
						// 如果结束，则是AI获胜
						if (selection.isDone()) {
							System.out.println("AI WIN!");
						}
						// 获得label，下AI子
						Label l2 = labels.get(selection.getPoint().getY() + "," + selection.getPoint().getX());
						l2.setBackground(count % 2 == 0 ? BLACK_POINT : WIHTE_POINT);
						count++;
						gobang.setBroad(selection.getGobang().getBroad());
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				root.getChildren().add(label);
			}
		}
		// 如果AI是黑子，则先在棋盘上中间点放一颗黑棋
		if (isBlack) {
			gobang.action(Point.newInstance(row / 2, col / 2), Gobang.POINT_AI);
			labels.get(row / 2 + "," + col / 2).setBackground(count % 2 == 0 ? BLACK_POINT : WIHTE_POINT);
			count++;
		}
		// 棋盘背景图
		Image image = new Image(this.getClass().getResourceAsStream("/bang.jpg"));
		root.setBackground(new Background(
				new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, null)));
		// 窗口大小
		Scene scene = new Scene(root, 470, 480);
		// 窗口标题
		primaryStage.setTitle("GoBang");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
