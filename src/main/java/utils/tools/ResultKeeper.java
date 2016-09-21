package utils.tools;

public class ResultKeeper<T> {

	private boolean hasResult;
	private T result;

	public ResultKeeper<T> put(T result) {
		this.result = result;
		hasResult = true;
		return this;
	}

	public T getResult() {
		return result;
	}

	public boolean hasResult() {
		return hasResult;
	}

}
