package org.motechproject.tama.web.view;

import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallFlowConstants {
    public static final Map<String, List<String>> treeToFlowMap = createTreeToFlowMap();

    private static Map<String, List<String>> createTreeToFlowMap() {
        HashMap<String, List<String>> stringListHashMap = new HashMap<String, List<String>>();
        stringListHashMap.put("Pill Reminder", Arrays.asList(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN,
                                                            TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM,
                                                            TAMATreeRegistry.CURRENT_DOSAGE_REMINDER,
                                                            TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER));
        stringListHashMap.put("Four Day Recall", Arrays.asList(TAMATreeRegistry.FOUR_DAY_RECALL, TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL));
        stringListHashMap.put("Symptoms", Arrays.asList(TAMATreeRegistry.REGIMEN_1_TO_6));
        stringListHashMap.put("Outbox", Arrays.asList(TAMATreeRegistry.OUTBOX_CALL));
        stringListHashMap.put("Menu", Arrays.asList(TAMATreeRegistry.MENU_TREE));
        return stringListHashMap;
    }

    public static final String MENU = "Menu";
    public static final String HEALTH_TIPS = "HealthTips";
    public static final String OUTBOX = "Outbox";

}