package utils.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import utils.WebDriverFactory;


public class SharedWebDriver extends EventFiringWebDriver {

	private static final WebDriverFactory DRIVER_FACTORY = new WebDriverFactory();

	private static final ThreadLocal<WebDriver> REAL_DRIVER = new ThreadLocal<WebDriver>() {

		@Override
		protected WebDriver initialValue() {
			final WebDriver driver = DRIVER_FACTORY.create();
			final Long currentThreadId = Thread.currentThread().getId();
			Thread closeThread = new Thread() {

				@Override
				public void run() {
					try {
						driver.quit();
					} finally {
						WebDriverFactory.stopService(currentThreadId);
					}
				}
			};
			Runtime.getRuntime().addShutdownHook(closeThread);
			CLOSE_THREAD.set(closeThread);
			return driver;
		}
	};

	private static final ThreadLocal<Thread> CLOSE_THREAD = new ThreadLocal<>();

	public SharedWebDriver() {
		super(REAL_DRIVER.get());
	}

	@Override
	public void close() {
		if (Thread.currentThread() != CLOSE_THREAD.get())
			throw new UnsupportedOperationException(
					"You shouldn't close this WebDriver. It's shared and will close when the JVM exits.");
		super.close();
	}

	@Override
	public void quit() {
		if (Thread.currentThread() != CLOSE_THREAD.get())
			throw new UnsupportedOperationException(
					"You shouldn't quit this WebDriver. It's shared and will quit when the JVM exits.");
		super.quit();
	}
}
