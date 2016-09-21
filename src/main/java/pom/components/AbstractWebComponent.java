package pom.components;

import static utils.WebDriverUtils.getDriver;

import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.tools.WebElementWait;


public abstract class AbstractWebComponent {
	protected final static Logger LOG = LoggerFactory.getLogger(AbstractWebComponent.class);
	protected final static long DEFAULT_TIME_TO_WAIT = 15000;
	protected final String componentName = this.getClass().getName();
	protected final String componentSimpleName = this.getClass().getSimpleName();
	protected final String componentCode = componentName + "@" + this.hashCode();

	protected WebElementWait wait = new WebElementWait();

	public AbstractWebComponent() {
		LOG.trace("Initializing lazy elements for object: " + this);
		PageFactory.initElements(getDriver(), this);
	}

	protected String getSimpleName() {
		return this.getClass().getSimpleName();
	}
}
