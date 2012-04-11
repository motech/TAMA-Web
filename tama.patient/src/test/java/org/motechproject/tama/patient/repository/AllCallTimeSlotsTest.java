package org.motechproject.tama.patient.repository;

import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.CallTimeSlotBuilder;
import org.motechproject.tama.patient.domain.AllottedSlot;
import org.motechproject.tama.patient.domain.AllottedSlots;
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
    public void shouldNotAddTimeSlotIfAlreadyPresent() {
        String patientDocumentId = "patientDocumentId";
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 0)).withPatientDocumentId(patientDocumentId).build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 0)).withPatientDocumentId(patientDocumentId).build());
        assertEquals(1, allDoseTimeSlots.findBySlotTimeAndPatientId(new LocalTime(10, 0), patientDocumentId).size());
    }

    @Test
    public void shouldFindByCallSlotTimeAndPatientDocumentId() {
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 0)).withPatientDocumentId("patientDocumentId1").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 0)).withPatientDocumentId("patientDocumentId2").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(11, 0)).withPatientDocumentId("patientDocumentId1").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 0)).withPatientDocumentId("patientDocumentId3").build());
        assertEquals(1, allDoseTimeSlots.findBySlotTimeAndPatientId(new LocalTime(10, 0), "patientDocumentId1").size());
    }

    @Test
    public void shouldReturnListOfAllottedTimeSlots() {
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(11, 55)).withPatientDocumentId("patientId1").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(11, 55)).withPatientDocumentId("patientId2").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 00)).withPatientDocumentId("patientId4").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 00)).withPatientDocumentId("patientId5").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 00)).withPatientDocumentId("patientId6").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 10)).withPatientDocumentId("patientId6").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 05)).withPatientDocumentId("patientId7").build());

        AllottedSlots allottedSlots = allDoseTimeSlots.getAllottedSlots();

        assertEquals(allottedSlots.size(), 4);
        assertAllottedTimeSlot(allottedSlots.get(0), new LocalTime(11, 55), 2);
        assertAllottedTimeSlot(allottedSlots.get(1), new LocalTime(12, 00), 3);
        assertAllottedTimeSlot(allottedSlots.get(2), new LocalTime(12, 05), 1);
        assertAllottedTimeSlot(allottedSlots.get(3), new LocalTime(12, 10), 1);
    }

    @Test
    // test - rereduce of calltimeslots
    public void shouldReturnListOfAllottedTimeSlots_WhenAMoreNumberOfPatientsAreAllottedPerSlot() {
        for (int i = 0; i < 30; i++) {
            allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(11, 55)).withPatientDocumentId("patientId" + i).build());
        }
        AllottedSlots allottedSlots = allDoseTimeSlots.getAllottedSlots();
        assertEquals(allottedSlots.size(), 1);
        assertAllottedTimeSlot(allottedSlots.get(0), new LocalTime(11, 55), 30);
    }

    @After
    public void tearDown() {
        markForDeletion(allDoseTimeSlots.getAll().toArray());
    }

    private void assertAllottedTimeSlot(AllottedSlot allottedSlot, LocalTime slotTime, int allottedCount) {
        assertEquals(allottedSlot.getSlotTimeAsLocalTime(), slotTime);
        assertEquals(allottedSlot.getAllottedCount(), allottedCount);
    }
}
