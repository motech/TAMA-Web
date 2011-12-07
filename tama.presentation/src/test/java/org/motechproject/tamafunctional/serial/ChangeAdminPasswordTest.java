package org.motechproject.tamafunctional.serial;


import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.ChangePasswordPage;
import org.motechproject.tamafunctional.page.ListClinicsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.PasswordSuccessPage;

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
