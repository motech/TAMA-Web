package org.motechproject.tama.patient.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.DosageTimeSlotBuilder;
import org.motechproject.tama.patient.domain.DosageTimeSlot;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath*:/applicationPatientContext.xml")
public class AllDosageTimeSlotsTest extends SpringIntegrationTest {

    @Autowired
    private AllDosageTimeSlots allDoseTimeSlots;

    @Test
    public void shouldCreateDoseTimeSlot() {
        final DosageTimeSlot dosageTimeSlot = DosageTimeSlotBuilder.startRecording().withDefaults().build();
        allDoseTimeSlots.add(dosageTimeSlot);
        assertNotNull(allDoseTimeSlots.get(dosageTimeSlot.getId()));
    }

    @Test
    public void shouldGetSlotCount() {
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(10, 4, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId1")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(10, 5, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId2")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(10, 5, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId3")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(10, 7, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId4")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(10, 9, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId5")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(10, 10, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId6")).build());

        final TimeOfDay slotStartTime = new TimeOfDay(10, 5, TimeMeridiem.AM);
        final TimeOfDay slotEndTime = new TimeOfDay(10, 9, TimeMeridiem.AM);
        assertEquals(4, allDoseTimeSlots.countOfPatientsAllottedForSlot(slotStartTime, slotEndTime));
    }

    @Test
    public void shouldGetSlotCount_WhenStartTimeAndEndTimeSpanAcrossMorningAndEvening() {
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(11, 54, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId1")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(11, 55, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId2")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(11, 55, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId3")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(11, 57, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId4")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(11, 59, TimeMeridiem.AM)).withPatientIds(Arrays.asList("patientId5")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(12, 0, TimeMeridiem.PM)).withPatientIds(Arrays.asList("patientId6")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(12, 1, TimeMeridiem.PM)).withPatientIds(Arrays.asList("patientId6")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(12, 2, TimeMeridiem.PM)).withPatientIds(Arrays.asList("patientId6")).build());
        allDoseTimeSlots.add(DosageTimeSlotBuilder.startRecording().withDosageTime(new TimeOfDay(12, 4, TimeMeridiem.PM)).withPatientIds(Arrays.asList("patientId6")).build());

        final TimeOfDay slotStartTime = new TimeOfDay(11, 57, TimeMeridiem.AM);
        final TimeOfDay slotEndTime = new TimeOfDay(12, 2, TimeMeridiem.PM);
        assertEquals(5, allDoseTimeSlots.countOfPatientsAllottedForSlot(slotStartTime, slotEndTime));
    }

    @After
    public void tearDown() {
        markForDeletion(allDoseTimeSlots.getAll().toArray());
    }
}
