package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallServiceTest extends BaseUnitTest {
    @Mock
    protected FourDayRecallSchedulerService fourDayRecallSchedulerService;
    @Mock
    protected PatientService patientService;
    @Mock
    protected TreatmentAdviceService treatmentAdviceService;
    @Mock
    private AllPatientEventLogs allPatientEventLogs;

    private FourDayRecallService fourDayRecallService;

    @Before
    public void setUp() {
        initMocks(this);
        mockCurrentDate(new DateTime(1983, 1, 30, 10, 0));
        fourDayRecallService = new FourDayRecallService(fourDayRecallSchedulerService, patientService, treatmentAdviceService, allPatientEventLogs);
    }

    @Test
    public void newPatient_WithoutATreatmentAdviceEnrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        fourDayRecallService.enroll(patient, null);

        verify(treatmentAdviceService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(patientService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService, never()).scheduleFourDayRecallJobs(patient, null);
        assertPatientEventLog(patient);
    }

    @Test
    public void existingPatient_WithHisFirstTreatmentAdvice_Enrolls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        fourDayRecallService.enroll(patient, treatmentAdvice);

        verify(patientService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(treatmentAdviceService).registerCallPlan(CallPreference.FourDayRecall, fourDayRecallService);
        verify(fourDayRecallSchedulerService).scheduleFourDayRecallJobs(patient, treatmentAdvice);
        assertPatientEventLog(patient);
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
        assertPatientEventLog(patient);
    }

    private void assertPatientEventLog(Patient patient) {
        ArgumentCaptor<PatientEventLog> eventLogArgumentCaptor = ArgumentCaptor.forClass(PatientEventLog.class);
        verify(allPatientEventLogs).add(eventLogArgumentCaptor.capture());
        assertEquals(PatientEvent.Switched_To_Weekly_Adherence, eventLogArgumentCaptor.getValue().getEvent());
        assertEquals(patient.getId(), eventLogArgumentCaptor.getValue().getPatientId());
        assertEquals(DateUtil.now(), eventLogArgumentCaptor.getValue().getDate());
    }
}
