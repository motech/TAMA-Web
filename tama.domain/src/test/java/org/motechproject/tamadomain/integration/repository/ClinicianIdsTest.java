package org.motechproject.tamadomain.integration.repository;

import org.ektorp.DocumentNotFoundException;
import org.junit.Test;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamadomain.domain.Clinician;
import org.motechproject.tamadomain.domain.ClinicianId;
import org.motechproject.tamadomain.repository.AllClinicianIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import static org.junit.Assert.assertEquals;

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
    public void shouldRemoveClinicianId(){
        Clinician clinician = new Clinician();
        clinician.setUsername("CL1");

        allClinicianIds.add(clinician);
        allClinicianIds.remove(clinician);
        allClinicianIds.get(clinician.getUsername());
    }
}
