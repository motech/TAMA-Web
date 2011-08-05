package org.motechproject.tamafunctional.test;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctional.context.ClinicContext;
import org.motechproject.tamafunctional.context.ClinicianContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.MyWebElement;
import org.motechproject.tamafunctional.page.ChangePasswordPage;
import org.motechproject.tamafunctional.page.ListPatientsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.PasswordSuccessPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")
public class ChangePasswordTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testChangePasswordLinkShouldNotBeShownOnTheLoginPage() {
        MyWebElement navigationLinks = new MyWebElement(MyPageFactory.initElements(webDriver, LoginPage.class).getNavigationLinks());
        assertNull(navigationLinks.findElement(By.id("changePasswordLink")));
    }

    @Test
    public void testChangePasswordLinkShouldBeShownOnAllPages() {
        ListPatientsPage homePage = MyPageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectAdminUserNamePassword();
        WebElement navigationLinks = homePage.getNavigationLinks();
        assertNotNull(navigationLinks.findElement(By.id("changePasswordLink")));
    }

    @Test
    public void testShouldNavigateToLoginPageWhenUserNotLoggedIn() {
        webDriver.get(ChangePasswordPage.CHANGE_PASSWORD_URL);
        assertEquals(LoginPage.LOGIN_URL, webDriver.getCurrentUrl());
    }

    @Test
    public void testChangePasswordForAdministrator() {
        ListPatientsPage homePage = MyPageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectAdminUserNamePassword();
        ChangePasswordPage changePasswordPage = homePage.goToChangePasswordPage();
        PasswordSuccessPage successPage = changePasswordPage.submitWithValidInput("password", "newPassword", "newPassword");

        assertNotNull(successPage.getSuccessMessageElement());
        cleanUp("newPassword", "password");
    }

    @Test
    public void testChangePasswordForClinician() {
        String clinicianForPasswordChangeTest = "clinicianForPasswordChangeTest";
        String clinicianPassword = "clinicianPassword";


        buildContexts(new ClinicianContext(clinicianForPasswordChangeTest, clinicianPassword, new ClinicContext("clinicForPasswordChangeTest")));

        ChangePasswordPage changePasswordPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianForPasswordChangeTest, clinicianPassword).goToChangePasswordPage();

        PasswordSuccessPage successPage = changePasswordPage.submitWithValidInput(clinicianPassword, "newPassword", "newPassword");
        assertNotNull(successPage.getSuccessMessageElement());
    }

    private void cleanUp(String oldPassword, String newPassword) {
        webDriver.get(ChangePasswordPage.CHANGE_PASSWORD_URL);
        ChangePasswordPage changePasswordPage = MyPageFactory.initElements(webDriver, ChangePasswordPage.class);
        changePasswordPage.submitWithValidInput(oldPassword, newPassword, newPassword);
    }
}
