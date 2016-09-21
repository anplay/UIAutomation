package pom.components;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebComponentFactory {

	private static final Logger LOG = LoggerFactory.getLogger(WebComponentFactory.class);

	private WebComponentFactory() {
	}

	public static <T extends AbstractFragment> T createFragment(WebElement rootElement, Class<T> type) {
		T instance = createFragment(type);
		instance.setRootElement(rootElement);
		return instance;
	}

	public static <T extends AbstractFragment> T createFragment(Class<T> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Unable to create an instance of class", e);
		}
		throw new IllegalStateException("Unable to create an instance of class " + type.getSimpleName());
	}

	public static <T extends AbstractFragment> List<T> createFragments(List<WebElement> elements, Class<T> type) {
		return elements.stream().map(element -> createFragment(element, type)).collect(Collectors.toList());
	}
}
