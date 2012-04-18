package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.api.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.CallPlan;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DailyPillReminderService implements CallPlan {

    private PillReminderService pillReminderService;
    private PillRegimenRequestMapper pillRegimenRequestMapper;
    private DailyPillReminderSchedulerService dailyPillReminderSchedulerService;

    @Autowired
    public DailyPillReminderService(PillReminderService pillReminderService, PillRegimenRequestMapper pillRegimenRequestMapper,
                                    DailyPillReminderSchedulerService dailyPillReminderSchedulerService,
                                    CallPlanRegistry callPlanRegistry) {
        this.pillReminderService = pillReminderService;
        this.pillRegimenRequestMapper = pillRegimenRequestMapper;
        this.dailyPillReminderSchedulerService = dailyPillReminderSchedulerService;
        callPlanRegistry.registerCallPlan(CallPreference.DailyPillReminder, this);
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
}