package org.motechproject.tamafunctional.test;


import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.page.SetClinicianPasswordPage;
import org.motechproject.tamafunctional.page.SetPasswordSuccessPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;

import static org.junit.Assert.assertNotNull;

public class SetClinicianPasswordTest extends BaseTest {

    @Test
    public void testChangePasswordForClinician() {
        TestClinic testClinic = TestClinic.withMandatory().name("clinicForClinicianPasswordChangeTest");
        String clinicianName = unique("clinicianForClinicianPasswordChangeTest");
        TestClinician testClinician = TestClinician.withMandatory().name(clinicianName).userName(clinicianName).password("clinicianOldPassword").clinic(testClinic);
        SetClinicianPasswordPage clinicianSetPasswordPage = new ClinicianDataService(webDriver).createWithClinicAndDontLogout(testClinician).goToSetClinicianPasswordPage();

        SetPasswordSuccessPage successPage = clinicianSetPasswordPage.submitWithValidInput("clinicianNewPassword", "clinicianNewPassword");
        assertNotNull(successPage.getSuccessMessageElement());
    }
}
