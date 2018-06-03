package mcts.exception;

/**
 * @author Jervis
 *	当前点不合法则会抛出此异常
 */
public class IllegalPointException extends Exception {

	private static final long serialVersionUID = -7057920676204621884L;

	public IllegalPointException() {
		super();
	}

	public IllegalPointException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IllegalPointException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalPointException(String message) {
		super(message);
	}

	public IllegalPointException(Throwable cause) {
		super(cause);
	}

}
