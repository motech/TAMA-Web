package org.motechproject.tama.patient.domain;

public enum CallPreference {
    DailyPillReminder {
        @Override
        public boolean isDaily() {
            return true;
        }
    }, FourDayRecall {
        @Override
        public boolean isDaily() {
            return false;
        }
    };

    public abstract boolean isDaily();

    public boolean isWeekly(){
        return !isDaily();
    }
}