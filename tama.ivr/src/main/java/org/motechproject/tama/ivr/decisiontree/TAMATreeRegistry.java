package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Tree;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TAMATreeRegistry {

    private Map<String, TamaDecisionTree> decisionTrees = new HashMap<String, TamaDecisionTree>();
    public static final String CURRENT_DOSAGE_TAKEN = "CurrentDosageTaken";

    public static final String PULL_MESSAGES_TREE = "PullMessagesTree";
    public static final String CURRENT_DOSAGE_REMINDER = "CurrentDosageReminder";
    public static final String PREVIOUS_DOSAGE_REMINDER = "PreviousDosageReminder";
    public static final String CURRENT_DOSAGE_CONFIRM = "CurrentDosageConfirm";
    public static final String REGIMEN_1_TO_6 = "Regimen_1_To_6";
    public static final String FOUR_DAY_RECALL = "FourDayRecall";
    public static final String FOUR_DAY_RECALL_INCOMING_CALL = "FourDayRecallIncomingCallTree";
    public static final String OUTBOX_CALL = "OutboxCallTree";
    public static final String MENU_TREE = "MenuTree";
    public static final String INCOMING_MENU_TREE = "IncomingMenuTree";

    private List<String> leafTreeNames;

    public TAMATreeRegistry() {
        leafTreeNames = Arrays.asList(PREVIOUS_DOSAGE_REMINDER, REGIMEN_1_TO_6, FOUR_DAY_RECALL);
    }

    public void register(String treeName, TamaDecisionTree tamaDecisionTree) {
        decisionTrees.put(treeName, tamaDecisionTree);
    }

    public Map<String, TamaDecisionTree> getDecisionTrees() {
        return decisionTrees;
    }

    public Tree getTree(String treeName) {
        return decisionTrees.get(treeName).getTree();
    }

    public boolean isLeafTree(String treeName) {
        return leafTreeNames.contains(treeName);
    }
}
