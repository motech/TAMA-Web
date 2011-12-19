package org.motechproject.tama.fourdayrecall.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallSchedulerService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallService;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
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
    private FourDayRecallService fourDayRecallService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    public FourDayRecallListener(@Qualifier("IVRCall") IVRCall ivrCall, FourDayRecallSchedulerService fourDayRecallSchedulerService, FourDayRecallService fourDayRecallService, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices) {
        this.ivrCall = ivrCall;
        this.fourDayRecallSchedulerService = fourDayRecallSchedulerService;
        this.fourDayRecallService = fourDayRecallService;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    @MotechListener(subjects = TAMAConstants.FOUR_DAY_RECALL_SUBJECT)
    public void handle(MotechEvent motechEvent) {
        String patientDocId = motechEvent.getParameters().get(PATIENT_DOC_ID_KEY).toString();
        Patient patient = allPatients.get(patientDocId);
        if (patient != null && patient.allowAdherenceCalls()) {
            try {
                TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
                Boolean isRetryEvent = (Boolean) motechEvent.getParameters().get(RETRY_EVENT_KEY);

                if (fourDayRecallService.isAdherenceCapturedForCurrentWeek(patientDocId, treatmentAdvice.getId()))
                    return;
                if (!isRetryEvent)
                    fourDayRecallSchedulerService.scheduleRepeatingJobsForFourDayRecall(patientDocId);

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
            TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());

            if (fourDayRecallService.isAdherenceCapturedForCurrentWeek(patientDocId, treatmentAdvice.getId()) || isLastRetryDay(motechEvent)) {

                if (!fourDayRecallService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientDocId))
                    fourDayRecallService.raiseAdherenceFallingAlert(patientDocId);

                if (!fourDayRecallService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(patientDocId))
                    fourDayRecallService.raiseAdherenceInRedAlert(patientDocId);
            }
        }
    }

    private boolean isLastRetryDay(MotechEvent motechEvent) {
        return "true".equals(motechEvent.getParameters().get(FourDayRecallListener.IS_LAST_RETRY_DAY));
    }
}
