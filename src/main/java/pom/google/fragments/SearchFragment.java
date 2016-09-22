package pom.google.fragments;

import static com.codeborne.selenide.Selenide.$;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import pom.components.AbstractFragment;


public class SearchFragment extends AbstractFragment {

	@FindBy(id="searchform")
	private WebElement rootElement;

	private By searchField = By.className("gsfi");
	private By searchButton = By.xpath(".//button");

	public SearchFragment() {
		setRootElement(rootElement);
	}

	public void setSearchQuery(String query) {
		$(getChildElement(searchField)).setValue(query);
	}

	public void clickSearchButton() {
		getChildElement(searchButton).click();
	}

}
