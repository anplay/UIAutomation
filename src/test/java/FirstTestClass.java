import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import pom.google.pages.GoogleMainPage;


public class FirstTestClass {

	@Test
	public void firstTest() {
		assertTrue((2*2)==4, "Hello World");
	}

	@Test
	public void googleTest() {
		GoogleMainPage mainPage = new GoogleMainPage();
		mainPage.visit();
		mainPage.getSearchFragment().setSearchQuery("Selenide");
		mainPage.getSearchFragment().clickSearchButton();
	}

}
