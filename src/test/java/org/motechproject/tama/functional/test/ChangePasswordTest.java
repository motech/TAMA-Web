package org.motechproject.tama.functional.test;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.functional.framework.BaseTest;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.framework.MyWebElement;
import org.motechproject.tama.functional.page.ListPatientsPage;
import org.motechproject.tama.functional.page.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class ChangePasswordTest extends BaseTest{

    public static final String CHANGE_PASSWORD_URL = "http://localhost:"+System.getProperty("jetty.port","8080")+"/tama/changePassword";

    @Before
    public void setUp(){
        super.setUp();
    }

    @Test
    public void changePasswordLinkShouldNotBeShownOnTheLoginPage() {
        MyWebElement linksDiv = new MyWebElement(MyPageFactory.initElements(webDriver,LoginPage.class).getLinksDiv());
        assertNull(linksDiv.findElement(By.id("changePasswordLink")));
    }

    @Test
    public void changePasswordLinkShouldBeInLinksDivSoThatItIsShownOnAllPages() {
        ListPatientsPage homePage = MyPageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectAdminUserNamePassword();
        WebElement linksDiv = homePage.getLinksDiv();
        assertNotNull(linksDiv.findElement(By.id("changePasswordLink")));
    }

    @Test
    public void shouldNotGoToChangePasswordPageWhenUserNotLoggedIn() {
        webDriver.get(CHANGE_PASSWORD_URL);
        MyWebElement links = new MyWebElement(webDriver.findElement(By.id("links")));
        assertNull(links.findElement(By.id("changePasswordLink")));
    }
}
