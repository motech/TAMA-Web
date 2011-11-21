package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TAMATreeRegistry {
    private Map<String, TamaDecisionTree> decisionTrees = new HashMap<String, TamaDecisionTree>();

    public static final String CURRENT_DOSAGE_TAKEN = "CurrentDosageTaken";
    public static final String CURRENT_DOSAGE_REMINDER = "CurrentDosageReminder";
    public static final String PREVIOUS_DOSAGE_REMINDER = "PreviousDosageReminder";
    public static final String CURRENT_DOSAGE_CONFIRM = "CurrentDosageConfirm";
    public static final String REGIMEN_1_TO_6 = "Regimen_1_To_6";
    public static final String FOUR_DAY_RECALL = "FourDayRecall";
    private List<String> leafTreeNames;
    public static final String FOUR_DAY_RECALL_INCOMING_CALL = "FourDayRecallIncomingCallTree";
    public static final String OUTBOX_CALL = "OutboxCallTree";
    public static final String MENU_TREE = "MenuTree";

    @Autowired
    public TAMATreeRegistry(CurrentDosageTakenTree currentDosageTakenTree, CurrentDosageReminderTree currentDosageReminderTree,
                            CurrentDosageConfirmTree currentDosageConfirmTree, SymptomReportingTree regimen1To6Tree, PreviousDosageReminderTree previousDosageReminderTree,
                            FourDayRecallTree fourDayRecallTree, FourDayRecallIncomingCallTree fourDayRecallIncomingCallTree, OutboxCallTree outboxCallTree, MenuTree menuTree) {
        decisionTrees.put(CURRENT_DOSAGE_TAKEN, currentDosageTakenTree);
        decisionTrees.put(CURRENT_DOSAGE_REMINDER, currentDosageReminderTree);
        decisionTrees.put(CURRENT_DOSAGE_CONFIRM, currentDosageConfirmTree);
        decisionTrees.put(PREVIOUS_DOSAGE_REMINDER, previousDosageReminderTree);
        decisionTrees.put(REGIMEN_1_TO_6, regimen1To6Tree);
        decisionTrees.put(FOUR_DAY_RECALL, fourDayRecallTree);
        decisionTrees.put(FOUR_DAY_RECALL_INCOMING_CALL, fourDayRecallIncomingCallTree);
        decisionTrees.put(OUTBOX_CALL, outboxCallTree);
        decisionTrees.put(MENU_TREE, menuTree);
        leafTreeNames = Arrays.asList(PREVIOUS_DOSAGE_REMINDER, REGIMEN_1_TO_6, FOUR_DAY_RECALL);
    }

    public Tree getTree(String treeName) {
        return decisionTrees.get(treeName).getTree();
    }

    public Tree getSymptomReportingTree(String treeName, String symptomReportingTreeName) {
        SymptomReportingTree tamaDecisionTree = (SymptomReportingTree) decisionTrees.get(treeName);
        return tamaDecisionTree.getTree(symptomReportingTreeName);
    }

    public boolean isLeafTree(String treeName) {
        return leafTreeNames.contains(treeName);
    }
}
