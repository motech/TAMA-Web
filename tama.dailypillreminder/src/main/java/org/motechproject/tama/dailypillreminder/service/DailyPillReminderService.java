package org.motechproject.tama.dailypillreminder.service;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            pillReminderService.createNew(pillRegimenRequestMapper.map(treatmentAdvice));
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
}