package org.motechproject.tama.patient.repository;

import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Test;
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
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 4)).withPatientDocumentId("patientId1").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 5)).withPatientDocumentId("patientId2").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 5)).withPatientDocumentId("patientId3").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 7)).withPatientDocumentId("patientId4").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 9)).withPatientDocumentId("patientId5").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(10, 10)).withPatientDocumentId("patientId6").build());

        final LocalTime slotStartTime = new LocalTime(10, 5);
        final LocalTime slotEndTime = new LocalTime(10, 9);
        assertEquals(4, allDoseTimeSlots.countOfPatientsAllottedForSlot(slotStartTime, slotEndTime));
    }

    @Test
    public void shouldGetSlotCount_WhenStartTimeAndEndTimeSpanAcrossMorningAndEvening() {
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(11, 54)).withPatientDocumentId("patientId1").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(11, 55)).withPatientDocumentId("patientId2").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(11, 55)).withPatientDocumentId("patientId3").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(11, 57)).withPatientDocumentId("patientId4").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(11, 59)).withPatientDocumentId("patientId5").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 0)).withPatientDocumentId("patientId6").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 1)).withPatientDocumentId("patientId6").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 2)).withPatientDocumentId("patientId6").build());
        allDoseTimeSlots.add(CallTimeSlotBuilder.startRecording().withCallTime(new LocalTime(12, 4)).withPatientDocumentId("patientId6").build());

        final LocalTime slotStartTime = new LocalTime(11, 57);
        final LocalTime slotEndTime = new LocalTime(12, 2);
        assertEquals(5, allDoseTimeSlots.countOfPatientsAllottedForSlot(slotStartTime, slotEndTime));
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

    @After
    public void tearDown() {
        markForDeletion(allDoseTimeSlots.getAll().toArray());
    }
}
