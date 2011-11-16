package org.motechproject.tamafunctional.test;


import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.page.ChangePasswordPage;
import org.motechproject.tamafunctional.page.ListClinicsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.PasswordSuccessPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.Assert.*;

public class ChangePasswordTest extends BaseTest {
    @Test
    public void testChangePasswordLinkShouldNotBeShownOnTheLoginPage() {
        WebElement navigationLinks = WebDriverFactory.createWebElement(MyPageFactory.initElements(webDriver, LoginPage.class).getNavigationLinks());
        assertNull(navigationLinks.findElement(By.id("changePasswordLink")));
    }

    @Test
    public void testChangePasswordLinkShouldBeShownOnAllPages() {
        ListClinicsPage homePage = MyPageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectAdminUserNamePassword();
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
        ListClinicsPage homePage = MyPageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectAdminUserNamePassword();
        ChangePasswordPage changePasswordPage = homePage.goToChangePasswordPage();
        PasswordSuccessPage successPage = changePasswordPage.submitWithValidInput("password", "newPassword", "newPassword");

        assertNotNull(successPage.getSuccessMessageElement());
        cleanUp("newPassword", "password");
    }

    @Test
    public void testChangePasswordForClinician() {
        TestClinic testClinic = TestClinic.withMandatory().name("clinicForPasswordChangeTest");
        String clinicianName = unique("clinicianForPasswordChangeTest");
        TestClinician testClinician = TestClinician.withMandatory().name(clinicianName).userName(clinicianName).password("clinicianPassword").clinic(testClinic);
        new ClinicianDataService(webDriver).createWithClinc(testClinician);

        ChangePasswordPage changePasswordPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianName, testClinician.password()).goToChangePasswordPage();

        PasswordSuccessPage successPage = changePasswordPage.submitWithValidInput(testClinician.password(), "newPassword", "newPassword");
        assertNotNull(successPage.getSuccessMessageElement());
    }

    private void cleanUp(String oldPassword, String newPassword) {
        webDriver.get(ChangePasswordPage.CHANGE_PASSWORD_URL);
        ChangePasswordPage changePasswordPage = MyPageFactory.initElements(webDriver, ChangePasswordPage.class);
        changePasswordPage.submitWithValidInput(oldPassword, newPassword, newPassword);
    }
}
