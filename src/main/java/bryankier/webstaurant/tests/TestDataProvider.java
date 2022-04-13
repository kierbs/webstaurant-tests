package bryankier.webstaurant.tests;

import org.testng.annotations.DataProvider;

/**
 * Provides test parameters to tests in this suite using TestNG annotations 
 */
public class TestDataProvider {

	/**
	 * Provides test parameters to the Webstaurant store product search and cart test. 
	 * <p>
	 * Additional sets of parameters can be added (it is a two-dimensional array).
	 * <p>
	 * Parameters / options to be used for product search and cart test. Additional
	 * lines can be added. See the {@link  WebstaurantStoreTests#verifyProductSearchAndCart} method for parameter descriptions.
	 * 
	 * @return a two dimensional array containing sets of parameters to be passed to the test method
	 */

	@DataProvider(name = "searchTestParams")
	public static Object[][] searchTestParams() {
		/* parameters: 
		   environment, browser, searchText, resultsExpectedText,
		   minResults, maxResults, addAccessories, maximizeBrowser, gridOrListView,
		   headless
		*/
		return new Object[][] {
				{ "production", "chrome", "stainless work table", "table", 1, 2000, true, true, "grid", false } 
				};
	}
}
