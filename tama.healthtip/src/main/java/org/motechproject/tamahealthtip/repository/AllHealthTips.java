package org.motechproject.tamahealthtip.repository;

import org.drools.runtime.StatelessKnowledgeSession;
import org.joda.time.LocalDate;
import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamahealthtip.domain.HealthTipParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class AllHealthTips {

    private StatelessKnowledgeSession healthTipsSession;

    @Autowired
    public AllHealthTips(StatelessKnowledgeSession healthTipsSession) {
        this.healthTipsSession = healthTipsSession;
    }

    public Map<String, String> findBy(LocalDate treatmentStartDate, Patient patient) {
        HealthTipParams params = setupParams(treatmentStartDate, patient);
        Map<String, String> healthTips = new HashMap<String, String>();
        healthTipsSession.setGlobal("healthTips", healthTips);
        healthTipsSession.execute(params);
        return healthTips;
    }

    private HealthTipParams setupParams(LocalDate treatmentStartDate, Patient patient) {
        MedicalCondition medicalCondition = new MedicalCondition();
        medicalCondition.treatmentStartDate(treatmentStartDate);
        return new HealthTipParams(medicalCondition, patient);
    }
}