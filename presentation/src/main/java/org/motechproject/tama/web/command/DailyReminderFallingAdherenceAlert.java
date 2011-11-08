package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.service.DailyReminderAdherenceTrendService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyReminderFallingAdherenceAlert implements ITreeCommand{

    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    private AlertService alertService;

    @Autowired
    public DailyReminderFallingAdherenceAlert(DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService, AlertService alertService) {
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.alertService = alertService;
    }
    @Override
    public String[] execute(Object o) {
        TAMAIVRContext ivrContext = (TAMAIVRContext)o;
        final String patientId = ivrContext.patientId();
        if(dailyReminderAdherenceTrendService.isAdherenceFalling(patientId)){
            final Alert alert = new Alert();
            alert.setExternalId(patientId);
            alert.setStatus(AlertStatus.NEW);
            alert.setDateTime(DateUtil.now());
            alertService.createAlert(alert);
        }
        return new String[0];
    }
}
