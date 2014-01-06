package tests;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import utils.MyFileUtils;
import check.Check;

public class TestJeuDeDonnees {
	
	private String url_jeu_de_donnees = "\\\\molene\\echange\\Partage\\adt\\jeu_de_donnees.txt";
	
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
	public void testKerevalPresentation_boucle() throws Exception {
		File jeuDeDonnees = new File(url_jeu_de_donnees);
		BufferedReader br;
		Check check = new Check();
		check.deleteMyLog();
		if (jeuDeDonnees.exists()) {
			try {
				br = MyFileUtils.getBufferedReader(jeuDeDonnees);
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println(line);
					testKerevalPresentation(line, check);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			testKerevalPresentation("", check);
		}
	}
	
	
	public void testKerevalPresentation(String test, Check check) throws Exception {
		
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
