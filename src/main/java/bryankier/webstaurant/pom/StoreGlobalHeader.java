package bryankier.webstaurant.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import bryankier.testframework.WebDriverHelper;

/**
 * The header bar that is used across multiple WebstaurantStore pages and
 * contains commone links and controls like the cart button showing the number
 * of items in the cart.
 */
public class StoreGlobalHeader {

	static final By CART_COUNT_LOCATOR = By.id("cartItemCountSpan");

	WebDriver driver;

	/**
	 * @param driver the web driver to use for this page
	 */
	public StoreGlobalHeader(WebDriver driver) {
		this.driver = driver;
	}

	private WebElement findCartItemCountElem() {
		return WebDriverHelper.findClickableElement(CART_COUNT_LOCATOR, driver, 30000, 500);
	}

	/**
	 * Gets the number of items in the cart as displayed in the header area
	 * 
	 * @return the number of items
	 */
	public int findCartItemCount() {
		return Integer.parseInt(findCartItemCountElem().getText());
	}

	public void clickIntoCart() {
		findCartItemCountElem().click();
	}
}
