package org.motechproject.tama.dailypillreminder.call;

import org.motechproject.ivr.service.IVRService;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class PillReminderCall extends IVRCall {
    public static final String TIMES_SENT = "times_sent";
    public static final String TOTAL_TIMES_TO_SEND = "total_times_to_send";
    public static final String RETRY_INTERVAL = "retry_interval";
    private AllPatients allPatients;
    private DailyPillReminderService dailyPillReminderService;
    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;

    @Autowired
    public PillReminderCall(IVRService ivrService, AllPatients allPatients, DailyPillReminderService dailyPillReminderService,
                            DailyPillReminderAdherenceService dailyPillReminderAdherenceService, @Qualifier("ivrProperties") Properties properties) {
        super(ivrService, properties);
        this.allPatients = allPatients;
        this.dailyPillReminderService = dailyPillReminderService;
        this.dailyPillReminderAdherenceService = dailyPillReminderAdherenceService;
    }

    public void execute(String patientDocId, final String dosageId, final int timesSent, final int totalTimesToSend, final int retryInterval) {
        final Patient patient = allPatients.get(patientDocId);
        if (patient != null && patient.allowAdherenceCalls()) {
            Map<String, String> params = new HashMap<String, String>() {{
                put(TIMES_SENT, String.valueOf(timesSent));
                put(TOTAL_TIMES_TO_SEND, String.valueOf(totalTimesToSend));
                put(RETRY_INTERVAL, String.valueOf(retryInterval));
            }};
            if (timesSent == 0) {
                recordDosageStatusAsNotRecordedByDefault(patientDocId);
            }
            makeCall(patient, params);
        }
    }

    private void recordDosageStatusAsNotRecordedByDefault(String patientDocId) {
        PillRegimen pillRegimen = dailyPillReminderService.getPillRegimen(patientDocId);
        Dose dose = pillRegimen.getDoseAt(DateUtil.now());
        dailyPillReminderAdherenceService.recordDosageAdherenceAsNotCaptured(patientDocId, pillRegimen.getId(), dose, DosageStatus.NOT_RECORDED, dose.getDoseTime());
    }
}