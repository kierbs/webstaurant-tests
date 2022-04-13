package bryankier.testframework;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import com.google.common.base.Function;

public class WebDriverHelper {

	/**
	 * Finds an element matching the expected condition using the web driver.
	 * <p>
	 * This is implemented as a private method because it should typically be called
	 * by a more specific method in this class. If there are reasonable exceptions,
	 * it can be made public.
	 * 
	 * @param expectedCondition    the condition to use to try to find the element
	 * @param driver               the automation web driver
	 * @param timeoutMilliseconds  the maximum time to spend looking for the element
	 * @param intervalMilliseconds the polling interval to wait between calls to the
	 *                             driver to try the find again
	 * @return the web element that was found, or null if it wasn't found
	 */
	private static WebElement findElement(ExpectedCondition<WebElement> expectedCondition, WebDriver driver,
			int timeoutMilliseconds, int intervalMilliseconds) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(timeoutMilliseconds, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);

		WebElement element = null;

		try {
			element = wait.until(expectedCondition);
		} catch (WebDriverException e) {
			// let the caller handle if element is not found (null), so execution
			// can continue and results can be reported in desired format.
			element = null;
		}

		return element;
	}

	/**
	 * Finds a visible element using the passed in locator with the web driver.
	 * 
	 * @param locator              the locator to use to try to find the element
	 * @param driver               the automation web driver
	 * @param timeoutMilliseconds  the maximum time to spend looking for the element
	 * @param intervalMilliseconds the polling interval to wait between calls to the
	 *                             driver to try the find again
	 * @return the web element that was found, or null if it wasn't found
	 */
	public static WebElement findVisibleElement(By locator, WebDriver driver, int timeoutMilliseconds,
			int intervalMilliseconds) {
		return findElement(ExpectedConditions.visibilityOfElementLocated(locator), driver, timeoutMilliseconds,
				intervalMilliseconds);

	}

	/**
	 * Finds a visible element using the locator, within the passed in parent
	 * element, with the web driver.
	 * 
	 * @param locator              the locator to use to try to find the child
	 *                             element
	 * @param driver               the automation web driver
	 * @param timeoutMilliseconds  the maximum time to spend looking for the element
	 * @param intervalMilliseconds the polling interval to wait between calls to the
	 *                             driver to try the find again
	 * @return the web element that was found, or null if it wasn't found
	 */
	public static WebElement findVisibleElement(By locator, WebElement parentElement, int timeoutMilliseconds,
			int intervalMilliseconds) {
		if (locator.toString().contains("By.xpath: /")) {
			throw new IllegalArgumentException("Locator '" + locator.toString()
					+ "' must start with a period, because this method is intended to search for child elements.");
		}

		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(((RemoteWebElement) parentElement).getWrappedDriver())
				.withTimeout(timeoutMilliseconds, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);

		WebElement element = null;

		try {
			element = wait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver driver) {
					return parentElement.findElement(locator);
				}
			});
		} catch (WebDriverException e) {
			// let the caller handle if element is not found (null), so execution
			// can continue and results can be reported in desired format.
		}

		return element;
	}

	/**
	 * Finds clickable (enabled) elements using the locator, within the passed in
	 * parent element, with the web driver.
	 * 
	 * @param locator              the locator to use to try to find the elements
	 * @param driver               the automation web driver to use to find the
	 *                             elements
	 * @param timeoutMilliseconds  the maximum time to spend looking for the
	 *                             elements
	 * @param intervalMilliseconds the polling interval to wait between calls to the
	 *                             driver to try the find again
	 * @return the web elements that were found, or an empty list
	 */
	public static WebElement findClickableElement(By locator, WebDriver driver, int timeoutMilliseconds,
			int intervalMilliseconds) {

		return findElement(ExpectedConditions.elementToBeClickable(locator), driver, timeoutMilliseconds,
				intervalMilliseconds);
	}

	/**
	 * Finds elements matching the expected condition using the web driver.
	 * <p>
	 * This is implemented as a private method because it should typically be called
	 * by a more specific method in this class. If there are reasonable exceptions,
	 * it can be made public.
	 * 
	 * @param expectedCondition    the condition to use to try to find the elements
	 * @param driver               the automation web driver
	 * @param timeoutMilliseconds  the maximum time to spend looking for the
	 *                             elements
	 * @param intervalMilliseconds the polling interval to wait between calls to the
	 *                             driver to try the find again
	 * @return the list of web element that were found, or an empty list
	 */
	private static List<WebElement> findElements(ExpectedCondition<List<WebElement>> expectedCondition,
			WebDriver driver, int timeoutMilliseconds, int intervalMilliseconds) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(timeoutMilliseconds, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);

		List<WebElement> elements = null;

		try {
			elements = wait.until(expectedCondition);
		} catch (WebDriverException e) {
			// let the caller handle if no elements are found, so execution
			// can continue and results can be reported in desired format.
			elements = new ArrayList<WebElement>();
		}

		return elements;
	}

	/**
	 * Finds visible elements using the locator, within the passed in parent
	 * element, with the web driver.
	 * 
	 * @param locator              the locator to use to try to find the elements
	 * @param driver               the automation web driver to use to find the
	 *                             elements
	 * @param timeoutMilliseconds  the maximum time to spend looking for the
	 *                             elements
	 * @param intervalMilliseconds the polling interval to wait between calls to the
	 *                             driver to try the find again
	 * @return the web elements that were found, or an empty list
	 */
	public static List<WebElement> findVisibleElements(By locator, WebDriver driver, int timeoutMilliseconds,
			int intervalMilliseconds) {

		// TODO: fix how slow this is. FluentWait takes about 5 seconds.
		// driver.FindElements runs full timeout if called too quickly, but is less than
		// a second if called slightly delayed.

		return findElements(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator), driver, timeoutMilliseconds,
				intervalMilliseconds);
	}

	/**
	 * Looks for enabled elements matching locator until the minimum number of
	 * elements to wait for are found, or until the timeout expires.
	 * 
	 * @param locator              locator to use to find the elements
	 * @param driver               driver to use to find the elements
	 * @param timeoutMilliseconds  time to look for the elements if the minimum
	 *                             number of elements aren't found sooner
	 * @param intervalMilliseconds polling interval to wait between calls to the
	 *                             driver to find the elements
	 * @return the list of elements that were found, or an empty list if not found
	 */
	public static List<WebElement> findClickableElements(By locator, WebDriver driver, int minElementsToWaitFor,
			int timeoutMilliseconds, int intervalMilliseconds) {

		long startTime = System.currentTimeMillis();
		long elapsedTime = 0;
		int numberEnabled = 0;
		ArrayList<WebElement> enabledElements = new ArrayList<WebElement>();

		do { // TODO review efficiency of these loops and finds
			try {
				List<WebElement> elements = findVisibleElements(locator, driver, timeoutMilliseconds,
						intervalMilliseconds);

				enabledElements.clear();

				for (WebElement we : elements) {
					if (we.isEnabled()) {
						enabledElements.add(we);
					}
				}

				elapsedTime = System.currentTimeMillis() - startTime;

				TimeUnit.MILLISECONDS.sleep(intervalMilliseconds);
			} catch (Exception e) {

			}
		} while (numberEnabled < minElementsToWaitFor && elapsedTime < timeoutMilliseconds);

		return enabledElements;
	}

	/**
	 * Counts elements matching the expected condition using the web driver.
	 * <p>
	 * This is implemented as a private method because it should typically be called
	 * by a more specific method in this class. If there are reasonable exceptions,
	 * it can be made public.
	 * 
	 * @param expectedCondition    the condition to use to try to find the elements
	 * @param driver               the automation web driver
	 * @param timeoutMilliseconds  the maximum time to spend looking for the
	 *                             elements
	 * @param intervalMilliseconds the polling interval to wait between calls to the
	 *                             driver to try the find again
	 * @return the number of matching elements that were found
	 */
	private static int countElements(ExpectedCondition<List<WebElement>> expectedCondition, WebDriver driver,
			int timeoutMilliseconds, int intervalMilliseconds) {
		List<WebElement> elements = findElements(expectedCondition, driver, timeoutMilliseconds, intervalMilliseconds);

		return elements.size();
	}

	/**
	 * Counts elements matching the passed in locator using the web driver.
	 * 
	 * @param locator              the locator to use to try to find the elements
	 * @param driver               the automation web driver
	 * @param timeoutMilliseconds  the maximum time to spend looking for the
	 *                             elements
	 * @param intervalMilliseconds the polling interval to wait between calls to the
	 *                             driver to try the find again
	 * @return the number of matching elements that were found
	 */
	public static int countElements(By locator, WebDriver driver, int timeoutMilliseconds, int intervalMilliseconds) {

		return countElements(ExpectedConditions.presenceOfAllElementsLocatedBy(locator), driver, timeoutMilliseconds,
				intervalMilliseconds);
	}
}
