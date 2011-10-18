package org.motechproject.tama.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.motechproject.tama.domain.PatientStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingRuleService {

    @Autowired
    private StatelessKnowledgeSession ksession;

    public String symptomReportingTransition(PatientStats stats) {
        final Transition result = new Transition();
        ksession.setGlobal("transition", result);
        ksession.execute(stats);
        return result.getEntryPoint();
    }

    public static class Transition {
        private String entryPoint;

        public String getEntryPoint() {
            return entryPoint;
        }

        public void setEntryPoint(String entryPoint) {
            this.entryPoint = entryPoint;
        }

        @Override
        public String toString() {
            return String.format("Transition :%s", this.entryPoint);
        }
    }

}
