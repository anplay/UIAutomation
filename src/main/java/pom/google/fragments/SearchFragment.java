package pom.google.fragments;

import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import com.codeborne.selenide.SelenideElement;

import pom.components.AbstractFragment;


public class SearchFragment extends AbstractFragment {

	@FindBy(id="searchform")
	private SelenideElement rootElement;

	private By searchField = By.xpath(".//*[@title='Search']");

	public SearchFragment() {
		setRootElement(rootElement);
	}

}
