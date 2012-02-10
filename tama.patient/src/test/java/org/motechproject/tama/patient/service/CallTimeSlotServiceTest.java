package org.motechproject.tama.patient.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallTimeSlot;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllCallTimeSlots;

import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        assertEquals(new TimeOfDay(10, 0, TimeMeridiem.AM), timeSlots.get(0).getCallTime());
        assertEquals(patientDocumentId, timeSlots.get(1).getPatientDocumentId());
        assertEquals(new TimeOfDay(6, 0, TimeMeridiem.PM), timeSlots.get(1).getCallTime());
    }

    @Test
    public void shouldGetAllAvailableMorningTimeSlots() {
        when(allCallTimeSlots.countOfPatientsAllottedForSlot(Matchers.<TimeOfDay>any(), Matchers.<TimeOfDay>any())).thenReturn(10, 10, 10, 10, 2, 10, 10, 2, 10);
        final List<String> timeSlots = callTimeSlotService.availableMorningSlots();
        assertEquals(2, timeSlots.size());
        assertEquals("01:00", timeSlots.get(0));
        assertEquals("01:45", timeSlots.get(1));
        assertFalse(timeSlots.contains("12:15"));
    }

    @Test
    public void shouldGetAllAvailableEveningTimeSlots() {
        when(allCallTimeSlots.countOfPatientsAllottedForSlot(Matchers.<TimeOfDay>any(), Matchers.<TimeOfDay>any())).thenReturn(10, 10, 10, 10, 2, 10, 10, 2, 10);
        final List<String> timeSlots = callTimeSlotService.availableEveningSlots();
        assertEquals(2, timeSlots.size());
        assertEquals("02:00", timeSlots.get(0));
        assertEquals("02:45", timeSlots.get(1));
        assertFalse(timeSlots.contains("00:15"));
    }
}