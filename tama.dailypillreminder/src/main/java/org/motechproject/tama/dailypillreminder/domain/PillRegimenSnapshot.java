package org.motechproject.tama.dailypillreminder.domain;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.util.DosageUtil;
import org.motechproject.util.DateUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.filter;

//TODO: We should remove the usage of today and use the time and convert it to date
public class PillRegimenSnapshot {
    private DailyPillReminderContext dailyPillReminderContext;
    private PillRegimenResponse pillRegimen;

    public PillRegimenSnapshot(DailyPillReminderContext dailyPillReminderContext, PillRegimenResponse pillRegimenResponse) {
        this.pillRegimen = pillRegimenResponse;
        this.dailyPillReminderContext = dailyPillReminderContext;
    }

    public Dose getPreviousDose() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        Dose currentDose = getCurrentDose();
        int currentDosageIndex = allDosages.indexOf(currentDose.getDosage());
        return currentDosageIndex == 0 ? getLastDose(allDosages, currentDose.getDate()) : new Dose(allDosages.get(currentDosageIndex - 1), currentDose.getDate());
    }

    public boolean isPreviousDosageCaptured() {
        return isVeryFirstDosageCall() || wasPreviousDosageCapturedYesterday();
    }

    public DateTime getPreviousDoseTime() {
        Dose previousDose = getPreviousDose();
        if (previousDose == null) return null;
        if (isTodaysDose(previousDose))
            return new Time(previousDose.getDosageHour(), previousDose.getDosageMinute()).getDateTime(dailyPillReminderContext.callStartTime());
        else
            return new Time(previousDose.getDosageHour(), previousDose.getDosageMinute()).getDateTime(dailyPillReminderContext.callStartTime().minusDays(1));
    }

    public Dose getNextDose() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        Dose currentDose = getCurrentDose();
        int currentDosageIndex = allDosages.indexOf(currentDose.getDosage());
        return currentDosageIndex == allDosages.size() - 1 ? new Dose(allDosages.get(0), currentDose.getDate().plusDays(1)) :
                new Dose(allDosages.get(currentDosageIndex + 1), currentDose.getDate());
    }

    public DateTime getNextDoseTime() {
        Dose nextDose = getNextDose();
        if (nextDose == null) return null;
        if (isTodaysDose(nextDose))
            return new Time(nextDose.getDosageHour(), nextDose.getDosageMinute()).getDateTime(dailyPillReminderContext.callStartTime());
        else
            return new Time(nextDose.getDosageHour(), nextDose.getDosageMinute()).getDateTime(dailyPillReminderContext.callStartTime().plusDays(1));
    }

    public Dose getCurrentDose() {
        if (dailyPillReminderContext.isIncomingCall()) {
            List<DosageResponse> dosageResponses = getSortedDosages();
            DosageResponse currentDosage = null;
            for (DosageResponse dosageResponse : dosageResponses) {
                if (isCandidate(dosageResponse))
                    currentDosage = dosageResponse;
            }
            return createCurrentDose(dosageResponses, currentDosage);
        } else {
            return getDose(dailyPillReminderContext.dosageId());
        }
    }

    private Dose createCurrentDose(List<DosageResponse> dosageResponses, DosageResponse currentDosage) {
        LocalDate today = dailyPillReminderContext.callStartTime().toLocalDate();
        if (currentDosage == null) {
            return getLastDose(dosageResponses, today);
        }

        int pillWindowStartHour = dailyPillReminderContext.callStartTime().withHourOfDay(currentDosage.getDosageHour()).minusHours(pillRegimen.getReminderRepeatWindowInHours()).getHourOfDay();
        boolean isTomorrowsDosage = pillWindowStartHour > currentDosage.getDosageHour();
        if (isTomorrowsDosage) {
            return new Dose(currentDosage, today.plusDays(1));
        }

        return new Dose(currentDosage, today);
    }

    public int getScheduledDosagesTotalCountForLastFourWeeks() {
        return getScheduledDosagesTotalCountForLastFourWeeks(dailyPillReminderContext.callStartTime());
    }

    protected int getScheduledDosagesTotalCountForLastFourWeeks(DateTime endDate) {
        DateTime startTime = endDate.minusWeeks(4);
        return DosageUtil.getScheduledDosagesTotalCountForLastFourWeeks(startTime, endDate, pillRegimen);
    }

    public int getScheduledDosagesTotalCount() {
        return DosageUtil.getScheduledDosagesTotalCount(dailyPillReminderContext.callStartTime(), pillRegimen);
    }

    public boolean isCurrentDoseTaken() {
        Dose currentDose = getCurrentDose();
        boolean currentDoseIsUndefined = currentDose.getDate().isBefore(currentDose.getStartDate());
        return currentDoseIsUndefined || (currentDose.getResponseLastCapturedDate() != null && currentDose.getResponseLastCapturedDate().equals(currentDose.getDate()));
    }

    public boolean isTodaysDose(Dose dose) {
        return dailyPillReminderContext.callStartTime().toLocalDate().equals(dose.getDate());
    }

    public boolean isTimeToTakeCurrentPill() {
        int pillWindowInMinutes = pillRegimen.getReminderRepeatWindowInHours() * 60;

        return nowIsWithin(pillWindowInMinutes);
    }

    public boolean isEarlyToTakeDose(int dosageIntervalInMinutes) {
        Dose currentDose = getCurrentDose();
        DateTime dosageTime = DateUtil.newDateTime(currentDose.getDate(), currentDose.getDosageHour(), currentDose.getDosageMinute(), 0);
        DateTime dosageWindowStart = dosageTime.minusMinutes(dosageIntervalInMinutes);
        DateTime pillWindowStart = dosageTime.minusHours(pillRegimen.getReminderRepeatWindowInHours());
        return dailyPillReminderContext.callStartTime().isAfter(pillWindowStart) && dailyPillReminderContext.callStartTime().isBefore(dosageWindowStart);
    }

    public boolean isLateToTakeDose() {
        Dose currentDose = getCurrentDose();
        DateTime dosageTime = DateUtil.newDateTime(currentDose.getDate(), currentDose.getDosageHour(), currentDose.getDosageMinute(), 0);
        DateTime pillWindowEnd = dosageTime.plusHours(pillRegimen.getReminderRepeatWindowInHours());
        return dailyPillReminderContext.callStartTime().isAfter(pillWindowEnd);
    }

    public boolean hasTakenDosageOnTime(int dosageIntervalInMinutes) {
        return nowIsWithin(dosageIntervalInMinutes);
    }

    private boolean nowIsWithin(int dosageIntervalInMinutes) {
        Dose dose = getCurrentDose();
        int dosageHour = dose.getDosageHour();
        int dosageMinute = dose.getDosageMinute();

        DateTime dosageTime = dailyPillReminderContext.callStartTime().withHourOfDay(dosageHour).withMinuteOfHour(dosageMinute);

        boolean nowAfterPillWindowStart = dailyPillReminderContext.callStartTime().isAfter(dosageTime.minusMinutes(dosageIntervalInMinutes));
        boolean nowBeforePillWindowEnd = dailyPillReminderContext.callStartTime().isBefore(dosageTime.plusMinutes(dosageIntervalInMinutes));

        return nowAfterPillWindowStart && nowBeforePillWindowEnd;
    }

    public List<String> medicinesForCurrentDose() {
        return medicinesFor(getCurrentDose());
    }

    public List<String> medicinesForPreviousDose() {
        return medicinesFor(getPreviousDose());
    }

    private boolean isVeryFirstDosageCall() {
        Dose currentDose = getCurrentDose();
        boolean noResponseCapturedForCurrentDosage = currentDose.getResponseLastCapturedDate() == null;
        boolean dosageStartedToday = currentDose.getStartDate().equals(currentDose.getDate());
        boolean firstDosageInTheDay = getSortedDosages().get(0).equals(currentDose.getDosage());
        return noResponseCapturedForCurrentDosage && dosageStartedToday && firstDosageInTheDay;
    }

    private boolean wasPreviousDosageCapturedYesterday() {
        Dose previousDose = getPreviousDose();
        return previousDose.getResponseLastCapturedDate() != null && !previousDose.getResponseLastCapturedDate().isBefore(previousDose.getDate());
    }

    protected List<DosageResponse> getSortedDosages() {
        if (CollectionUtils.isEmpty(pillRegimen.getDosages())) return null;
        List<DosageResponse> sortedDosages = pillRegimen.getDosages();
        Collections.sort(sortedDosages, new Comparator<DosageResponse>() {
            @Override
            public int compare(DosageResponse d1, DosageResponse d2) {
                int d1Minutes = d1.getDosageHour() * 60 + d1.getDosageMinute();
                int d2Minutes = d2.getDosageHour() * 60 + d2.getDosageMinute();
                return d1Minutes - d2Minutes;
            }
        });

        return filter(new TypeSafeMatcher<DosageResponse>() {
            @Override
            public boolean matchesSafely(DosageResponse resp) {
                return !resp.getStartDate().isAfter(DateUtil.today());
            }

            @Override
            public void describeTo(Description description) {
            }
        }, sortedDosages);
    }

    private List<String> medicinesFor(Dose dose) {
        if (dose == null) return new ArrayList<String>();
        List<String> medicines = new ArrayList<String>();

        for (MedicineResponse medicine : dose.getMedicines()) {
            if (!dose.getDate().isBefore(medicine.getStartDate()) &&
                    (medicine.getEndDate() == null || !dose.getDate().isAfter(medicine.getEndDate())))
                medicines.add(String.format("pill%s", medicine.getName()));
        }
        return medicines;
    }

    private boolean isCandidate(DosageResponse dosageResponse) {
        DateTime callStartTime = dailyPillReminderContext.callStartTime();
        int hourToCaptureDosage = callStartTime.withHourOfDay(dosageResponse.getDosageHour()).minusHours(pillRegimen.getReminderRepeatWindowInHours()).getHourOfDay();
        int minuteToCaptureDosage = dosageResponse.getDosageMinute();

        return callStartTime.isAfter(callStartTime.withHourOfDay(hourToCaptureDosage).withMinuteOfHour(minuteToCaptureDosage));
    }

    private Dose getLastDose(List<DosageResponse> dosageResponses, LocalDate currentDoseDate) {
        return new Dose(dosageResponses.get(dosageResponses.size() - 1), currentDoseDate.minusDays(1));
    }

    private Dose getDose(String dosageId) {
        if (pillRegimen == null) return null;
        for (DosageResponse dosageResponse : pillRegimen.getDosages()) {
            if (dosageResponse.getDosageId().equals(dosageId)) {
                return new Dose(dosageResponse, dailyPillReminderContext.callStartTime().toLocalDate());
            }
        }
        return null;
    }
}
