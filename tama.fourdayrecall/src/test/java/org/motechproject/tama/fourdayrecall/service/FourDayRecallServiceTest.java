package org.motechproject.tama.fourdayrecall.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.TreatmentAdviceService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallServiceTest {
    @Mock
    protected FourDayRecallSchedulerService fourDayRecallSchedulerService;
    @Mock
    protected TreatmentAdviceService treatmentAdviceService;
    private FourDayRecallService fourDayRecallService;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallService = new FourDayRecallService(fourDayRecallSchedulerService, treatmentAdviceService);
    }

    @Test
    public void patientEnrolls_ToFourDayRecall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        fourDayRecallService.enroll(patient, treatmentAdvice);

        verify(treatmentAdviceService).registerFourDayRecall(fourDayRecallService);
        verify(fourDayRecallSchedulerService).scheduleFourDayRecallJobs(patient, treatmentAdvice);
    }

    @Test
    public void patientReEnrolls_ToFourDayRecall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        fourDayRecallService.reEnroll(patient, treatmentAdvice);

        verify(treatmentAdviceService).registerFourDayRecall(fourDayRecallService);
        verify(fourDayRecallSchedulerService).rescheduleFourDayRecallJobs(patient, treatmentAdvice);
    }

}
