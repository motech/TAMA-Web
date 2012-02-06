package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Service;

import static org.motechproject.tama.patient.util.CallPlanUtil.callPlanStartDate;

@Service
public class FourDayRecallDateService {


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
        if (Days.daysBetween(weekStartDate, callDate).getDays() < 4) {
            weekStartDate = weekStartDate.minusWeeks(1);
        }
        final LocalDate nextRecallDate = nextRecallOn(weekStartDate, patient).toLocalDate();
        if (callDate.isBefore(nextRecallDate)) {
            weekStartDate = weekStartDate.minusWeeks(1);
        }
        return weekStartDate;
    }

    public DateTime nextRecallOn(LocalDate weekStartDate, Patient patient) {
        Time bestCallTime = patient.getPatientPreferences().getBestCallTime().toTime();
        LocalDate recallDate = weekStartDate.plusDays(TAMAConstants.DAYS_TO_RECALL_FOR_PATIENTS_ON_WEEKLY_ADHERENCE_CALL);
        while (recallDate.getDayOfWeek() != patient.getPatientPreferences().getDayOfWeeklyCall().getValue()) {
            recallDate = recallDate.plusDays(1);
        }
        return DateUtil.newDateTime(recallDate, bestCallTime);
    }

    public LocalDate firstTreatmentWeekStartDate(Patient patient, TreatmentAdvice treatmentAdvice) {
        final LocalDate callPlanStartDate = callPlanStartDate(patient, treatmentAdvice);
        LocalDate firstWeekStartDate = treatmentWeekStartDate(callPlanStartDate, treatmentAdvice);
        LocalDate nextRecallDate = nextRecallOn(firstWeekStartDate, patient).toLocalDate();
        if (Days.daysBetween(callPlanStartDate, nextRecallDate).getDays() < 4) {
            firstWeekStartDate = firstWeekStartDate.plusWeeks(1);
        }
        return firstWeekStartDate;
    }

    public LocalDate firstRecallDate(Patient patient, TreatmentAdvice treatmentAdvice) {
        final LocalDate firstWeekStartDate = firstTreatmentWeekStartDate(patient, treatmentAdvice);
        return nextRecallOn(firstWeekStartDate, patient).toLocalDate();
    }

    public boolean isFirstTreatmentWeek(Patient patient, TreatmentAdvice treatmentAdvice) {
        final LocalDate firstWeekStartDate = firstTreatmentWeekStartDate(patient, treatmentAdvice);
        final LocalDate currentWeekStartDate = treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);
        return firstWeekStartDate.equals(currentWeekStartDate) || currentWeekStartDate.isBefore(firstWeekStartDate);
    }
}
