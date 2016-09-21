package pom.components.iframecomponents;

public final class DriverContext {

	protected static ThreadLocal<Boolean> isSwitchedToIFrame = new ThreadLocal<>();

	private DriverContext() {
	}

	public static boolean isSwitchedToIFrame() {
		if (isSwitchedToIFrame.get() != null) {
			return isSwitchedToIFrame.get();
		}
		return false;
	}

	public static void setSwitchedToIFrame(boolean value) {
		isSwitchedToIFrame.set(value);
	}

}
