package org.motechproject.tama.fourdayrecall.listener;

import org.joda.time.LocalDate;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAlertService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallDateService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallSchedulerService;
import org.motechproject.tama.fourdayrecall.service.WeeklyAdherenceLogService;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallListener {
    public static final String PATIENT_DOC_ID_KEY = "patient_id";
    public static final String RETRY_EVENT_KEY = "retry_event";
    public static final String IS_LAST_RETRY_DAY = "is_last_retry_day";

    private IVRCall ivrCall;
    private FourDayRecallSchedulerService fourDayRecallSchedulerService;
    private FourDayRecallAlertService fourDayRecallAlertService;
    private FourDayRecallDateService fourDayRecallDateService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private WeeklyAdherenceLogService weeklyAdherenceLogService;

    @Autowired
    public FourDayRecallListener(@Qualifier("IVRCall") IVRCall ivrCall, FourDayRecallSchedulerService fourDayRecallSchedulerService,
                                 FourDayRecallAlertService fourDayRecallAlertService, FourDayRecallDateService fourDayRecallDateService,
                                 AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices,
                                 AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, WeeklyAdherenceLogService weeklyAdherenceLogService) {
        this.ivrCall = ivrCall;
        this.fourDayRecallSchedulerService = fourDayRecallSchedulerService;
        this.fourDayRecallAlertService = fourDayRecallAlertService;
        this.fourDayRecallDateService = fourDayRecallDateService;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.weeklyAdherenceLogService = weeklyAdherenceLogService;
    }

    @MotechListener(subjects = TAMAConstants.FOUR_DAY_RECALL_SUBJECT)
    public void handle(MotechEvent motechEvent) {
        String patientDocId = motechEvent.getParameters().get(PATIENT_DOC_ID_KEY).toString();
        Patient patient = allPatients.get(patientDocId);
        if (patient != null && patient.allowAdherenceCalls()) {
            try {
                TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
                Boolean isRetryEvent = (Boolean) motechEvent.getParameters().get(RETRY_EVENT_KEY);

                if (isAdherenceCapturedForCurrentWeek(patient, treatmentAdvice))
                    return;
                if (!isRetryEvent) {
                    weeklyAdherenceLogService.createNotRespondedLog(patient.getId(), 4);
                    fourDayRecallSchedulerService.scheduleRepeatingJobsForFourDayRecall(patient);
                }

                ivrCall.makeCall(patient);
            } catch (Exception e) {
                logger.error("Failed to handle FourDayRecall event, this event would not be retried but the subsequent repeats would happen.", e);
            }
        }
    }

    private boolean isAdherenceCapturedForCurrentWeek(Patient patient, TreatmentAdvice treatmentAdvice) {
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);
        WeeklyAdherenceLog log = allWeeklyAdherenceLogs.findLogsByWeekStartDate(patient, treatmentAdvice, startDateForWeek);
        return log != null && !log.getNotResponded();
    }

    @MotechListener(subjects = TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT)
    public void handleWeeklyFallingAdherenceAndRedAlert(MotechEvent motechEvent) {
        String patientDocId = motechEvent.getParameters().get(PATIENT_DOC_ID_KEY).toString();
        Patient patient = allPatients.get(patientDocId);
        if (patient != null && patient.allowAdherenceCalls()) {
            TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());

            if (isAdherenceCapturedForCurrentWeek(patient, treatmentAdvice) || isLastRetryDay(motechEvent)) {
                if (!fourDayRecallAlertService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientDocId))
                    fourDayRecallAlertService.raiseAdherenceFallingAlert(patientDocId);

                if (!fourDayRecallAlertService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(patientDocId))
                    fourDayRecallAlertService.raiseAdherenceInRedAlert(patientDocId);
            }
        }
    }

    private boolean isLastRetryDay(MotechEvent motechEvent) {
        return "true".equals(motechEvent.getParameters().get(FourDayRecallListener.IS_LAST_RETRY_DAY));
    }
}
