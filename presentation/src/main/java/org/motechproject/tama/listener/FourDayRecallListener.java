package org.motechproject.tama.listener;

import org.joda.time.LocalDate;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.ivr.call.IvrCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallListener {
    public static final String PATIENT_DOC_ID_KEY = "patient_id";
    public static final String TREATMENT_ADVICE_DOC_ID_KEY = "treatment_advice_id";
    public static final String TREATMENT_ADVICE_START_DATE_KEY = "start_date";
    public static final String RETRY_EVENT_KEY = "retry_event";

    private IvrCall ivrCall;
    private TamaSchedulerService schedulerService;
    private FourDayRecallService fourDayRecallService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public FourDayRecallListener(IvrCall ivrCall, TamaSchedulerService schedulerService, FourDayRecallService fourDayRecallService) {
        this.ivrCall = ivrCall;
        this.schedulerService = schedulerService;
        this.fourDayRecallService = fourDayRecallService;
    }

    @MotechListener(subjects = TAMAConstants.FOUR_DAY_RECALL_SUBJECT)
    public void handle(MotechEvent motechEvent) {
        try {
            String patientDocId = motechEvent.getParameters().get(PATIENT_DOC_ID_KEY).toString();
            String treatmentAdviceId = motechEvent.getParameters().get(TREATMENT_ADVICE_DOC_ID_KEY).toString();
            LocalDate treatmentAdviceStartDate = (LocalDate) motechEvent.getParameters().get(TREATMENT_ADVICE_START_DATE_KEY);
            Boolean isRetryEvent = (Boolean) motechEvent.getParameters().get(RETRY_EVENT_KEY);

            if (fourDayRecallService.isAdherenceCapturedForCurrentWeek(patientDocId, treatmentAdviceId)) return;
            if (!isRetryEvent) schedulerService.scheduleRepeatingJobsForFourDayRecall(patientDocId, treatmentAdviceId, treatmentAdviceStartDate);

            ivrCall.makeCall(patientDocId);
        } catch (Exception e) {
            logger.error("Failed to handle FourDayRecall event, this event would not be retried but the subsequent repeats would happen.", e);
        }
    }

    @MotechListener(subjects = TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT)
    public void handleWeeklyFallingAdherence(MotechEvent motechEvent) {
        String patientDocId = motechEvent.getParameters().get(PATIENT_DOC_ID_KEY).toString();
        fourDayRecallService.raiseAdherenceFallingAlert(patientDocId);
    }
}
