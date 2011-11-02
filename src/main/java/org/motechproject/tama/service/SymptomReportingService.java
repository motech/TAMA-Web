package org.motechproject.tama.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.domain.MedicalCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SymptomReportingService {
    private StatelessKnowledgeSession ksession;
    private ArrayList<String> tree;

    @Autowired
    public SymptomReportingService(StatelessKnowledgeSession ksession) {
        this(ksession, new ArrayList<String>());
    }

    public SymptomReportingService(StatelessKnowledgeSession ksession, ArrayList<String> tree) {
        this.ksession = ksession;
        this.tree = tree;
    }

    public String getSymptomReportingTree(MedicalCondition medicalCondition) {
        ksession.setGlobal("tree", tree);
        ksession.execute(medicalCondition);
        if (tree.size() > 1) throw new TamaException("Should not match more than one tree condition.");
        return tree.size() == 0 ? null : tree.get(0);
    }
}
