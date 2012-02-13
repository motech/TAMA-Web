package org.motechproject.tama.dailypillreminder.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DailyPillReminderServiceTest {

    @Mock
    protected PillReminderService pillReminderService;
    @Mock
    protected PillRegimenRequestMapper pillRegimenRequestMapper;
    @Mock
    protected DailyPillReminderSchedulerService dailyPillReminderSchedulerService;
    @Mock
    protected PatientService patientService;
    @Mock
    protected TreatmentAdviceService treatmentAdviceService;
    private DailyPillReminderService dailyPillReminderService;

    @Before
    public void setUp() {
        initMocks(this);
        dailyPillReminderService = new DailyPillReminderService(pillReminderService, pillRegimenRequestMapper, dailyPillReminderSchedulerService, patientService, treatmentAdviceService);
    }

    @Test
    public void newPatient_WithoutATreatmentAdviceEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        dailyPillReminderService.enroll(patient, null);

        verify(treatmentAdviceService).registerCallPlan(CallPreference.DailyPillReminder, dailyPillReminderService);
        verify(patientService).registerCallPlan(CallPreference.DailyPillReminder, dailyPillReminderService);
        verify(pillRegimenRequestMapper, never()).map(null, null);
        verify(pillReminderService, never()).createNew(any(DailyPillRegimenRequest.class));
        verify(dailyPillReminderSchedulerService, never()).scheduleDailyPillReminderJobs(patient, null);
    }

    @Test
    public void existingPatient_WithHisFirstTreatmentAdvice_Enrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        dailyPillReminderService.enroll(patient, treatmentAdvice);

        verify(treatmentAdviceService).registerCallPlan(CallPreference.DailyPillReminder, dailyPillReminderService);
        verify(patientService).registerCallPlan(CallPreference.DailyPillReminder, dailyPillReminderService);
        verify(pillRegimenRequestMapper).map(patient, treatmentAdvice);
        verify(pillReminderService).createNew(any(DailyPillRegimenRequest.class));
        verify(dailyPillReminderSchedulerService).scheduleDailyPillReminderJobs(patient, treatmentAdvice);
    }

    @Test
    public void existingPatient_WithoutATreatmentAdviceDisEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        dailyPillReminderService.disEnroll(patient, null);

        verify(treatmentAdviceService).registerCallPlan(CallPreference.DailyPillReminder, dailyPillReminderService);
        verify(patientService).registerCallPlan(CallPreference.DailyPillReminder, dailyPillReminderService);
        verify(pillReminderService, never()).remove(patient.getId());
        verify(dailyPillReminderSchedulerService, never()).unscheduleDailyPillReminderJobs(patient);
    }

    @Test
    public void patientReEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        dailyPillReminderService.reEnroll(patient, treatmentAdvice);

        verify(treatmentAdviceService).registerCallPlan(CallPreference.DailyPillReminder, dailyPillReminderService);
        verify(patientService).registerCallPlan(CallPreference.DailyPillReminder, dailyPillReminderService);
        verify(pillRegimenRequestMapper).map(patient, treatmentAdvice);
        verify(pillReminderService).remove(patient.getId());
        verify(pillReminderService).createNew(any(DailyPillRegimenRequest.class));
        verify(dailyPillReminderSchedulerService).unscheduleDailyPillReminderJobs(patient);
        verify(dailyPillReminderSchedulerService).scheduleDailyPillReminderJobs(patient, treatmentAdvice);
    }
}
