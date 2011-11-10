package org.motechproject.tama.tools;

import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.*;
import org.motechproject.util.DateUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupWeeklyAdherenceLog {

    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tools.xml";
    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    public SetupWeeklyAdherenceLog() {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);

        allPatients = context.getBean(AllPatients.class);
        allTreatmentAdvices = context.getBean(AllTreatmentAdvices.class);
        allWeeklyAdherenceLogs = context.getBean(AllWeeklyAdherenceLogs.class);
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Invalid arguments!");
            return;
        }
        SetupWeeklyAdherenceLog setup = new SetupWeeklyAdherenceLog();
        setup.log(args[0], args[1], args[2], args[3], args[4]);
    }

    private void log(String patientId, String logDateYear, String logDateMonth, String logDateDay, String numDaysMissed) {

        Patient patient = allPatients.findByPatientId(patientId).get(0);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());

        WeeklyAdherenceLog log = new WeeklyAdherenceLog();
        log.setPatientId(patient.getId());
        log.setTreatmentAdviceId(treatmentAdvice.getId());
        log.setLogDate(DateUtil.newDate(Integer.parseInt(logDateYear), Integer.parseInt(logDateMonth), Integer.parseInt(logDateDay)));
        log.setNumberOfDaysMissed(Integer.parseInt(numDaysMissed));

        allWeeklyAdherenceLogs.add(log);
    }
}
