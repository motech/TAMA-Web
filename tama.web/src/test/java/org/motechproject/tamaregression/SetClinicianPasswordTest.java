package org.motechproject.tamaregression;


import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.page.SetClinicianPasswordPage;
import org.motechproject.tamafunctionalframework.page.SetPasswordSuccessPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinic;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;

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
