package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.FourDayRecallTimeLine;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class ResumeFourDayRecallService {
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllPatients allPatients;
    private Properties properties;

    @Autowired
    public ResumeFourDayRecallService(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, @Qualifier("fourDayRecallProperties") Properties properties) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.properties = properties;
    }

    public void backFillAdherenceForPeriodOfSuspension(String patientId, boolean doseTaken) {
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        String treatmentAdviceDocId = treatmentAdvice.getId();
        DateTime resumeDate = DateUtil.now();

        FourDayRecallTimeLine fourDayRecallTimeLine = new FourDayRecallTimeLine(patient, resumeDate, treatmentAdvice, daysToRetry);
        List<LocalDate> weekStartDates = fourDayRecallTimeLine.weekStartDates();
        if (weekStartDates.size() > 0){
            if(allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientId, treatmentAdviceDocId, weekStartDates.get(0)).size() == 0) {
                allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, weekStartDates.get(0), doseTaken ? 0 : PatientPreferences.DAYS_TO_RECALL));
            }
            weekStartDates.remove(0);
        }

        for (LocalDate date : weekStartDates) {
            allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, date, doseTaken ? 0 : PatientPreferences.DAYS_TO_RECALL));
        }
    }
}