package org.motechproject.tama.healthtips.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.joda.time.LocalDate;
import org.motechproject.tama.healthtips.domain.HealthTipParams;
import org.motechproject.tama.ivr.domain.AdherenceComplianceReport;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllLabResults;
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
                if (Integer.valueOf(priority) >= oldPriorityValue) return;
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
        final AdherenceComplianceReport report = adherenceService.lastWeekAdherence(patient);
        healthTipsSession.execute(new HealthTipParams(patient, report, labResults, treatmentStartDate));
        return healthTipList.getHealthTips();
    }
}