package org.motechproject.tamaregression;


import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.page.ChangePasswordPage;
import org.motechproject.tamafunctionalframework.page.ListClinicsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.PasswordSuccessPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinic;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
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
    public void testChangePasswordForClinician() {
        TestClinic testClinic = TestClinic.withMandatory().name("clinicForPasswordChangeTest");
        String clinicianName = unique("clinicianForPasswordChangeTest");
        TestClinician testClinician = TestClinician.withMandatory().name(clinicianName).userName(clinicianName).password("clinicianPassword").clinic(testClinic);
        new ClinicianDataService(webDriver).createWithClinic(testClinician);

        ChangePasswordPage changePasswordPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianName, testClinician.password()).goToChangePasswordPage();

        PasswordSuccessPage successPage = changePasswordPage.submitWithValidInput(testClinician.password(), "newPassword", "newPassword");
        assertNotNull(successPage.getSuccessMessageElement());
    }
}
