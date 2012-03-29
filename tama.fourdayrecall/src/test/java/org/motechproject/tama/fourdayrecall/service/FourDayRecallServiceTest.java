package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.testing.utils.BaseUnitTest;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallServiceTest extends BaseUnitTest {
    @Mock
    protected FourDayRecallSchedulerService fourDayRecallSchedulerService;
    @Mock
    private CallPlanRegistry callPlanRegistry;

    private FourDayRecallService fourDayRecallService;

    @Before
    public void setUp() {
        initMocks(this);
        mockCurrentDate(new DateTime(1983, 1, 30, 10, 0));
        fourDayRecallService = new FourDayRecallService(fourDayRecallSchedulerService, callPlanRegistry);
    }

    @Test
    public void newPatient_WithoutATreatmentAdviceEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = null;
        fourDayRecallService.enroll(patient, treatmentAdvice);

        verify(callPlanRegistry).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService, never()).scheduleFourDayRecallJobs(patient, treatmentAdvice);
    }

    @Test
    public void existingPatient_WithHisFirstTreatmentAdvice_Enrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        fourDayRecallService.enroll(patient, treatmentAdvice);

        verify(callPlanRegistry).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService).scheduleFourDayRecallJobs(patient, treatmentAdvice);
    }

    @Test
    public void existingPatient_WithoutATreatmentAdviceDisEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = null;
        fourDayRecallService.disEnroll(patient, treatmentAdvice);

        verify(callPlanRegistry).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService, never()).unscheduleFourDayRecallJobs(patient);
    }

    @Test
    public void patientReEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        fourDayRecallService.reEnroll(patient, treatmentAdvice);

        verify(callPlanRegistry).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService).unscheduleFourDayRecallJobs(patient);
        verify(fourDayRecallSchedulerService).scheduleFourDayRecallJobs(patient, treatmentAdvice);
    }

}
