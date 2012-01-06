package org.motechproject.tama.patient.integration.service;

import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class PatientServiceIT extends SpringIntegrationTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AllPatients allPatients;

    @Test
    public void shouldSuspendPatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        allPatients.add(patient);
        markForDeletion(patient);

        patientService.suspend(patient.getId());

        Patient reloadedPatient = allPatients.get(patient.getId());
        assertEquals(Status.Suspended, reloadedPatient.getStatus());
        assertEquals(DateUtil.today(), reloadedPatient.getLastSuspendedDate().toLocalDate());
    }

    @Test
    public void shouldActivatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        allPatients.add(patient);
        markForDeletion(patient);

        patientService.activate(patient.getId());

        Patient reloadedPatient = allPatients.get(patient.getId());
        assertEquals(Status.Active, reloadedPatient.getStatus());
        assertEquals(DateUtil.today(), reloadedPatient.getActivationDate().toLocalDate());
    }
}
