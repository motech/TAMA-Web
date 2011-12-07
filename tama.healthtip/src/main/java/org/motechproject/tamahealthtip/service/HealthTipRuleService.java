package org.motechproject.tamahealthtip.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.joda.time.LocalDate;
import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tamacallflow.service.AdherenceService;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamahealthtip.domain.HealthTipParams;
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

    @Autowired
    public HealthTipRuleService(StatelessKnowledgeSession healthTipsSession, AdherenceService adherenceService) {
        this.healthTipsSession = healthTipsSession;
        this.adherenceService = adherenceService;
    }

    public Map<String, String> getHealthTipsFromRuleEngine(LocalDate treatmentStartDate, Patient patient) {
        HealthTipParams params = setupParams(treatmentStartDate, patient);
        HealthTipList healthTipList = new HealthTipList();
        healthTipsSession.setGlobal("healthTips", healthTipList);
        healthTipsSession.execute(params);
        return healthTipList.getHealthTips();
    }

    private HealthTipParams setupParams(LocalDate treatmentStartDate, Patient patient) {
        MedicalCondition medicalCondition = new MedicalCondition();
        medicalCondition.treatmentStartDate(treatmentStartDate);
        return new HealthTipParams(medicalCondition, patient, adherenceService.isDosageMissedLastWeek(patient));
    }
}