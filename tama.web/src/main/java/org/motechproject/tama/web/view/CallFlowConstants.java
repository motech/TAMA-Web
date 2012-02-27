package org.motechproject.tama.web.view;

import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;

import java.util.HashMap;
import java.util.Map;

public class CallFlowConstants {

    public static final Map<String, String> TREE_TO_FLOW_MAP = new HashMap<String, String>() {{
        put(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, CallTypeConstants.DAILY_PILL_REMINDER_CALL);
        put(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER, CallTypeConstants.DAILY_PILL_REMINDER_CALL);
        put(TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER, CallTypeConstants.DAILY_PILL_REMINDER_CALL);
        put(TAMATreeRegistry.FOUR_DAY_RECALL, CallTypeConstants.FOUR_DAY_RECALL_CALL);
        put(TAMATreeRegistry.REGIMEN_1_TO_6, CallTypeConstants.SYMPTOMS_CALL);
        put(TAMATreeRegistry.OUTBOX_CALL, CallTypeConstants.OUTBOX_CALL);
        put(TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL, CallTypeConstants.MENU);
        put(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN, CallTypeConstants.MENU);
        put(TAMATreeRegistry.MENU_TREE, CallTypeConstants.MENU);
    }};
}