package org.motechproject.tama.fourdayrecall.outbox;

import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.outbox.handler.OutboxCallHandler;
import org.motechproject.tama.outbox.listener.OutboxCallListener;
import org.motechproject.tama.outbox.service.OutboxSchedulerService;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class FourDayRecallOutboxCallHandler implements OutboxCallHandler {

    private AllPatients allPatients;
    private Properties fourDayRecallProperties;
    private OutboxService outboxService;
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    @Autowired
    public FourDayRecallOutboxCallHandler(AllPatients allPatients, @Qualifier("fourDayRecallProperties") Properties fourDayRecallProperties, OutboxService outboxService, FourDayRecallAdherenceService fourDayRecallAdherenceService, OutboxCallListener outboxCallListener) {
        this.allPatients = allPatients;
        this.fourDayRecallProperties = fourDayRecallProperties;
        this.outboxService = outboxService;
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
        outboxCallListener.register(CallPreference.FourDayRecall, this);
    }

    public void handle(MotechEvent motechEvent) {
        String patientDocId = (String) motechEvent.getParameters().get(OutboxSchedulerService.EXTERNAL_ID_KEY);
        Patient patient = allPatients.get(patientDocId);
        if (patient.getStatus().isSuspended() || !isFourDayRecallDay(patient) || fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient)) {
            outboxService.call(motechEvent);
        }
    }

    private boolean isFourDayRecallDay(Patient patient) {
        DayOfWeek bestCallDay = patient.getPatientPreferences().getDayOfWeeklyCall();
        Integer daysToRetry = Integer.valueOf(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        DayOfWeek dayToday = DayOfWeek.getDayOfWeek(DateUtil.today().getDayOfWeek());
        return DayOfWeek.daysStarting(bestCallDay, daysToRetry).contains(dayToday);
    }
}
