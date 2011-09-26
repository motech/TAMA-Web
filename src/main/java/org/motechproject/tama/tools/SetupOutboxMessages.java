package org.motechproject.tama.tools;

import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.HashMap;

public class SetupOutboxMessages {
    public static final String APPLICATION_CONTEXT_XML = "META-INF/spring/applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        OutboundVoiceMessageDao outboxRepository = context.getBean(OutboundVoiceMessageDao.class);
        AllPatients patientRepository = context.getBean(AllPatients.class);

        Patient patient = patientRepository.findByPatientId("1111").get(0);
        int messageCount = Integer.parseInt("4");

        for (int count = 0; count < messageCount; count++) {
            OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
            outboundVoiceMessage.setPartyId(patient.getId());
            outboundVoiceMessage.setCreationTime(DateUtil.now().toDate());
            outboundVoiceMessage.setExpirationDate(DateUtil.tomorrow().toDate());
            outboundVoiceMessage.setStatus(OutboundVoiceMessageStatus.PENDING);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("audioFiles", Arrays.asList(String.format("Num_00%d", count)));
            outboundVoiceMessage.setParameters(map);

            outboxRepository.add(outboundVoiceMessage);
            System.out.println(String.format("Added message %d", count));
        }
    }
}