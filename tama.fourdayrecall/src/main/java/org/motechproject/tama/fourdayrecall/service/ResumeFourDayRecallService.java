package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.FourDayRecallTimeLine;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class ResumeFourDayRecallService {
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private Properties properties;
    private FourDayRecallDateService fourDayRecallDateService;

    @Autowired
    public ResumeFourDayRecallService(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, @Qualifier("fourDayRecallProperties") Properties properties, FourDayRecallDateService fourDayRecallDateService) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.properties = properties;
        this.fourDayRecallDateService = fourDayRecallDateService;
    }

    public void backFillAdherence(Patient patient, TreatmentAdvice treatmentAdvice, DateTime fromDate, DateTime toDate, boolean doseTaken) {
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));

        FourDayRecallTimeLine fourDayRecallTimeLine = new FourDayRecallTimeLine(patient, fromDate, toDate, treatmentAdvice, daysToRetry, fourDayRecallDateService);
        List<LocalDate> weekStartDates = fourDayRecallTimeLine.weekStartDates();
        if (weekStartDates.size() > 0) {
            if (allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weekStartDates.get(0)) == null) {
                allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patient.getId(), treatmentAdvice.getId(), weekStartDates.get(0), doseTaken ? 0 : fourDayRecallDateService.DAYS_TO_RECALL));
            }
            weekStartDates.remove(0);
        }

        for (LocalDate date : weekStartDates) {
            allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patient.getId(), treatmentAdvice.getId(), date, doseTaken ? 0 : fourDayRecallDateService.DAYS_TO_RECALL));
        }
    }
}