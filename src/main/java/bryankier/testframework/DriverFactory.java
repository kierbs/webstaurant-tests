package bryankier.testframework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Creates test automation drivers to run tests using different browsers,
 * option, etc.
 * <p>
 * At the time of its creation, this is just a limited proof of concept that
 * supports Chrome. Other browsers should be easily added in this method and
 * anywhere browser-specific logic needs to be implemented (which should be
 * isolated as much as possible.").
 */
public class DriverFactory {

	/**
	 * Creates a test driver for the desired browser. This can be called on-demand
	 * if needed, and then pass the driver between tests and page objects, or it can
	 * be used to populate a static field based on the design of the test suite.
	 * 
	 * @param browserName the name of the browser to create a driver for
	 * @param maximize    if true, maximize the browser session when it is started
	 * @param headless    if true, run in headless mode (if supported)
	 * 
	 * @return a web driver for the browser with specified options
	 */
	public static WebDriver createDriver(String browserName, boolean maximize, boolean headless) {

		WebDriver returnDriver = null;

		if (browserName != null && browserName.toLowerCase().contains("chrome")) {
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			if (headless) {
				options.addArguments("--headless");
			}
			returnDriver = new ChromeDriver(options);
		} else {
			throw new IllegalArgumentException(
					"Browser name '" + "' is not a valid browser name, or is not handled by this method");
		}

		if (maximize) {
			returnDriver.manage().window().maximize();
		}

		return returnDriver;
	}
}
