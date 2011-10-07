package org.motechproject.tama.web.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateWeeklyAdherenceLogs implements ITreeCommand {
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    public CreateWeeklyAdherenceLogs(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, AllTreatmentAdvices allTreatmentAdvices) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String patientId = ivrContext.ivrSession().getExternalId();
        String treatmentAdviceDocId = allTreatmentAdvices.findByPatientId(patientId).getId();
        int numberOfDaysMissed = Integer.parseInt(ivrContext.ivrRequest().getData());
        
        WeeklyAdherenceLog adherenceLog = new WeeklyAdherenceLog(patientId, DateUtil.today(), numberOfDaysMissed, treatmentAdviceDocId);
        allWeeklyAdherenceLogs.add(adherenceLog);

        return new String[0];
    }
}