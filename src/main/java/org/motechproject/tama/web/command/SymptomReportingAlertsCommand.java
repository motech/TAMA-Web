package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.web.command.BaseTreeCommand;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingAlertsCommand {

    private AlertService alertService;

    @Autowired
    public SymptomReportingAlertsCommand(AlertService alertService) {
        this.alertService = alertService;
    }

    public ITreeCommand symptomReportingAlertWithPriority(final Integer priority) {
        return new BaseTreeCommand() {
            @Override
            public String[] execute(Object o) {
                IVRContext ivrContext = (IVRContext) o;
                String externalId = ivrContext.ivrSession().getExternalId();
                final Alert symptomsAlert = new Alert(externalId, AlertType.MEDIUM, AlertStatus.NEW, priority);
                symptomsAlert.setDateTime(DateUtil.now());
                alertService.createAlert(symptomsAlert);
                return new String[0];
            }
        };
    }
}
