package org.motechproject.tama.symptomreporting.command;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.symptomreporting.service.SymptomReportingAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.startsWith;

@Component
public class UpdateAdviceCommand {

    private Logger logger = Logger.getLogger(this.getClass());
    private SymptomReportingAlertService symptomReportingAlertService;

    private Properties properties;
    private TAMAIVRContextFactory contextFactory;

    @Autowired
    public UpdateAdviceCommand(SymptomReportingAlertService symptomReportingAlertService, @Qualifier("symptomProperties") Properties properties) {
        this(symptomReportingAlertService, properties, new TAMAIVRContextFactory());
    }

    UpdateAdviceCommand(SymptomReportingAlertService symptomReportingAlertService, Properties properties, TAMAIVRContextFactory contextFactory) {
        this.symptomReportingAlertService = symptomReportingAlertService;
        this.properties = properties;
        this.contextFactory = contextFactory;
    }

    public ITreeCommand get(final Integer priority, final Node node) {
        final String adviceGiven = getAdviceGiven(node);
        return new ITreeCommand() {
            @Override
            public String[] execute(Object o) {
                TAMAIVRContext ivrContext = contextFactory.create((KooKooIVRContext) o);
                String externalId = ivrContext.patientDocumentId();
                symptomReportingAlertService.updateAdviceOnSymptomsReportingAlert(externalId, adviceGiven, priority);
                return new String[0];
            }
        };
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
}
