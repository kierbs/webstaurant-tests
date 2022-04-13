package bryankier.webstaurant.tests;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
//import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import bryankier.testframework.DriverFactory;
import bryankier.webstaurant.pom.CartPage;
import bryankier.webstaurant.pom.StoreHomePage;

/** Contains test method(s) for WebstaurantStore search and cart functionality
 * <p>
 * ***  Please see both personal and technical developer notes about this project and exercise in the project-level overview.html file.***
 * @author Bryan Kier
 */
public class WebstaurantStoreTests {

	WebDriver driver;
	String environment;
	String browser;

	/**
	 * Verify searching the store for products, checking that they contain expected
	 * text, and can be added and removed from the shopping cart.
	 * 
	 * @param environment         test environment (production, etc.)
	 * @param browser             name of the browser to use
	 * @param searchText          text to enter in the search box
	 * @param resultsExpectedText text expected to be in each returned item's
	 *                            description
	 * @param minResults          minimum number of results that need to be returned
	 *                            for the test to pass
	 * @param maxResultsToCheck   maximum number of tests to process. This is not a
	 *                            pass/fail criteria, it's mostly to limit scope and
	 *                            prevent infinite loop.
	 * @param addAccessories      arbitrarily select accessories for products if
	 *                            prompted, otherwise prompts will fail the test
	 *                            appear
	 * @param maximizeBrowser     maximize the browser when its driver is created
	 * @param gridOrListView      layout to select when viewing search results
	 *                            ('grid' or 'list')
	 * @param headless            whether to run the browser in headless mode, if
	 *                            applicable
	 */
	@Test(dataProvider = "searchTestParams", dataProviderClass = TestDataProvider.class)
	public void verifyProductSearchAndCart(String environment, String browser, String searchText,
			String resultsExpectedText, int minResults, int maxResultsToCheck, boolean addAccessories,
			boolean maximizeBrowser, String gridOrListView, boolean headless) {

		int numberOfResultsReturned = 0;
		int numberOfResultsProcessed = 0;
		WebElement lastItemBox = null;
		String linkDescription = null;
		boolean doneProcessingResults = false;
		String stepDescr = null;
		SoftAssert softAssert = new SoftAssert();

		try {
			stepDescr = "Open the browser (browser=" + browser + ", maximized=" + maximizeBrowser + ", headless="
					+ headless + ").";
			driver = DriverFactory.createDriver(browser, maximizeBrowser, headless);

			stepDescr = "Go to WebstaurantStore homepage (environment=" + environment + ").";
			StoreHomePage homePage = new StoreHomePage(driver, environment);
			homePage.go();

			stepDescr = "Search products (search text='" + searchText + "').";
			homePage.searchProducts(searchText);

			stepDescr = "Select grid or list view for the results layout (layout=" + gridOrListView + ").";
			homePage.setResultsLayout(gridOrListView);

			// Loop through all of the pages of search results (or until maximum number of
			// results to process is reached)
			do {
				stepDescr = "Get the parent boxes containing all of the products returned on this page of the search results.";
				List<WebElement> resultBoxes = homePage.findAllResultItemBoxes();
				numberOfResultsReturned = resultBoxes.size();

				stepDescr = "Confirm that the number of returned results meets the expected minimum (expected >= "
						+ minResults + ", actual = " + numberOfResultsReturned + ").";
				softAssert.assertTrue(numberOfResultsReturned >= minResults,
						"The number of search results returned is less than the expected minimum (minimum=" + minResults
								+ ", actual=" + numberOfResultsProcessed + ").");

				// Loop through the results (parent boxes containing the items) on this page.";
				for (WebElement box : resultBoxes) {

					stepDescr = "Check if item's description contains the expected text (item "
							+ (numberOfResultsProcessed + 1) + ", expected text='" + resultsExpectedText + "').";
					boolean containsExpectedText = homePage.itemLinkContains(box, resultsExpectedText, true);
					if (!containsExpectedText) {
						linkDescription = homePage.findLinkTextInItemBox(box);
						softAssert.assertTrue(false, "Item description '" + linkDescription + "' on page "
								+ homePage.getPageNumber() + " does not contain '" + resultsExpectedText + "'.");
					} else {
						// keep track of the last matching item('s parent box)
						lastItemBox = box;
					}
					numberOfResultsProcessed++;

					// break out of loop if we hit hit max number of results to process
					if (numberOfResultsProcessed >= maxResultsToCheck) {
						doneProcessingResults = true;
						Reporter.log(
								"Info: The maximum number of results was reached. No more results will be processed, but remaining tests will still be performed. (maximum results to check = "
										+ maxResultsToCheck + ")");
						break;
					}
				}

				stepDescr = "Go to the next page if the last page or maximum number of results have not been reached";
				try {
					doneProcessingResults |= homePage.isLastPage();

					if (!doneProcessingResults) {
						homePage.goToNextPage();
					}
				} catch (Exception e) {
					softAssert.assertTrue(false, "Failed to go to the next page.");
				}
			} while (!doneProcessingResults);

			String lastItemDescription = homePage.findLinkTextInItemBox(lastItemBox);

			stepDescr = "Add the last item to the cart (item description = '" + lastItemDescription + ").";
			boolean addedToCart = homePage.addItemInBoxToCart(lastItemBox, addAccessories);
			softAssert.assertTrue(addedToCart, "Nothing was added to the cart (count of items did not increase).");

			// Handling the "...item was added to your cart dialog that appears briefly
			// could be unstable or brittle, because we don't know if it will be there once
			// we spend time checking for other prompts and it will block clicks while it's
			// up. Instead, just close it if it's there and then check the cart.
			stepDescr = "If a popup appears saying the item was added to your cart, close it if it's not already gone.";
			homePage.tryClickAddedToYourCartCloseButton();

			stepDescr = "Click into the cart (internally, a new page object will be created for this test to use).";
			homePage.clickIntoCart();
			CartPage cartPage = new CartPage(driver);

			stepDescr = "Confirm that the item with the correct description is found in the cart (description='"
					+ lastItemDescription + "').";
			boolean foundItemInCart = cartPage.itemWithDescriptionIsInCart(lastItemDescription);
			softAssert.assertTrue(foundItemInCart,
					"Item with description '" + lastItemDescription + "' was not found in the cart.");

			stepDescr = "Empty the cart using the Empty Cart button and the additional Empty Cart button in the confirmation dialog.";
			softAssert.assertTrue(cartPage.emptyCart(), "Failed to empty the cart.");

		} catch (Exception e) {
			softAssert.assertTrue(false,
					"An unhandled exception occured during step, '" + stepDescr + "': " + e.getMessage());
			e.printStackTrace();
		}
		softAssert.assertAll();
	}

	@AfterTest
	public void afterClass() {
		try {
			if (driver != null && !driver.getWindowHandles().isEmpty()) {
				driver.close();
				driver.quit();
			}
		} catch (WebDriverException e) // TODO:
		{
			// this is just a final check, we don't want it to throw an exception
		}
	}
}
