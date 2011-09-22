package org.motechproject.tama.web.command;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.startsWith;

@Component
public class SymptomReportingAlertsCommand {

    private Logger logger = Logger.getLogger(this.getClass());
    private AlertService alertService;

    private Properties properties;

    @Autowired
    public SymptomReportingAlertsCommand(AlertService alertService, @Qualifier("symptomProperties") Properties properties) {
        this.alertService = alertService;
        this.properties = properties;
    }

    public ITreeCommand symptomReportingAlertWithPriority(final Integer priority, Node node) {
        final String symptomReported = getSymptomReported(node);
        final String adviceGiven = getAdviceGiven(node);

        return new BaseTreeCommand() {
            @Override
            public String[] execute(Object o) {
                IVRContext ivrContext = (IVRContext) o;
                String externalId = ivrContext.ivrSession().getExternalId();
                final Alert symptomsAlert = new Alert(externalId, AlertType.MEDIUM, AlertStatus.NEW, priority);
                symptomsAlert.setDateTime(DateUtil.now());
                symptomsAlert.setDescription(symptomReported);
                symptomsAlert.setName(adviceGiven);
                alertService.createAlert(symptomsAlert);
                return new String[0];
            }
        };
    }

    private String getSymptomReported(Node node) {
        final Prompt prompt = selectUnique(node.getPrompts(), having(on(Prompt.class).getName(), startsWith("ppc_")));
        if (prompt == null) {
            logger.error(String.format("No prompt found for node :%s", node.toString()));
            return StringUtils.EMPTY;
        }
        final Object label = properties.get(String.valueOf(prompt.getName()));
        if (label == null) {
            logger.error(String.format("No label found for prompt :%s", prompt.getName()));
            return StringUtils.EMPTY;
        }
        return String.valueOf(label);
    }

    private String getAdviceGiven(Node node) {
        final Prompt prompt = selectUnique(node.getPrompts(), having(on(Prompt.class).getName(), startsWith("adv_")));
        if (prompt != null) {
            final Object label = properties.get(String.valueOf(prompt.getName()));
            if (label == null) {
                logger.error(String.format("No label found for prompt :%s", prompt.getName()));
                return StringUtils.EMPTY;
            }
            return String.valueOf(label);
        }
        return StringUtils.EMPTY;
    }
}
