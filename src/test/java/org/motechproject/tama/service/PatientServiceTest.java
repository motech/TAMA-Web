package org.motechproject.tama.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllUniquePatientFields;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest {

    private PatientService patientService;

    private Patient dbPatient;

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllUniquePatientFields allUniquePatientFields;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private TamaSchedulerService tamaSchedulerService;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;

    @Before
    public void setUp() {
        initMocks(this);
        patientService = new PatientService(allPatients, allUniquePatientFields, tamaSchedulerService, allTreatmentAdvices, pillReminderService);
        dbPatient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withRevision("revision").build();
        when(allPatients.get(dbPatient.getId())).thenReturn(dbPatient);
    }

    @Test
    public void shouldUpdatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").build();
        patientService.update(patient);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(allPatients).update(captor.capture());
        assertEquals(captor.getValue().getRevision(), "revision");
    }

    @Test
    public void shouldUnschedulePillReminderCallsWhenCallPreferenceIsChangedToFourDayRecall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patientService.update(patient);
        verify(pillReminderService).unscheduleJobs(patient.getId());
    }

    @Test
    public void shouldNotUnschedulePillReminderCallsWhenCallPreferenceIsNotChanged() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").build();
        patientService.update(patient);
        verify(pillReminderService, never()).unscheduleJobs(patient.getId());
    }

    @Test
    public void shouldUnscheduleJobsForAdherenceTrendFeedbackOutboxMessage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        when(allTreatmentAdvices.findByPatientId(patient.getId())).thenReturn(treatmentAdvice);
        patientService.update(patient);
        verify(tamaSchedulerService).unscheduleJobForAdherenceTrendFeedback(treatmentAdvice);
    }
}
