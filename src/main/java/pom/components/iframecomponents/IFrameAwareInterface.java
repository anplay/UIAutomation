package pom.components.iframecomponents;

import org.openqa.selenium.WebElement;


public interface IFrameAwareInterface {

	void switchToIFrame();

	void switchToDefaultContent();

	WebElement getIFrameRoot();

	void setIFrameRoot(WebElement iFrame);

	boolean isIFrameVisible();

	boolean isSwitchedToIFrame();

}
