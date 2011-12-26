package org.motechproject.tama.fourdayrecall.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallServiceTest {
    @Mock
    protected FourDayRecallSchedulerService fourDayRecallSchedulerService;
    @Mock
    protected PatientService patientService;
    @Mock
    protected TreatmentAdviceService treatmentAdviceService;
    private FourDayRecallService fourDayRecallService;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallService = new FourDayRecallService(fourDayRecallSchedulerService, patientService, treatmentAdviceService);
    }

    @Test
    public void newPatient_WithoutATreatmentAdviceEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        fourDayRecallService.enroll(patient, null);

        verify(treatmentAdviceService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(patientService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService, never()).scheduleFourDayRecallJobs(patient, null);
    }

    @Test
    public void existingPatient_WithHisFirstTreatmentAdvice_Enrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        fourDayRecallService.enroll(patient, treatmentAdvice);

        verify(patientService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(treatmentAdviceService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService).scheduleFourDayRecallJobs(patient, treatmentAdvice);
    }

    @Test
    public void existingPatient_WithoutATreatmentAdviceDisEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        fourDayRecallService.disEnroll(patient, null);

        verify(treatmentAdviceService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(patientService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService, never()).unscheduleFourDayRecallJobs(patient);
    }

    @Test
    public void patientReEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        fourDayRecallService.reEnroll(patient, treatmentAdvice);

        verify(patientService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(treatmentAdviceService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService).unscheduleFourDayRecallJobs(patient);
        verify(fourDayRecallSchedulerService).scheduleFourDayRecallJobs(patient, treatmentAdvice);
    }
}
