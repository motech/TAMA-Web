package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DailyPillReminderService implements CallPlan {

    private PillReminderService pillReminderService;
    private PillRegimenRequestMapper pillRegimenRequestMapper;
    private DailyPillReminderSchedulerService dailyPillReminderSchedulerService;

    @Autowired
    public DailyPillReminderService(PillReminderService pillReminderService, PillRegimenRequestMapper pillRegimenRequestMapper, DailyPillReminderSchedulerService dailyPillReminderSchedulerService, PatientService patientService, TreatmentAdviceService treatmentAdviceService) {
        this.pillReminderService = pillReminderService;
        this.pillRegimenRequestMapper = pillRegimenRequestMapper;
        this.dailyPillReminderSchedulerService = dailyPillReminderSchedulerService;
        patientService.registerCallPlan(CallPreference.DailyPillReminder, this);
        treatmentAdviceService.registerCallPlan(CallPreference.DailyPillReminder, this);
    }

    public void enroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        if (treatmentAdvice != null) {
            pillReminderService.createNew(pillRegimenRequestMapper.map(patient, treatmentAdvice));
            dailyPillReminderSchedulerService.scheduleDailyPillReminderJobs(patient, treatmentAdvice);
        }
    }

    public void disEnroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        if (treatmentAdvice != null) {
            pillReminderService.remove(patient.getId());
            dailyPillReminderSchedulerService.unscheduleDailyPillReminderJobs(patient);
        }
    }

    public void reEnroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        disEnroll(patient, treatmentAdvice);
        enroll(patient, treatmentAdvice);
    }

    public PillRegimen getPillRegimen(String patientId) {
        return new PillRegimen(pillReminderService.getPillRegimen(patientId));
    }

    public void setLastCapturedDate(String pillRegimenId, String dosageId, LocalDate lastCapturedDate) {
        pillReminderService.dosageStatusKnown(pillRegimenId, dosageId, lastCapturedDate);
    }

    public Map<String, Time> getDosageTimesFor(String patientDocId) {
        PillRegimenResponse pillRegimenResponse = pillReminderService.getPillRegimen(patientDocId);
        List<DosageResponse> dosageResponses = pillRegimenResponse.getDosages();

        Map<String, Time> map = new HashMap<String, Time>();

        for(DosageResponse dosageResponse : dosageResponses){
            map.put(dosageResponse.getDosageId(), new Time(dosageResponse.getDosageHour(), dosageResponse.getDosageMinute()));
        }
        return map;
    }
}