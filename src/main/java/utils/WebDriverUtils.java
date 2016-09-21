package utils;

import static utils.ConfigurationConstants.PAGE_LOAD_TIMEOUT;
import static utils.ConfigurationService.getProperty;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.driver.SharedWebDriver;


public class WebDriverUtils {
	private static final Logger LOG = LoggerFactory.getLogger(WebDriverUtils.class);
	private static final int PAGE_LOADING_TIMEOUT = 60;

	private static final String RETURN_SCREEN_WIDTH = "return screen.availWidth;";
	private static final String RETURN_SCREEN_HEIGHT = "return screen.availHeight;";
	private static final int ZERO_COORDINATE = 0;
	private static final int SCREEN_SIZE_ADJUSTMENT_PIXELS = 5;

	private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	private WebDriverUtils() {
	}

	public static WebDriver getDriver() {
		if (isDriverAlive())
			return driver.get();
		try {
			LOG.info("Initializing driver instance...");
			driver.set(new SharedWebDriver());
			maximizePage();
			setPageLoadingTime(ConfigurationService.getConfigInstance().getInt(PAGE_LOAD_TIMEOUT, PAGE_LOADING_TIMEOUT));
			return driver.get();
		} catch (Exception e) {
			LOG.error("Failed to create driver instance.", e);
			throw e;
		}
	}

	public static boolean isDriverAlive() {
		return driver.get() != null;
	}

	public static Object executeScript(String script, Object... elements) {
		return ((JavascriptExecutor) getDriver()).executeScript(script, elements);
	}

	public static String getCurrentWindowHandle() {
		return getDriver().getWindowHandle();
	}

	public static Set<String> getAvailableWindowsHandles() {
		return getDriver().getWindowHandles();
	}

	public static void switchTo(String windowHandler) {
		getDriver().switchTo().window(windowHandler);

	}

	public static void closeWindowByWindowHandle(String windowHandle) {
		getDriver().switchTo().window(windowHandle).close();
	}

	public static void setImplicitWait(int seconds) {
		if (ConfigurationHelper.isImplicitWaitSupported())
			getDriver().manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
	}

	public static void navigateToPage(String pageUrl) {
		getDriver().get(ConfigurationHelper.getWebEndpoint() + pageUrl);
	}

	public static void navigateBrowserTo(String url) {
		getDriver().get(getProperty(url));
	}

	public static void openAbsoluteUrl(final String url) {
		getDriver().get(url);
	}

	public static void refreshPage() {
		if (isDriverAlive()) {
			LOG.info("Driver is alive. Refreshing current page...");
			getDriver().navigate().refresh();
		}
	}

	public static String getCurrentURL() {
		if (isDriverAlive()) {
			LOG.info("Driver is alive. Getting current URL...");
			return getDriver().getCurrentUrl();
		}
		throw new IllegalStateException("Something went wrong. Can't get current URL.");
	}

	public static void navigateBack() {
		if (isDriverAlive()) {
			LOG.info("Driver is alive. Refreshing current page...");
			getDriver().navigate().back();
		}
	}

	public static void switchToIFrame(WebElement iFrame) {
		getDriver().switchTo().frame(iFrame);
	}

	public static void switchToDefaultContent() {
		getDriver().switchTo().defaultContent();
	}

	public static long getScrollPositionInPixels() {
		Long value = (Long) executeScript("return window.scrollY;");
		return value;
	}

	public static void scrollToPageBottom() {
		executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	public static String getCookieValue(String cookieName) {
		Cookie cookie = getDriver().manage().getCookieNamed(cookieName);
		return cookie != null ? cookie.getValue() : StringUtils.EMPTY;
	}

	public static void navigateToInternalSystemPage(String pageUrl) {
		String baseUrl = getProperty(ConfigurationConstants.INTERNAL_SYSTEM_URL);
		getDriver().get(baseUrl + pageUrl);
	}

	/** start private */

	private static void maximizePage() {
		if (ConfigurationHelper.isMaximizePageSupported()) {
			driver.get().manage().window().maximize();
			if (!isWindowMaximized()) {
				int width = getWindowWidth();
				int height = getWindowHeight();
				driver.get().manage().window().setPosition(new Point(ZERO_COORDINATE, ZERO_COORDINATE));
				driver.get().manage().window().setSize(new Dimension(width, height));
			}
		}
	}

	private static int getWindowWidth() {
		JavascriptExecutor js = (JavascriptExecutor) driver.get();
		return ((Long) js.executeScript(RETURN_SCREEN_WIDTH)).intValue();
	}

	private static int getWindowHeight() {
		JavascriptExecutor js = (JavascriptExecutor) driver.get();
		return ((Long) js.executeScript(RETURN_SCREEN_HEIGHT)).intValue();
	}

	private static boolean isWindowMaximized() {
		return driver.get().manage().window().getSize().getWidth() + SCREEN_SIZE_ADJUSTMENT_PIXELS >= getWindowWidth();
	}

	private static void setPageLoadingTime(int seconds) {
		if (ConfigurationHelper.isPageLoadingTimeSupported())
			driver.get().manage().timeouts().pageLoadTimeout(seconds, TimeUnit.SECONDS);
		driver.get().manage().timeouts().setScriptTimeout(seconds, TimeUnit.SECONDS);
	}
}
