package org.motechproject.tama.ivr.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.AllIVRURLs;
import org.motechproject.ivr.kookoo.extensions.CallFlowController;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.ivr.context.OutboxModuleStratergy;
import org.motechproject.tama.ivr.context.PillModuleStratergy;
import org.motechproject.tama.ivr.context.SymptomModuleStratergy;
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
    private PillModuleStratergy pillModuleStratergy;
    private SymptomModuleStratergy symptomModuleStratergy;
    private OutboxModuleStratergy outboxModuleStratergy;

    @Autowired
    public TAMACallFlowController(TAMATreeRegistry treeRegistry, AllPatients allPatients) {
        this(treeRegistry, allPatients, new TAMAIVRContextFactory());
    }

    public TAMACallFlowController(TAMATreeRegistry treeRegistry, AllPatients allPatients, TAMAIVRContextFactory factory) {
        this.treeRegistry = treeRegistry;
        this.factory = factory;
        this.allPatients = allPatients;
    }

    public void registerPillModule(PillModuleStratergy pillModuleStratergy) {
        this.pillModuleStratergy = pillModuleStratergy;
    }

    public void registerSymptomModule(SymptomModuleStratergy symptomModuleStratergy) {
        this.symptomModuleStratergy = symptomModuleStratergy;
    }

    public void registerOutboxModule(OutboxModuleStratergy outboxModuleStratergy) {
        this.outboxModuleStratergy = outboxModuleStratergy;
    }

    @Override
    public String urlFor(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = factory.create(kooKooIVRContext);
        CallState callState = tamaivrContext.callState();
        if (callState.equals(CallState.STARTED)) return ControllerURLs.AUTHENTICATION_URL;
        if (tamaivrContext.isDialState()) return ControllerURLs.DIAL_URL;
        if (callState.equals(CallState.SYMPTOM_REPORTING)) return ControllerURLs.SYMPTOM_REPORTING_URL;
        if (callState.equals(CallState.HEALTH_TIPS)) return ControllerURLs.HEALTH_TIPS_URL;
        if (callState.equals(CallState.AUTHENTICATED) || callState.equals(CallState.SYMPTOM_REPORTING_TREE)) {
            return AllIVRURLs.DECISION_TREE_URL;
        }
        if (outboxModuleStratergy.hasOutboxCompleted(tamaivrContext)) return ControllerURLs.MENU_REPEAT;
        if (callState.equals(CallState.OUTBOX)) return ControllerURLs.OUTBOX_URL;
        if (callState.equals(CallState.END_OF_FLOW)) return ControllerURLs.MENU_REPEAT;
        if (callState.equals(CallState.ALL_TREES_COMPLETED))
            return hasPendingOutboxMessages(tamaivrContext) ? ControllerURLs.PRE_OUTBOX_URL : ControllerURLs.MENU_REPEAT;
        throw new TamaException("No URL found");
    }

    private boolean hasPendingOutboxMessages(TAMAIVRContext tamaivrContext) {
        return outboxModuleStratergy.getNumberPendingMessages(tamaivrContext.patientId()) != 0;
    }

    @Override
    public String decisionTreeName(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = factory.create(kooKooIVRContext);
        if (StringUtils.isEmpty(tamaivrContext.lastCompletedTree())) return getStartingTree(tamaivrContext);
        if (tamaivrContext.callState().equals(CallState.SYMPTOM_REPORTING_TREE)) return TAMATreeRegistry.REGIMEN_1_TO_6;
        if (onCurrentDosage(tamaivrContext.lastCompletedTree()) && !pillModuleStratergy.previousDosageCaptured(tamaivrContext)) {
            return TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER;
        }
        throw new TamaException("No trees to serve.");
    }

    private boolean onCurrentDosage(String treeName) {
        return TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM.equals(treeName) ||
                TAMATreeRegistry.CURRENT_DOSAGE_REMINDER.equals(treeName);
    }

    private String getStartingTree(TAMAIVRContext tamaivrContext) {
        Patient patient = allPatients.get(tamaivrContext.patientId());
        if (tamaivrContext.isIncomingCall()) {
            if (!patient.isOnDailyPillReminder())
                return TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL;
            else {
                if (patient.getStatus().isSuspended())
                    return TAMATreeRegistry.MENU_TREE;
                if (pillModuleStratergy.isCurrentDoseTaken(tamaivrContext)) {
                    return TAMATreeRegistry.CURRENT_DOSAGE_TAKEN;
                } else {
                    return TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM;
                }
            }
        }

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

    @Override
    public Tree getTree(String treeName, KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = factory.create(kooKooIVRContext);
        if (tamaivrContext.callState().equals(CallState.SYMPTOM_REPORTING_TREE)) {
            return symptomModuleStratergy.getTree(treeName, tamaivrContext);
        }
        return treeRegistry.getTree(treeName);
    }

    @Override
    public void treeComplete(String treeName, KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext ivrContext = factory.create(kooKooIVRContext);
        ivrContext.lastCompletedTree(treeName);
        if ((onCurrentDosage(treeName) && pillModuleStratergy.previousDosageCaptured(ivrContext) && CallState.AUTHENTICATED.equals(ivrContext.callState())) ||
                treeRegistry.isLeafTree(treeName))
            ivrContext.callState(CallState.ALL_TREES_COMPLETED);
    }
}
