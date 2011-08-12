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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")
public class LoginTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

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

    @After
    public void  tearDown() throws IOException {
       super.tearDown();
    }

}
