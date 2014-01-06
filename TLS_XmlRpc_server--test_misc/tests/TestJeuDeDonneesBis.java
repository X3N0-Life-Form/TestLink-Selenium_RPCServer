package tests;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import check.Check;
import dataSet.DataSet;
import dataSet.TestInterface;

public class TestJeuDeDonneesBis implements TestInterface {
	
	private WebDriver driver;
	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		baseUrl = "http://www.kereval.com/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void realTest() throws Exception {
		DataSet ds = new DataSet(this);
		ds.boucle();
	}
	
	@Override
	public String getUrl() {
		return "change me";
	}
	
	//@Test
	public void test(String test, Check check) throws Exception {
		
		driver.get(baseUrl + "/");
		try {
			check.check(test, isElementPresent(By.xpath("//img[@alt='pictogramme_automobile_200x181']")));
		} catch (Error e) {
			verificationErrors.append(e.toString());
		}
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	
}
