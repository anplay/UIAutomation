package utils;

import static utils.ConfigurationConstants.Browser;
import static utils.ConfigurationConstants.INTERNAL_SYSTEM_NODE_COUNT;
import static utils.ConfigurationConstants.INTERNAL_SYSTEM_NODE_URL;
import static utils.ConfigurationConstants.INTERNAL_SYSTEM_SECURE_URL;
import static utils.ConfigurationConstants.INTERNAL_SYSTEM_URL;
import static utils.ConfigurationConstants.REST_URL;
import static utils.ConfigurationConstants.SECURE_WEBSITE_URL;
import static utils.ConfigurationConstants.SELENIUM_BROWSER;
import static utils.ConfigurationConstants.SELENIUM_LOGGING_BROWSER_LEVEL;
import static utils.ConfigurationConstants.SELENIUM_LOGGING_CLIENT_LEVEL;
import static utils.ConfigurationConstants.SELENIUM_LOGGING_DRIVER_LEVEL;
import static utils.ConfigurationConstants.SELENIUM_LOGGING_PERFORMANCE_LEVEL;
import static utils.ConfigurationConstants.SELENIUM_LOGGING_SERVER_LEVEL;
import static utils.ConfigurationConstants.WEBSITE_URL;
import static utils.ConfigurationService.getProperty;

import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigurationHelper {
	private final static Logger LOG = LoggerFactory.getLogger(ConfigurationHelper.class);

	private ConfigurationHelper() {

	}

	public static String getRestUrl() {
		return getProperty(REST_URL);
	}

	public static String getRestImpexUrl() {
		String restImpexURL = ConfigurationService.getConfigInstance().getString(ConfigurationConstants.REST_IMPEX_URL);
		if (restImpexURL == null) {
			String defaultImpexRestPath = "/impex";
			return getRestUrl() + defaultImpexRestPath;
		} else
			return restImpexURL;
	}

	public static String getWebEndpoint() {
		return getProperty(WEBSITE_URL);
	}

	public static String getSecureWebEndpoint() {
		return getProperty(SECURE_WEBSITE_URL);
	}

	public static String getSecureInternalSystemsEndpoint() {
		return getProperty(INTERNAL_SYSTEM_SECURE_URL);
	}

	public static String getInternalSystemsEndpointNode() {
		return getProperty(INTERNAL_SYSTEM_NODE_URL);
	}

	public static String getInternalSystemsNodeCount() {
		return getProperty(INTERNAL_SYSTEM_NODE_COUNT);
	}

	public static String getInternalSystemsEndpoint() {
		return getProperty(INTERNAL_SYSTEM_URL);
	}

	public static Browser getBrowser() {
		String value = getProperty(SELENIUM_BROWSER);
		for (Browser browser : Browser.values())
			if (value.equalsIgnoreCase(browser.getName()))
				return browser;
		throw new IllegalStateException(String.format("'%s' is not a valid value of Enum 'Browser'", value));
	}

	public static LoggingPreferences getLoggingPreferences() {
		LoggingPreferences loggingPreferences = new LoggingPreferences();
		Level browserLogLevel = getLevel(getProperty(SELENIUM_LOGGING_BROWSER_LEVEL));
		if (browserLogLevel != Level.OFF)
			loggingPreferences.enable(LogType.BROWSER, browserLogLevel);
		Level clientLogLevel = getLevel(getProperty(SELENIUM_LOGGING_CLIENT_LEVEL));
		if (clientLogLevel != Level.OFF)
			loggingPreferences.enable(LogType.CLIENT, clientLogLevel);
		Level driverLogLevel = getLevel(getProperty(SELENIUM_LOGGING_DRIVER_LEVEL));
		if (driverLogLevel != Level.OFF)
			loggingPreferences.enable(LogType.DRIVER, driverLogLevel);
		Level performanceLogLevel = getLevel(getProperty(SELENIUM_LOGGING_PERFORMANCE_LEVEL));
		if (performanceLogLevel != Level.OFF)
			loggingPreferences.enable(LogType.PERFORMANCE, performanceLogLevel);
		Level serverLogLevel = getLevel(getProperty(SELENIUM_LOGGING_SERVER_LEVEL));
		if (serverLogLevel != Level.OFF)
			loggingPreferences.enable(LogType.SERVER, serverLogLevel);
		return loggingPreferences;
	}

	private static Level getLevel(String level) {
		if (!level.equals(StringUtils.EMPTY))
			return Level.parse(level.toUpperCase());
		return Level.OFF;
	}

	public static boolean isMaximizePageSupported() {
		return !isMobile();
	}

	public static boolean isPageLoadingTimeSupported() {
		return !isMobile() && !isSafari();
	}

	public static boolean isImplicitWaitSupported() {
		return !isSelendroid();
	}

	public static boolean areTouchActionsSupported() {
		return isMobile();
	}

	private static boolean isMobile() {
		return isSelendroid() || isAppium();
	}

	private static boolean isSelendroid() {
		return getBrowser() == Browser.ANDROID;
	}

	private static boolean isAppium() {
		return getBrowser() == Browser.IOS;
	}

	private static boolean isSafari() {
		return getBrowser() == Browser.SAFARI;
	}
}
