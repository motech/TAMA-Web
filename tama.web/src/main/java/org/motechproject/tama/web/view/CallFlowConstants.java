package org.motechproject.tama.web.view;

import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;

import java.util.HashMap;
import java.util.Map;

public class CallFlowConstants {
    
    public static final Map<String, String> TREE_TO_FLOW_MAP = new HashMap<String, String>() {{
        put(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, "Pill Reminder");
        put(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER, "Pill Reminder");
        put(TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER, "Pill Reminder");
        put(TAMATreeRegistry.FOUR_DAY_RECALL, "4 Day Recall");
        put(TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL, "4 Day Recall");
        put(TAMATreeRegistry.REGIMEN_1_TO_6, "Symptoms");
        put(TAMATreeRegistry.OUTBOX_CALL, "Outbox");
        put(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN, "Menu");
        put(TAMATreeRegistry.MENU_TREE, "Menu");
    }};

    public static final String MENU = "Menu";
    public static final String HEALTH_TIPS = "Health Tips";
    public static final String OUTBOX = "Outbox";
    public static final String UNAUTHENTICATED = "Unauthenticated";
}