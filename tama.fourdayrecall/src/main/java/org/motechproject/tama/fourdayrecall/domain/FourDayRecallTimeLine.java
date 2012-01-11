package org.motechproject.tama.fourdayrecall.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.tama.fourdayrecall.util.FourDayRecallUtil.getStartDateForWeek;
import static org.motechproject.tama.fourdayrecall.util.FourDayRecallUtil.nextRecallOn;

public class FourDayRecallTimeLine {
    private TreatmentAdvice treatmentAdvice;
    private int daysToRetry;
    private Patient patient;
    private DateTime startDate;
    private DateTime toDate;

    public FourDayRecallTimeLine(Patient patient, DateTime startDate, DateTime endDate, TreatmentAdvice treatmentAdvice, int daysToRetry) {
        this.patient = patient;
        this.startDate = startDate;
        this.toDate = endDate;
        this.treatmentAdvice = treatmentAdvice;
        this.daysToRetry = daysToRetry;
    }

    public List<LocalDate> weekStartDates() {
        List<LocalDate> allWeekStartDates = new ArrayList<LocalDate>();
        LocalDate weekStartDate = computeFirstWeekStartDate(startDate);
        while (weekStartDate != null) {
            allWeekStartDates.add(weekStartDate);
            weekStartDate = computeNextWeekStartDate(weekStartDate);
        }
        return allWeekStartDates;
    }

    private LocalDate computeFirstWeekStartDate(DateTime fromDate) {
        LocalDate startDateForFirstWeek = getStartDateForWeek(fromDate.toLocalDate(), patient, treatmentAdvice);

        DateTime recallDateTimeForFirstWeek = nextRecallOn(startDateForFirstWeek, patient);
        LocalDate nextWeekStartDate = null;
        if (DateUtil.isOnOrBefore(DateUtil.newDate(treatmentAdvice.getStartDate()), startDateForFirstWeek)
                && !recallDateTimeForFirstWeek.plusDays(daysToRetry).isBefore(fromDate)
                && !recallDateTimeForFirstWeek.isAfter(toDate)) {
            nextWeekStartDate = startDateForFirstWeek;
        }
        if (recallDateTimeForFirstWeek.plusDays(daysToRetry).isBefore(fromDate) && startDateForFirstWeek.plusWeeks(1).isBefore(toDate.toLocalDate())) {
            nextWeekStartDate = computeNextWeekStartDate(startDateForFirstWeek);
        }
        return nextWeekStartDate;
    }

    private LocalDate computeNextWeekStartDate(LocalDate currentWeekStartDate) {
        DateTime currentWeekRecallDateTime = nextRecallOn(currentWeekStartDate, patient);
        DateTime nextWeekRecallDateTime = currentWeekRecallDateTime.plusWeeks(1);
        if (nextWeekRecallDateTime.isAfter(toDate)) return null;
        return getStartDateForWeek(nextWeekRecallDateTime.toLocalDate(), patient, treatmentAdvice);
    }
}
