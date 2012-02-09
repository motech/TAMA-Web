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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DosageTimeSlotService {

    private AllDosageTimeSlots allDosageTimeSlots;

    @Autowired
    public DosageTimeSlotService(AllDosageTimeSlots allDosageTimeSlots) {
        this.allDosageTimeSlots = allDosageTimeSlots;
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

    public List<String> availableSlots() {
        LocalTime startTime = new LocalTime(0, 0, 0);
        final LocalTime endTime = new LocalTime(12, 59, 0);
        final List<String> allTimeSlots = new ArrayList<String>();
        while (startTime.isBefore(endTime)) {
            TimeOfDay slotStartTime = new TimeOfDay(startTime);
            TimeOfDay slotEndTime = new TimeOfDay(startTime.plusMinutes(15).minusMinutes(1));
            int allottedCount = allDosageTimeSlots.countOfPatientsAllottedForSlot(slotStartTime, slotEndTime);
            if (allottedCount < 10) {
                allTimeSlots.add(startTime.toString("HH:mm"));
            }
            startTime = startTime.plusMinutes(15);
        }
        return allTimeSlots;
    }
}