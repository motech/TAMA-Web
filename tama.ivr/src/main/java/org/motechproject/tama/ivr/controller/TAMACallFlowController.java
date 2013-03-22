package org.motechproject.tama.ivr.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.extensions.CallFlowController;
import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.ivr.context.OutboxModuleStrategy;
import org.motechproject.tama.ivr.context.PillModuleStrategy;
import org.motechproject.tama.ivr.context.SymptomModuleStrategy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TAMACallFlowController implements CallFlowController {
    private TAMATreeRegistry treeRegistry;
    private TAMAIVRContextFactory factory;
    private AllPatients allPatients;
    private PillModuleStrategy pillModuleStrategy;
    private SymptomModuleStrategy symptomModuleStrategy;
    private OutboxModuleStrategy outboxModuleStrategy;

    @Autowired
    public TAMACallFlowController(TAMATreeRegistry treeRegistry, AllPatients allPatients) {
        this(treeRegistry, allPatients, new TAMAIVRContextFactory());
    }

    public TAMACallFlowController(TAMATreeRegistry treeRegistry, AllPatients allPatients, TAMAIVRContextFactory factory) {
        this.treeRegistry = treeRegistry;
        this.factory = factory;
        this.allPatients = allPatients;
    }

    public void registerPillModule(PillModuleStrategy pillModuleStrategy) {
        this.pillModuleStrategy = pillModuleStrategy;
    }

    public void registerSymptomModule(SymptomModuleStrategy symptomModuleStrategy) {
        this.symptomModuleStrategy = symptomModuleStrategy;
    }

    public void registerOutboxModule(OutboxModuleStrategy outboxModuleStrategy) {
        this.outboxModuleStrategy = outboxModuleStrategy;
    }

    @Override
    public String urlFor(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = factory.create(kooKooIVRContext);
        CallState callState = tamaivrContext.callState();
        if (null != callState) {
            return callState.nextURL(tamaivrContext);
        } else {
            throw new TamaException("No URL found");
        }
    }

    @Override
    public String decisionTreeName(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = factory.create(kooKooIVRContext);
        if (StringUtils.isEmpty(tamaivrContext.lastCompletedTree())) return getStartingTree(tamaivrContext);
        if (tamaivrContext.callState().equals(CallState.PULL_MESSAGES)) return TAMATreeRegistry.PULL_MESSAGES_TREE;
        if (tamaivrContext.callState().equals(CallState.SYMPTOM_REPORTING_TREE)) return TAMATreeRegistry.REGIMEN_1_TO_6;
        if (onCurrentDosage(tamaivrContext.lastCompletedTree()) && !pillModuleStrategy.previousDosageCaptured(tamaivrContext)) {
            return TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER;
        }
        throw new TamaException("No trees to serve.");
    }

    private String getStartingTree(TAMAIVRContext tamaivrContext) {
        Patient patient = allPatients.get(tamaivrContext.patientDocumentId());
        if (tamaivrContext.isIncomingCall()) {
            return startingTreeForIncomingCalls(tamaivrContext, patient);
        } else {
            return startingTreeForOutgoingCalls(tamaivrContext, patient);
        }
    }

    private String startingTreeForOutgoingCalls(TAMAIVRContext tamaivrContext, Patient patient) {
        if (tamaivrContext.hasTraversedTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER)
                || tamaivrContext.hasTraversedTree(TAMATreeRegistry.FOUR_DAY_RECALL)
                || tamaivrContext.hasTraversedTree(TAMATreeRegistry.OUTBOX_CALL)) {
            return TAMATreeRegistry.MENU_TREE;
        }

        if (tamaivrContext.isOutBoxCall() && !tamaivrContext.hasTraversedTree(TAMATreeRegistry.OUTBOX_CALL)) {
            return TAMATreeRegistry.OUTBOX_CALL;
        }

        if (patient.isOnDailyPillReminder() && !tamaivrContext.hasTraversedTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER)) {
            return TAMATreeRegistry.CURRENT_DOSAGE_REMINDER;
        } else {
            return TAMATreeRegistry.FOUR_DAY_RECALL;
        }
    }

    private String startingTreeForIncomingCalls(TAMAIVRContext tamaivrContext, Patient patient) {
        if (patient.isOnWeeklyPillReminder())
            return TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL;
        else {
            if (patient.getStatus().isSuspended())
                return TAMATreeRegistry.INCOMING_MENU_TREE;
            if (pillModuleStrategy.isCurrentDoseTaken(tamaivrContext)) {
                return TAMATreeRegistry.CURRENT_DOSAGE_TAKEN;
            } else {
                return TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM;
            }
        }
    }

    @Override
    public Tree getTree(String treeName, KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = factory.create(kooKooIVRContext);
        if (tamaivrContext.callState().equals(CallState.SYMPTOM_REPORTING_TREE)) {
            return symptomModuleStrategy.getTree(treeName, tamaivrContext);
        }
        return treeRegistry.getTree(treeName);
    }

    @Override
    public void treeComplete(String treeName, KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext ivrContext = factory.create(kooKooIVRContext);
        ivrContext.lastCompletedTree(treeName);
        if ((onCurrentDosage(treeName) && pillModuleStrategy.previousDosageCaptured(ivrContext) && CallState.AUTHENTICATED.equals(ivrContext.callState())) || treeRegistry.isLeafTree(treeName))
            ivrContext.callState(CallState.ALL_TREES_COMPLETED);
    }

    private boolean onCurrentDosage(String treeName) {
        return TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM.equals(treeName) || TAMATreeRegistry.CURRENT_DOSAGE_REMINDER.equals(treeName);
    }
}
