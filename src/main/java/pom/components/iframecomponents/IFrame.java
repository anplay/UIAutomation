package pom.components.iframecomponents;

import org.openqa.selenium.WebElement;

import utils.WebDriverUtils;


public class IFrame extends IFrameAwareInterfaceImpl {

	@Override
	public void switchToIFrame() {
		if (!isSwitchedToIFrame) {
			WebDriverUtils.getDriver().switchTo().defaultContent();
			isSwitchedToIFrame = true;
		}
	}

	@Override
	public void switchToDefaultContent() {
		WebDriverUtils.getDriver().switchTo().defaultContent();
		isSwitchedToIFrame = false;
	}

	@Override
	public boolean isSwitchedToIFrame() {
		return isSwitchedToIFrame && !DriverContext.isSwitchedToIFrame();
	}

	@Override
	public WebElement getIFrameRoot() {
		checkIFrameIsSet();
		return getRootElement();
	}

	@Override
	public void setIFrameRoot(WebElement iFrame) {
		//No implementation since the iFrame root is the same as FragmentRoot here.
	}

}
