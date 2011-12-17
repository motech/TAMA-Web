package org.motechproject.tama.healthtips.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.joda.time.LocalDate;
import org.motechproject.tama.healthtips.domain.HealthTipParams;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tamacallflow.service.AdherenceService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HealthTipRuleService {
    
    public static class HealthTipList {
        Map<String, String> healthTips = new HashMap<String, String>();
        public void addHealthTip(String filename, String priority) {
            String oldPriority = healthTips.get(filename);
            if (oldPriority != null) {
                int oldPriorityValue = Integer.valueOf(oldPriority);
                if(Integer.valueOf(priority) >= oldPriorityValue) return;
            }
            healthTips.put(filename, priority);
        }
        public Map<String, String> getHealthTips() {
            return healthTips;
        }
    }

    private StatelessKnowledgeSession healthTipsSession;
    private AdherenceService adherenceService;
    private AllLabResults allLabResults;

    @Autowired
    public HealthTipRuleService(StatelessKnowledgeSession healthTipsSession, AdherenceService adherenceService, AllLabResults allLabResults) {
        this.healthTipsSession = healthTipsSession;
        this.adherenceService = adherenceService;
        this.allLabResults = allLabResults;
    }

    public Map<String, String> getHealthTipsFromRuleEngine(LocalDate treatmentStartDate, Patient patient) {
        HealthTipList healthTipList = new HealthTipList();
        healthTipsSession.setGlobal("healthTips", healthTipList);
        LabResults labResults = allLabResults.findByPatientId(patient.getId());
        LocalDate latestCD4LabTestDate = labResults.latestLabTestDate();
        int latestCD4Count = labResults.latestCD4Count();
        HealthTipParams params = setupParams(treatmentStartDate, patient, latestCD4LabTestDate, latestCD4Count);
        healthTipsSession.execute(params);
        return healthTipList.getHealthTips();
    }

    private HealthTipParams setupParams(LocalDate treatmentStartDate, Patient patient, LocalDate latestCD4LabTestDate, int latestCD4Count) {
        LocalDate lastSevenDays = DateUtil.today().minusDays(6);
        boolean anyDoseTakenLateLastWeek = adherenceService.anyDoseTakenLateSince(patient, lastSevenDays);
        boolean dosageMissedLastWeek = adherenceService.isDosageMissedLastWeek(patient);
        HealthTipParams healthTipParams = new HealthTipParams(patient, dosageMissedLastWeek, anyDoseTakenLateLastWeek);
        healthTipParams.treatmentAdviceStartDate(treatmentStartDate);
        healthTipParams.lastCD4TestDate(latestCD4LabTestDate);
        healthTipParams.lastCD4Count(latestCD4Count);
        return healthTipParams;
    }
}