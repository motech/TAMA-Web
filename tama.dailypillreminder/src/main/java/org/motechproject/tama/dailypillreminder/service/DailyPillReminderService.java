package org.motechproject.tama.dailypillreminder.service;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.patient.strategy.DailyPillReminder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyPillReminderService extends DailyPillReminder {

    private PillReminderService pillReminderService;
    private DailyPillReminderSchedulerService dailyPillReminderSchedulerService;
    private PillRegimenRequestMapper pillRegimenRequestMapper;

    @Autowired
    public DailyPillReminderService(TreatmentAdviceService treatmentAdviceService, PillReminderService pillReminderService, DailyPillReminderSchedulerService dailyPillReminderSchedulerService, PillRegimenRequestMapper pillRegimenRequestMapper) {
        this.pillReminderService = pillReminderService;
        this.dailyPillReminderSchedulerService = dailyPillReminderSchedulerService;
        this.pillRegimenRequestMapper = pillRegimenRequestMapper;
        treatmentAdviceService.registerDailyPillReminder(this);
    }

    @Override
    public void enroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        pillReminderService.createNew(pillRegimenRequestMapper.map(treatmentAdvice));
        dailyPillReminderSchedulerService.scheduleDailyPillReminderJobs(patient, treatmentAdvice);
    }

    @Override
    public void reEnroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        pillReminderService.renew(pillRegimenRequestMapper.map(treatmentAdvice));
        dailyPillReminderSchedulerService.rescheduleDailyPillReminderJobs(patient, treatmentAdvice);
    }
}