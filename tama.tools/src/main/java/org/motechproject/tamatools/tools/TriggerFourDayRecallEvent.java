package org.motechproject.tamatools.tools;

import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.call.IvrCall;
import org.motechproject.tamadomain.repository.AllPatients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TriggerFourDayRecallEvent {
    public static final String APPLICATION_CONTEXT_XML = "applicationContext.xml";

    public static void main(String[] args) {
        String patientId = args[0];

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllPatients allPatients = applicationContext.getBean(AllPatients.class);
        Patient patient = allPatients.findByPatientId(patientId).get(0);

        System.out.println("Triggering the FourDayRecall event...");
        IvrCall ivrCall = applicationContext.getBean(IvrCall.class);
        ivrCall.makeCall(patient);
    }
}