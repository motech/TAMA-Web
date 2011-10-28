package org.motechproject.tama.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.motechproject.tama.domain.MedicalCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SymptomReportingService {
    @Autowired
    private StatelessKnowledgeSession ksession;

    public String getSymptomReportingTree(MedicalCondition medicalCondition) {
        ArrayList<String> tree = new ArrayList<String>();
        ksession.setGlobal("tree", tree);
        ksession.execute(medicalCondition);
        return tree.size() == 0 ? null : tree.get(0);
    }
}
