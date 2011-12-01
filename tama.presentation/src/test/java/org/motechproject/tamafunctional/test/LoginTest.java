package org.motechproject.tamafunctional.test;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.ListClinicsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginTest extends BaseTest {
    @Test
    public void testLoginFailure() {
        LoginPage page = MyPageFactory.initElements(webDriver, LoginPage.class).loginWithIncorrectAdminUserNamePassword();
        assertEquals(LoginPage.FAILURE_MESSAGE, page.errorMessage());
    }

    @Test
    public void testLoginSuccess() {
        ListClinicsPage homePage = MyPageFactory.initElements(webDriver, LoginPage.class).loginWithCorrectAdminUserNamePassword();
        assertTrue(StringUtils.contains(homePage.getListClinicsPane(), ListClinicsPage.WELCOME_MESSAGE));
        homePage.logout();
    }
}
