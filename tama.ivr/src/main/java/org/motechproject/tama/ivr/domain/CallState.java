package org.motechproject.tama.ivr.domain;

import org.motechproject.ivr.kookoo.controller.AllIVRURLs;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

import java.util.Arrays;
import java.util.List;

public enum CallState {

    STARTED(ControllerURLs.AUTHENTICATION_URL) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    MAIN_MENU(ControllerURLs.MENU_REPEAT) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    DIAL(ControllerURLs.DIAL_URL) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    AUTHENTICATED(AllIVRURLs.DECISION_TREE_URL) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    ALL_TREES_COMPLETED(ControllerURLs.PUSH_MESSAGES_URL, ControllerURLs.PRE_OUTBOX_URL, ControllerURLs.DIAL_URL) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            if (context.isDialState()) {
                return possibleTransitionURLs.get(2);
            } else {
                return (context.isOutgoingCall()) ? possibleTransitionURLs.get(0) : possibleTransitionURLs.get(1);
            }
        }
    },

    SYMPTOM_REPORTING(ControllerURLs.SYMPTOM_REPORTING_URL) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    SYMPTOM_REPORTING_TREE(AllIVRURLs.DECISION_TREE_URL) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    OUTBOX(ControllerURLs.OUTBOX_URL) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    HEALTH_TIPS(ControllerURLs.HEALTH_TIPS_URL) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    END_OF_HEALTH_TIPS_FLOW(ControllerURLs.MENU_REPEAT) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    PUSH_MESSAGES_COMPLETE(ControllerURLs.MENU_REPEAT) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    },

    PULL_MESSAGES(AllIVRURLs.DECISION_TREE_URL) {
        @Override
        public String nextURL(TAMAIVRContext context) {
            return possibleTransitionURLs.get(0);
        }
    };

    protected List<String> possibleTransitionURLs;

    private CallState(String... possibleTransitionURLs) {
        this.possibleTransitionURLs = Arrays.asList(possibleTransitionURLs);
    }

    public abstract String nextURL(TAMAIVRContext context);
}
