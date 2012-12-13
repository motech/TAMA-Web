package org.motechproject.tama.facility.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.refdata.domain.Administrator;
import org.motechproject.tama.refdata.domain.Analyst;
import org.motechproject.tama.refdata.repository.AllAdministrators;
import org.motechproject.tama.refdata.repository.AllAnalysts;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllTAMAUsersTest {

    @Mock
    private AllClinicians allClinicians;
    @Mock
    private AllAdministrators allAdministrators;
    @Mock
    private AllAnalysts allAnalysts;

    private AllTAMAUsers allTAMAUsers;

    @Before
    public void setup() {
        initMocks(this);
        allTAMAUsers = new AllTAMAUsers(allClinicians, allAdministrators, allAnalysts);
    }

    @Test
    public void shouldUpdatePasswordForClinician() {
        Clinician clinician = new Clinician();
        clinician.setUsername("clinicianUserName");

        allTAMAUsers.update(clinician, clinician.getUsername());
        verify(allClinicians).updatePassword(clinician, clinician.getUsername());
    }

    @Test
    public void shouldUpdatePasswordForAdministrators() {
        Administrator admin = new Administrator();
        admin.setUsername("clinicianUserName");

        allTAMAUsers.update(admin, admin.getUsername());
        verify(allAdministrators).updatePassword(admin);
    }

    @Test
    public void shouldUpdatePasswordForAnalysts() {
        Analyst analyst = new Analyst();
        analyst.setUsername("clinicianUserName");

        allTAMAUsers.update(analyst, analyst.getUsername());
        verify(allAnalysts).updatePassword(analyst);
    }
}
