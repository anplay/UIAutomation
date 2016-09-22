package pom.google.pages;

import static pom.components.WebComponentFactory.createFragment;

import pom.components.AbstractSitePage;
import pom.google.fragments.SearchFragment;


public class GoogleMainPage extends AbstractSitePage {

	private final static String PAGE_NAME = "Google";
	private final static String PATH = "/";
	private final static String BODY_CLASS = "hp";

	public GoogleMainPage() {
		setPageUrl(PATH);
		setPageName(PAGE_NAME);
		setBodyClass(BODY_CLASS);
	}

	public SearchFragment getSearchFragment() {
		return createFragment(SearchFragment.class);
	}

}
