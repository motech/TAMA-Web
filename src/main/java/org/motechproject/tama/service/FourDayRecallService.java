package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallService {

    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    @Autowired
    public FourDayRecallService(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
    }

    public boolean isAdherenceCapturedForCurrentWeek(String patientDocId, String treatmentAdviceId, LocalDate startDateOfTreatmentAdvice) {
        LocalDate startDateForCurrentWeek = getStartDateForCurrentWeek(startDateOfTreatmentAdvice);
        return allWeeklyAdherenceLogs.findLogCountByPatientIDAndTreatmentAdviceIdAndDateRange(patientDocId, treatmentAdviceId, startDateForCurrentWeek, DateUtil.today()) > 0;
    }

    public LocalDate getStartDateForCurrentWeek(LocalDate startDateOfTreatmentAdvice) {
        DayOfWeek startDayForTreatmentAdvice = DayOfWeek.getDayOfWeek(startDateOfTreatmentAdvice);
        return DateUtil.pastDateWith(startDayForTreatmentAdvice, 4);
    }
}
