package org.motechproject.tamadatasetup.domain;

import org.motechproject.model.Time;

public class ExpectedDailyPillAdherence {
    private int totalNumberOfDosages;
    private int percentage;
    private int dosagePerDay;

    public ExpectedDailyPillAdherence(int numberOfDays, int percentage, Time morningDoseTime, Time eveningDoseTime) {
        this.percentage = percentage;
        this.dosagePerDay = (morningDoseTime == null ? 0 : 1) +
                                    (eveningDoseTime == null ? 0 : 1);
        totalNumberOfDosages = numberOfDays * dosagePerDay;
    }

    public int numberOfDosageTaken() {
        double returnValue = (totalNumberOfDosages * percentage);
        return (int) (returnValue / (100 * dosagePerDay));
    }
}
