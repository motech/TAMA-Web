package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;

import java.util.ArrayList;
import java.util.List;

//TODO: We should remove the usage of today and use the time and convert it to date
public class PillRegimenSnapshot {
    private DailyPillReminderContext dailyPillReminderContext;
    private PillRegimenResponse pillRegimenResponse;

    public PillRegimenSnapshot(DailyPillReminderContext dailyPillReminderContext, PillRegimenResponse pillRegimenResponse) {
        this.pillRegimenResponse = pillRegimenResponse;
        this.dailyPillReminderContext = dailyPillReminderContext;
    }

    public Dose getPreviousDose() {
        return new PillRegimen(pillRegimenResponse).getPreviousDoseAt(dailyPillReminderContext.callStartTime());
    }

    public DateTime getPreviousDoseTime() {
        return new PillRegimen(pillRegimenResponse).getPreviousDoseTime(dailyPillReminderContext.callStartTime());
    }

    public boolean isPreviousDosageCaptured() {
        return new PillRegimen(pillRegimenResponse).isPreviousDosageTaken(dailyPillReminderContext.callStartTime());
    }

    public Dose getCurrentDose() {
        return new PillRegimen(pillRegimenResponse).getDoseAt(dailyPillReminderContext.callStartTime());
    }

    public Dose getNextDose() {
        return new PillRegimen(pillRegimenResponse).getNextDoseAt(dailyPillReminderContext.callStartTime());
    }

    public DateTime getNextDoseTime() {
        return new PillRegimen(pillRegimenResponse).getNextDoseTime(dailyPillReminderContext.callStartTime());
    }

    public boolean isTimeToTakeCurrentPill() {
        return new PillRegimen(pillRegimenResponse).isNowWithinCurrentDosePillWindow(dailyPillReminderContext.callStartTime());
    }

    public boolean hasTakenDosageOnTime(int dosageIntervalInMinutes) {
        return new PillRegimen(pillRegimenResponse).isNowWithinCurrentDosageInterval(dailyPillReminderContext.callStartTime(), dosageIntervalInMinutes);
    }

    public boolean isCurrentDoseTaken() {
        return new PillRegimen(pillRegimenResponse).isCurrentDoseTaken(dailyPillReminderContext.callStartTime());
    }

    public boolean isEarlyToTakeDose(int dosageIntervalInMinutes) {
        return new PillRegimen(pillRegimenResponse).isEarlyToTakeDose(dailyPillReminderContext.callStartTime(), dosageIntervalInMinutes);
    }

    public boolean isLateToTakeDose(int dosageIntervalInMinutes) {
        return new PillRegimen(pillRegimenResponse).isLateToTakeDose(dailyPillReminderContext.callStartTime(), dosageIntervalInMinutes);
    }

    public List<String> medicinesForCurrentDose() {
        return medicinesFor(getCurrentDose());
    }

    public List<String> medicinesForPreviousDose() {
        return medicinesFor(getPreviousDose());
    }

    private List<String> medicinesFor(Dose dose) {
        if (dose == null) return new ArrayList<String>();
        return dose.medicineNames();
    }
}
