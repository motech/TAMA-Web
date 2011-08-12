package org.motechproject.tama.tools;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.repository.Patients;
import org.motechproject.tama.repository.TreatmentAdvices;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupAdherenceLogs {
    public static final String APPLICATION_CONTEXT_XML = "META-INF/spring/applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        DosageAdherenceLogs dosageAdherenceLogs = context.getBean(DosageAdherenceLogs.class);
        Patients patients = context.getBean(Patients.class);
        TreatmentAdvices treatmentAdvices = context.getBean(TreatmentAdvices.class);

        Patient patient = patients.findByPatientId(args[0]).get(0);
        TreatmentAdvice treatmentAdvice = treatmentAdvices.findByPatientId(patient.getId());
        String regimenId = treatmentAdvice.getRegimenId();

        String dosageId = args[1];
        LocalDate fromDate = LocalDate.parse(args[2]);
        LocalDate toDate = LocalDate.parse(args[3]);
        int nthDosageThatWillNotBeTaken = Integer.parseInt(args[4]);

        int days = Days.daysBetween(fromDate, toDate).getDays() + 1;
        System.out.println(String.format("Days: %s", days));

        for (int count = 0; count < days; count++) {
            DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog();
            dosageAdherenceLog.setPatientId(patient.getId());
            dosageAdherenceLog.setRegimenId(regimenId);
            dosageAdherenceLog.setDosageId(dosageId);
            dosageAdherenceLog.setDosageDate(fromDate.plusDays(count));

            if ((count + 1) % nthDosageThatWillNotBeTaken == 0)
                dosageAdherenceLog.setDosageStatus(DosageStatus.NOT_TAKEN);
            else
                dosageAdherenceLog.setDosageStatus(DosageStatus.TAKEN);

            dosageAdherenceLogs.add(dosageAdherenceLog);
            System.out.println(String.format("Inserted record: %s", count + 1));
        }
    }
}
