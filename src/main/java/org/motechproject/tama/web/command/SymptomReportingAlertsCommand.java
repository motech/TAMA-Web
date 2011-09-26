package org.motechproject.tama.web.command;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
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

    public ITreeCommand symptomReportingAlertWithPriority(final Integer priority, final Node node) {
        final String adviceGiven = getAdviceGiven(node);
        final String symptomReported = getSymptomReported(node);



        return new BaseTreeCommand() {
            @Override
            public String[] execute(Object o) {
                IVRContext ivrContext = (IVRContext) o;
                String externalId = ivrContext.ivrSession().getExternalId();
                final Alert symptomsAlert = new Alert(externalId, AlertType.MEDIUM, AlertStatus.NEW, priority);
                final DateTime now = DateUtil.now();
                symptomsAlert.setDateTime(now);
                symptomsAlert.setDescription(symptomReported);
                symptomsAlert.setName(adviceGiven);
                alertService.createAlert(symptomsAlert);
                return new String[0];
            }
        };
    }

    private String getSymptomReported(Node node) {
        if(isN02Node(node))
            return "-";
        Prompt summaryPrompt = selectFirst(node.getPrompts(), having(on(Prompt.class).getName(), startsWith("ppc_")));
        Prompt affirmativePrompt = selectFirst(node.getPrompts(), having(on(Prompt.class).getName(), startsWith("cy_")));
        Prompt negativePrompt = selectFirst(node.getPrompts(), having(on(Prompt.class).getName(), startsWith("cn_")));

        Prompt prompt = selectFirst(java.util.Arrays.asList(summaryPrompt, affirmativePrompt, negativePrompt), Matchers.<Prompt>notNullValue());

        if (prompt == null) {
            logger.debug(String.format("No prompt found for node :%s", node.toString()));
            return StringUtils.EMPTY;
        }
        final Object label = properties.get(String.valueOf(prompt.getName()));
        if (label == null) {
            logger.debug(String.format("No label found for prompt :%s", prompt.getName()));
            return StringUtils.EMPTY;
        }
        return String.valueOf(label);
    }

    private String getAdviceGiven(Node node) {
        final Prompt prompt = selectUnique(node.getPrompts(), having(on(Prompt.class).getName(), startsWith("adv_")));
        if (prompt != null) {
            final Object label = properties.get(String.valueOf(prompt.getName()));
            if (label == null) {
                logger.debug(String.format("No label found for prompt :%s", prompt.getName()));
                return StringUtils.EMPTY;
            }
            return String.valueOf(label);
        }
        return StringUtils.EMPTY;
    }

    private boolean isN02Node(Node node) {
        return select(node.getPrompts(), having(on(Prompt.class).getName(), equalTo("adv_callclinic"))).size() > 0;
    }
}
