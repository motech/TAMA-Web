package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.ivr.service.AdherenceServiceStrategy;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class FourDayRecallAdherenceService implements AdherenceServiceStrategy {
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllPatients allPatients;
    private PatientAlertService patientAlertService;
    private FourDayRecallDateService fourDayRecallDateService;
    private Properties properties;

    @Autowired
    public FourDayRecallAdherenceService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs,
                                         PatientAlertService patientAlertService, FourDayRecallDateService fourDayRecallDateService, @Qualifier("fourDayRecallProperties") Properties properties, AdherenceService adherenceService) {
        this.allPatients = allPatients;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.patientAlertService = patientAlertService;
        this.fourDayRecallDateService = fourDayRecallDateService;
        this.properties = properties;
        adherenceService.register(CallPreference.FourDayRecall, this);
    }

    public int getAdherencePercentageForPreviousWeek(String patientId) {
        return adherencePercentageFor(getAdherenceLog(patientId, 1));
    }

    public int adherencePercentageFor(int numDaysMissed) {
        return (fourDayRecallDateService.DAYS_TO_RECALL - numDaysMissed) * 100 / fourDayRecallDateService.DAYS_TO_RECALL;
    }

    protected int adherencePercentageFor(WeeklyAdherenceLog weeklyAdherenceLog) {
        if (weeklyAdherenceLog == null) return 0;
        return adherencePercentageFor(weeklyAdherenceLog.getNumberOfDaysMissed());
    }

    protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        LocalDate startDateForPreviousWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice).minusWeeks(weeksBefore);

        List<WeeklyAdherenceLog> logs = allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientId, treatmentAdvice.getId(), startDateForPreviousWeek);
        return logs.size() == 0 ? null : logs.get(0);
    }

    public boolean isAdherenceFalling(int dosageMissedDays, String patientId) {
        return adherencePercentageFor(dosageMissedDays) < getAdherencePercentageForPreviousWeek(patientId);
    }

    public void raiseAdherenceFallingAlert(String patientId) {
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        if (fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice)) return;

        int adherencePercentageForCurrentWeek = getAdherencePercentageForCurrentWeek(patientId);
        if (adherencePercentageForCurrentWeek >= getAdherencePercentageForPreviousWeek(patientId)) return;

        final Map<String, String> data = new HashMap<String, String>();
        final int previousWeekPercentage = getAdherencePercentageForPreviousWeek(patientId);
        final double fall = ((previousWeekPercentage - adherencePercentageForCurrentWeek) / (double) previousWeekPercentage) * 100.0;
        final String description = String.format(TAMAMessages.ADHERENCE_FALLING_FROM_TO, fall, (double) previousWeekPercentage, (double) adherencePercentageForCurrentWeek);
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.FALLING_ADHERENCE, description, PatientAlertType.FallingAdherence, data);
    }

    protected int getAdherencePercentageForCurrentWeek(String patientId) {
        return adherencePercentageFor(getAdherenceLog(patientId, 0));
    }

    public boolean hasAdherenceInRedAlertBeenRaisedForCurrentWeek(String patientId) {
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);

        DateTime nextRecallDate = fourDayRecallDateService.nextRecallOn(startDateForWeek, patient);
        return patientAlertService.getAdherenceInRedAlerts(patientId, nextRecallDate, DateUtil.now()).size() > 0;
    }

    public boolean hasAdherenceFallingAlertBeenRaisedForCurrentWeek(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientDocId);
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);

        DateTime nextRecallDate = fourDayRecallDateService.nextRecallOn(startDateForWeek, patient);
        return patientAlertService.getFallingAdherenceAlerts(patientDocId, nextRecallDate, DateUtil.now()).size() > 0;
    }

    public void raiseAdherenceInRedAlert(String patientId) {
        WeeklyAdherenceLog adherenceLog = getAdherenceLog(patientId, 0);
        String description = PatientAlertService.RED_ALERT_MESSAGE_NO_RESPONSE;
        double adherencePercentage = adherencePercentageFor(adherenceLog);

        if (adherenceLog != null) {
            double acceptableAdherencePercentage = Double.parseDouble(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE));
            if (adherencePercentage >= acceptableAdherencePercentage) return;
            description = String.format(TAMAMessages.ADHERENCE_PERCENTAGE_IS, adherencePercentage);
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, Double.toString(adherencePercentage));
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT, description, PatientAlertType.AdherenceInRed, data);
    }

    @Override
    public boolean wasAnyDoseMissedLastWeek(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        if (fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice)) return false;
        return ((double) getAdherencePercentageForPreviousWeek(patient.getId()) != 100.0);
    }

    @Override
    public boolean wasAnyDoseTakenLateSince(Patient patient, LocalDate since) {
        return false;
    }
}