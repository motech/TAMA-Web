package org.motechproject.tama.functional.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.functional.page.HomePage;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.setup.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class LoginTest {
    @Test
    public void testLoginFailure() {
        LoginPage page = PageFactory.initElements(WebDriverFactory.getInstance(), LoginPage.class).loginWithIncorrectUserNamePassword();
        Assert.assertEquals(LoginPage.FAILURE_MESSAGE, page.errorMessage());
    }

    @Test
    public void testLoginSuccess() {
        HomePage homePage = PageFactory.initElements(WebDriverFactory.getInstance(), LoginPage.class).loginWithCorrectUserNamePassword();
        Assert.assertEquals(HomePage.WELCOME_MESSAGE, homePage.getWelcomeMessage());
    }
}
