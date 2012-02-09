package org.motechproject.tama.patient.service;

import org.joda.time.LocalTime;
import org.motechproject.tama.patient.domain.DosageTimeSlot;
import org.motechproject.tama.patient.domain.TimeOfDay;
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

    public void allotSlot(String patientDocumentId, TimeOfDay timeOfDay) {
        DosageTimeSlot timeSlot = new DosageTimeSlot();
        timeSlot.setDosageTime(timeOfDay);
        timeSlot.setPatientDocumentId(patientDocumentId);
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