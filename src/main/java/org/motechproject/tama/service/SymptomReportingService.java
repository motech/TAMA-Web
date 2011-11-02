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

    @Autowired
    public SymptomReportingService(StatelessKnowledgeSession ksession) {
        this.ksession = ksession;
    }

    public String getSymptomReportingTree(MedicalCondition medicalCondition) {
        ArrayList<String> tree = getTree();
        ksession.setGlobal("tree", tree);
        ksession.execute(medicalCondition);
        if (tree.size() > 1) throw new TamaException("Should not match more than one tree condition.");
        return tree.size() == 0 ? null : tree.get(0);
    }

    protected ArrayList<String> getTree() {
        return new ArrayList<String>();
    }
}
