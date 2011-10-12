package org.motechproject.tama.tools;

import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.*;
import org.motechproject.util.DateUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UpdateTreatmentAdvice {

    public static final String APPLICATION_CONTEXT_XML = "META-INF/spring/applicationContext-tools.xml";
    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;

    public UpdateTreatmentAdvice() {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);

        allPatients = context.getBean(AllPatients.class);
        allTreatmentAdvices = context.getBean(AllTreatmentAdvices.class);
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Invalid arguments!");
            return;
        }
        UpdateTreatmentAdvice update = new UpdateTreatmentAdvice();
        update.treatmentAdvice(args[0], args[1], args[2], args[3]);
    }

    private void treatmentAdvice(String patientId, String startDateYear, String startDateMonth, String startDateDay) {
        Patient patient = allPatients.findByPatientId(patientId).get(0);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patient.getId());
        for (DrugDosage drugDosage : treatmentAdvice.getDrugDosages()) {
            drugDosage.setStartDate(DateUtil.newDate(Integer.parseInt(startDateYear), Integer.parseInt(startDateMonth), Integer.parseInt(startDateDay)));
        }

        allTreatmentAdvices.update(treatmentAdvice);
    }
}
