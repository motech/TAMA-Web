package org.motechproject.tama.functional.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.page.HomePage;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.setup.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class LoginTest {

    private WebDriver webDriver;

    @Before
    public void setUp() {
        webDriver = WebDriverFactory.getInstance();
    }

    @Test
    public void testLoginFailure() {
        LoginPage page = PageFactory.initElements(webDriver, LoginPage.class).loginWithIncorrectUserNamePassword();
        Assert.assertEquals(LoginPage.FAILURE_MESSAGE, page.errorMessage());
    }

    @Test
    public void testLoginSuccess() {
        HomePage homePage = MyPageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectUserNamePassword();
        Assert.assertEquals(HomePage.WELCOME_MESSAGE, homePage.getWelcomeMessage());
    }

    @After
    public void tearDown() {
        webDriver.quit();
    }
}
