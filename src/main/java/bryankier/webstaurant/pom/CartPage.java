package bryankier.webstaurant.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import bryankier.testframework.WebDriverHelper;

/**
 * Page model for the standard cart page
 */
public class CartPage {

	static final By MAIN_EMPTY_CART_BUTTON_LOCATOR = By.linkText("Empty Cart");
	static final By EMPTY_CART_CONFIRM_BUTTON_LOCATOR = By.xpath("//button[text()='Empty Cart']");
	static final By CART_EMPTY_TEXT_LOCATOR = By.xpath("//*[text()='Your cart is empty.']");

	WebDriver driver;
	StoreGlobalHeader globalMenu;

	/**
	 * @param driver the driver to use
	 */
	public CartPage(WebDriver driver) {
		this.driver = driver;
		globalMenu = new StoreGlobalHeader(driver);
	}

	/**
	 * finds an item in the cart matching the given description
	 * 
	 * @param itemDescription the description, which is the link text
	 * @return the link element
	 */
	private WebElement findItemDescriptionInCart(String itemDescription) { // TODO find by product number where
																			// appropriate
		return WebDriverHelper.findClickableElement(By.linkText(itemDescription), driver, 10000, 250);
	}

	/**
	 * Checks whether an item with the given description is in the cart.
	 * 
	 * @param itemDescription the description, which is the link text
	 * @return true if the description is found
	 */
	public boolean itemWithDescriptionIsInCart(String itemDescription) {
		return findItemDescriptionInCart(itemDescription) != null;
	}

	/**
	 * Finds the Empty Cart button on the main cart page
	 * 
	 * @return the Empty Cart button
	 */
	private WebElement findEmptyCartButtonOnMainCartPage() {
		return WebDriverHelper.findClickableElement(MAIN_EMPTY_CART_BUTTON_LOCATOR, driver, 10000, 250);
	}

	/**
	 * finds the Empty Cart button in the Empty Cart confirmation dialog after
	 * clicking the Empty Cart button on the main cart page.
	 * 
	 * @return the Empty Cart button
	 */
	private WebElement findEmptyCartConfirmationButton() {
		return WebDriverHelper.findClickableElement(EMPTY_CART_CONFIRM_BUTTON_LOCATOR, driver, 10000, 250);
	}

	/**
	 * Empties the cart by clicking the Empty Cart button in the main cart page,
	 * then clicking the Empty Cart button in the confirmation dialog that appears.
	 * 
	 * @return true if no exceptions are thrown, the "Your cart is empty." message
	 *         displays and the cart count in the global header menu goes to zero.
	 */
	public boolean emptyCart() {
		boolean success = false;

		try {
			findEmptyCartButtonOnMainCartPage().click();
			findEmptyCartConfirmationButton().click();

			success = true;
		} catch (Exception e) {
			success = false;
		}

		success &= yourCartIsEmptyHeaderIsFound();

		success &= globalMenu.findCartItemCount() == 0;

		return success;
	}

	/**
	 * Finds large text saying "Your cart is empty."
	 * 
	 * @return the element containing the text
	 */
	private WebElement findYourCartIsEmptyHeader() {
		return WebDriverHelper.findVisibleElement(CART_EMPTY_TEXT_LOCATOR, driver, 10000, 250);
	}

	/**
	 * Check whether large text saying "Your cart is empty" is found
	 * 
	 * @return true if found
	 */
	public boolean yourCartIsEmptyHeaderIsFound() {
		return findYourCartIsEmptyHeader() != null;
	}
}
