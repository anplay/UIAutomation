package utils;

public class ConfigurationConstants {
	/** KEYS */
	public static final String SELENIUM_USE_GRID = "selenium.use.grid";
	public static final String SELENIUM_GRID_ENDPOINT = "selenium.grid.endpoint";
	public static final String SELENIUM_LOGGING_ENABLED = "selenium.logging.enabled";
	public static final String SELENIUM_LOGGING_BROWSER_LEVEL = "selenium.logging.browser.level";
	public static final String SELENIUM_LOGGING_CLIENT_LEVEL = "selenium.logging.client.level";
	public static final String SELENIUM_LOGGING_DRIVER_LEVEL = "selenium.logging.driver.level";
	public static final String SELENIUM_LOGGING_PERFORMANCE_LEVEL = "selenium.logging.performance.level";
	public static final String SELENIUM_LOGGING_SERVER_LEVEL = "selenium.logging.server.level";
	public static final String SELENIUM_CHROME_VERBOSE_MODE = "selenium.chrome.verbose.mode";
	public static final String SELENIUM_CHROME_MOBILE_EMULATION_DEVICE = "selenium.chrome.mobile.emulation.device";
	public static final String SELENIUM_FIREFOX_BIN_LOCATION = "selenium.firefox.bin.location";
	public static final String SELENIUM_BROWSER = "selenium.browser";
	public static final String SELENIUM_BROWSER_VERSION = "selenium.browser.version";
	public static final String SELENIUM_BROWSER_ACCEPT_UNTRUSTED_SSL_CERTS = "selenium.browser.accept.untrusted.ssl.certs";
	public static final String APPIUM_DEVICE = "appium.device";
	public static final String APPIUM_IOS_VERSION = "appium.ios.version";
	public static final String WEBSITE_URL = "website.url";
	public static final String SECURE_WEBSITE_URL = "website.url.secure";
	public static final String INTERNAL_SYSTEM_URL = "internal.systems.url";
	public static final String INTERNAL_SYSTEM_SECURE_URL = "internal.systems.url.secure";
	public static final String INTERNAL_SYSTEM_NODE_URL = "internal.systems.node.url";
	public static final String INTERNAL_SYSTEM_NODE_COUNT = "internal.systems.node.count";
	public static final String REST_WS_URL = "rest.ws.url";
	public static final String REST_URL = "rest.url";
	public static final String REST_URL_V2 = "rest.url.v2";
	public static final String REST_IMPEX_URL = "rest.impex.url";
	public static final String WEB_ELEMENT_TIMEOUT = "webelement.wait.timeout";
	public static final String PAGE_LOAD_TIMEOUT = "page.load.timeout";

	/** VALUES */
	public enum Browser {

		ANDROID("android"), CHROME("chrome"), CHROME_MOBILE("chrome-mobile"), FIREFOX("firefox"), IE("ie"), IOS("ios"),
		SAFARI("safari");

		private final String name;

		private Browser(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}
}
