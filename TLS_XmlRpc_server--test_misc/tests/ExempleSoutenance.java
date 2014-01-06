package tests;

import static org.junit.Assert.assertTrue;
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

public class ExempleSoutenance {
	private WebDriver driver;
	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();
	private Check check = new Check();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		baseUrl = "http://www.kereval.com/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testExempleSoutenance() throws Exception {
		driver.get(baseUrl + "/index.php/fre/Presentation");
		driver.findElement(By.xpath("//span")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
		try {
			assertTrue(isElementPresent(By.xpath("//h1")));
		} catch (Error e) {
			verificationErrors.append(e.toString());
		}
		driver.findElement(By.xpath("//li[2]/div/a/span")).click();
		try {
			check.check("Le lien vers conseil est présent",
					isElementPresent(By.linkText("Conseil")));
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
