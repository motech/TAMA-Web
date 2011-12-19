package org.motechproject.tama.tools;

import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TriggerFourDayRecallEvent {
    public static final String APPLICATION_CONTEXT_XML = "applicationToolsContext.xml";

    public static void main(String[] args) {
        String patientId = args[0];

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllPatients allPatients = applicationContext.getBean(AllPatients.class);
        Patient patient = allPatients.findByPatientId(patientId).get(0);

        System.out.println("Triggering the FourDayRecall event...");
        IVRCall ivrCall = applicationContext.getBean(IVRCall.class);
        ivrCall.makeCall(patient);
    }
}