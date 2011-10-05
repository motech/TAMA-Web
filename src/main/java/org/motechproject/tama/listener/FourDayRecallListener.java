package org.motechproject.tama.listener;

import org.joda.time.LocalDate;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.call.FourDayRecallCall;
import org.motechproject.tama.service.FourDayRecallService;
import org.motechproject.tama.service.TamaSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallListener {
    public static final String PATIENT_DOC_ID_KEY = "patient_id";
    public static final String TREATMENT_ADVICE_DOC_ID_KEY = "treatment_advice_id";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";

    private FourDayRecallCall fourDayRecallCall;
    private TamaSchedulerService schedulerService;
    private FourDayRecallService fourDayRecallService;

    @Autowired
    public FourDayRecallListener(FourDayRecallCall fourDayRecallCall, TamaSchedulerService schedulerService, FourDayRecallService fourDayRecallService) {
        this.fourDayRecallCall = fourDayRecallCall;
        this.schedulerService = schedulerService;
        this.fourDayRecallService = fourDayRecallService;
    }

    @MotechListener(subjects = TAMAConstants.FOUR_DAY_RECALL_SUBJECT)
    public void handle(MotechEvent motechEvent) {
        String patientDocId = motechEvent.getParameters().get(PATIENT_DOC_ID_KEY).toString();
        String treatmentAdviceId = motechEvent.getParameters().get(TREATMENT_ADVICE_DOC_ID_KEY).toString();
        LocalDate startDate = (LocalDate) motechEvent.getParameters().get(START_DATE);
        LocalDate endDate = (LocalDate) motechEvent.getParameters().get(END_DATE);

        if (fourDayRecallService.isAdherenceCapturedForCurrentWeek(patientDocId, treatmentAdviceId, startDate)) return;

        schedulerService.scheduleRepeatingJobsForFourDayRecall(patientDocId, startDate, endDate);
        fourDayRecallCall.execute(patientDocId);
    }
}
