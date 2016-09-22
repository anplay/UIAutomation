package pom.components.iframecomponents;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import pom.components.AbstractFragmentInterface;
import pom.components.AbstractWebComponent;
import pom.components.proxy.IFrameAwareMethodInterceptor;
import pom.components.proxy.ProxifiedFragmentMethodInterceptor;
import pom.components.proxy.ProxyFactory;


public class IFrameFragmentProxy extends AbstractWebComponent implements AbstractFragmentInterface, IFrameAwareInterface {

	private static final String IFRAME_ABSENT_MESSAGE = "Timeout waiting parent iFrame to appear.";
	private IFrame iFrame = ProxyFactory.create(IFrame.class, new IFrameAwareMethodInterceptor());
	private IFrameAwareInterfaceImpl iFrameAwareFragment =
			ProxyFactory.create(IFrameAwareInterfaceImpl.class, new IFrameAwareMethodInterceptor());

	protected void setRootElement(WebElement rootElement) {
		iFrameAwareFragment.setRootElement(rootElement);
	}

	protected void checkIFrameIsSet() {
		if (iFrameAwareFragment.getIFrameRoot() == null)
			throw new IllegalStateException("Native IFrame element is not set for : " + this.getClass().getName());
	}

	@Override
	public void switchToIFrame() {
		iFrameAwareFragment.switchToIFrame();
	}

	@Override
	public void switchToDefaultContent() {
		iFrameAwareFragment.switchToDefaultContent();
	}

	@Override
	public boolean isIFrameVisible() {
		return iFrame.isVisible();
	}

	@Override
	public boolean isSwitchedToIFrame() {
		return iFrameAwareFragment.isSwitchedToIFrame() && DriverContext.isSwitchedToIFrame.get();
	}

	@Override
	public WebElement getIFrameRoot() {
		checkIFrameIsSet();
		return iFrameAwareFragment.getIFrameRoot();
	}

	@Override
	public void setIFrameRoot(WebElement iFrameRoot) {
		iFrameAwareFragment.setIFrameRoot(iFrameRoot);
		iFrame.setRootElement(iFrameRoot);
	}

	@Override
	public boolean isChildElementVisible(By childElement) {
		try {
			return iFrameAwareFragment.isChildElementVisible(childElement);
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public boolean isChildElementPresent(By childElement) {
		try {
			return iFrameAwareFragment.isChildElementPresent(childElement);
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public boolean isVisible() {
		return iFrame.isVisible() && iFrameAwareFragment.isVisible();
	}

	@Override
	public boolean isInvisible() {
		return iFrame.isInvisible() && iFrameAwareFragment.isInvisible();
	}

	@Override
	public boolean isPresent() {
		return iFrame.isPresent() && iFrameAwareFragment.isPresent();
	}

	@Override
	public void waitForAppear() {
		iFrame.waitForAppear();
		iFrameAwareFragment.waitForAppear();
	}

	@Override
	public boolean waitForAppearLazy() {
		return iFrame.waitForAppearLazy() && iFrameAwareFragment.waitForAppearLazy();
	}

	@Override
	public void waitForDisappear() {
		if (!iFrame.isPresent()) {
			return;
		}
		iFrameAwareFragment.waitForDisappear();
	}

	@Override
	public void waitForDisappear(long timeMilliseconds) {
		if (!iFrame.isPresent()) {
			return;
		}
		iFrameAwareFragment.waitForDisappear(timeMilliseconds);
	}

	@Override
	public boolean waitForDisappearLazy() {
		if (iFrame.isPresent()) {
			iFrameAwareFragment.waitForDisappearLazy();
		}
		return true;
	}

	@Override
	public AbstractFragmentInterface waitForShown() {
		iFrame.waitForShown(IFRAME_ABSENT_MESSAGE);
		return iFrameAwareFragment.waitForShown();
	}

	@Override
	public AbstractFragmentInterface waitForShown(long milliseconds) {
		iFrame.waitForShown(milliseconds, IFRAME_ABSENT_MESSAGE);
		return iFrameAwareFragment.waitForShown(milliseconds);
	}

	@Override
	public AbstractFragmentInterface waitForShown(String exceptionMessageInCaseOfFailure) {
		iFrame.waitForShown(IFRAME_ABSENT_MESSAGE);
		return iFrameAwareFragment.waitForShown(exceptionMessageInCaseOfFailure);
	}

	@Override
	public AbstractFragmentInterface waitForShown(long milliseconds, String exceptionMessageInCaseOfFailure) {
		iFrame.waitForShown(milliseconds, IFRAME_ABSENT_MESSAGE);
		return iFrameAwareFragment.waitForShown(milliseconds, exceptionMessageInCaseOfFailure);
	}

	@Override
	public void scrollTo() {
		iFrameAwareFragment.scrollTo();
	}

	@Override
	public AbstractFragmentInterface waitForCurrentFragmentShown() {
		iFrame.waitForShown(IFRAME_ABSENT_MESSAGE);
		return iFrameAwareFragment.waitForCurrentFragmentShown();
	}

	@Override
	public AbstractFragmentInterface waitForHidden() {
		if (iFrame.isVisible()) {
			return iFrameAwareFragment.waitForHidden();
		}
		return this;
	}

	@Override
	public boolean waitForShownLazy(long milliseconds) {
		if (!iFrame.waitForShownLazy(milliseconds)) {
			return false;
		}
		return iFrameAwareFragment.waitForShownLazy(milliseconds);
	}

	@Override
	public boolean waitForShownLazy() {
		if (!iFrame.waitForShownLazy()) {
			return false;
		}
		return iFrameAwareFragment.waitForShownLazy();
	}

	@Override
	public boolean waitForHiddenLazy() {
		if (iFrame.isInvisible()) {
			return true;
		}
		return iFrameAwareFragment.waitForHiddenLazy();
	}

	@Override
	public boolean waitForHiddenLazy(long milliseconds) {
		if (iFrame.isInvisible()) {
			return true;
		}
		return iFrameAwareFragment.waitForHiddenLazy(milliseconds);
	}

	@Override
	public AbstractFragmentInterface mouseOver() {
		return iFrameAwareFragment.mouseOver();
	}

	@Override
	public WebElement getChildElement(By byLocator) {
		return iFrameAwareFragment.getChildElement(byLocator);
	}

	@Override
	public List<WebElement> getChildElements(By byLocator) {
		return iFrameAwareFragment.getChildElements(byLocator);
	}

	@Override
	public boolean hasChildElement(By by) {
		try {
			return iFrameAwareFragment.hasChildElement(by);
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public boolean hasVisibleChildElement(By by) {
		try {
			return iFrameAwareFragment.hasVisibleChildElement(by);
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public IFrameFragmentProxy waitForRefresh() {
		iFrame.waitForRefresh();
		iFrameAwareFragment.waitForRefresh();
		return this;
	}

	public static <T extends IFrameFragmentProxy> T newInstance(Class<T> type) {
		T fragment = ProxyFactory.create(type, new ProxifiedFragmentMethodInterceptor());
		return fragment;
	}

}
