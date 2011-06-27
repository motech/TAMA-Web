package org.motechproject.tama.functional.tests;

import com.thoughtworks.selenium.SeleneseTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tama.functional.pages.HomePage;
import org.motechproject.tama.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;

public class LoginTest{

    private WebDriver webDriver = new FirefoxDriver();

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
}
