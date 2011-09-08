package org.motechproject.tama.integration.repository;

import org.ektorp.DocumentNotFoundException;
import org.junit.Test;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.UniquePatientField;
import org.motechproject.tama.repository.AllUniquePatientFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllUniquePatientFieldsTest extends SpringIntegrationTest{

    @Autowired
    private AllUniquePatientFields allPatientUniqueFields;

    @Test
    public void shouldPersistPatientUniqueFields() {
        Clinic clinic = new Clinic("C1");
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("P1").withClinic(clinic).withMobileNumber("1234567890").withPasscode("1234").build();

        allPatientUniqueFields.add(patient);

        List<UniquePatientField> uniquePatientFields = allPatientUniqueFields.get(patient);

        markForDeletion(uniquePatientFields.get(0));
        markForDeletion(uniquePatientFields.get(1));

        assertEquals("ClinicAndPatientIdUniqueConstraint:clinic_id_C1_patient_id_P1", uniquePatientFields.get(0).getId());
        assertEquals("PhoneNumberAndPasscodeUniqueConstraint:ph_no_1234567890_pass_code_1234", uniquePatientFields.get(1).getId());
    }

    @Test
    @ExpectedException(DocumentNotFoundException.class)
    public void shouldRemovePatientUniqueFields() {
        Clinic clinic = new Clinic("C1");
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("P1").withClinic(clinic).withMobileNumber("1234567890").withPasscode("1234").build();

        allPatientUniqueFields.add(patient);
        allPatientUniqueFields.remove(patient);
        allPatientUniqueFields.get(patient);

    }


}
