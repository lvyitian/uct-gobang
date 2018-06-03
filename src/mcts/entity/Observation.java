package mcts.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 行动后的描述对象
 * @author Jervis
 *
 * @param <E> 环境类型（二维数组，一维数组等等）
 */
public class Observation<E> {

	
	private double reward;				// 得分
	private E environment;				// 环境
	private boolean done;				// 是否结束
	private Map<String, String> info;	// 具体描述
	
	public Observation() {
		super();
	}
	public Observation(double reward, E environment, boolean done, Map<String, String> info) {
		super();
		this.reward = reward;
		this.environment = environment;
		this.done = done;
		this.info = info;
	}
	public Observation(double reward, E environment, boolean done, String key, String value) {
		super();
		this.reward = reward;
		this.environment = environment;
		this.done = done;
		this.putInfo(key, value);
	}
	public Observation(E environment, Map<String, String> info) {
		super();
		this.environment = environment;
		this.info = info;
	}
	
	public Observation(E environment, String key, String value) {
		super();
		this.environment = environment;
		this.putInfo(key, value);
	}
	public Observation(E environment) {
		super();
		this.environment = environment;
	}
	public double getReward() {
		return reward;
	}
	public void setReward(double reward) {
		this.reward = reward;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public Map<String, String> getInfo() {
		return info;
	}
	public void setInfo(Map<String, String> info) {
		this.info = info;
	}
	public E getEnvironment() {
		return environment;
	}
	public void setEnvironment(E environment) {
		this.environment = environment;
	}
	public void putInfo(String key, String value) {
		if (this.info == null) {
			this.info = new HashMap<>();
		}
		this.info.put(key, value);
	}
	@Override
	public String toString() {
		return "Observation [reward=" + reward + ", environment=" + environment + ", done=" + done + ", info=" + info
				+ "]";
	}
	
}
