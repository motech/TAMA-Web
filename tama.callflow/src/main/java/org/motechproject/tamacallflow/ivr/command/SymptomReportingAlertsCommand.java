package org.motechproject.tamacallflow.ivr.command;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.PatientAlert;
import org.motechproject.tamadomain.domain.PatientAlertType;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tamacallflow.service.PatientAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Properties;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

@Component
public class SymptomReportingAlertsCommand {

    private Logger logger = Logger.getLogger(this.getClass());
    private PatientAlertService patientAlertService;

    private Properties properties;
    private TAMAIVRContextFactory contextFactory;

    @Autowired
    public SymptomReportingAlertsCommand(PatientAlertService patientAlertService, @Qualifier("symptomProperties") Properties properties) {
        this(patientAlertService, properties, new TAMAIVRContextFactory());
    }

    SymptomReportingAlertsCommand(PatientAlertService patientAlertService, Properties properties, TAMAIVRContextFactory contextFactory) {
        this.patientAlertService = patientAlertService;
        this.properties = properties;
        this.contextFactory = contextFactory;
    }

    public ITreeCommand symptomReportingAlertWithPriority(final Integer priority, final Node node, final TAMAConstants.ReportedType symptomReportedToDoctor) {
        final String adviceGiven = getAdviceGiven(node);
        final String symptomReported = getSymptomReported(node);

        return new ITreeCommand() {
            @Override
            public String[] execute(Object o) {
                TAMAIVRContext ivrContext = contextFactory.create((KooKooIVRContext) o);
                String externalId = ivrContext.patientId();
                HashMap<String, String> data = new HashMap<String, String>();
                data.put(PatientAlert.CONNECTED_TO_DOCTOR, symptomReportedToDoctor.toString());
                patientAlertService.createAlert(externalId, priority, adviceGiven, symptomReported, PatientAlertType.SymptomReporting, data);
                return new String[0];
            }
        };
    }

    private String getSymptomReported(Node node) {
        if (isN02Node(node))
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
