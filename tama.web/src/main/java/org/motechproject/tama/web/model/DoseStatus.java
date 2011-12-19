package org.motechproject.tama.web.model;

public enum DoseStatus {
    TAKEN("Dose Taken") {
        @Override
        public boolean isTaken() {
            return true;
        }
    }, NOT_TAKEN("Dose Not Taken") {
        @Override
        public boolean isTaken() {
            return false;
        }
    };
    private String displayName;

    DoseStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return this.name();
    }

    public abstract boolean isTaken();

    @Override
    public String toString() {
        return displayName;
    }
}
