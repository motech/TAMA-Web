package org.motechproject.tamatools.tools;

import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.controller.OutboxController;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.HashMap;

public class SetupOutboxMessages {
    public static final String APPLICATION_CONTEXT_XML = "applicationContext.xml";

    public static void main(String[] args) {
        System.out.println("Args length: " + args.length);

        if (args.length < 2) {
            System.err.println("Invalid arguments. Specify patient ID followed by wav file name without extension. Ex: 111 Num_000 Num_001");
            return;
        }

        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        OutboundVoiceMessageDao outboxRepository = context.getBean(OutboundVoiceMessageDao.class);
        AllPatients patientRepository = context.getBean(AllPatients.class);

        System.out.println("Finding patient for ID:" + args[0] + ".");
        Patient patient = patientRepository.findByPatientId(args[0]).get(0);

        for (int i = 1; i < args.length; i++) {
            System.out.println(String.format("Adding message %s", args[i]));

            OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
            VoiceMessageType voiceMessageType = new VoiceMessageType();
            voiceMessageType.setPriority(MessagePriority.MEDIUM);
            voiceMessageType.setVoiceMessageTypeName(OutboxController.VOICE_MESSAGE_COMMAND_AUDIO);
            outboundVoiceMessage.setVoiceMessageType(voiceMessageType);
            outboundVoiceMessage.setPartyId(patient.getId());
            outboundVoiceMessage.setCreationTime(DateUtil.now().toDate());
            outboundVoiceMessage.setExpirationDate(DateUtil.tomorrow().toDate());
            outboundVoiceMessage.setStatus(OutboundVoiceMessageStatus.PENDING);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(OutboxController.VOICE_MESSAGE_COMMAND, Arrays.asList(args[i]));
            outboundVoiceMessage.setParameters(map);

            outboxRepository.add(outboundVoiceMessage);
        }
    }
}