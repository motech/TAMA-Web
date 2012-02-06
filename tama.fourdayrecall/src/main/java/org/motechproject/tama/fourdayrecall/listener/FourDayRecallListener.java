package org.motechproject.tama.fourdayrecall.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAlertService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallSchedulerService;
import org.motechproject.tama.fourdayrecall.service.WeeklyAdherenceLogService;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
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
    public static final String FIRST_CALL = "first_call";

    private IVRCall ivrCall;
    private FourDayRecallSchedulerService fourDayRecallSchedulerService;
    private FourDayRecallAlertService fourDayRecallAlertService;
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private AllPatients allPatients;
    private WeeklyAdherenceLogService weeklyAdherenceLogService;

    @Autowired
    public FourDayRecallListener(@Qualifier("IVRCall") IVRCall ivrCall, FourDayRecallSchedulerService fourDayRecallSchedulerService,
                                 FourDayRecallAlertService fourDayRecallAlertService, FourDayRecallAdherenceService fourDayRecallAdherenceService,
                                 AllPatients allPatients, WeeklyAdherenceLogService weeklyAdherenceLogService) {
        this.ivrCall = ivrCall;
        this.fourDayRecallSchedulerService = fourDayRecallSchedulerService;
        this.fourDayRecallAlertService = fourDayRecallAlertService;
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
        this.allPatients = allPatients;
        this.weeklyAdherenceLogService = weeklyAdherenceLogService;
    }

    @MotechListener(subjects = TAMAConstants.FOUR_DAY_RECALL_SUBJECT)
    public void handle(MotechEvent motechEvent) {
        String patientDocId = motechEvent.getParameters().get(PATIENT_DOC_ID_KEY).toString();
        Patient patient = allPatients.get(patientDocId);
        if (patient != null && patient.allowAdherenceCalls()) {
            try {
                Boolean isRetryEvent = (Boolean) motechEvent.getParameters().get(RETRY_EVENT_KEY);
                Boolean isFirstCall = (Boolean) motechEvent.getParameters().get(FIRST_CALL);

                if (fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient))
                    return;
                if (isFirstCall != null && isFirstCall)
                    weeklyAdherenceLogService.createNotRespondedLog(patient.getId());
                if (!isRetryEvent)
                    fourDayRecallSchedulerService.scheduleRetryJobsForFourDayRecall(patient);

                ivrCall.makeCall(patient);
            } catch (Exception e) {
                logger.error("Failed to handle FourDayRecall event, this event would not be retried but the subsequent repeats would happen.", e);
            }
        }
    }

    @MotechListener(subjects = TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT)
    public void handleWeeklyFallingAdherenceAndRedAlert(MotechEvent motechEvent) {
        String patientDocId = motechEvent.getParameters().get(PATIENT_DOC_ID_KEY).toString();
        Patient patient = allPatients.get(patientDocId);
        if (patient != null && patient.allowAdherenceCalls()) {
            if (fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient) || isLastRetryDay(motechEvent)) {
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
