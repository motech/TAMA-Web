package org.motechproject.tama.patient.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.patient.domain.DosageTimeSlot;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.motechproject.tama.patient.repository.AllDosageTimeSlots;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DosageTimeSlotServiceTest {

    @Mock
    private AllDosageTimeSlots allDosageTimeSlots;

    private DosageTimeSlotService dosageTimeSlotService;

    @Before
    public void setUp() {
        initMocks(this);
        dosageTimeSlotService = new DosageTimeSlotService(allDosageTimeSlots);
    }

    @Test
    public void shouldAllotASlotToThePatient() {
        String patientDocumentId = "patientDocumentId";
        TimeOfDay slotTime = new TimeOfDay(10, 0, TimeMeridiem.PM);

        dosageTimeSlotService.allotSlot(patientDocumentId, slotTime);

        ArgumentCaptor<DosageTimeSlot> dosageTimeSlotArgumentCaptor = ArgumentCaptor.forClass(DosageTimeSlot.class);
        verify(allDosageTimeSlots).add(dosageTimeSlotArgumentCaptor.capture());
        assertEquals(patientDocumentId, dosageTimeSlotArgumentCaptor.getValue().getPatientDocumentId());
        assertEquals(slotTime, dosageTimeSlotArgumentCaptor.getValue().getDosageTime());

    }

    @Test
    public void shouldGetAvailableTimeSlots() {
        when(allDosageTimeSlots.countOfPatientsAllottedForSlot(Matchers.<TimeOfDay>any(), Matchers.<TimeOfDay>any())).thenReturn(10, 10, 10, 10, 2, 10, 10, 2, 10);
        final List<String> timeSlots = dosageTimeSlotService.availableSlots();
        assertEquals(2, timeSlots.size());
        assertEquals("01:00", timeSlots.get(0));
        assertEquals("01:45", timeSlots.get(1));
    }
}
