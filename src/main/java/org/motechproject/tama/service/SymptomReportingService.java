package org.motechproject.tama.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.motechproject.tama.domain.PatientMedicalConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SymptomReportingService {
    @Autowired
    private StatelessKnowledgeSession ksession;

    public String getSymptomReportingTree(PatientMedicalConditions patientMedicalConditions) {
        ArrayList<String> tree = new ArrayList<String>();
        ksession.setGlobal("tree", tree);
        ksession.execute(patientMedicalConditions);
        return tree.get(0);
    }
}
