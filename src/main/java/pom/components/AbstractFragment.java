package pom.components;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import utils.tools.WebElementTools;


public class AbstractFragment extends AbstractWebComponent implements AbstractFragmentInterface {

	protected static final long TIME_TO_WAIT = 5000;
	private static final String EXCEPTION_MESSAGE_FOR_FRAGMENT = "Fragment %s is not visible";

	private SelenideElement rootElement;

	protected SelenideElement getRootElement() {
		return rootElement;
	}

	protected void setRootElement(SelenideElement rootElement) {
		this.rootElement = rootElement;
	}

	@Override
	public boolean isVisible() {
		checkRoot();
		return WebElementTools.isVisible(getRootElement());
	}

	@Override
	public boolean isInvisible() {
		return !isVisible();
	}

	@Override
	public boolean isPresent() {
		checkRoot();
		return WebElementTools.isPresent(getRootElement());
	}

	@Override
	public void waitForAppear() {
		wait.forAppear(getRootElement());
	}

	@Override
	public boolean waitForAppearLazy() {
		return wait.forLazyAppear(getRootElement());
	}

	@Override
	public void waitForDisappear() {
		waitForDisappear(DEFAULT_TIME_TO_WAIT);
	}

	@Override
	public void waitForDisappear(long timeMilliseconds) {
		wait.forDisappear(getRootElement(), timeMilliseconds);
	}

	@Override
	public boolean waitForDisappearLazy() {
		return wait.forLazyDisappear(getRootElement(), 3000);
	}

	@Override
	public AbstractFragmentInterface waitForShown() {
		waitForShown(DEFAULT_TIME_TO_WAIT);
		return this;
	}

	@Override
	public AbstractFragmentInterface waitForShown(long milliseconds) {
		waitForAppear();
		wait.forShown(getRootElement(), milliseconds);
		return this;
	}

	@Override
	public AbstractFragmentInterface waitForShown(String exceptionMessageInCaseOfFailure) {
		try {
			return waitForShown();
		} catch (TimeoutException e) {
			throw new IllegalStateException(exceptionMessageInCaseOfFailure, e);
		}
	}

	@Override
	public AbstractFragmentInterface waitForShown(long milliseconds, String exceptionMessageInCaseOfFailure) {
		try {
			return waitForShown(milliseconds);
		} catch (TimeoutException | NoSuchElementException e) {
			throw new IllegalStateException(exceptionMessageInCaseOfFailure, e);
		}
	}

	@Override
	public void scrollTo() {
		WebElementTools.scrollTo(getRootElement());
	}

	@Override
	public AbstractFragmentInterface waitForCurrentFragmentShown() {
		return waitForShown(TIME_TO_WAIT, String.format(EXCEPTION_MESSAGE_FOR_FRAGMENT, this.getClass().getSimpleName()));
	}

	@Override
	public AbstractFragmentInterface waitForHidden() {
		$(getRootElement()).shouldBe(hidden);
		return this;
	}

	@Override
	public boolean waitForShownLazy(long milliseconds) {
		boolean appear = waitForAppearLazy();
		if (appear)
			return wait.forShownLazy(getRootElement(), milliseconds);
		return appear;
	}

	@Override
	public boolean waitForShownLazy() {
		return waitForShownLazy(30000);
	}

	@Override
	public boolean waitForHiddenLazy() {
		return wait.forHiddenLazy(getRootElement());
	}

	@Override
	public boolean waitForHiddenLazy(long milliseconds) {
		return wait.forHiddenLazy(getRootElement(), milliseconds);
	}

	@Override
	public AbstractFragmentInterface mouseOver() {
		WebElementTools.scrollAndMouseOver(getRootElement());
		return this;
	}

	@Override
	public SelenideElement getChildElement(By byLocator) {
		return $(getRootElement()).find(byLocator);
	}

	@Override
	public ElementsCollection getChildElements(By byLocator) {
		return $(getRootElement()).findAll(byLocator);
	}

	@Override
	public boolean hasChildElement(By by) {
		return $(getChildElement(by)).exists();
	}

	@Override
	public boolean hasVisibleChildElement(By by) {
		WebElement child = getChildElement(by);
		return $(child).exists() && $(child).is(visible);
	}

	@Override
	public AbstractFragment waitForRefresh() {
		wait.forRefreshLazy(getRootElement(), 2000);
		try {
			return this.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("Unable to initialize instance of fragment: \n" + e.getMessage());
		}
	}

	private void checkRoot() {
		if (getRootElement() == null)
			throw new IllegalStateException("Root element is not specified for this component: " + this.getClass().getName());
	}

	protected void selectIfNotBlank(WebElement select, String value) {
		if (!StringUtils.isBlank(value))
			$(select).selectOption(value);
	}

	protected void setIfNotBlank(WebElement element, String value) {
		$(element).waitUntil(Condition.enabled, DEFAULT_TIME_TO_WAIT);
		if (!StringUtils.isBlank(value))
			$(element).setValue(value);
	}

	@Override
	public boolean isChildElementVisible(By childElement) {
		return isVisible() && wait.forShownLazy(getChildElement(childElement), TIME_TO_WAIT);
	}

	@Override
	public boolean isChildElementPresent(By childElement) {
		return isPresent() && wait.forLazyAppear(getChildElement(childElement), TIME_TO_WAIT);
	}
}
