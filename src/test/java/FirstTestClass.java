import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;


/**
 * Created by Andrii_Palii on 9/12/2016.
 */
public class FirstTestClass {

	@Test
	public void firstTest() {
		assertTrue((2*2)==5, "Hello World");
	}

}
