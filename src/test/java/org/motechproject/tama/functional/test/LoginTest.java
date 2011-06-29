package org.motechproject.tama.functional.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.functional.page.HomePage;
import org.motechproject.tama.functional.page.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class LoginTest {

    @Autowired
    private WebDriver webDriver;

    @Test
    public void testLoginFailure() {
        LoginPage page = PageFactory.initElements(webDriver, LoginPage.class).loginWithIncorrectUserNamePassword();
        Assert.assertEquals(LoginPage.FAILURE_MESSAGE, page.errorMessage());
    }

    @Test
    public void testLoginSuccess() {
        HomePage homePage = PageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectUserNamePassword();
        Assert.assertEquals(HomePage.WELCOME_MESSAGE, homePage.getWelcomeMessage());
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @After
    public void tearDown(){
        this.webDriver.quit();
    }


}
