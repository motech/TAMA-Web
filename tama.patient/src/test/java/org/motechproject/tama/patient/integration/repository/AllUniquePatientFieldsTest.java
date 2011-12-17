package org.motechproject.tama.patient.integration.repository;

import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.UniquePatientField;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class AllUniquePatientFieldsTest extends SpringIntegrationTest {

    @Autowired
    private AllUniquePatientFields allPatientUniqueFields;

    @Test
    public void shouldPersistPatientUniqueFields() {
        Clinic clinic = new Clinic("C1");
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("P1").withClinic(clinic).withMobileNumber("1234567890").withPasscode("1234").build();
        patient.setId("1234");

        allPatientUniqueFields.add(patient);

        List<UniquePatientField> uniquePatientFields = allPatientUniqueFields.get(patient);

        markForDeletion(uniquePatientFields.get(0));
        markForDeletion(uniquePatientFields.get(1));

        assertEquals(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT + "C1/P1", uniquePatientFields.get(0).getId());
        assertEquals(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT + "1234567890/1234", uniquePatientFields.get(1).getId());
    }

    @Test
    public void shouldRemovePatientUniqueFields() {
        Clinic clinic = new Clinic("C1");
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("P1").withClinic(clinic).withMobileNumber("1234567890").withPasscode("1234").build();
        patient.setId("1234");

        allPatientUniqueFields.add(patient);
        allPatientUniqueFields.remove(patient);
        List<UniquePatientField> uniquePatientFields = allPatientUniqueFields.get(patient);

        assertEquals(0, uniquePatientFields.size());
    }
}
