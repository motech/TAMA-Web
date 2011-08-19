package org.motechproject.tama.domain;

import org.apache.commons.lang.StringUtils;

public enum AilmentState {
    NONE("") {
        @Override
        public boolean isWithDescription() {
            return false;
        }
    },
    YES_WITH_HISTORY("") {
        @Override
        public boolean isWithDescription() {
            return true;
        }
    },
    YES("") {
        @Override
        public boolean isWithDescription() {
            return true;
        }
    };

    private String displayName;

    AilmentState(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public abstract boolean isWithDescription();
}
