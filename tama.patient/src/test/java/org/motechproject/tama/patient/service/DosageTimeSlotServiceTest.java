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
import org.motechproject.tama.patient.domain.DosageTimeSlot;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllDosageTimeSlots;

import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DosageTimeSlotServiceTest {

    @Mock
    private AllDosageTimeSlots allDosageTimeSlots;
    @Mock
    private Properties timeSlotProperties;

    private DosageTimeSlotService dosageTimeSlotService;

    @Before
    public void setUp() {
        initMocks(this);
        when(timeSlotProperties.getProperty(DosageTimeSlotService.SLOT_DURATION_MINS)).thenReturn("15");
        when(timeSlotProperties.getProperty(DosageTimeSlotService.MAX_PATIENTS_PER_SLOT)).thenReturn("10");
        dosageTimeSlotService = new DosageTimeSlotService(allDosageTimeSlots, timeSlotProperties);
    }

    @Test
    public void shouldAllotSlotsForTheGivenTreatmentAdvice() {
        String patientDocumentId = "patientDocumentId";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientDocumentId).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withDrugDosages("10:00am", "06:00pm").build();

        dosageTimeSlotService.allotSlots(patient, treatmentAdvice);

        ArgumentCaptor<DosageTimeSlot> dosageTimeSlotArgumentCaptor = ArgumentCaptor.forClass(DosageTimeSlot.class);
        verify(allDosageTimeSlots, times(2)).add(dosageTimeSlotArgumentCaptor.capture());
        List<DosageTimeSlot> timeSlots = dosageTimeSlotArgumentCaptor.getAllValues();
        assertEquals(patientDocumentId, timeSlots.get(0).getPatientDocumentId());
        assertEquals(new TimeOfDay(10, 0, TimeMeridiem.AM), timeSlots.get(0).getDosageTime());
        assertEquals(patientDocumentId, timeSlots.get(1).getPatientDocumentId());
        assertEquals(new TimeOfDay(6, 0, TimeMeridiem.PM), timeSlots.get(1).getDosageTime());
    }

    @Test
    public void shouldGetAllAvailableMorningTimeSlots() {
        when(allDosageTimeSlots.countOfPatientsAllottedForSlot(Matchers.<TimeOfDay>any(), Matchers.<TimeOfDay>any())).thenReturn(10, 10, 10, 10, 2, 10, 10, 2, 10);
        final List<String> timeSlots = dosageTimeSlotService.availableMorningSlots();
        assertEquals(2, timeSlots.size());
        assertEquals("01:00", timeSlots.get(0));
        assertEquals("01:45", timeSlots.get(1));
        assertFalse(timeSlots.contains("12:15"));
    }

    @Test
    public void shouldGetAllAvailableEveningTimeSlots() {
        when(allDosageTimeSlots.countOfPatientsAllottedForSlot(Matchers.<TimeOfDay>any(), Matchers.<TimeOfDay>any())).thenReturn(10, 10, 10, 10, 2, 10, 10, 2, 10);
        final List<String> timeSlots = dosageTimeSlotService.availableEveningSlots();
        assertEquals(2, timeSlots.size());
        assertEquals("02:00", timeSlots.get(0));
        assertEquals("02:45", timeSlots.get(1));
        assertFalse(timeSlots.contains("00:15"));
    }
}