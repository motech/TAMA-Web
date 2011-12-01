package org.motechproject.tamafunctional.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowClinicPage;
import org.motechproject.tamafunctional.testdata.TestClinic;

import java.io.IOException;

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
