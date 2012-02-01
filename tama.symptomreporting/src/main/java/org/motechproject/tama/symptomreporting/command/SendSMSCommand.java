package org.motechproject.tama.symptomreporting.command;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.BaseTreeCommand;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.text.StringStartsWith.startsWith;

public class SendSMSCommand extends BaseTreeCommand {

    private List<Prompt> prompts;
    private SmsService smsService;
    private AllPatients allPatients;
    private Properties messageDescriptions;

    public SendSMSCommand(List<Prompt> prompts, SmsService smsService, AllPatients allPatients, Properties messageDescriptions) {
        this.prompts = prompts;
        this.smsService = smsService;
        this.allPatients = allPatients;
        this.messageDescriptions = messageDescriptions;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext tamaivrContext) {
        Patient patient = allPatients.get(tamaivrContext.patientDocumentId());
        Set<String> messages = new HashSet<String>();
        if (patient.hasAgreedToReceiveOTCAdvice()) {
            for (Prompt advicePrompt : getAdvicePrompts()) {
                String messageBody = messageDescriptions.getProperty(advicePrompt.getName());
                if (StringUtils.isNotEmpty(messageBody)) {
                    smsService.sendSMS(patient.getMobilePhoneNumber(), messageBody);
                    messages.add(TamaIVRMessage.WILL_SEND_SMS);
                }
            }
        }
        return messages.toArray(new String[]{});
    }

    public List<Prompt> getAdvicePrompts() {
        return filter(having(on(AudioPrompt.class).getName(), startsWith("adv_")), prompts);
    }
}
