package org.motechproject.tama.fourdayrecall.command;

import org.joda.time.LocalDate;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallDateService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateWeeklyAdherenceLogs implements ITreeCommand {
    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private FourDayRecallDateService fourDayRecallDateService;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    @Autowired
    public CreateWeeklyAdherenceLogs(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, FourDayRecallDateService fourDayRecallDateService) {
        this.allPatients = allPatients;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.fourDayRecallDateService = fourDayRecallDateService;
    }

    @Override
    public String[] execute(Object o) {
        KooKooIVRContext ivrContext = (KooKooIVRContext) o;
        TAMAIVRContext tamaivrContext = new TAMAIVRContext(ivrContext);

        return executeCommand(tamaivrContext);
    }

    String[] executeCommand(TAMAIVRContext tamaivrContext) {
        String patientId = tamaivrContext.patientId();
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        String treatmentAdviceDocId = treatmentAdvice.getId();

        int numberOfDaysMissed = Integer.parseInt(tamaivrContext.dtmfInput());
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);
        allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, startDateForCurrentWeek, numberOfDaysMissed));
        return new String[0];
    }
}