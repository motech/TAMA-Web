package org.motechproject.tama.dailypillreminder.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.*;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.repository.AllRegimens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest_PatientOnDailyPillReminder {

    private PatientService patientService;

    private Patient dbPatient;

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllUniquePatientFields allUniquePatientFields;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private DailyPillReminderSchedulerService dailyPillReminderSchedulerService;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllRegimens allRegimens;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        dbPatient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withRevision("revision").withCallPreference(CallPreference.DailyPillReminder)
                .withBestCallTime(new TimeOfDay(11, 20, TimeMeridiem.PM)).build();
        when(allPatients.get(dbPatient.getId())).thenReturn(dbPatient);
        patientService = new PatientService(allPatients, allUniquePatientFields);
    }

    @Test
    public void shouldUpdatePatient() {
        CallPreference callPreference = dbPatient.getPatientPreferences().getCallPreference();
        TimeOfDay bestCallTime = dbPatient.getPatientPreferences().getBestCallTime();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMobileNumber("7777777777").withId("patient_id").withCallPreference(callPreference).withBestCallTime(bestCallTime).build();
        patientService.update(patient);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(allPatients).update(captor.capture());
        assertEquals(captor.getValue().getMobilePhoneNumber(), "7777777777");
        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
    }

    @Test
    public void shouldNotUnschedulePillReminderCalls_WhenCallPreferenceIsChangedToFourDayRecall_AndPatientDoesNotHaveATreatmentAdvice() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withCallPreference(CallPreference.FourDayRecall).withBestCallTime(new TimeOfDay(null, null, null)).build();

        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(null);

        patientService.update(patient);
        verify(pillReminderService, never()).remove(patient.getId());
    }

    @Test
    public void shouldNotUnschedulePillReminderCalls_WhenCallPreferenceIsNotChanged() {
        PatientPreferences patientPreferences = dbPatient.getPatientPreferences();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(patientPreferences.getCallPreference()).withBestCallTime(patientPreferences.getBestCallTime()).withId("patient_id").build();
        patientService.update(patient);
        verify(pillReminderService, never()).remove(patient.getId());
    }
}
