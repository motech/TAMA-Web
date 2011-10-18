package org.motechproject.tama.service;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.domain.UniquePatientField;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllUniquePatientFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientService {

    private AllPatients allPatients;
    private AllUniquePatientFields allUniquePatientFields;
    private PillReminderService pillReminderService;
    private AllTreatmentAdvices allTreatmentAdvices;
    private TamaSchedulerService tamaSchedulerService;

    @Autowired
    public PatientService(AllPatients allPatients, AllUniquePatientFields allUniquePatientFields, TamaSchedulerService tamaSchedulerService, AllTreatmentAdvices allTreatmentAdvices, PillReminderService pillReminderService) {
        this.allPatients = allPatients;
        this.allUniquePatientFields = allUniquePatientFields;
        this.tamaSchedulerService = tamaSchedulerService;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.pillReminderService = pillReminderService;
    }

    public void update(Patient patient) {
        Patient dbPatient = allPatients.get(patient.getId());
        patient.setRevision(dbPatient.getRevision());

        List<UniquePatientField> oldUniquePatientFields = allUniquePatientFields.get(patient);
        allUniquePatientFields.remove(patient);
        try {
            allUniquePatientFields.add(patient);
        } catch (TamaException e) {
            for (UniquePatientField uniquePatientField : oldUniquePatientFields) {
                allUniquePatientFields.add(new UniquePatientField(uniquePatientField.getId(), uniquePatientField.getPrimaryDocId()));
            }
            throw e;
        }
        allPatients.update(patient);
        postUpdate(patient, dbPatient);
    }

    private void postUpdate(Patient patient, Patient dbPatient) {
        if (callPreferenceChangedFromDailyToFourDayRecall(patient, dbPatient)) {
            unscheduleDailyReminderJobs(patient);
            scheduleFourDayRecallJobs(patient);
        }
    }

    private void scheduleFourDayRecallJobs(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patient.getId());
        if (treatmentAdvice != null)
            tamaSchedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);
    }

    private void unscheduleDailyReminderJobs(Patient patient) {
        pillReminderService.unscheduleJobs(patient.getId());
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patient.getId());
        if (treatmentAdvice != null)
            tamaSchedulerService.unscheduleJobForAdherenceTrendFeedback(treatmentAdvice);
    }

    private boolean callPreferenceChangedFromDailyToFourDayRecall(Patient patient, Patient dbPatient) {
        return dbPatient.getPatientPreferences().getCallPreference().equals(CallPreference.DailyPillReminder) && patient.getPatientPreferences().getCallPreference().equals(CallPreference.FourDayRecall);
    }
}
