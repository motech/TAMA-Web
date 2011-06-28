package org.motechproject.tama.functional.tests;

import junit.framework.Assert;
import org.junit.*;
import org.motechproject.tama.functional.pages.HomePage;
import org.motechproject.tama.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;

public class LoginTest{

    private static WebDriver webDriver;

    @BeforeClass
    public static void setUp(){
        webDriver = new HtmlUnitDriver(true);
    }

    @Test
    public void testLoginFailure(){
        LoginPage page = PageFactory.initElements(webDriver, LoginPage.class).loginWithIncorrectUserNamePassword();
        Assert.assertEquals(LoginPage.FAILURE_MESSAGE, page.errorMessage());
    }
    @Test
    public void testLoginSuccess(){
        HomePage homePage = PageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectUserNamePassword();
        Assert.assertEquals(HomePage.WELCOME_MESSAGE, homePage.getWelcomeMessage());
    }

    @AfterClass
    public static void tearDown(){
        webDriver.quit();
    }
}
