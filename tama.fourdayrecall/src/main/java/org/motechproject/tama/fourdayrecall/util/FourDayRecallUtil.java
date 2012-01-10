package org.motechproject.tama.fourdayrecall.util;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

public class FourDayRecallUtil {

    public static LocalDate getStartDateForWeek(LocalDate date, Patient patient, TreatmentAdvice treatmentAdvice) {
        DayOfWeek preferredCallDay = patient.getPatientPreferences().getDayOfWeeklyCall();
        int retryDayCount = 0;
        boolean isRetry = date.getDayOfWeek() != preferredCallDay.getValue();
        if (isRetry) retryDayCount = DateUtil.daysPast(date, preferredCallDay);

        DayOfWeek treatmentAdviceStartDay = DayOfWeek.getDayOfWeek(DateUtil.newDate(treatmentAdvice.getStartDate()));
        return dateWith(treatmentAdviceStartDay, PatientPreferences.DAYS_TO_RECALL, date.minusDays(retryDayCount));
    }

    private static LocalDate dateWith(DayOfWeek dayOfWeek, int minNumberOfDaysAgo, LocalDate maxDate) {
        LocalDate date = dateWith(dayOfWeek, maxDate);

        Period period = new Period(date, maxDate, PeriodType.days());
        if (period.getDays() >= minNumberOfDaysAgo) return date;

        return dateWith(dayOfWeek, date);
    }

    private static LocalDate dateWith(DayOfWeek dayOfWeek, LocalDate maxDate) {
        LocalDate returnDate = maxDate.withDayOfWeek(dayOfWeek.getValue());
        boolean dateAfterMaxDate = returnDate.compareTo(maxDate) >= 0;
        if (dateAfterMaxDate) {
            returnDate = returnDate.minusWeeks(1);
        }
        return returnDate;
    }
}
