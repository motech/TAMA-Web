package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.FourDayRecallTimeLine;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class ResumeFourDayRecallService {
    private AllTreatmentAdvices allTreatmentAdvices;
    private WeeklyAdherenceLogService weeklyAdherenceLogService;
    private Properties properties;
    private FourDayRecallDateService fourDayRecallDateService;

    @Autowired
    public ResumeFourDayRecallService(AllTreatmentAdvices allTreatmentAdvices, @Qualifier("fourDayRecallProperties") Properties properties, FourDayRecallDateService fourDayRecallDateService, WeeklyAdherenceLogService weeklyAdherenceLogService) {
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.properties = properties;
        this.fourDayRecallDateService = fourDayRecallDateService;
        this.weeklyAdherenceLogService = weeklyAdherenceLogService;
    }

    public void backFillAdherence(Patient patient, DateTime fromDate, DateTime toDate, boolean doseTaken) {
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());

        FourDayRecallTimeLine fourDayRecallTimeLine = new FourDayRecallTimeLine(patient, fromDate, toDate, currentTreatmentAdvice, daysToRetry, fourDayRecallDateService);
        List<LocalDate> weekStartDates = fourDayRecallTimeLine.weekStartDates();
        for (LocalDate date : weekStartDates) {
            DateTime bestCallDate = fourDayRecallDateService.nextRecallOn(date, patient);
            weeklyAdherenceLogService.createLog(patient.getId(), date, doseTaken ? 0 : fourDayRecallDateService.DAYS_TO_RECALL, bestCallDate.toLocalDate());
        }
    }
}