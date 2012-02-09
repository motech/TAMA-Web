package org.motechproject.tama.patient.service;

import org.joda.time.LocalTime;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.common.util.TimeUtil;
import org.motechproject.tama.patient.domain.DosageTimeSlot;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllDosageTimeSlots;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class DosageTimeSlotService {

    public static final String SLOT_DURATION_MINS = "slot.duration.mins";
    public static final String MAX_PATIENTS_PER_SLOT = "max.patients.per.slot";

    private int slot_duration_in_mins;
    private int max_patients_per_slot;

    private AllDosageTimeSlots allDosageTimeSlots;

    @Autowired
    public DosageTimeSlotService(AllDosageTimeSlots allDosageTimeSlots, @Qualifier("timeSlotProperties") Properties timeSlotProperties) {
        this.allDosageTimeSlots = allDosageTimeSlots;
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
        DosageTimeSlot timeSlot = new DosageTimeSlot();
        timeSlot.setDosageTime(new TimeUtil(timeString).getTimeOfDay());
        timeSlot.setPatientDocumentId(patient.getId());
        allDosageTimeSlots.add(timeSlot);
    }

    public List<String> availableMorningSlots() {
        LocalTime startTime = new LocalTime(0, 0, 0);
        final LocalTime endTime = new LocalTime(11, 59, 0);
        return timeSlots(startTime, endTime);
    }

    public List<String> availableEveningSlots() {
        LocalTime startTime = new LocalTime(1, 0, 0);
        final LocalTime endTime = new LocalTime(12, 59, 0);
        return timeSlots(startTime, endTime);
    }

    private List<String> timeSlots(LocalTime startTime, LocalTime endTime) {
        final List<String> allTimeSlots = new ArrayList<String>();
        while (startTime.isBefore(endTime)) {
            TimeOfDay slotStartTime = new TimeOfDay(startTime);
            TimeOfDay slotEndTime = new TimeOfDay(startTime.plusMinutes(slot_duration_in_mins).minusMinutes(1));
            int allottedCount = allDosageTimeSlots.countOfPatientsAllottedForSlot(slotStartTime, slotEndTime);
            if (allottedCount < max_patients_per_slot) {
                allTimeSlots.add(startTime.toString("HH:mm"));
            }
            startTime = startTime.plusMinutes(slot_duration_in_mins);
        }
        return allTimeSlots;
    }
}