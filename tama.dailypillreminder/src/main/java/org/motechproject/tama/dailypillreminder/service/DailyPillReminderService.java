package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DailyPillReminderService implements CallPlan {

    private PillReminderService pillReminderService;
    private PillRegimenRequestMapper pillRegimenRequestMapper;
    private DailyPillReminderSchedulerService dailyPillReminderSchedulerService;
    private AllPatientEventLogs allPatientEventLogs;

    @Autowired
    public DailyPillReminderService(PillReminderService pillReminderService, PillRegimenRequestMapper pillRegimenRequestMapper,
                                    DailyPillReminderSchedulerService dailyPillReminderSchedulerService, PatientService patientService,
                                    TreatmentAdviceService treatmentAdviceService, AllPatientEventLogs allPatientEventLogs) {
        this.pillReminderService = pillReminderService;
        this.pillRegimenRequestMapper = pillRegimenRequestMapper;
        this.dailyPillReminderSchedulerService = dailyPillReminderSchedulerService;
        this.allPatientEventLogs = allPatientEventLogs;
        patientService.registerCallPlan(CallPreference.DailyPillReminder, this);
        treatmentAdviceService.registerCallPlan(CallPreference.DailyPillReminder, this);
    }

    public void enroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        if (treatmentAdvice != null) {
            pillReminderService.createNew(pillRegimenRequestMapper.map(patient, treatmentAdvice));
            dailyPillReminderSchedulerService.scheduleDailyPillReminderJobs(patient, treatmentAdvice);
        }
        allPatientEventLogs.add(new PatientEventLog(patient.getId(), PatientEvent.Switched_To_Daily_Pill_Reminder, DateUtil.now()));
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