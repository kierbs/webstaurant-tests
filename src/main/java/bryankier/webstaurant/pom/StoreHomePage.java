package bryankier.webstaurant.pom;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import bryankier.testframework.WebDriverHelper;

public class StoreHomePage {

	static final String PRODUCTION_URL = "https://www.webstaurantstore.com";
	static final By SEARCH_BUTTON_LOCATOR = By.xpath("//button[text()='Search']");
	static final By SEARCH_TEXTBOX_LOCATOR = By.id("searchval");
	static final By GRIDVIEW_BUTTON_LOCATOR = By.xpath("//button[@aria-label='Switch to Grid view']");
	static final By LISTVIEW_BUTTON_LOCATOR = By.xpath("//button[@aria-label='Switch to List view']");
//	static final By SEARCH_RESULT_BOX_lOCATOR = By
//			.xpath("//div[@id='product_listing']//div[@id='ProductBoxContainer']");
	static final By SEARCH_RESULT_BOX_lOCATOR = By.id("ProductBoxContainer");
	static final By SEARCH_RESULT_LINK_LOCATOR = By.xpath(".//a[@data-testid='itemDescription']");
	static final By SEARCH_RESULT_CART_BUTTON_LOCATOR = By.xpath(".//input[@name='addToCartButton']");
	static final By ADDED_TO_CART_CLOSE_BUTTON_LOCATOR = By
			.xpath("//div[@class='notification__content']/../button[@class='close']");
	static final By ACCESSORY_DROPDOWN_LOCATOR = By.xpath("//select[@name='accessories']");
	static final By ACCESSORY_CLOSE_BUTTON_LOCATOR = By
			.xpath("//div[@role='dialog'][@aria-modal='true']//button[text()='Add To Cart']");

	private WebDriver driver;
	private String environment;
	private StoreGlobalHeader globalMenu;

	/**
	 * Currently used to keep track of the page number based on when next page is
	 * clicked. This may be switched to get it each time from the page nav control
	 * in the future
	 */
	private int pageNumber = 0;

	/**
	 * Page model for the WebStaurantStore home page including search results.
	 * 
	 * @param driver      the web driver to automate this page
	 * @param environment the testing environment, "production" for example
	 */
	public StoreHomePage(WebDriver driver, String environment) {
		this.driver = driver;
		this.environment = environment;
		globalMenu = new StoreGlobalHeader(driver);
	}

	/**
	 * Gets the current page number. For initial implementation, this is the result
	 * of incrementing the page number when going to the next page.
	 * 
	 * @return the current page number
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * Builds the page's URL based on the testing environment
	 * 
	 * @return
	 */
	private String buildUrl() {
		String returnUrl = "";
		if (environment.toLowerCase().contains("prod")) {
			returnUrl = PRODUCTION_URL;
		} else {
			throw new IllegalArgumentException("Environment '" + environment + "' is not valid or not implemented.");
		}

		return returnUrl;
	}

	/**
	 * Builds and navigates to this page's home URL
	 */
	public void go() {
		driver.get(buildUrl());
	}

	/**
	 * Uses the store's global header page model to find the element containing the
	 * number of items in the cart then get the count from it.
	 * 
	 * @return the number of items in the cart as displayed in the header bar
	 */
	public int findCartItemCount() {
		return globalMenu.findCartItemCount();
	}

	/**
	 * Finds the search textbox on the screen
	 * 
	 * @return the textbox element, or null if it is not found
	 */
	private WebElement findSearchTextBox() {
		return WebDriverHelper.findClickableElement(SEARCH_TEXTBOX_LOCATOR, driver, 30000, 500);
	}

	/**
	 * Finds the search button that submits the product search
	 * 
	 * @return the search button element, or null if it is not found
	 */
	private WebElement findSearchButton() {
		return WebDriverHelper.findClickableElement(SEARCH_BUTTON_LOCATOR, driver, 5000, 250);
	}

	/**
	 * Searches products by entering text in the search box and clicking the Search
	 * button.
	 * 
	 * @param searchText the text to enter in the search box
	 */
	public void searchProducts(String searchText) {
		WebElement searchBox = findSearchTextBox();
		searchBox.click();
		searchBox.sendKeys(searchText);
		findSearchButton().click();
		pageNumber = 1;
	}

	/**
	 * Finds the control that switches the search results layout to grid view
	 * 
	 * @return the element that can be clicked to switch to grid view, or null if it
	 *         is not found
	 */
	private WebElement findGridViewButton() {
		return WebDriverHelper.findVisibleElement(GRIDVIEW_BUTTON_LOCATOR, driver, 10000, 250);
	}

	/**
	 * Finds the control that switches the search results layout to list view
	 * 
	 * @return the element that can be clicked to switch to list view, or null if it
	 *         is not found
	 * @
	 *   <p>
	 *   Note: The initial implementation of the WebstaurantStore search test does
	 *   (or did) not support this view.
	 */
	private WebElement findListViewButton() {
		return WebDriverHelper.findVisibleElement(LISTVIEW_BUTTON_LOCATOR, driver, 10000, 250);
	}

	/**
	 * Sets the page's layout option for the search results to either grid or list
	 * style.
	 * 
	 * @param layoutOption the results layout to select, grid or list
	 */
	public void setResultsLayout(String layoutOption) {
		if (layoutOption.toLowerCase().contains("list")) {
			findListViewButton().click();
		} else {
			findGridViewButton().click();
		}
	}

	/**
	 * Finds all of the items in the search results on this page
	 * 
	 * @return a list of all of the parent boxes containing one search result each,
	 *         or an empty list if not found
	 */
	public List<WebElement> findAllResultItemBoxes() {
		// SLOW:
		return WebDriverHelper.findVisibleElements(SEARCH_RESULT_BOX_lOCATOR, driver, 30000, 500);
	}

	/**
	 * Finds the product link, whose text is the product description, from the
	 * product item's parent container
	 * 
	 * @param parentBox the parent element containing one product search result
	 * @return the link whose text is the product description, or null if it is not
	 *         found
	 */
	private WebElement findLinkInItemBox(WebElement parentBox) {
		return WebDriverHelper.findVisibleElement(SEARCH_RESULT_LINK_LOCATOR, parentBox, 5000, 100);
	}

	/**
	 * Gets the text from a product search result link, which is the product
	 * description
	 * 
	 * @param parentBox the parent element containing one product search result
	 * @return the link text, which is the product description, or null if it is not
	 *         found
	 */
	public String findLinkTextInItemBox(WebElement parentBox) {
		try {
			return findLinkInItemBox(parentBox).getText();
		} catch (Exception e) {
			return "[Unable to get link text (Error='" + e.getMessage() + "')]";
		}
	}

	/**
	 * Checks whether the product description link contained within a parent product
	 * search result box contains the specified text.
	 * 
	 * @param parentBox     the parent product search result box containing the
	 *                      product description link
	 * @param containedText the text that is expected to be contained in the link
	 *                      description
	 * @param ignoreCase    ignore capitalization when comparing text
	 * @return true if the expected text is found within the link description
	 */
	public boolean itemLinkContains(WebElement parentBox, String containedText, boolean ignoreCase) {
		String itemBoxText = findLinkTextInItemBox(parentBox);
		if (ignoreCase) {
			containedText = containedText.toLowerCase(); // immutable, won't modify original
			itemBoxText = itemBoxText.toLowerCase();
		}

		return itemBoxText.contains(containedText);
	}

	/**
	 * Finds the Add to Cart button for a product search result given the search
	 * result item's containing box
	 * 
	 * @param parentBox the parent element to a single search result item,
	 *                  containing the description link, Add to Cart button, etc.
	 * @return the Add to Cart button element for the item, or null if it is not
	 *         found
	 */
	private WebElement findAddToCartButtonInItemBox(WebElement parentBox) {
		return WebDriverHelper.findVisibleElement(SEARCH_RESULT_CART_BUTTON_LOCATOR, parentBox, 5000, 100);
	}

	/**
	 * Clicks the Add to Cart button for a product search result given the search
	 * result item's containing box
	 * 
	 * @param parentBox the parent element to a single search result item,
	 *                  containing the description link, Add to Cart button, etc.
	 */
	private void clickAddToCartButtonInItemBox(WebElement parentBox) {
		findAddToCartButtonInItemBox(parentBox).click();
	}

	/**
	 * Adds a product from a search result to the cart. Given the item's containing
	 * element, finds and clicks the Add to Cart button within. Handles prompts to
	 * add accessories when adding the item to the cart, based on allowAccessories
	 * parameter. Checks that the number of items in the cart increases.
	 * 
	 * @param parentBox        the item's containing search results box element
	 * @param allowAccessories if true, attempt to handle prompts to add accessories
	 *                         to the cart by arbitrarily selecting accessories
	 * @return true it the item passes the checks in this method to verify it was
	 *         added to the cart
	 */
	public boolean addItemInBoxToCart(WebElement parentBox, boolean allowAccessories) {
		boolean success = false;

		try {
			int cartCountBefore = findCartItemCount();
			clickAddToCartButtonInItemBox(parentBox);

			if (allowAccessories) {
				trySelectingAccessoriesDialogOptions();
			}

			success = cartCountIncreased(cartCountBefore);
		} catch (Exception e) {
			success = false;
		}

		return success;
	}

	/**
	 * Finds the next page button on the right side of the page navigation control.
	 * 
	 * @return the next-page button element, or null if it is not found
	 */
	private WebElement findNavNextPageButton() {
		String xPath = "//nav[@aria-label='pagination']/ul/li";
		int pageRightButtonIndex = WebDriverHelper.countElements(By.xpath(xPath), driver, 30000, 500) - 1;
		WebElement pageRightButton = WebDriverHelper
				.findVisibleElement(By.xpath(xPath + "[" + pageRightButtonIndex + "]"), driver, 15000, 500);

		return pageRightButton;
	}

	/**
	 * Checks with the next page button on the right side of the page navigation
	 * control is disabled.
	 * 
	 * @return true if the next-page button is disabled
	 */
	public boolean navPageRightIsDisabled() {
		boolean isDisabled = false;
		try {
			isDisabled = findNavNextPageButton().getAttribute("aria-disabled").equals("true");
		} catch (Exception e) {
			// return whether it is specifically disabled, so false
			isDisabled = false;
		}

		return isDisabled;
	}

	/**
	 * Checks whether this is the last page of search results, based on whether the
	 * next page button on the right side of the page navigation control is
	 * disabled.
	 * 
	 * @return true if the next-page control is disabled, indicating that this is
	 *         the last page
	 */
	public boolean isLastPage() {
		return navPageRightIsDisabled();
	}

	/**
	 * If this is not the last page, goes to the next page by clicking the next page
	 * button on the right side of the page navigation control.
	 * 
	 * @return true if no errors occurred clicking the next-page button
	 */
	public boolean goToNextPage() {
		boolean clickedNextPageButton = false;

		if (!isLastPage()) {
			findNavNextPageButton().click();
			clickedNextPageButton = true;
			pageNumber++;
		}

		return clickedNextPageButton;
	}

	/**
	 * Checks whether the number of items in the cart, as indicated in the bar near
	 * the top of the screen, has increased from the previous value (passed in as a
	 * parameter).
	 * 
	 * @param itemsPreviouslyInCart the number of items previously determined to be
	 *                              in the cart and passed in
	 * 
	 * @return true if the number of items in the cart increased from the specified
	 *         previous value
	 */
	public boolean cartCountIncreased(int itemsPreviouslyInCart) {
		return findCartItemCount() > itemsPreviouslyInCart;
	}

	/**
	 * Clicks into the cart using the cart button in the header bar
	 */
	public void clickIntoCart() {
		globalMenu.clickIntoCart();
	}

	/**
	 * Finds the close (x) button on the "... items added to your cart" dialog that
	 * appears briefly after adding an item to the cart
	 * <p>
	 * Note: Keep the timeout short (but long enough to be stable). Because this is
	 * part of a dialog that isn't expected to always be there, the timeout will
	 * have to expire to determine that it's not present.
	 * 
	 * @return the close button element, or null if it is not found
	 */
	private WebElement findAddedToYourCartCloseButton() {
		return WebDriverHelper.findClickableElement(ADDED_TO_CART_CLOSE_BUTTON_LOCATOR, driver, 3000, 250);
	}

	public void tryClickAddedToYourCartCloseButton() {
		try {
			findAddedToYourCartCloseButton().click();
		} catch (Exception e) {

		}
	}

	/* Product Accessories Dialog Window - consider moving to separate class */
	/**
	 * Finds all of the dropdown boxes for accessory options for the product being
	 * added to the cart.
	 * <p>
	 * Note: Keep the timeout short (but long enough to be stable). Because this is
	 * part of a dialog that isn't expected to always be there, the timeout will
	 * have to expire to determine that it's not present.
	 * 
	 * @return the list of accessory option dropdown elements, or an empty list if
	 *         not found
	 */
	private List<WebElement> findDropDownsInProductAccessoriesDialog() {
		return WebDriverHelper.findClickableElements(ACCESSORY_DROPDOWN_LOCATOR, driver, 1, 2000, 250);
	}

	/**
	 * Attempts to find and select options from dropdown boxes in the dialog that
	 * may appears when adding an item with optional accessories to the cart.
	 */
	public void trySelectingAccessoriesDialogOptions() {
		try {
			List<WebElement> accessoriesDropdowns = findDropDownsInProductAccessoriesDialog();
			for (WebElement dd : accessoriesDropdowns) {
				new Actions(driver).sendKeys(dd, Keys.ARROW_DOWN).sendKeys(Keys.ENTER).perform();
			}

			if (accessoriesDropdowns.size() > 0) {
				findAddToCartButtonInAccessoriesDialog().click();
			}
		} catch (Exception e) {
			// just trying, this may not even exist so keep going
		}
	}

	/**
	 * Attempts to find the Add to Cart button in the accessories dialog that may
	 * appears when adding an item with optional accessories to the cart.
	 * 
	 * @return the Add to Cart button in the Accessories dialog, or null if it is
	 *         not found
	 */
	private WebElement findAddToCartButtonInAccessoriesDialog() {
		return WebDriverHelper.findClickableElement(ACCESSORY_CLOSE_BUTTON_LOCATOR, driver, 1000, 250);
	}

}
