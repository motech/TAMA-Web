package org.motechproject.tama.patient.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.CallTimeSlotBuilder;
import org.motechproject.tama.patient.domain.CallTimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath*:/applicationPatientContext.xml")
public class AllCallTimeSlotsTest extends SpringIntegrationTest {

    @Autowired
    private AllCallTimeSlots allDoseTimeSlots;

    @Test
    public void shouldCreateDoseTimeSlot() {
        final CallTimeSlot callTimeSlot = CallTimeSlotBuilder.startRecording().withDefaults().build();
        allDoseTimeSlots.add(callTimeSlot);
        assertNotNull(allDoseTimeSlots.get(callTimeSlot.getId()));
    }

    @Test
    public void shouldGetSlotCount() {
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(10, 4, TimeMeridiem.AM)).withPatientDocumentId("patientId1").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(10, 5, TimeMeridiem.AM)).withPatientDocumentId("patientId2").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(10, 5, TimeMeridiem.AM)).withPatientDocumentId("patientId3").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(10, 7, TimeMeridiem.AM)).withPatientDocumentId("patientId4").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(10, 9, TimeMeridiem.AM)).withPatientDocumentId("patientId5").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM)).withPatientDocumentId("patientId6").build());

        final TimeOfDay slotStartTime = new TimeOfDay(10, 5, TimeMeridiem.AM);
        final TimeOfDay slotEndTime = new TimeOfDay(10, 9, TimeMeridiem.AM);
        assertEquals(4, allDoseTimeSlots.countOfPatientsAllottedForSlot(slotStartTime, slotEndTime));
    }

    @Test
    public void shouldGetSlotCount_WhenStartTimeAndEndTimeSpanAcrossMorningAndEvening() {
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(11, 54, TimeMeridiem.AM)).withPatientDocumentId("patientId1").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(11, 55, TimeMeridiem.AM)).withPatientDocumentId("patientId2").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(11, 55, TimeMeridiem.AM)).withPatientDocumentId("patientId3").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(11, 57, TimeMeridiem.AM)).withPatientDocumentId("patientId4").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(11, 59, TimeMeridiem.AM)).withPatientDocumentId("patientId5").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(12, 0, TimeMeridiem.PM)).withPatientDocumentId("patientId6").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(12, 1, TimeMeridiem.PM)).withPatientDocumentId("patientId6").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(12, 2, TimeMeridiem.PM)).withPatientDocumentId("patientId6").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new TimeOfDay(12, 4, TimeMeridiem.PM)).withPatientDocumentId("patientId6").build());

        final TimeOfDay slotStartTime = new TimeOfDay(11, 57, TimeMeridiem.AM);
        final TimeOfDay slotEndTime = new TimeOfDay(12, 2, TimeMeridiem.PM);
        assertEquals(5, allDoseTimeSlots.countOfPatientsAllottedForSlot(slotStartTime, slotEndTime));
    }

    @After
    public void tearDown() {
        markForDeletion(allDoseTimeSlots.getAll().toArray());
    }
}
