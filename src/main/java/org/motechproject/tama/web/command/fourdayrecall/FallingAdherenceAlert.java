package org.motechproject.tama.web.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FallingAdherenceAlert implements ITreeCommand {

    private FourDayRecallService fourDayRecallService;

    private AlertService alertService;

    @Autowired
    public FallingAdherenceAlert(FourDayRecallService fourDayRecallService, AlertService alertService) {
        this.fourDayRecallService = fourDayRecallService;
        this.alertService = alertService;
    }

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext ivrContext = (TAMAIVRContext)o;
        int dosageMissedDays = Integer.parseInt(ivrContext.dtmfInput());
        String patientId = ivrContext.patientId();
        if(fourDayRecallService.isAdherenceFalling(dosageMissedDays, patientId)){
            final Alert alert = new Alert();
            alert.setExternalId(patientId);
            alert.setStatus(AlertStatus.NEW);
            alert.setDateTime(DateUtil.now());
            alertService.createAlert(alert);
        }
        return null;
    }

}
