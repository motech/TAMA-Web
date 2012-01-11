package org.motechproject.tama.patient.domain;

public enum CallPreference {
    DailyPillReminder {
        @Override
        public boolean isDaily() {
            return true;
        }

        @Override
        public String displayName() {
            return "Daily";
        }
    }, FourDayRecall {
        @Override
        public boolean isDaily() {
            return false;
        }

        @Override
        public String displayName() {
            return "Weekly";
        }
    };

    public abstract boolean isDaily();

    public abstract String displayName();

    public boolean isWeekly(){
        return !isDaily();
    }
}