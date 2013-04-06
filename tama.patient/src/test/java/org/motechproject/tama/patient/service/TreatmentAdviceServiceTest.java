package org.motechproject.tama.patient.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.reporting.PillTimeRequestMapper;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.reporting.service.PatientReportingService;
import org.motechproject.tama.reports.contract.PillTimeRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TreatmentAdviceServiceTest {

    private static final String USER_NAME = "userName";

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllRegimens allRegimens;
    @Mock
    private CallPlan dailyCallPlan;
    @Mock
    private CallPlan weeklyCallPlan;
    @Mock
    private CallTimeSlotService callTimeSlotService;

    @Mock
    private AllPatientEventLogs allPatientEventLogs;

    @Mock
    private PatientReportingService patientReportingService;
    private TreatmentAdviceService treatmentAdviceService;

    @Before
    public void setUp() {
        initMocks(this);
        CallPlanRegistry callPlanRegistry = new CallPlanRegistry();
        callPlanRegistry.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        callPlanRegistry.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);
        treatmentAdviceService = new TreatmentAdviceService(allPatients, allTreatmentAdvices, allRegimens, callTimeSlotService, callPlanRegistry, patientReportingService, allPatientEventLogs);
    }

    @Test
    public void dailyPillReminderPatient_createsANewRegimen() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        PillTimeRequestMapper pillTimeRequestMapper = new PillTimeRequestMapper(treatmentAdvice);
        PillTimeRequest pillTimesRequest = pillTimeRequestMapper.map();

        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        treatmentAdviceService.createRegimen(treatmentAdvice, USER_NAME);
        verify(allTreatmentAdvices).add(treatmentAdvice, USER_NAME);
        verify(dailyCallPlan).enroll(patient, treatmentAdvice);
        verify(patientReportingService).savePillTimes(pillTimesRequest);
    }

    @Test
    public void fourDayRecallPatient_createsANewRegimen() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        PillTimeRequestMapper pillTimeRequestMapper = new PillTimeRequestMapper(treatmentAdvice);
        PillTimeRequest pillTimesRequest = pillTimeRequestMapper.map();

        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        treatmentAdviceService.createRegimen(treatmentAdvice, USER_NAME);
        verify(allTreatmentAdvices).add(treatmentAdvice, USER_NAME);
        verify(weeklyCallPlan).enroll(patient, treatmentAdvice);
        verify(patientReportingService).savePillTimes(pillTimesRequest);
    }

    @Test
    public void dailyPillReminderPatient_changesCurrentRegimen() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Regimen newRegimen = RegimenBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        PillTimeRequestMapper pillTimeRequestMapper = new PillTimeRequestMapper(treatmentAdvice);
        PillTimeRequest pillTimesRequest = pillTimeRequestMapper.map();

        when(allTreatmentAdvices.get(existingTreatmentAdvice.getId())).thenReturn(existingTreatmentAdvice);
        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);
        when(allRegimens.get(treatmentAdvice.getRegimenId())).thenReturn(newRegimen);

        final String newTreatmentAdviceId = treatmentAdviceService.changeRegimen(existingTreatmentAdvice.getId(), "stop", treatmentAdvice, USER_NAME);

        assertEquals(treatmentAdvice.getId(), newTreatmentAdviceId);
        verify(allTreatmentAdvices).add(treatmentAdvice, USER_NAME);
        verify(allTreatmentAdvices).update(existingTreatmentAdvice, USER_NAME);
        verify(allPatientEventLogs).add(new PatientEventLog(patient.getId(), PatientEvent.Regimen_Changed, newRegimen.getDisplayName()), USER_NAME);
        verify(dailyCallPlan).reEnroll(patient, treatmentAdvice);
        verify(callTimeSlotService).freeSlots(patient, existingTreatmentAdvice);
        verify(callTimeSlotService).allotSlots(patient, treatmentAdvice);
        verify(patientReportingService).savePillTimes(pillTimesRequest);
    }

    @Test
    public void fourDayRecallPatient_changesCurrentRegimen() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        Regimen newRegimen = RegimenBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        PillTimeRequestMapper pillTimeRequestMapper = new PillTimeRequestMapper(treatmentAdvice);
        PillTimeRequest pillTimesRequest = pillTimeRequestMapper.map();

        when(allTreatmentAdvices.get(existingTreatmentAdvice.getId())).thenReturn(existingTreatmentAdvice);
        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);
        when(allRegimens.get(treatmentAdvice.getRegimenId())).thenReturn(newRegimen);

        treatmentAdviceService.changeRegimen(existingTreatmentAdvice.getId(), "stop", treatmentAdvice, USER_NAME);
        verify(allTreatmentAdvices).add(treatmentAdvice, USER_NAME);
        verify(allTreatmentAdvices).update(existingTreatmentAdvice, USER_NAME);
        verify(allPatientEventLogs).add(new PatientEventLog(patient.getId(), PatientEvent.Regimen_Changed, newRegimen.getDisplayName()), USER_NAME);
        verify(weeklyCallPlan).reEnroll(patient, treatmentAdvice);
        verify(callTimeSlotService).freeSlots(patient, existingTreatmentAdvice);
        verify(callTimeSlotService, times(0)).allotSlots(patient, treatmentAdvice);
        verify(patientReportingService).savePillTimes(pillTimesRequest);
    }
}
