package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.strategy.DailyPillReminder;
import org.motechproject.tama.patient.strategy.FourDayRecall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TreatmentAdviceService {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private DailyPillReminder dailyPillReminder;
    private FourDayRecall fourDayRecall;

    @Autowired
    public TreatmentAdviceService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    public void registerDailyPillReminder(DailyPillReminder dailyPillReminder) {
        this.dailyPillReminder = dailyPillReminder;
    }

    public void registerFourDayRecall(FourDayRecall fourDayRecall) {
        this.fourDayRecall = fourDayRecall;
    }

    public void createRegimen(TreatmentAdvice treatmentAdvice) {
        allTreatmentAdvices.add(treatmentAdvice);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        final CallPreference callPreference = patient.getPatientPreferences().getCallPreference();
        if (callPreference.equals(CallPreference.DailyPillReminder)) {
            dailyPillReminder.enroll(patient, treatmentAdvice);
        } else if (CallPreference.FourDayRecall.equals(callPreference)) {
            fourDayRecall.enroll(patient, treatmentAdvice);
        }
    }

    public void changeRegimen(String existingTreatmentAdviceId, String discontinuationReason, TreatmentAdvice treatmentAdvice) {
        endCurrentRegimen(existingTreatmentAdviceId, discontinuationReason);
        allTreatmentAdvices.add(treatmentAdvice);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        final CallPreference callPreference = patient.getPatientPreferences().getCallPreference();
        if (callPreference.equals(CallPreference.DailyPillReminder)) {
            dailyPillReminder.reEnroll(patient, treatmentAdvice);
        } else if (CallPreference.FourDayRecall.equals(callPreference)) {
            fourDayRecall.reEnroll(patient, treatmentAdvice);
        }
    }

    private void endCurrentRegimen(String treatmentAdviceId, String discontinuationReason) {
        TreatmentAdvice existingTreatmentAdvice = allTreatmentAdvices.get(treatmentAdviceId);
        existingTreatmentAdvice.endTheRegimen(discontinuationReason);
        allTreatmentAdvices.update(existingTreatmentAdvice);
    }
}






