package utils;

import static utils.ConfigurationConstants.SELENIUM_BROWSER;
import static utils.ConfigurationConstants.SELENIUM_FIREFOX_BIN_LOCATION;
import static utils.ConfigurationConstants.SELENIUM_GRID_ENDPOINT;
import static utils.ConfigurationConstants.SELENIUM_USE_GRID;
import static utils.ConfigurationConstants.WEB_ELEMENT_TIMEOUT;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.codeborne.selenide.WebDriverRunner;

import io.appium.java_client.ios.IOSDriver;
import utils.driver.DesiredCapabilitiesHelper;
import utils.tools.ResultKeeper;


public class WebDriverFactory {
	private static final String WEBDRIVER_FIREFOX_BIN_SYSTEM_PROPERTY = "webdriver.firefox.bin";
	private static final int TWO_HOURS = 7200;
	private static final long DEFAULT_WAIT_MILLISECONDS = 30000;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static Map<Long, ChromeDriverService> serviceMap = new ConcurrentHashMap<>();

	public EventFiringWebDriver create() {
		WebDriver driver;
		DesiredCapabilitiesHelper capabilitiesHelper = new DesiredCapabilitiesHelper();
		DesiredCapabilities capabilities = capabilitiesHelper.getCapabilities();
		if (ConfigurationService.getConfigInstance().getBoolean(SELENIUM_USE_GRID, false))
			driver = getRemoteWebDriver(capabilities);
		else
			driver = getLocalWebDriver(capabilities);
		EventFiringWebDriver webDriver = new EventFiringWebDriver(driver);
		setupSelenide(webDriver);
		return webDriver;
	}

	private void setupSelenide(EventFiringWebDriver webDriver) {
		WebDriverRunner.setWebDriver(webDriver);
		com.codeborne.selenide.Configuration.timeout =
				ConfigurationService.getConfigInstance().getLong(WEB_ELEMENT_TIMEOUT, DEFAULT_WAIT_MILLISECONDS);
		com.codeborne.selenide.Configuration.screenshots = false;
	}

	private WebDriver getLocalWebDriver(DesiredCapabilities capabilities) {
		switch (ConfigurationHelper.getBrowser()) {
			case CHROME:
			case CHROME_MOBILE:
				return new RemoteWebDriver(getChromeDriverService().getUrl(), capabilities);
			case FIREFOX:
				if (ConfigurationService.getConfigInstance().containsKey(SELENIUM_FIREFOX_BIN_LOCATION)
						&& System.getProperty(WEBDRIVER_FIREFOX_BIN_SYSTEM_PROPERTY) == null)
					System.setProperty(WEBDRIVER_FIREFOX_BIN_SYSTEM_PROPERTY,
							ConfigurationService.getConfigInstance().getString(SELENIUM_FIREFOX_BIN_LOCATION));
				return new FirefoxDriver(capabilities);
			case IE:
				if (!isWindows())
					throw new IllegalArgumentException("Internet Explorer is only supported as a local browser on Windows");
				return new RemoteWebDriver(getIEDriverService().getUrl(), capabilities);
			case SAFARI:
				return new SafariDriver(capabilities);
//			case ANDROID:
//				SelendroidConfiguration config = new SelendroidConfiguration();
//				config.setShouldKeepAdbAlive(true);
//				config.setSessionTimeoutSeconds(TWO_HOURS);
//				SelendroidLauncher selendroidServer = new SelendroidLauncher(config);
//				selendroidServer.launchSelendroid();
//				try {
//					return new SelendroidDriver(capabilities);
//				} catch (Exception e) {
//					throw new IllegalStateException("Unable to create selendroid driver instance", e);
//				}
			case IOS:
				try {
					return new IOSDriver(new URL("http://localhost:4723/wd/hub"), capabilities);
				} catch (MalformedURLException e) {
					throw new IllegalStateException("Malformed localhost URL for hub", e);
				}
			default:
				throw new IllegalArgumentException(ConfigurationService.getConfigInstance().getString(SELENIUM_BROWSER)
						+ " is not a supported browser type");
		}
	}

	protected static ChromeDriverService getChromeDriverService() {
		ResultKeeper<ChromeDriverService> container = extractCurrentService();
		if (!extractCurrentService().hasResult())
			container.put(startChromeService());
		return container.getResult();
	}

	protected static void stopService() {
		stopService(Thread.currentThread().getId());
	}

	public static void stopService(Long threadId) {
		ResultKeeper<ChromeDriverService> container = extractCurrentService(threadId);
		if (container.hasResult())
			if (container.getResult().isRunning())
				container.getResult().stop();
	}

	private static ResultKeeper<ChromeDriverService> extractCurrentService() {
		return extractCurrentService(Thread.currentThread().getId());
	}

	private static ResultKeeper<ChromeDriverService> extractCurrentService(Long threadId) {
		ResultKeeper<ChromeDriverService> container = new ResultKeeper<>();
		ChromeDriverService service = serviceMap.get(threadId);
		if (service != null)
			container.put(service);
		return container;
	}

	private static void registerService(ChromeDriverService service) {
		serviceMap.put(Thread.currentThread().getId(), service);
	}

	private WebDriver getRemoteWebDriver(DesiredCapabilities capabilities) {
		for (int i = 0; i < 3; i++) {
			CreateWebDriver create = new CreateWebDriver(capabilities);
			Thread t = new Thread(create);
			t.setDaemon(true);
			t.start();
			try {
				t.join(30000);
				if (create.driver != null)
					return create.driver;
				logger.warn("Timed out waiting for remote browser session to be created");
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("Creating remote browser failed, all retries exhausted");
	}

	private static ChromeDriverService startChromeService() {
		File chromeDriver;
		if (isWindows())
			chromeDriver = new File("drivers/chrome/win32/chromedriver.exe");
		else if (isMac())
			chromeDriver = new File("drivers/chrome/mac32/chromedriver");
		else
			chromeDriver = new File("drivers/chrome/linux64/chromedriver");
		ChromeDriverService service = ChromeDriverService.createDefaultService();
		try {
			service.start();
		} catch (IOException e) {
			throw new IllegalStateException(String.format("Unable to start chrome driver from location '%s': \n %s",
					chromeDriver.getAbsolutePath(), e.getMessage()));
		}
		registerService(service);
		return extractCurrentService().getResult();
	}

	private InternetExplorerDriverService getIEDriverService() {
		File ieDriver = new File("drivers/internetexplorer/IEDriverServer.exe");
		InternetExplorerDriverService service = InternetExplorerDriverService.createDefaultService();
		try {
			service.start();
		} catch (IOException e) {
			throw new IllegalStateException(String.format("Unable to start IE driver from location '%s': \n %s",
					ieDriver.getAbsolutePath(), e.getMessage()));
		}
		return service;
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	private static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}

	private class CreateWebDriver implements Runnable {

		RemoteWebDriver driver;
		private DesiredCapabilities capabilities;

		public CreateWebDriver(DesiredCapabilities capabilities) {
			this.capabilities = capabilities;
		}

		@Override
		public void run() {
			try {
				driver = new RemoteWebDriver(new URL(ConfigurationService.getConfigInstance().getString(SELENIUM_GRID_ENDPOINT)),
						capabilities);
				driver.setFileDetector(new LocalFileDetector());
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(
						ConfigurationService.getConfigInstance().getString(SELENIUM_GRID_ENDPOINT) + " is not a valid URL", e);
			}
		}
	}
}
