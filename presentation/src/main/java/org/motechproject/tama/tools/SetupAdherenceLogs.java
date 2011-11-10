package org.motechproject.tama.tools;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupAdherenceLogs {
    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllDosageAdherenceLogs allDosageAdherenceLogs = context.getBean(AllDosageAdherenceLogs.class);
        AllPatients allPatients = context.getBean(AllPatients.class);

        Patient patient = allPatients.findByPatientId(args[0]).get(0);
        PillReminderService pillReminderService = (PillReminderService)context.getBean("pillReminderService");
        final PillRegimenResponse pillRegimen = pillReminderService.getPillRegimen(patient.getId());


        String regimenId = pillRegimen.getPillRegimenId();
        String dosageId = pillRegimen.getDosages().get(0).getDosageId();
        LocalDate fromDate = LocalDate.parse(args[1]);
        LocalDate toDate = LocalDate.parse(args[2]);
        int nthDosageThatWillNotBeTaken = Integer.parseInt(args[3]);

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

            allDosageAdherenceLogs.add(dosageAdherenceLog);
            System.out.println(String.format("Inserted record: %s", count + 1));
        }
    }
}
