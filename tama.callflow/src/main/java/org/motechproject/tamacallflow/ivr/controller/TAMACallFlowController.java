package org.motechproject.tamacallflow.ivr.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.AllIVRURLs;
import org.motechproject.ivr.kookoo.extensions.CallFlowController;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacommon.ControllerURLs;
import org.motechproject.tamacommon.TamaException;
import org.motechproject.tamadomain.domain.CallPreference;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.PillRegimenSnapshot;
import org.motechproject.tamacallflow.ivr.context.SymptomsReportingContext;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TAMACallFlowController implements CallFlowController {
    public static final String AUTHENTICATION_URL = ControllerURLs.AUTHENTICATION_URL;
    public static final String OUTBOX_URL = ControllerURLs.OUTBOX_URL;
    public static final String HEALTH_TIPS_URL = ControllerURLs.HEALTH_TIPS_URL;
    public static final String PRE_OUTBOX_URL = ControllerURLs.PRE_OUTBOX_URL;
    public static final String HANG_UP_URL = ControllerURLs.HANG_UP_URL;
    public static final String SYMPTOM_REPORTING_URL = ControllerURLs.SYMPTOM_REPORTING_URL;
    public static final String DIAL_URL = ControllerURLs.DIAL_URL;
    public static final String MENU_REPEAT = ControllerURLs.MENU_REPEAT;
    private TAMATreeRegistry treeRegistry;
    private PillReminderService pillReminderService;
    private VoiceOutboxService voiceOutboxService;
    private TAMAIVRContextFactory factory;
    private AllPatients allPatients;

    @Autowired
    public TAMACallFlowController(TAMATreeRegistry treeRegistry, PillReminderService pillReminderService, AllPatients allPatients, VoiceOutboxService voiceOutboxService) {
        this(treeRegistry, pillReminderService, voiceOutboxService, allPatients, new TAMAIVRContextFactory());
    }

    public TAMACallFlowController(TAMATreeRegistry treeRegistry, PillReminderService pillReminderService, VoiceOutboxService voiceOutboxService, AllPatients allPatients, TAMAIVRContextFactory factory) {
        this.treeRegistry = treeRegistry;
        this.pillReminderService = pillReminderService;
        this.voiceOutboxService = voiceOutboxService;
        this.factory = factory;
        this.allPatients = allPatients;
    }

    @Override
    public String urlFor(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = factory.create(kooKooIVRContext);
        SymptomsReportingContext symptomsReportingContext = factory.createSymptomReportingContext(kooKooIVRContext);
        CallState callState = tamaivrContext.callState();
        if (callState.equals(CallState.STARTED)) return AUTHENTICATION_URL;
        if (symptomsReportingContext.isDialState()) return DIAL_URL;
        if (callState.equals(CallState.SYMPTOM_REPORTING)) return SYMPTOM_REPORTING_URL;
        if (callState.equals(CallState.HEALTH_TIPS)) return HEALTH_TIPS_URL;
        if (callState.equals(CallState.AUTHENTICATED) || callState.equals(CallState.SYMPTOM_REPORTING_TREE)) {
            return AllIVRURLs.DECISION_TREE_URL;
        }
        if (tamaivrContext.hasOutboxCompleted()) return MENU_REPEAT;
        if (callState.equals(CallState.OUTBOX)) return OUTBOX_URL;
        if (callState.equals(CallState.END_OF_FLOW)) return MENU_REPEAT;
        if (callState.equals(CallState.ALL_TREES_COMPLETED))
            return hasPendingOutboxMessages(tamaivrContext) ? PRE_OUTBOX_URL : MENU_REPEAT;
        throw new TamaException("No URL found");
    }

    private boolean hasPendingOutboxMessages(TAMAIVRContext tamaivrContext) {
        return voiceOutboxService.getNumberPendingMessages(tamaivrContext.patientId()) != 0;
    }

    @Override
    public String decisionTreeName(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = factory.create(kooKooIVRContext);
        if (StringUtils.isEmpty(tamaivrContext.lastCompletedTree())) return getStartingTree(tamaivrContext);
        if (tamaivrContext.callState().equals(CallState.SYMPTOM_REPORTING_TREE)) return TAMATreeRegistry.REGIMEN_1_TO_6;
        if (onCurrentDosage(tamaivrContext.lastCompletedTree()) && !previousDosageCaptured(tamaivrContext)) {
            return TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER;
        }
        throw new TamaException("No trees to serve.");
    }

    private boolean previousDosageCaptured(TAMAIVRContext tamaivrContext) {
        PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(tamaivrContext);
        return pillRegimenSnapshot.isPreviousDosageCaptured();
    }

    private PillRegimenSnapshot pillRegimenSnapshot(TAMAIVRContext tamaivrContext) {
        return tamaivrContext.pillRegimenSnapshot(pillReminderService);
    }

    private boolean onCurrentDosage(String treeName) {
        return TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM.equals(treeName) ||
                TAMATreeRegistry.CURRENT_DOSAGE_REMINDER.equals(treeName);
    }

    private String getStartingTree(TAMAIVRContext tamaivrContext) {
        Patient patient = tamaivrContext.patient(allPatients);
        boolean isPatientOnDailyPillReminder = CallPreference.DailyPillReminder.equals(patient.getPatientPreferences().getCallPreference());
        if (tamaivrContext.isIncomingCall()) {
            if (!isPatientOnDailyPillReminder)
                return TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL;
            else {
                if (patient.getStatus().isSuspended())
                    return TAMATreeRegistry.MENU_TREE;
                PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(tamaivrContext);
                if (pillRegimenSnapshot.isCurrentDosageTaken()) {
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

        if (isPatientOnDailyPillReminder && !tamaivrContext.hasTraversedTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER)) {
            return TAMATreeRegistry.CURRENT_DOSAGE_REMINDER;
        }
        else {
            return TAMATreeRegistry.FOUR_DAY_RECALL;
        }
    }

    @Override
    public Tree getTree(String treeName, KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = factory.create(kooKooIVRContext);
        if (tamaivrContext.callState().equals(CallState.SYMPTOM_REPORTING_TREE)) {
            return treeRegistry.getSymptomReportingTree(treeName, tamaivrContext.symptomReportingTree());
        }

        return treeRegistry.getTree(treeName);
    }

    @Override
    public void treeComplete(String treeName, KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext ivrContext = factory.create(kooKooIVRContext);
        ivrContext.lastCompletedTree(treeName);
        if ((onCurrentDosage(treeName) && previousDosageCaptured(ivrContext) && CallState.AUTHENTICATED.equals(ivrContext.callState())) ||
                treeRegistry.isLeafTree(treeName))
            ivrContext.callState(CallState.ALL_TREES_COMPLETED);
    }
}
