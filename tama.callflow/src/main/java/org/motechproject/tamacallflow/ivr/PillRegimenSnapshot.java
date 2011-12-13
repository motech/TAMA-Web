package org.motechproject.tamacallflow.ivr;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.util.DosageUtil;
import org.motechproject.util.DateUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.filter;

//TODO: We should remove the usage of today and use the time and convert it to date
public class PillRegimenSnapshot {
    private TAMAIVRContext tamaIVRContext;
    private PillRegimenResponse pillRegimen;

    public static PillRegimenSnapshot create(TAMAIVRContext tamaIVRContext, PillReminderService pillReminderService) {
        PillRegimenResponse pillRegimenResponse = pillReminderService.getPillRegimen(tamaIVRContext.patientId());
        return new PillRegimenSnapshot(tamaIVRContext, pillRegimenResponse);
    }

    public PillRegimenSnapshot(TAMAIVRContext tamaIVRContext, PillRegimenResponse pillRegimenResponse) {
        this.pillRegimen = pillRegimenResponse;
        this.tamaIVRContext = tamaIVRContext;
    }

    public Dose getPreviousDosage() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        Dose currentDose = getCurrentDosage();
        int currentDosageIndex = allDosages.indexOf(currentDose.getDosage());
        return currentDosageIndex == 0 ? getLastDosage(allDosages, currentDose.getDosageDate()) : new Dose(allDosages.get(currentDosageIndex - 1), currentDose.getDosageDate());
    }

    public boolean isPreviousDosageCaptured() {
        return isVeryFirstDosageCall() || wasPreviousDosageCapturedYesterday();
    }

    public DateTime getPreviousDosageTime() {
        Dose previousDose = getPreviousDosage();
        if (previousDose == null) return null;
        if (isTodaysDosage(previousDose))
            return new Time(previousDose.getDosageHour(), previousDose.getDosageMinute()).getDateTime(tamaIVRContext.callStartTime());
        else
            return new Time(previousDose.getDosageHour(), previousDose.getDosageMinute()).getDateTime(tamaIVRContext.callStartTime().minusDays(1));
    }

    public Dose getNextDosage() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        Dose currentDose = getCurrentDosage();
        int currentDosageIndex = allDosages.indexOf(currentDose.getDosage());
        return currentDosageIndex == allDosages.size() - 1 ? new Dose(allDosages.get(0), currentDose.getDosageDate().plusDays(1)) :
                new Dose(allDosages.get(currentDosageIndex + 1), currentDose.getDosageDate());
    }

    public DateTime getNextDosageTime() {
        Dose nextDose = getNextDosage();
        if (nextDose == null) return null;
        if (isTodaysDosage(nextDose))
            return new Time(nextDose.getDosageHour(), nextDose.getDosageMinute()).getDateTime(tamaIVRContext.callStartTime());
        else
            return new Time(nextDose.getDosageHour(), nextDose.getDosageMinute()).getDateTime(tamaIVRContext.callStartTime().plusDays(1));
    }

    public Dose getCurrentDosage() {
        if (tamaIVRContext.isIncomingCall()) {
            List<DosageResponse> dosageResponses = getSortedDosages();
            DosageResponse currentDosage = null;
            for (DosageResponse dosageResponse : dosageResponses) {
                if (isCandidate(dosageResponse))
                    currentDosage = dosageResponse;
            }
            return getCurrentDosageWithDosageDate(dosageResponses, currentDosage);
        } else {
            return getDosage(tamaIVRContext.dosageId());
        }
    }

    private Dose getCurrentDosageWithDosageDate(List<DosageResponse> dosageResponses, DosageResponse currentDosage) {
        LocalDate today = tamaIVRContext.callStartTime().toLocalDate();
        if (currentDosage == null) {
            return getLastDosage(dosageResponses, today);
        }

        int pillWindowStartHour = tamaIVRContext.callStartTime().withHourOfDay(currentDosage.getDosageHour()).minusHours(pillRegimen.getReminderRepeatWindowInHours()).getHourOfDay();
        boolean isTomorrowsDosage = pillWindowStartHour > currentDosage.getDosageHour();
        if (isTomorrowsDosage) {
            return new Dose(currentDosage, today.plusDays(1));
        }

        return new Dose(currentDosage, today);
    }

    public int getScheduledDosagesTotalCountForLastFourWeeks() {
        return getScheduledDosagesTotalCountForLastFourWeeks(tamaIVRContext.callStartTime());
    }

    protected int getScheduledDosagesTotalCountForLastFourWeeks(DateTime endDate) {
        DateTime startTime = endDate.minusWeeks(4);
        return DosageUtil.getScheduledDosagesTotalCountForLastFourWeeks(startTime, endDate, pillRegimen);
    }

    public int getScheduledDosagesTotalCount() {
        return DosageUtil.getScheduledDosagesTotalCount(tamaIVRContext.callStartTime(), pillRegimen);
    }

    public boolean isCurrentDosageTaken() {
        Dose currentDose = getCurrentDosage();
        boolean currentDosageIsUndefined = currentDose.getDosageDate().isBefore(currentDose.getStartDate());
        return currentDosageIsUndefined || (currentDose.getResponseLastCapturedDate() != null && currentDose.getResponseLastCapturedDate().equals(currentDose.getDosageDate()));
    }

    public boolean isTodaysDosage(Dose dose) {
        return tamaIVRContext.callStartTime().toLocalDate().equals(dose.getDosageDate());
    }

    public boolean isTimeToTakeCurrentPill() {
        int pillWindowInMinutes = pillRegimen.getReminderRepeatWindowInHours() * 60;

        return nowIsWithin(pillWindowInMinutes);
    }

    public boolean isEarlyToTakeDosage(int dosageIntervalInMinutes) {
        Dose currentDose = getCurrentDosage();
        DateTime dosageTime = DateUtil.newDateTime(currentDose.getDosageDate(), currentDose.getDosageHour(), currentDose.getDosageMinute(), 0);
        DateTime dosageWindowStart = dosageTime.minusMinutes(dosageIntervalInMinutes);
        DateTime pillWindowStart = dosageTime.minusHours(pillRegimen.getReminderRepeatWindowInHours());
        return tamaIVRContext.callStartTime().isAfter(pillWindowStart) && tamaIVRContext.callStartTime().isBefore(dosageWindowStart);
    }

    public boolean isLateToTakeDosage() {
        Dose currentDose = getCurrentDosage();
        DateTime dosageTime = DateUtil.newDateTime(currentDose.getDosageDate(), currentDose.getDosageHour(), currentDose.getDosageMinute(), 0);
        DateTime pillWindowEnd = dosageTime.plusHours(pillRegimen.getReminderRepeatWindowInHours());
        return tamaIVRContext.callStartTime().isAfter(pillWindowEnd);
    }

    public boolean hasTakenDosageOnTime(int dosageIntervalInMinutes) {
        return nowIsWithin(dosageIntervalInMinutes);
    }

    private boolean nowIsWithin(int dosageIntervalInMinutes) {
        Dose dose = getCurrentDosage();
        int dosageHour = dose.getDosageHour();
        int dosageMinute = dose.getDosageMinute();

        DateTime dosageTime = tamaIVRContext.callStartTime().withHourOfDay(dosageHour).withMinuteOfHour(dosageMinute);

        boolean nowAfterPillWindowStart = tamaIVRContext.callStartTime().isAfter(dosageTime.minusMinutes(dosageIntervalInMinutes));
        boolean nowBeforePillWindowEnd = tamaIVRContext.callStartTime().isBefore(dosageTime.plusMinutes(dosageIntervalInMinutes));

        return nowAfterPillWindowStart && nowBeforePillWindowEnd;
    }

    public List<String> medicinesForCurrentDosage() {
        return medicinesFor(getCurrentDosage());
    }

    public List<String> medicinesForPreviousDosage() {
        return medicinesFor(getPreviousDosage());
    }

    private boolean isVeryFirstDosageCall() {
        Dose currentDose = getCurrentDosage();
        boolean noResponseCapturedForCurrentDosage = currentDose.getResponseLastCapturedDate() == null;
        boolean dosageStartedToday = currentDose.getStartDate().equals(currentDose.getDosageDate());
        boolean firstDosageInTheDay = getSortedDosages().get(0).equals(currentDose.getDosage());
        return noResponseCapturedForCurrentDosage && dosageStartedToday && firstDosageInTheDay;
    }

    private boolean wasPreviousDosageCapturedYesterday() {
        Dose previousDose = getPreviousDosage();
        return previousDose.getResponseLastCapturedDate() != null && !previousDose.getResponseLastCapturedDate().isBefore(previousDose.getDosageDate());
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
            if (!dose.getDosageDate().isBefore(medicine.getStartDate()) &&
                    (medicine.getEndDate() == null || !dose.getDosageDate().isAfter(medicine.getEndDate())))
                medicines.add(String.format("pill%s", medicine.getName()));
        }
        return medicines;
    }

    private boolean isCandidate(DosageResponse dosageResponse) {
        DateTime callStartTime = tamaIVRContext.callStartTime();
        int hourToCaptureDosage = callStartTime.withHourOfDay(dosageResponse.getDosageHour()).minusHours(pillRegimen.getReminderRepeatWindowInHours()).getHourOfDay();
        int minuteToCaptureDosage = dosageResponse.getDosageMinute();

        return callStartTime.isAfter(callStartTime.withHourOfDay(hourToCaptureDosage).withMinuteOfHour(minuteToCaptureDosage));
    }

    private Dose getLastDosage(List<DosageResponse> dosageResponses, LocalDate currentDoseDate) {
        return new Dose(dosageResponses.get(dosageResponses.size() - 1), currentDoseDate.minusDays(1));
    }

    private Dose getDosage(String dosageId) {
        if (pillRegimen == null) return null;
        for (DosageResponse dosageResponse : pillRegimen.getDosages()) {
            if (dosageResponse.getDosageId().equals(dosageId)) {
                return new Dose(dosageResponse, tamaIVRContext.callStartTime().toLocalDate());
            }
        }
        return null;
    }
}
