package utils.driver;

import static utils.ConfigurationConstants.APPIUM_DEVICE;
import static utils.ConfigurationConstants.APPIUM_IOS_VERSION;
import static utils.ConfigurationConstants.SELENIUM_BROWSER;
import static utils.ConfigurationConstants.SELENIUM_BROWSER_ACCEPT_UNTRUSTED_SSL_CERTS;
import static utils.ConfigurationConstants.SELENIUM_BROWSER_VERSION;
import static utils.ConfigurationConstants.SELENIUM_CHROME_MOBILE_EMULATION_DEVICE;
import static utils.ConfigurationConstants.SELENIUM_LOGGING_ENABLED;
import static utils.ConfigurationHelper.getLoggingPreferences;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import utils.ConfigurationHelper;
import utils.ConfigurationService;

public class DesiredCapabilitiesHelper {

	private DesiredCapabilities capabilities;

	public DesiredCapabilities getCapabilities() {
		switch (ConfigurationHelper.getBrowser()) {
			case ANDROID:
				capabilities = DesiredCapabilities.android();
				break;
			case CHROME:
				capabilities = DesiredCapabilities.chrome();
				capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				break;
			case CHROME_MOBILE:
				capabilities = DesiredCapabilities.chrome();
				capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				Map<String, String> mobileEmulation = new HashMap<String, String>();
				mobileEmulation.put("deviceName", ConfigurationService.getProperty(SELENIUM_CHROME_MOBILE_EMULATION_DEVICE));
				Map<String, Object> chromeOptions = new HashMap<String, Object>();
				chromeOptions.put("mobileEmulation", mobileEmulation);
				capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
				break;
			case FIREFOX:
				capabilities = DesiredCapabilities.firefox();
				break;
			case IE:
				capabilities = DesiredCapabilities.internetExplorer();
				break;
			case IOS:
				capabilities = new DesiredCapabilities();
				capabilities.setCapability("browserName", "Safari");
				capabilities.setCapability("deviceName", ConfigurationService.getProperty(APPIUM_DEVICE));
				capabilities.setCapability("platformVersion", ConfigurationService.getProperty(APPIUM_IOS_VERSION));
				break;
			case SAFARI:
				capabilities = DesiredCapabilities.safari();
				break;
			default:
				throw new IllegalArgumentException(
						ConfigurationService.getProperty(SELENIUM_BROWSER) + " is not a supported browser type");
		}
		setBrowserVersion();
		setAcceptSSLCerts();
		setLoggingPreferences();
		return capabilities;
	}

	private void setAcceptSSLCerts() {
		if (ConfigurationService.getConfigInstance().getBoolean(SELENIUM_BROWSER_ACCEPT_UNTRUSTED_SSL_CERTS, false))
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	}

	private void setBrowserVersion() {
		if (ConfigurationService.getConfigInstance().containsKey(SELENIUM_BROWSER_VERSION))
			capabilities.setVersion(ConfigurationService.getProperty(SELENIUM_BROWSER_VERSION));
	}

	private void setLoggingPreferences() {
		if (ConfigurationService.getConfigInstance().getBoolean(SELENIUM_LOGGING_ENABLED, false)) {
			LoggingPreferences loggingPreferences = getLoggingPreferences();
			if (!loggingPreferences.getEnabledLogTypes().isEmpty())
				capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
		}
	}
}
