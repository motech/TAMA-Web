package org.motechproject.tama.patient.service;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.CallTimeSlotBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallTimeSlot;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllCallTimeSlots;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallTimeSlotServiceTest {

    @Mock
    private AllCallTimeSlots allCallTimeSlots;
    @Mock
    private Properties timeSlotProperties;

    private CallTimeSlotService callTimeSlotService;

    @Before
    public void setUp() {
        initMocks(this);
        when(timeSlotProperties.getProperty(CallTimeSlotService.SLOT_DURATION_MINS)).thenReturn("15");
        when(timeSlotProperties.getProperty(CallTimeSlotService.MAX_PATIENTS_PER_SLOT)).thenReturn("10");
        callTimeSlotService = new CallTimeSlotService(allCallTimeSlots, timeSlotProperties);
    }

    @Test
    public void shouldAllotSlotsForTheGivenTreatmentAdvice() {
        String patientDocumentId = "patientDocumentId";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientDocumentId).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withDrugDosages("10:00am", "06:00pm").build();

        callTimeSlotService.allotSlots(patient, treatmentAdvice);

        ArgumentCaptor<CallTimeSlot> callTimeSlotArgumentCaptor = ArgumentCaptor.forClass(CallTimeSlot.class);
        verify(allCallTimeSlots, times(2)).add(callTimeSlotArgumentCaptor.capture());
        List<CallTimeSlot> timeSlots = callTimeSlotArgumentCaptor.getAllValues();
        assertEquals(patientDocumentId, timeSlots.get(0).getPatientDocumentId());
        assertEquals(new LocalTime(10, 0), timeSlots.get(0).getCallTime());
        assertEquals(patientDocumentId, timeSlots.get(1).getPatientDocumentId());
        assertEquals(new LocalTime(18, 0), timeSlots.get(1).getCallTime());
    }

    @Test
    public void shouldFreeSlotsForTheGivenTreatmentAdvice() {
        String patientDocumentId = "patientDocumentId";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientDocumentId).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withDrugDosages("10:00am", "06:00pm").build();
        LocalTime dose1Time = new LocalTime(10, 0);
        LocalTime dose2Time = new LocalTime(18, 0);
        List<CallTimeSlot> dose1TimeSlot = Arrays.asList(CallTimeSlotBuilder.startRecording().withCallTime(dose1Time).withPatientDocumentId(patientDocumentId).build());
        when(allCallTimeSlots.findBySlotTimeAndPatientId(dose1Time, patientDocumentId)).thenReturn(dose1TimeSlot);
        List<CallTimeSlot> dose2TimeSlot = Arrays.asList(CallTimeSlotBuilder.startRecording().withCallTime(dose2Time).withPatientDocumentId(patientDocumentId).build());
        when(allCallTimeSlots.findBySlotTimeAndPatientId(dose2Time, patientDocumentId)).thenReturn(dose2TimeSlot);

        callTimeSlotService.freeSlots(patient, treatmentAdvice);

        ArgumentCaptor<CallTimeSlot> callTimeSlotArgumentCaptor = ArgumentCaptor.forClass(CallTimeSlot.class);
        verify(allCallTimeSlots, times(2)).remove(callTimeSlotArgumentCaptor.capture());
        List<CallTimeSlot> timeSlots = callTimeSlotArgumentCaptor.getAllValues();
        assertEquals(patientDocumentId, timeSlots.get(0).getPatientDocumentId());
        assertEquals(dose1Time, timeSlots.get(0).getCallTime());
        assertEquals(patientDocumentId, timeSlots.get(1).getPatientDocumentId());
        assertEquals(dose2Time, timeSlots.get(1).getCallTime());

    }

    @Test
    public void shouldGetAllMorningTimeSlots() {
        when(allCallTimeSlots.countOfPatientsAllottedForSlot(Matchers.<LocalTime>any(), Matchers.<LocalTime>any())).thenReturn(2);
        final List<String> morningTimeSlots = callTimeSlotService.availableMorningSlots();
        assertEquals(48, morningTimeSlots.size());
    }

    @Test
    public void shouldGetAllEveningTimeSlots() {
        when(allCallTimeSlots.countOfPatientsAllottedForSlot(Matchers.<LocalTime>any(), Matchers.<LocalTime>any())).thenReturn(2);
        final List<String> eveningTimeSlots = callTimeSlotService.availableEveningSlots();
        assertEquals(48, eveningTimeSlots.size());
    }

    @Test
    public void shouldGetAllAvailableMorningTimeSlots() {
        when(allCallTimeSlots.countOfPatientsAllottedForSlot(Matchers.<LocalTime>any(), Matchers.<LocalTime>any())).thenReturn(2, 10, 10, 10, 10, 2, 10, 10, 2, 10);
        final List<String> timeSlots = callTimeSlotService.availableMorningSlots();
        assertEquals(3, timeSlots.size());
        assertEquals("12:00", timeSlots.get(0));
        assertEquals("01:15", timeSlots.get(1));
        assertEquals("02:00", timeSlots.get(2));
    }

    @Test
    public void shouldGetAllAvailableEveningTimeSlots() {
        when(allCallTimeSlots.countOfPatientsAllottedForSlot(Matchers.<LocalTime>any(), Matchers.<LocalTime>any())).thenReturn(2, 10, 10, 10, 10, 2, 10, 10, 2, 10);
        final List<String> timeSlots = callTimeSlotService.availableEveningSlots();
        assertEquals(3, timeSlots.size());
        assertEquals("12:00", timeSlots.get(0));
        assertEquals("01:15", timeSlots.get(1));
        assertEquals("02:00", timeSlots.get(2));
    }
}