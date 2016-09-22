package pom.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public interface AbstractFragmentInterface {

	boolean isVisible();

	boolean isInvisible();

	boolean isPresent();

	void waitForAppear();

	boolean waitForAppearLazy();

	void waitForDisappear();

	void waitForDisappear(long timeMilliseconds);

	boolean waitForDisappearLazy();

	AbstractFragmentInterface waitForShown();

	AbstractFragmentInterface waitForShown(String exceptionMessageInCaseOfFailure);

	AbstractFragmentInterface waitForShown(long milliseconds);

	AbstractFragmentInterface waitForShown(long milliseconds, String exceptionMessageInCaseOfFailure);

	void scrollTo();

	AbstractFragmentInterface waitForHidden();

	AbstractFragmentInterface waitForRefresh();

	boolean waitForShownLazy(long milliseconds);

	boolean waitForShownLazy();

	boolean waitForHiddenLazy();

	boolean waitForHiddenLazy(long milliseconds);

	AbstractFragmentInterface mouseOver();

	WebElement getChildElement(By byLocator);

	List<WebElement> getChildElements(By byLocator);

	boolean hasChildElement(By by);

	boolean hasVisibleChildElement(By by);

	AbstractFragmentInterface waitForCurrentFragmentShown();

	boolean isChildElementVisible(By childElement);

	boolean isChildElementPresent(By childElement);
}
