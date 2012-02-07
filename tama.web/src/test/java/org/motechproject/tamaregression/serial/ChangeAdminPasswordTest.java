package org.motechproject.tamaregression.serial;


import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.ChangePasswordPage;
import org.motechproject.tamafunctionalframework.page.ListClinicsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.PasswordSuccessPage;

import static junit.framework.Assert.assertNotNull;

public class ChangeAdminPasswordTest extends BaseTest {
    @Test
    public void testChangePasswordForAdministrator() {
        ListClinicsPage homePage = MyPageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectAdminUserNamePassword();
        ChangePasswordPage changePasswordPage = homePage.goToChangePasswordPage();
        PasswordSuccessPage successPage = changePasswordPage.submitWithValidInput("password", "newPassword", "newPassword");

        assertNotNull(successPage.getSuccessMessageElement());
        cleanUp("newPassword", "password");
    }

    private void cleanUp(String oldPassword, String newPassword) {
        webDriver.get(ChangePasswordPage.CHANGE_PASSWORD_URL);
        ChangePasswordPage changePasswordPage = MyPageFactory.initElements(webDriver, ChangePasswordPage.class);
        changePasswordPage.submitWithValidInput(oldPassword, newPassword, newPassword);
    }
}
