package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Service;

import static org.motechproject.tama.patient.util.CallPlanUtil.callPlanStartDate;

@Service
public class FourDayRecallDateService {

    public final int DAYS_TO_RECALL = 4;

    public LocalDate treatmentWeekStartDate(LocalDate date, TreatmentAdvice treatmentAdvice) {
        DayOfWeek treatmentAdviceStartDay = DayOfWeek.getDayOfWeek(DateUtil.newDate(treatmentAdvice.getStartDate()));
        LocalDate weekStartDate = date.withDayOfWeek(treatmentAdviceStartDay.getValue());
        if (weekStartDate.isAfter(date)) {
            weekStartDate = weekStartDate.minusWeeks(1);
        }
        return weekStartDate;
    }

    public LocalDate treatmentWeekStartDate(LocalDate callDate, Patient patient, TreatmentAdvice treatmentAdvice) {
        LocalDate weekStartDate = treatmentWeekStartDate(callDate, treatmentAdvice);
        final DateTime nextRecallDate = nextRecallOn(weekStartDate, patient);
        if (nextRecallDate.toLocalDate().isAfter(callDate)) {
            weekStartDate = weekStartDate.minusWeeks(1);
        }
        return weekStartDate;
    }

    public DateTime nextRecallOn(LocalDate weekStartDate, Patient patient) {
        Time bestCallTime = patient.getPatientPreferences().getBestCallTime().toTime();
        LocalDate recallDate = weekStartDate.plusDays(DAYS_TO_RECALL);
        while (recallDate.getDayOfWeek() != patient.getPatientPreferences().getDayOfWeeklyCall().getValue()) {
            recallDate = recallDate.plusDays(1);
        }
        return DateUtil.newDateTime(recallDate, bestCallTime);
    }

    public LocalDate firstRecallDate(Patient patient, TreatmentAdvice treatmentAdvice) {
        final LocalDate callPlanStartDate = callPlanStartDate(patient, treatmentAdvice);
        final LocalDate firstWeekStartDate = treatmentWeekStartDate(callPlanStartDate, treatmentAdvice);
        LocalDate nextRecallDate = nextRecallOn(firstWeekStartDate, patient).toLocalDate();
        if (Days.daysBetween(callPlanStartDate, nextRecallDate).getDays() < 4) {
            nextRecallDate = nextRecallDate.plusWeeks(1);
        }
        return nextRecallDate;
    }
}
