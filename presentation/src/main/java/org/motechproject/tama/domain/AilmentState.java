package org.motechproject.tama.domain;

public enum AilmentState {
    NONE("None") {
        @Override
        public boolean isWithDescription() {
            return false;
        }
    },
    YES_WITH_HISTORY("History and not active") {
        @Override
        public boolean isWithDescription() {
            return true;
        }
    },
    YES("Currently Active") {
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
