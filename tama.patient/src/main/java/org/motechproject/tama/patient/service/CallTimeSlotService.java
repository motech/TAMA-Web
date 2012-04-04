package org.motechproject.tama.patient.service;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.motechproject.tama.common.util.TimeUtil;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllCallTimeSlots;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class CallTimeSlotService {

    public static final String SLOT_DURATION_MINS = "slot.duration.mins";
    public static final String MAX_PATIENTS_PER_SLOT = "max.patients.per.slot";

    private int slot_duration_in_mins;
    private int max_patients_per_slot;

    private AllCallTimeSlots allCallTimeSlots;

    @Autowired
    public CallTimeSlotService(AllCallTimeSlots allCallTimeSlots, @Qualifier("timeSlotProperties") Properties timeSlotProperties) {
        this.allCallTimeSlots = allCallTimeSlots;
        slot_duration_in_mins = Integer.parseInt(timeSlotProperties.getProperty(SLOT_DURATION_MINS));
        max_patients_per_slot = Integer.parseInt(timeSlotProperties.getProperty(MAX_PATIENTS_PER_SLOT));
    }

    public void freeSlots(Patient patient, TreatmentAdvice treatmentAdvice) {
        for (DrugDosage drugDosage : treatmentAdvice.getDrugDosages()) {
            freeSlot(patient, drugDosage.getMorningTime());
            freeSlot(patient, drugDosage.getEveningTime());
        }
    }

    private void freeSlot(Patient patient, String timeString) {
        if (timeString == null ) return;
        List<CallTimeSlot> slots = allCallTimeSlots.findBySlotTimeAndPatientId(new TimeUtil(timeString).toLocalTime(), patient.getId());
        for (CallTimeSlot slot : slots) {
            allCallTimeSlots.remove(slot);
        }
    }

    public void allotSlots(Patient patient, TreatmentAdvice treatmentAdvice) {
        for (DrugDosage drugDosage : treatmentAdvice.getDrugDosages()) {
            allotSlot(patient, drugDosage.getMorningTime());
            allotSlot(patient, drugDosage.getEveningTime());
        }
    }

    private void allotSlot(Patient patient, String timeString) {
        if (timeString == null ) return;
        CallTimeSlot timeSlot = new CallTimeSlot();
        timeSlot.setCallTime(new TimeUtil(timeString).toLocalTime());
        timeSlot.setPatientDocumentId(patient.getId());
        allCallTimeSlots.add(timeSlot);
    }

    public List<String> availableMorningSlots() {
        DateTime startDate = new LocalTime(0, 0, 0).toDateTimeToday();
        DateTime endDate = new LocalTime(11, 59, 0).toDateTimeToday();
        return timeSlots(startDate, endDate);
    }

    public List<String> availableEveningSlots() {
        DateTime startDate = new LocalTime(12, 0, 0).toDateTimeToday();
        DateTime endDate = new LocalTime(23, 59, 0).toDateTimeToday();
        return timeSlots(startDate, endDate);
    }

    private List<String> timeSlots(DateTime startDate, DateTime endDate) {
        final AllottedSlots allottedSlots = allCallTimeSlots.getAllottedSlots();
        final List<String> allTimeSlots = new ArrayList<String>();
        while (startDate.isBefore(endDate)) {
            LocalTime slotStartTime = startDate.toLocalTime();
            LocalTime slotEndTime = startDate.toLocalTime().plusMinutes(slot_duration_in_mins).minusMinutes(1);
            int allottedCount = allottedSlots.numberOfPatientsAllottedPerSlot(slotStartTime, slotEndTime);
            if (allottedCount < max_patients_per_slot) {
                allTimeSlots.add(slotStartTime.toString("hh:mm"));
            }
            startDate = startDate.plusMinutes(slot_duration_in_mins);
        }
        return allTimeSlots;
    }
}