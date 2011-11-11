package org.motechproject.tama.tools;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.DateUtil;
import org.motechproject.util.datetime.DateTimeSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.logging.Logger;

public class SetupAdherenceLogs {
    final static Logger log = Logger.getLogger(SetupAdherenceLogs.class.getName());
    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllDosageAdherenceLogs allDosageAdherenceLogs = context.getBean(AllDosageAdherenceLogs.class);
        AllPatients allPatients = context.getBean(AllPatients.class);

        Patient patient = allPatients.findByPatientId(args[0]).get(0);

        LocalDate fromDate = LocalDate.parse(args[1]);
        LocalDate toDate = LocalDate.parse(args[2]);

        int nthDosageThatWillNotBeTaken = Integer.parseInt(args[3]);
        if (patient.getPatientPreferences().getCallPreference() == CallPreference.DailyPillReminder) {
            createAdherenceForDailyReminderPatient(fromDate, toDate, allDosageAdherenceLogs, patient, context, nthDosageThatWillNotBeTaken);
        }
        else if (patient.getPatientPreferences().getCallPreference() == CallPreference.FourDayRecall) {
            log.info("Patient is on four-day recall.");
            if (nthDosageThatWillNotBeTaken>=0 && nthDosageThatWillNotBeTaken<=4){
                createAdherenceForFourDayRecallPatient(fromDate, toDate, patient, context, nthDosageThatWillNotBeTaken);
            }  else {
                log.warning("Invalid number of days missed, valid range [0-4]");
            }

        }
        
        
        
        System.exit(0);
    }

    private static void createAdherenceForFourDayRecallPatient(LocalDate fromDate, LocalDate toDate, Patient patient, ApplicationContext context, int numberOfDosagesMissed) {
        String patientId = patient.getId();
        AllTreatmentAdvices allTreatmentAdvices = context.getBean(AllTreatmentAdvices.class);
        String treatmentAdviceDocId = allTreatmentAdvices.currentTreatmentAdvice(patientId).getId();

        FourDayRecallService fourDayRecallService = (FourDayRecallService)context.getBean("fourDayRecallService");
        AllWeeklyAdherenceLogs allWeeklyAdherenceLogs = context.getBean(AllWeeklyAdherenceLogs.class);

        final DateTimeSource originalSource = DateTimeSourceUtil.SourceInstance;
        for (; fromDate.isBefore(toDate); fromDate = fromDate.plusWeeks(1)){
            final DateTime refDate = DateUtil.newDateTime(fromDate.toDate());
            DateTimeSourceUtil.SourceInstance = new DateTimeSource() {
                @Override
                public DateTimeZone timeZone() {
                    return originalSource.timeZone();
                }

                @Override
                public DateTime now() {
                    return DateUtil.newDateTime(refDate.toDate());
                }

                @Override
                public LocalDate today() {
                    return refDate.toLocalDate();
                }
            };
            WeeklyAdherenceLog adherenceLog = new WeeklyAdherenceLog(patientId, treatmentAdviceDocId, fourDayRecallService.getStartDateForCurrentWeek(patientId), fromDate, numberOfDosagesMissed);
            allWeeklyAdherenceLogs.add(adherenceLog);
        }

        log.info("Adherence % for previous week "  + fourDayRecallService.adherencePercentageForPreviousWeek(patientId));
    }

    private static void createAdherenceForDailyReminderPatient(LocalDate fromDate, LocalDate toDate, AllDosageAdherenceLogs allDosageAdherenceLogs, Patient patient, ApplicationContext context, int nthDosageThatWillNotBeTaken) {
        PillReminderService pillReminderService = (PillReminderService)context.getBean("pillReminderService");
        final PillRegimenResponse pillRegimen = pillReminderService.getPillRegimen(patient.getId());


        String regimenId = pillRegimen.getPillRegimenId();
        String dosageId = pillRegimen.getDosages().get(0).getDosageId();

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
