package pom.google.pages;

import pom.components.AbstractSitePage;


public class GoogleMainPage extends AbstractSitePage {

	private final static String PAGE_NAME = "Google";
	private final static String PATH = "/";
	private final static String BODY_CLASS = "hp";

	public GoogleMainPage() {
		setPageUrl(PATH);
		setPageName(PAGE_NAME);
		setBodyClass(BODY_CLASS);
	}


}
