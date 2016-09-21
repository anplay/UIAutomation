package utils.tools;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static org.openqa.selenium.Keys.CONTROL;
import static utils.WebDriverUtils.executeScript;
import static utils.WebDriverUtils.getDriver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import utils.UrlUtil;


public class WebElementTools {
	private static final String A_KEY = "a";
	private static final String C_KEY = "c";
	private static final String V_KEY = "v";

	private static final String TARGET_ATTRIBUTE = "target";
	private static final String TARGET_ATTRIBUTE_VALUE_BLANK = "_blank";
	private static final String HREF_ATTRIBUTE = "href";
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String SRC_ATTRIBUTE = "src";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String PLACEHOLDER = "placeholder";

	private static final String VALUE = "value";

	private WebElementTools() {
	}

	/** COMMON */
	public static boolean isVisible(WebElement element) {
		return $(element).isDisplayed();
	}

	public static boolean isVisible(By by) {
		return $(by).isDisplayed();
	}

	public static boolean isVisible(WebElement parentElement, By by) {
		return $(parentElement, by).isDisplayed();
	}

	public static boolean isPresent(WebElement element) {
		try {
			element.isDisplayed();
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public static boolean isPresent(By by) {
		try {
			getDriver().findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public static boolean isPresent(WebElement parentElement, By by) {
		try {
			return isPresent(parentElement) && isPresent(parentElement.findElement(by));
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public static List<String> getElemsTexts(List<WebElement> elements) {
		return elements.stream().map(element -> getElementText(element)).collect(Collectors.toList());
	}

	public static String getElementText(WebElement element) {
		return element.getText().trim();
	}

	public static WebElement getFirstElement(List<WebElement> list) {
		return list.get(0);
	}

	public static WebElement getElementByText(List<WebElement> webElementList, final String text) {
		return webElementList.stream().filter(element -> element.getText().trim().equals(text.trim())).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unable to find element with text: " + text));
	}

	public static List<WebElement> getElementsByText(List<WebElement> webElementList, final String text) {
		return webElementList.parallelStream().filter(element -> element.getText().equals(text.trim()))
				.collect(Collectors.toList());
	}

	public static WebElement getParentElement(WebElement element) {
		return element.findElement(By.xpath("./parent::*"));
	}

	public static List<WebElement> filterVisibleElements(List<WebElement> elements) {
		return newArrayList(filter(elements, WebElementTools::isVisible));
	}

	/** ATTRIBUTE */
	public static boolean isElementHasAttribute(WebElement element, String attributeName) {
		return element.getAttribute(attributeName) != null;
	}

	public static String getElementClass(WebElement element) {
		return element.getAttribute(CLASS_ATTRIBUTE);
	}

	public static String getInputFieldValue(WebElement element) {
		return element.getAttribute(VALUE);
	}

	public static String getSrcAttribute(WebElement element) {
		return element.getAttribute(SRC_ATTRIBUTE);
	}

	public static String getPlaceHolderAttributeText(WebElement element) {
		return element.getAttribute(PLACEHOLDER).trim();
	}

	public static boolean isTargetAttributeHasBlankValue(WebElement element) {
		return isElementHasAttribute(element, TARGET_ATTRIBUTE)
				&& element.getAttribute(TARGET_ATTRIBUTE).equals(TARGET_ATTRIBUTE_VALUE_BLANK);
	}

	public static String getElementHref(WebElement element) {
		try {
			return new URI(element.getAttribute(HREF_ATTRIBUTE).trim()).getPath();
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	public static List<String> getImageUrls(By imagesLocator) {
		List<String> imageUrls = new ArrayList<>();
		ElementsCollection $$ = $$(imagesLocator);
		for (WebElement webElement : $$) {
			String absoluteURL = $(webElement).getAttribute(SRC_ATTRIBUTE);
			imageUrls.add(UrlUtil.getRelativeUrl(absoluteURL));
		}
		return imageUrls;
	}

	/** CHECKBOX */
	public static boolean isChecked(WebElement element) {
		return isVisible(element) && element.isSelected();
	}

	public static void check(WebElement checkbox) {
		if (!isChecked(checkbox))
			checkbox.click();
	}

	public static void uncheck(WebElement checkbox) {
		if (isChecked(checkbox))
			checkbox.click();
	}

	/** INPUT */
	public static void clearAndPopulateField(WebElement field, String text) {
		$(field).setValue(text);
	}

	public static void typeToFieldAndCopyValueToClipboard(WebElement field, String valueForCopyToClipboard) {
		clearAndPopulateField(field, valueForCopyToClipboard);
		field.sendKeys(Keys.chord(Keys.CONTROL, A_KEY));
		field.sendKeys(Keys.chord(Keys.CONTROL, C_KEY));
	}

	public static void clearFieldAndPasteValueFromClipboard(WebElement field) {
		field.clear();
		field.sendKeys(Keys.chord(Keys.CONTROL + V_KEY));
	}

	/** SELECT */

	public static Select newSelect(WebElement selectElement) {
		return new Select(selectElement);
	}

	public static List<String> getSelectOptionText(WebElement element) {
		List<String> values = newArrayList();
		if (isVisible(element)) {
			List<WebElement> options = new Select(element).getOptions();
			values.addAll(options.stream().map(WebElement::getText).collect(Collectors.toList()));
		}
		return values;
	}

	public static String getSelectedOption(WebElement selectElement) {
		return new Select(selectElement).getFirstSelectedOption().getText();
	}

	public static List<String> getSelectOptions(WebElement selectElement) {
		return new Select(selectElement).getOptions().stream().map(WebElement::getText).collect(Collectors.toList());
	}

	public static void selectDropDownValueByText(WebElement dropdown, String text) {
		List<WebElement> options = new Select(dropdown).getOptions();
		for (WebElement el : options)
			if (text != null && text.equals(el.getText())) {
				el.click();
				return;
			}
	}

	public static void selectDropDownValueByRowNumber(WebElement dropdown, int rowNumber) {
		new Select(dropdown).getOptions().get(rowNumber).click();
	}

	public static void selectIfNotBlank(WebElement select, String value) {
		if (!StringUtils.isBlank(value))
			$(select).selectOption(value);
	}

	public static void setIfNotBlank(WebElement element, String value) {
		$(element).waitUntil(Condition.enabled, 15000);
		if (!StringUtils.isBlank(value))
			$(element).setValue(value);
	}

	/** ACTIONS */
	public static void mouseOver(WebElement element) {
		new WebElementWait().forDocumentReady();
		newActions().moveToElement(element).build().perform();
	}

	public static void scrollAndMouseOver(WebElement element) {
		scrollTo(element);
		newActions().moveToElement(element).build().perform();
	}

	public static void mouseOverAndClick(WebElement element) {
		newActions().moveToElement(element).click().perform();
	}

	public static void holdCtrlKey() {
		newActions().keyDown(CONTROL).build().perform();
	}

	public static void releaseCtrlKey() {
		newActions().keyUp(CONTROL).build().perform();
	}

	private static Actions newActions() {
		return new Actions(getDriver());
	}

	/** JS */
	public static void scrollTo(WebElement element) {
		int x = element.getLocation().x;
		int y = element.getLocation().y;
		scrollTo(x, y);
	}

	public static void scrollTo(int x, int y) {
		executeScript(String.format("window.scrollTo(%d,%d)", x, y), "");
	}

	public static void setInputValue(WebElement element, String value) {
		String elementId = element.getAttribute("id");
		executeScript("document.getElementById(arguments[0]).value = arguments[1]", elementId, value);
	}

	public static void forceHoverForElement(String cssLocator) {
		String script = "var element = document.querySelector(" + cssLocator + ");" + "if( document.createEvent ) { "
				+ "var eventObject = document.createEvent('MouseEvents');" + "eventObject.initEvent( 'mouseover', true, false );"
				+ "element.dispatchEvent(eventObject);" + "} else if( document.createEventObject ) {"
				+ "element.fireEvent('onmouseover');" + "};";
		executeScript(script);
	}

	public static void hoverElementByJS(WebElement element) {
		String script = "var evObj = document.createEvent('MouseEvents');"
				+ "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
				+ "arguments[0].dispatchEvent(evObj);";
		executeScript(script, element);

	}

	public static void forceChangedForElement(WebElement element) {
		String script = "var element = document.getElementById(arguments[0]);" + "if( document.createEvent ) { "
				+ "var eventObject = document.createEvent('HTMLEvents');" + "eventObject.initEvent( 'change', false, true );"
				+ "element.dispatchEvent(eventObject);" + "} else if( document.createEventObject ) {"
				+ "element.fireEvent('onchange');" + "};";
		executeScript(script, element.getAttribute("id"));
	}

	public static void forceMouseDownEventElement(WebElement element) {
		String script = "var element = arguments[0]; " + "if( document.createEvent ) { "
				+ "var eventObject = document.createEvent('MouseEvents');" + "eventObject.initEvent( 'mousedown', true, false );"
				+ "element.dispatchEvent(eventObject);" + "} else if( document.createEventObject ) {"
				+ "element.fireEvent('mousedown');" + "};";
		executeScript(script, element);
	}

	public static void makeVisible(WebElement element) {
		executeScript(String.format("$('#%s').show();", element.getAttribute("id")));

	}

	public static void clickByJavaScript(WebElement element) {
		executeScript("arguments[0].click();", element);
	}
}
