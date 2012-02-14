package org.motechproject.tama.patient.service;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.motechproject.tama.common.util.TimeUtil;
import org.motechproject.tama.patient.domain.CallTimeSlot;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
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

    public void allotSlots(Patient patient, TreatmentAdvice treatmentAdvice) {
        for (DrugDosage drugDosage : treatmentAdvice.getDrugDosages()) {
            String morningTime = drugDosage.getMorningTime();
            if (morningTime != null) allotSlot(patient, morningTime);
            String eveningTime = drugDosage.getEveningTime();
            if (eveningTime != null) allotSlot(patient, eveningTime);
        }
    }

    private void allotSlot(Patient patient, String timeString) {
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
        DateTime endDate = new LocalTime(23, 59, 0).toDateTimeToday().plusDays(1);
        return timeSlots(startDate, endDate);
    }

    private List<String> timeSlots(DateTime startDate, DateTime endDate) {
        final List<String> allTimeSlots = new ArrayList<String>();
        while (startDate.isBefore(endDate)) {
            LocalTime slotStartTime = startDate.toLocalTime();
            LocalTime slotEndTime = startDate.toLocalTime().plusMinutes(slot_duration_in_mins).minusMinutes(1);
            int allottedCount = allCallTimeSlots.countOfPatientsAllottedForSlot(slotStartTime, slotEndTime);
            if (allottedCount < max_patients_per_slot) {
                allTimeSlots.add(slotStartTime.toString("hh:mm"));
            }
            startDate = startDate.plusMinutes(slot_duration_in_mins);
        }
        return allTimeSlots;
    }
}