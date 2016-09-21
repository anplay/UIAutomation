package utils.tools;

import static utils.ConfigurationConstants.WEB_ELEMENT_TIMEOUT;
import static utils.WebDriverUtils.getDriver;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.ConfigurationService;
import utils.WebDriverUtils;


public class WebElementWait {

	private static final Logger LOG = LoggerFactory.getLogger(WebElement.class);

	private static final long DEFAULT_TIMEOUT = 30;
	private static final long DEFAULT_TIMEOUT_MILLISECONDS = 60000;
	private static final long DEFAULT_POLLING_MILLISECONDS = 500;
	private static final String DEFAULT_MESSAGE = "ExpectedCondition didn't happen during timeout";

	public void forComplexPageLoad() {
		forDocumentReady();
		forAjaxReadyLazy();
	}

	public void forDocumentReady() {
		ExpectedCondition<Boolean> expectation =
				driver -> WebDriverUtils.executeScript("return document.readyState").equals("complete");
		new WebDriverWait(getDriver(), DEFAULT_TIMEOUT).until(expectation);
	}

	public void forAjaxReadyLazy() {
		ExpectedCondition<Boolean> expectation =
				driver -> WebDriverUtils.executeScript("return jQuery.active").equals("0");
		new WebDriverWait(getDriver(), DEFAULT_TIMEOUT).until(expectation);
	}

	private long getTimeout() {
		return ConfigurationService.getConfigInstance().getLong(WEB_ELEMENT_TIMEOUT, DEFAULT_TIMEOUT_MILLISECONDS);
	}

	public WebElement forAppear(WebElement element) {
		return forAppear(element, getTimeout());
	}

	public WebElement forAppear(WebElement element, long milliseconds) {
		return forConditionStaleSafe(presenceOfElement(element), milliseconds);
	}

	public WebElement forAppear(By byLocator) {
		return forConditionStaleSafe(presenceOfElement(byLocator), getTimeout());
	}

	public WebElement forAppear(By byLocator, long milliseconds) {
		return forConditionStaleSafe(presenceOfElement(byLocator), milliseconds);
	}

	public WebElement forAppear(WebElement element, By by) {
		return forAppear(element, by, getTimeout());
	}

	public WebElement forAppear(WebElement element, By by, long milliseconds) {
		return forConditionStaleSafe(presenceOfElement(element, by), milliseconds);
	}

	public <T> T forConditionStaleSafe(final ExpectedCondition<T> condition) {
		return forConditionStaleSafe(condition, getTimeout());
	}

	public <T> T forConditionStaleSafe(final ExpectedCondition<T> condition, long milliseconds) {
		return forConditionStaleSafe(condition, milliseconds, DEFAULT_POLLING_MILLISECONDS);
	}

	public <T> T forConditionStaleSafe(final ExpectedCondition<T> condition, long milliseconds, long pollingTimeMilliseconds) {
		return forConditionStaleSafe(condition, milliseconds, pollingTimeMilliseconds, DEFAULT_MESSAGE);
	}

	public <T> T forConditionStaleSafe(final ExpectedCondition<T> condition, String errorMessage) {
		return forConditionStaleSafe(condition, getTimeout(), errorMessage);
	}

	public <T> T forConditionStaleSafe(final ExpectedCondition<T> condition, long milliseconds, String errorMessage) {
		return forConditionStaleSafe(condition, milliseconds, DEFAULT_POLLING_MILLISECONDS, errorMessage);
	}

	public <T> T forConditionStaleSafe(final ExpectedCondition<T> userCondition, long milliseconds, long pollingTimeMilliseconds,
			String errorMessage) {
		return newWebDriverWait(milliseconds).pollingEvery(pollingTimeMilliseconds, TimeUnit.MILLISECONDS)
				.ignoring(StaleElementReferenceException.class).withMessage(errorMessage).until(userCondition);
	}

	private ExpectedCondition<WebElement> presenceOfElement(final WebElement element) {
		return driver -> WebElementTools.isPresent(element) ? element : null;
	}

	private ExpectedCondition<WebElement> presenceOfElement(final By by) {
		return driver -> WebElementTools.isPresent(by) ? getDriver().findElement(by) : null;
	}

	private ExpectedCondition<WebElement> presenceOfElement(final WebElement element, final By by) {
		return driver -> WebElementTools.isPresent(element, by) ? element.findElement(by) : null;
	}

	private WebDriverWait newWebDriverWait(long milliseconds) {
		return new WebDriverWait(getDriver(), milliseconds / 1000);
	}

	public boolean forLazyAppear(final WebElement element) {
		return forLazyAppear(element, getTimeout());
	}

	public boolean forLazyAppear(final WebElement element, final long milliseconds) {
		return new AbstractLazyWait() {

			@Override
			void waitCondition() {
				LOG.debug("Waiting for lazy appear {}", element);
				forAppear(element, milliseconds);
			}
		}.isSuccess();
	}

	public void forDisappear(By byLocator) {
		forDisappear(byLocator, getTimeout());
	}

	public void forDisappear(By byLocator, long milliseconds) {
		forDisappear(presenceOfElement(byLocator), milliseconds);
	}

	public void forDisappear(WebElement element) {
		forDisappear(element, getTimeout());
	}

	public void forDisappear(WebElement element, long milliseconds) {
		forDisappear(presenceOfElement(element), milliseconds);
	}

	private void forDisappear(ExpectedCondition<WebElement> condition, long milliseconds) {
		forConditionStaleSafe(ExpectedConditions.not(condition), milliseconds);
	}

	public boolean forLazyDisappear(final WebElement element, final long milliseconds) {
		return new AbstractLazyWait() {

			@Override
			void waitCondition() {
				LOG.debug("Waiting for lazy disappear {}", element);
				forDisappear(element, milliseconds);
			}
		}.isSuccess();
	}

	public WebElement forShown(WebElement element) {
		return forShown(element, getTimeout());
	}

	public WebElement forShown(WebElement element, long milliseconds) {
		return forConditionStaleSafe(ExpectedConditions.visibilityOf(element), milliseconds);
	}

	public WebElement forShown(By locator) {
		return forShown(locator, getTimeout());
	}

	public WebElement forShown(By locator, long milliseconds) {
		return forConditionStaleSafe(ExpectedConditions.visibilityOfElementLocated(locator), milliseconds);
	}

	public WebElement forShown(WebElement parent, By child) {
		return forShown(parent, child, getTimeout());
	}

	public WebElement forShown(WebElement parent, By child, long milliseconds) {
		return forConditionStaleSafe(visibilityOfElement(parent, child), milliseconds);
	}

	public WebElement forShown(WebElement parent, By child, String failureMessage) {
		return forConditionStaleSafe(visibilityOfElement(parent, child), failureMessage);
	}

	private ExpectedCondition<WebElement> visibilityOfElement(final WebElement parent, final By child) {
		return driver -> WebElementTools.isVisible(parent, child) ? parent.findElement(child) : null;
	}

	public boolean forShownLazy(final WebElement element) {
		return forShownLazy(element, getTimeout());
	}

	public boolean forShownLazy(final WebElement element, final long milliseconds) {
		return new AbstractLazyWait() {

			@Override
			void waitCondition() {
				LOG.debug("Waiting for lazy shown {}", element);
				forShown(element, milliseconds);
			}
		}.isSuccess();
	}

	public void forHidden(WebElement element) {
		forHidden(element, getTimeout());
	}

	public void forHidden(WebElement element, long milliseconds) {
		forHidden(ExpectedConditions.visibilityOf(element), milliseconds);
	}

	public void forHidden(By locator) {
		forHidden(locator, getTimeout());
	}

	public void forHidden(By locator, long milliseconds) {
		forHidden(ExpectedConditions.visibilityOfElementLocated(locator), milliseconds);
	}

	public void forHidden(WebElement parent, By child) {
		ExpectedCondition<WebElement> condition = visibilityOfElement(parent, child);
		forHidden(condition, getTimeout());
	}

	public void forHidden(WebElement parent, By child, long milliseconds) {
		ExpectedCondition<WebElement> condition = visibilityOfElement(parent, child);
		forHidden(condition, milliseconds);
	}

	private Boolean forHidden(ExpectedCondition<WebElement> condition, long milliseconds) {
		LOG.debug("Waiting for element became invisible in: " + milliseconds);
		return forConditionStaleSafe(ExpectedConditions.not(condition), milliseconds);
	}

	public boolean forHiddenLazy(final WebElement element) {
		return forHiddenLazy(element, DEFAULT_TIMEOUT_MILLISECONDS);
	}

	public boolean forHiddenLazy(final WebElement element, final long milliseconds) {
		return new AbstractLazyWait() {

			@Override
			void waitCondition() {
				LOG.debug("Waiting for hidden {}", element);
				forHidden(element, milliseconds);
			}
		}.isSuccess();
	}

	public boolean forRefreshLazy(final WebElement element, final long milliseconds) {
		return new AbstractLazyWait() {

			@Override
			void waitCondition() {
				forRefresh(element, milliseconds);
			}
		}.isSuccess();
	}

	public WebElement forRefresh(final By locator, final long milliseconds) {
		return forConditionStaleSafe(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(locator)),
				milliseconds);
	}

	public WebElement forRefresh(final WebElement element, final By locator, final long milliseconds) {
		return forConditionStaleSafe(ExpectedConditions.refreshed(presenceOfElement(element, locator)), milliseconds);
	}

	public WebElement forRefresh(final WebElement element, final long milliseconds) {
		return forConditionStaleSafe(ExpectedConditions.refreshed(presenceOfElement(element)), milliseconds);
	}

	private abstract class AbstractLazyWait {

		abstract void waitCondition();

		boolean isSuccess() {
			try {
				waitCondition();
				return true;
			} catch (@SuppressWarnings("unused") TimeoutException e) {
				LOG.debug(DEFAULT_MESSAGE);
				return false;
			}
		}

	}
}
