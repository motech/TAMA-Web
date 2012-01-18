package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class FourDayRecallAlertService {
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllPatients allPatients;
    private Properties properties;
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    private FourDayRecallDateService fourDayRecallDateService;
    private PatientAlertService patientAlertService;

    @Autowired
    public FourDayRecallAlertService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices,
                                     @Qualifier("fourDayRecallProperties") Properties properties, PatientAlertService patientAlertService, FourDayRecallDateService fourDayRecallDateService,
                                     FourDayRecallAdherenceService fourDayRecallAdherenceService) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.patientAlertService = patientAlertService;
        this.fourDayRecallDateService = fourDayRecallDateService;
        this.properties = properties;
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
    }

    public void raiseAdherenceFallingAlert(String patientId) {
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        if (fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice)) return;

        int adherencePercentageForCurrentWeek = fourDayRecallAdherenceService.getAdherencePercentageForCurrentWeek(patientId);
        if (adherencePercentageForCurrentWeek >= fourDayRecallAdherenceService.getAdherencePercentageForPreviousWeek(patientId))
            return;

        final Map<String, String> data = new HashMap<String, String>();
        final int previousWeekPercentage = fourDayRecallAdherenceService.getAdherencePercentageForPreviousWeek(patientId);
        final double fall = ((previousWeekPercentage - adherencePercentageForCurrentWeek) / (double) previousWeekPercentage) * 100.0;
        final String description = String.format(TAMAMessages.ADHERENCE_FALLING_FROM_TO, fall, (double) previousWeekPercentage, (double) adherencePercentageForCurrentWeek);
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.FALLING_ADHERENCE, description, PatientAlertType.FallingAdherence, data);
    }

    public boolean hasAdherenceFallingAlertBeenRaisedForCurrentWeek(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientDocId);
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);

        DateTime nextRecallDate = fourDayRecallDateService.nextRecallOn(startDateForWeek, patient);
        return patientAlertService.getFallingAdherenceAlerts(patientDocId, nextRecallDate, DateUtil.now()).size() > 0;
    }

    public void raiseAdherenceInRedAlert(String patientId) {
        String description;
        WeeklyAdherenceLog adherenceLog = fourDayRecallAdherenceService.getAdherenceLog(patientId, 0);

        if(adherenceLog == null)
            return;

        double adherencePercentage = fourDayRecallAdherenceService.adherencePercentageFor(adherenceLog);
        if (adherenceLog.getNotResponded()) {
            description = PatientAlertService.RED_ALERT_MESSAGE_NO_RESPONSE;
        } else {
            double acceptableAdherencePercentage = Double.parseDouble(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE));
            if (adherencePercentage >= acceptableAdherencePercentage) return;
            description = String.format(TAMAMessages.ADHERENCE_PERCENTAGE_IS, adherencePercentage);
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, Double.toString(adherencePercentage));
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT, description, PatientAlertType.AdherenceInRed, data);
    }

    public boolean hasAdherenceInRedAlertBeenRaisedForCurrentWeek(String patientDocumentId) {
        Patient patient = allPatients.get(patientDocumentId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientDocumentId);
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);

        DateTime nextRecallDate = fourDayRecallDateService.nextRecallOn(startDateForWeek, patient);
        return patientAlertService.getAdherenceInRedAlerts(patientDocumentId, nextRecallDate, DateUtil.now()).size() > 0;
    }
}