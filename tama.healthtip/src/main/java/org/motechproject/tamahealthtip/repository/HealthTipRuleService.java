package org.motechproject.tamahealthtip.repository;

import org.drools.runtime.StatelessKnowledgeSession;
import org.joda.time.LocalDate;
import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tamacallflow.service.AdherenceService;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamahealthtip.domain.HealthTipParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Component
public class HealthTipRuleService {

    private StatelessKnowledgeSession healthTipsSession;
    private AdherenceService adherenceService;

    @Autowired
    public HealthTipRuleService(StatelessKnowledgeSession healthTipsSession, AdherenceService adherenceService) {
        this.healthTipsSession = healthTipsSession;
        this.adherenceService = adherenceService;
    }

    public Map<String, String> getHealthTipsFromRuleEngine(LocalDate treatmentStartDate, Patient patient) {
        HealthTipParams params = setupParams(treatmentStartDate, patient);
        Map<String, String> healthTips = new HashMap<String, String>();
        healthTipsSession.setGlobal("healthTips", healthTips);
        healthTipsSession.execute(params);
        return healthTips;
    }

    private HealthTipParams setupParams(LocalDate treatmentStartDate, Patient patient) {
        MedicalCondition medicalCondition = new MedicalCondition();
        medicalCondition.treatmentStartDate(treatmentStartDate);
        return new HealthTipParams(medicalCondition, patient, adherenceService.isDosageMissedLastWeek(patient));
    }
}