package org.motechproject.tamacallflow.ivr.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tamacallflow.domain.WeeklyAdherenceLog;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.platform.service.FourDayRecallService;
import org.motechproject.tamacallflow.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateWeeklyAdherenceLogs implements ITreeCommand {
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllTreatmentAdvices allTreatmentAdvices;
    private FourDayRecallService fourDayRecallService;

    @Autowired
    public CreateWeeklyAdherenceLogs(AllTreatmentAdvices allTreatmentAdvices, FourDayRecallService fourDayRecallService, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs) {
        this.fourDayRecallService = fourDayRecallService;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    @Override
    public String[] execute(Object o) {
        KooKooIVRContext ivrContext = (KooKooIVRContext) o;
        TAMAIVRContext tamaivrContext = new TAMAIVRContext(ivrContext);

        return executeCommand(tamaivrContext);
    }

    String[] executeCommand(TAMAIVRContext tamaivrContext) {
        String patientId = tamaivrContext.patientId();
        String treatmentAdviceDocId = allTreatmentAdvices.currentTreatmentAdvice(patientId).getId();
        int numberOfDaysMissed = Integer.parseInt(tamaivrContext.dtmfInput());

        WeeklyAdherenceLog adherenceLog = new WeeklyAdherenceLog(patientId, treatmentAdviceDocId, fourDayRecallService.getStartDateForCurrentWeek(patientId), DateUtil.today(), numberOfDaysMissed);
        allWeeklyAdherenceLogs.add(adherenceLog);

        return new String[0];
    }
}