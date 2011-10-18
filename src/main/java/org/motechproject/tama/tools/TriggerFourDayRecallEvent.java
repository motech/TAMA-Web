package org.motechproject.tama.tools;

import org.motechproject.model.MotechEvent;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.listener.FourDayRecallListener;
import org.motechproject.tama.platform.service.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class TriggerFourDayRecallEvent {
    public static final String APPLICATION_CONTEXT_XML = "META-INF/spring/applicationContext.xml";

    public static void main(String[] args) {
        String patientId = args[0];

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllPatients allPatients = applicationContext.getBean(AllPatients.class);
        AllTreatmentAdvices allTreatmentAdvices = applicationContext.getBean(AllTreatmentAdvices.class);

        Patient patient = allPatients.findByPatientId(patientId).get(0);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patient.getId());

        Map<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                .withPatientDocId(patient.getId())
                .withTreatmentAdviceId(treatmentAdvice.getId())
                .withTreatmentAdviceStartDate(DateUtil.newDate(treatmentAdvice.getStartDate()))
                .withRetryFlag(false)
                .payload();
        MotechEvent fourDayRecallEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, eventParams);

        System.out.println("Triggering the FourDayRecall event...");
        FourDayRecallListener fourDayRecallListener = applicationContext.getBean(FourDayRecallListener.class);
        fourDayRecallListener.handle(fourDayRecallEvent);
    }
}