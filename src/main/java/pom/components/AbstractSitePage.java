package pom.components;

import static com.codeborne.selenide.Selenide.$;
import static utils.WebDriverUtils.getDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import utils.ConfigurationHelper;
import utils.WebDriverUtils;


public class AbstractSitePage extends AbstractWebComponent {
	private static final String HTTPS = "https://";

	private String urlFormat; // can be regExp
	private String pageUrl;
	private String pageTitle;
	private String bodyClass;

	protected String getUrlFormat() {
		return urlFormat;
	}

	/**
	 * Use this method to provide flexible way to validate page URL. </br>
	 * For example if URL is generic: /path/gen_id000X, use regex pattern /path/.*+ to validate the page.
	 *
	 * @param urlFormat
	 */
	public void setUrlFormat(String urlFormat) {
		this.urlFormat = urlFormat;
	}

	public final boolean hasUrlFormat() {
		return getUrlFormat() != null;
	}

	// relative page URL to web root property
	protected String getPageUrl() {
		if (pageUrl == null)
			throw new IllegalStateException(
					"Please set page URL using appropriate method ( setPageUrl(String pageUrl) ). Page URL must not be null.");
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getPageTitle() {
		if (StringUtils.isEmpty(pageTitle))
			return StringUtils.EMPTY;
		return pageTitle;
	}

	public void setPageName(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	protected String getBodyClass() {
		if (StringUtils.isEmpty(bodyClass))
			return StringUtils.EMPTY;
		return bodyClass;
	}

	protected void setBodyClass(String bodyClass) {
		this.bodyClass = bodyClass;
	}

	public boolean isCurrent() {
		return new PageChecker().isCurrent();
	}

	public boolean isCurrentWithTimeout(long timeout) {
		final long expiration = System.currentTimeMillis() + timeout;
		while (System.currentTimeMillis() < expiration)
			if (isCurrent())
				return true;
		return false;
	}

	public void check() {
		new PageChecker().check();
	}

	public String getMetaValue(String key) {
		WebElement metaElement = getDriver().findElement(By.cssSelector(String.format("head meta[name=%s]", key)));
		return metaElement.getAttribute("content");
	}

	public String getCanonicalLink() {
		WebElement metaElement = getDriver().findElement(By.cssSelector("link[rel='canonical']"));
		return metaElement.getAttribute("href");
	}

	protected WebElement getPageBody() {
		return $(By.cssSelector("body"));
	}

	/** AbstractPage */
	public String getCurrentPageTitle() {
		String actualTitle = getDriver().getTitle();
		if (actualTitle == null)
			return StringUtils.EMPTY;
		return actualTitle;
	}

	public String getCurrentPageUrl() {
		return getDriver().getCurrentUrl();
	}

	public boolean isSecure() {
		return getCurrentPageUrl().startsWith(HTTPS);
	}

	private String removeJSessionId(String path) {
		if (path.contains(";jsessionid="))
			return path.substring(0, path.indexOf(";jsessionid=") + 1);
		return path;
	}

	public void visit() {
		final String pageUrl = getEndpoint() + getPageUrl();
		LOG.info("Getting page from " + pageUrl + " / " + getDriver());
		try {
			getDriver().get(pageUrl);
		} catch (TimeoutException e) {
			LOG.warn(String.format("Timeout loading %s by URL: '%s'\n", getSimpleName(), getPageUrl() + e.getMessage()));
			if (isInteractive()) {
				check();
				return;
			}
			throw e;
		}
	}

	public void refresh() {
		getDriver().navigate().refresh();
	}

	public String getPageSource() {
		return getDriver().getPageSource();
	}

	protected boolean isInteractive() {
		return getDocumentReadyState().equals("interactive");
	}

	private String getDocumentReadyState() {
		return (String) WebDriverUtils.executeScript("return document.readyState");
	}

	public String getAlertMessage() {
		wait.forConditionStaleSafe(ExpectedConditions.alertIsPresent(), DEFAULT_TIME_TO_WAIT);
		return getDriver().switchTo().alert().getText();
	}

	public void acceptAlert() {
		wait.forConditionStaleSafe(ExpectedConditions.alertIsPresent(), DEFAULT_TIME_TO_WAIT);
		getDriver().switchTo().alert().accept();
	}

	protected String getEndpoint() {
		return ConfigurationHelper.getWebEndpoint();
	}

	private class PageChecker {

		private String currentUrl;
		private String currentBody;
		private String currentTitle;

		private PageChecker() {
			wait.forComplexPageLoad();
		}

		public void check() {
			checkUrl();
			checkBodyClass();
			checkPageTitle();
		}

		public boolean isCurrent() {
			return isCurrentByUrl() && isCurrentByBodyClass() && isCurrentByTitle();
		}

		private void checkUrl() {
			if (!this.isCurrentByUrl()) {
				String format = "Invalid page URL: %s, valid format: %s";
				String expected = hasUrlFormat() ? getUrlFormat() : getPageUrl();
				String message = String.format(format, currentUrl, expected);
				throw new IllegalStateException(message);
			}
		}

		private boolean isCurrentByUrl() {
			currentUrl = getCurrentPageUrl();
			LOG.info(String.format("Checking page [%s]: %s", getSimpleName(), currentUrl));
			boolean isCurrent = false;
			if (hasUrlFormat()) {
				LOG.info("By URL format: " + getUrlFormat());
				isCurrent = Pattern.compile(getUrlFormat()).matcher(currentUrl).find();
			} else {
				LOG.info("By URL path: " + getPageUrl());
				URL actual = null;
				URL expected = null;
				try {
					actual = new URL(currentUrl);
					expected = new URL("http://any.com:80" + getPageUrl());
				} catch (MalformedURLException e) {
					LOG.warn("Malformed URL: " + currentUrl, e);
					return false;
				}
				final String actualPath = removeJSessionId(actual.getPath());
				final String expectedPath = expected.getPath();
				// actual path should start with expected but actual may contain '/' character
				isCurrent = actualPath.startsWith(expectedPath) && (actualPath.length() - expectedPath.length() <= 1);
			}
			LOG.info(currentUrl);
			LOG.info(String.format("Is [%s] page: [%b]", getSimpleName(), isCurrent));
			return isCurrent;
		}

		private void checkBodyClass() {
			if (!this.isCurrentByBodyClass())
				throw new IllegalStateException(String.format(
						"Page body class mismatch. \nPage with name [%s] is expected. "
								+ "\nExpected page body class should contain: [%s]. "
								+ "\nActual page body class: [%s]. \nPage URL: [%s]",
						getSimpleName(), getBodyClass(), currentBody, currentUrl));
		}

		private boolean isCurrentByBodyClass() {
			if (StringUtils.isNotEmpty(getBodyClass())) {
				currentBody = getBodyTagClassAttribute();
				return currentBody.contains(getBodyClass());
			}
			return true;
		}

		private String getBodyTagClassAttribute() {
			String bodyClass;
			try {
				bodyClass = getBodyTag().getAttribute("class");
			} catch (@SuppressWarnings("unused") StaleElementReferenceException e) {
				bodyClass = getBodyTag().getAttribute("class");
			}
			if (bodyClass == null)
				throw new IllegalStateException(
						String.format("Tag 'body' of this page doesn't have attribute 'class'. \nPage with name [%s] is "
								+ "expected. \nActual page URL: [%s].", getSimpleName(), currentUrl));
			return bodyClass;
		}

		private WebElement getBodyTag() {
			try {
				return getDriver().findElement(By.tagName("body"));
			} catch (@SuppressWarnings("unused") NoSuchElementException e) {
				throw new IllegalStateException(String.format(
						"This page doesn't have tag 'body'.\nPage with name [%s] is expected.\nActual page URL: [%s].",
						getSimpleName(), currentUrl));
			}
		}

		private void checkPageTitle() {
			if (!isCurrentByTitle())
				throw new IllegalStateException(String.format(
						"Page title mismatch. \nExpected page title: [%s]. \nActual page title: [%s]. \nActual page URL: [%s]",
						getPageTitle(), currentTitle, currentUrl));
		}

		private boolean isCurrentByTitle() {
			if (StringUtils.isNotEmpty(getPageTitle())) {
				currentTitle = getCurrentPageTitle();
				return currentTitle.equals(getPageTitle());
			}
			return true;
		}
	}
}
