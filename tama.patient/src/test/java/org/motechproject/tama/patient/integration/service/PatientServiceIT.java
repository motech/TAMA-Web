package org.motechproject.tama.patient.integration.service;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class PatientServiceIT extends SpringIntegrationTest {

    public static final String USER_NAME = "userName";
    @Autowired
    private PatientService patientService;

    @Autowired
    private AllPatients allPatients;

    @Autowired
    private AllPatientEventLogs allPatientEventLogs;

    @Autowired
    private AllRegimens allRegimens;

    @Autowired
    private AllTreatmentAdvices allTreatmentAdvices;

    @Test
    public void shouldSuspendPatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        allPatients.add(patient, USER_NAME);
        markForDeletion(patient);

        patientService.suspend(patient.getId(), USER_NAME);

        Patient reloadedPatient = allPatients.get(patient.getId());
        assertEquals(Status.Suspended, reloadedPatient.getStatus());
        assertEquals(DateUtil.today(), reloadedPatient.getLastSuspendedDate().toLocalDate());

        List<PatientEventLog> patientEventLogs = allPatientEventLogs.findByPatientId(patient.getId());
        assertEquals(1, patientEventLogs.size());
        markForDeletion(patientEventLogs.get(0));
        assertEquals(PatientEvent.Suspension, patientEventLogs.get(0).getEvent());
    }

    @Test
    public void shouldActivatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        allPatients.add(patient, USER_NAME);
        markForDeletion(patient);

        patientService.activate(patient.getId(), USER_NAME);

        Patient reloadedPatient = allPatients.get(patient.getId());
        assertEquals(Status.Active, reloadedPatient.getStatus());
        assertEquals(DateUtil.today(), reloadedPatient.getActivationDate().toLocalDate());

        List<PatientEventLog> patientEventLogs = allPatientEventLogs.findByPatientId(patient.getId());
        assertEquals(1, patientEventLogs.size());
        markForDeletion(patientEventLogs.get(0));
        assertEquals(PatientEvent.Activation, patientEventLogs.get(0).getEvent());
    }

    @Test
    public void shouldDeactivatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).build();
        allPatients.add(patient, USER_NAME);
        markForDeletion(patient);

        patientService.deactivate(patient.getId(), Status.Loss_To_Follow_Up, USER_NAME);

        Patient reloadedPatient = allPatients.get(patient.getId());
        Assert.assertEquals(Status.Loss_To_Follow_Up, reloadedPatient.getStatus());
        Assert.assertEquals(DateUtil.today(), reloadedPatient.getLastDeactivationDate().toLocalDate());

        List<PatientEventLog> patientEventLogs = allPatientEventLogs.findByPatientId(patient.getId());
        assertEquals(0, patientEventLogs.size());
    }

    @Test
    public void shouldDeactivatePatient_AndCreatePatientEventLogWhenTemporaryDeactivation() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).build();
        allPatients.add(patient, USER_NAME);
        markForDeletion(patient);

        patientService.deactivate(patient.getId(), Status.Temporary_Deactivation, USER_NAME);

        Patient reloadedPatient = allPatients.get(patient.getId());
        Assert.assertEquals(Status.Temporary_Deactivation, reloadedPatient.getStatus());
        Assert.assertEquals(DateUtil.today(), reloadedPatient.getLastDeactivationDate().toLocalDate());

        List<PatientEventLog> patientEventLogs = allPatientEventLogs.findByPatientId(patient.getId());
        assertEquals(1, patientEventLogs.size());
        markForDeletion(patientEventLogs.get(0));
        assertEquals(PatientEvent.Temporary_Deactivation, patientEventLogs.get(0).getEvent());
    }

    @Test
    public void shouldReturnCurrentRegimen() {
        Regimen regimen = RegimenBuilder.startRecording().withDefaults().build();
        allRegimens.add(regimen);

        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        allPatients.add(patient, USER_NAME);

        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults()
                .withPatientId(patient.getId()).withRegimenId(regimen.getId()).build();
        allTreatmentAdvices.add(treatmentAdvice, USER_NAME);

        markForDeletion(regimen);
        markForDeletion(patient);
        markForDeletion(treatmentAdvice);

        assertEquals(regimen.getId(), patientService.currentRegimen(patient).getId());
    }
}
