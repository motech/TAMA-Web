package org.motechproject.tama.fourdayrecall.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateWeeklyAdherenceLogs implements ITreeCommand {
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllTreatmentAdvices allTreatmentAdvices;
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    @Autowired
    public CreateWeeklyAdherenceLogs(AllTreatmentAdvices allTreatmentAdvices, FourDayRecallAdherenceService fourDayRecallAdherenceService, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs) {
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
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
        allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, fourDayRecallAdherenceService.getStartDateForCurrentWeek(patientId), numberOfDaysMissed));
        return new String[0];
    }
}