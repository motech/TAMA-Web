package org.motechproject.tama.facility.integration.repository;

import org.ektorp.DocumentNotFoundException;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.domain.ClinicianId;
import org.motechproject.tama.facility.repository.AllClinicianIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationFacilityContext.xml", inheritLocations = false)
public class ClinicianIdsTest extends SpringIntegrationTest {

    @Autowired
    private AllClinicianIds allClinicianIds;

    @Test
    public void shouldPersistClinicianId() {
        Clinician clinician = new Clinician();
        clinician.setUsername("CL1");

        allClinicianIds.add(clinician);

        ClinicianId clinicianId = allClinicianIds.get(clinician.getUsername());
        assertEquals(clinician.getUsername(), clinicianId.getId());
        markForDeletion(clinicianId);
    }

    @Test
    @ExpectedException(DocumentNotFoundException.class)
    public void shouldRemoveClinicianId() {
        Clinician clinician = new Clinician();
        clinician.setUsername("CL1");

        allClinicianIds.add(clinician);
        allClinicianIds.remove(clinician);
        allClinicianIds.get(clinician.getUsername());
    }
}
