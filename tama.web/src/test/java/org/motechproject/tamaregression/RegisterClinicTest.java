package org.motechproject.tamaregression;

import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowClinicPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinic;

import static junit.framework.Assert.assertEquals;

public class RegisterClinicTest extends BaseTest {
    @Test
    public void testClinicRegistration() {
        TestClinic clinic = TestClinic.withMandatory();
        ShowClinicPage showClinicPage = MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic);

        assertEquals(clinic.phoneNumber(), showClinicPage.getPhone());
        assertEquals(clinic.address(), showClinicPage.getAddress());
        showClinicPage.logout();
    }
}
