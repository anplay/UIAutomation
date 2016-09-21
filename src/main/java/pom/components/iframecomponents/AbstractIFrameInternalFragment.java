package pom.components.iframecomponents;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import utils.StringHelper;
import utils.tools.WebElementTools;


public abstract class AbstractIFrameInternalFragment extends IFrameFragmentProxy {

	protected String getStringValue(By by) {
		return getChildElement(by).getText().trim();
	}

	protected String getNumberValue(By by) {
		return StringHelper.getAmountNumber(getChildElement(by).getText());
	}

	protected void clearAndPopulateField(By by, String value) {
		WebElementTools.clearAndPopulateField(getChildElement(by), value);
	}

	protected void setInputValue(By by, String value) {
		WebElementTools.setInputValue(getChildElement(by), value);
	}

	protected void selectByVisibleText(By by, String value) {
		new Select(getChildElement(by)).selectByVisibleText(value);
	}

	protected void selectByValue(By by, String value) {
		new Select(getChildElement(by)).selectByValue(value);
	}

	protected void click(By by) {
		getChildElement(by).click();
	}

}
