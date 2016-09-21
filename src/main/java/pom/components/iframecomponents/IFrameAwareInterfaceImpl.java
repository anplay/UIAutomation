package pom.components.iframecomponents;

import org.openqa.selenium.WebElement;

import pom.components.AbstractFragment;
import utils.WebDriverUtils;
import utils.tools.WebElementTools;


public class IFrameAwareInterfaceImpl extends AbstractFragment implements IFrameAwareInterface {

	protected boolean isSwitchedToIFrame;
	private WebElement iFrameRoot;

	@Override
	protected void setRootElement(WebElement rootElement) {
		super.setRootElement(rootElement);
	}

	@Override
	public void switchToIFrame() {
		if (!isSwitchedToIFrame) {
			if (DriverContext.isSwitchedToIFrame()) {
				switchToDefaultContent();
			}
			checkNativeFrameVisibility();
			WebDriverUtils.getDriver().switchTo().frame(getIFrameRoot());
			isSwitchedToIFrame = true;
		}

	}

	@Override
	public void switchToDefaultContent() {
		WebDriverUtils.getDriver().switchTo().defaultContent();
		isSwitchedToIFrame = false;
	}

	@Override
	public boolean isIFrameVisible() {
		return WebElementTools.isVisible(getIFrameRoot());
	}

	@Override
	public boolean isSwitchedToIFrame() {
		return isSwitchedToIFrame && DriverContext.isSwitchedToIFrame.get();
	}

	@Override
	public WebElement getIFrameRoot() {
		checkIFrameIsSet();
		return iFrameRoot;
	}

	@Override
	public void setIFrameRoot(WebElement iFrame) {
		this.iFrameRoot = iFrame;
	}

	protected void checkIFrameIsSet() {
		if (iFrameRoot == null)
			throw new IllegalStateException("Native IFrame element is not set for : " + this.getClass().getSimpleName());
	}

	protected void checkNativeFrameVisibility() {
		if (!isIFrameVisible()) {
			throw new IllegalStateException(String.format("Unable to get internal elements: parent iFrame is not visible for %s.",
					this.getClass().getSimpleName()));
		}
	}

}
