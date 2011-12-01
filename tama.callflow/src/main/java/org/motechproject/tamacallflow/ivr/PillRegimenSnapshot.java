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

    public DosageResponseWithDate getPreviousDosage() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        DosageResponseWithDate currentDosage = getCurrentDosage();
        int currentDosageIndex = allDosages.indexOf(currentDosage.getDosage());
        return currentDosageIndex == 0 ? getLastDosage(allDosages, currentDosage.getDosageDate()) : new DosageResponseWithDate(allDosages.get(currentDosageIndex - 1), currentDosage.getDosageDate());
    }

    public boolean isPreviousDosageCaptured() {
        return isVeryFirstDosageCall() || wasPreviousDosageCapturedYesterday();
    }

    public DateTime getPreviousDosageTime() {
        DosageResponseWithDate previousDosage = getPreviousDosage();
        if (previousDosage == null) return null;
        if (isTodaysDosage(previousDosage))
            return new Time(previousDosage.getDosageHour(), previousDosage.getDosageMinute()).getDateTime(tamaIVRContext.callStartTime());
        else
            return new Time(previousDosage.getDosageHour(), previousDosage.getDosageMinute()).getDateTime(tamaIVRContext.callStartTime().minusDays(1));
    }

    public DosageResponseWithDate getNextDosage() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        DosageResponseWithDate currentDosage = getCurrentDosage();
        int currentDosageIndex = allDosages.indexOf(currentDosage.getDosage());
        return currentDosageIndex == allDosages.size() - 1 ? new DosageResponseWithDate(allDosages.get(0), currentDosage.getDosageDate().plusDays(1)) :
                new DosageResponseWithDate(allDosages.get(currentDosageIndex + 1), currentDosage.getDosageDate());
    }

    public DateTime getNextDosageTime() {
        DosageResponseWithDate nextDosage = getNextDosage();
        if (nextDosage == null) return null;
        if (isTodaysDosage(nextDosage))
            return new Time(nextDosage.getDosageHour(), nextDosage.getDosageMinute()).getDateTime(tamaIVRContext.callStartTime());
        else
            return new Time(nextDosage.getDosageHour(), nextDosage.getDosageMinute()).getDateTime(tamaIVRContext.callStartTime().plusDays(1));
    }

    public DosageResponseWithDate getCurrentDosage() {
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

    private DosageResponseWithDate getCurrentDosageWithDosageDate(List<DosageResponse> dosageResponses, DosageResponse currentDosage) {
        LocalDate today = tamaIVRContext.callStartTime().toLocalDate();
        if (currentDosage == null) {
            return getLastDosage(dosageResponses, today);
        }

        int pillWindowStartHour = tamaIVRContext.callStartTime().withHourOfDay(currentDosage.getDosageHour()).minusHours(pillRegimen.getReminderRepeatWindowInHours()).getHourOfDay();
        boolean isTomorrowsDosage = pillWindowStartHour > currentDosage.getDosageHour();
        if (isTomorrowsDosage) {
            return new DosageResponseWithDate(currentDosage, today.plusDays(1));
        }

        return new DosageResponseWithDate(currentDosage, today);
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
        DosageResponseWithDate currentDosage = getCurrentDosage();
        boolean currentDosageIsUndefined = currentDosage.getDosageDate().isBefore(currentDosage.getStartDate());
        return currentDosageIsUndefined || (currentDosage.getResponseLastCapturedDate() != null && currentDosage.getResponseLastCapturedDate().equals(currentDosage.getDosageDate()));
    }

    public boolean isTodaysDosage(DosageResponseWithDate dosage) {
        return tamaIVRContext.callStartTime().toLocalDate().equals(dosage.getDosageDate());
    }

    public boolean isTimeToTakeCurrentPill() {
        int pillWindowInMinutes = pillRegimen.getReminderRepeatWindowInHours() * 60;

        return nowIsWithin(pillWindowInMinutes);
    }

    public boolean isEarlyToTakeDosage(int dosageIntervalInMinutes) {
        DosageResponseWithDate currentDosage = getCurrentDosage();
        DateTime dosageTime = DateUtil.newDateTime(currentDosage.getDosageDate(), currentDosage.getDosageHour(), currentDosage.getDosageMinute(), 0);
        DateTime dosageWindowStart = dosageTime.minusMinutes(dosageIntervalInMinutes);
        DateTime pillWindowStart = dosageTime.minusHours(pillRegimen.getReminderRepeatWindowInHours());
        return tamaIVRContext.callStartTime().isAfter(pillWindowStart) && tamaIVRContext.callStartTime().isBefore(dosageWindowStart);
    }

    public boolean isLateToTakeDosage() {
        DosageResponseWithDate currentDosage = getCurrentDosage();
        DateTime dosageTime = DateUtil.newDateTime(currentDosage.getDosageDate(), currentDosage.getDosageHour(), currentDosage.getDosageMinute(), 0);
        DateTime pillWindowEnd = dosageTime.plusHours(pillRegimen.getReminderRepeatWindowInHours());
        return tamaIVRContext.callStartTime().isAfter(pillWindowEnd);
    }

    public boolean hasTakenDosageOnTime(int dosageIntervalInMinutes) {
        return nowIsWithin(dosageIntervalInMinutes);
    }

    private boolean nowIsWithin(int dosageIntervalInMinutes) {
        DosageResponseWithDate dosage = getCurrentDosage();
        int dosageHour = dosage.getDosageHour();
        int dosageMinute = dosage.getDosageMinute();

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
        DosageResponseWithDate currentDosage = getCurrentDosage();
        boolean noResponseCapturedForCurrentDosage = currentDosage.getResponseLastCapturedDate() == null;
        boolean dosageStartedToday = currentDosage.getStartDate().equals(currentDosage.getDosageDate());
        boolean firstDosageInTheDay = getSortedDosages().get(0).equals(currentDosage.getDosage());
        return noResponseCapturedForCurrentDosage && dosageStartedToday && firstDosageInTheDay;
    }

    private boolean wasPreviousDosageCapturedYesterday() {
        DosageResponseWithDate previousDosage = getPreviousDosage();
        return previousDosage.getResponseLastCapturedDate() != null && !previousDosage.getResponseLastCapturedDate().isBefore(previousDosage.getDosageDate());
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

    private List<String> medicinesFor(DosageResponseWithDate dosage) {
        if (dosage == null) return new ArrayList<String>();
        List<String> medicines = new ArrayList<String>();

        for (MedicineResponse medicine : dosage.getMedicines()) {
            if (!dosage.getDosageDate().isBefore(medicine.getStartDate()) &&
                    (medicine.getEndDate() == null || !dosage.getDosageDate().isAfter(medicine.getEndDate())))
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

    private DosageResponseWithDate getLastDosage(List<DosageResponse> dosageResponses, LocalDate currentDoseDate) {
        return new DosageResponseWithDate(dosageResponses.get(dosageResponses.size() - 1), currentDoseDate.minusDays(1));
    }

    private DosageResponseWithDate getDosage(String dosageId) {
        if (pillRegimen == null) return null;
        for (DosageResponse dosageResponse : pillRegimen.getDosages()) {
            if (dosageResponse.getDosageId().equals(dosageId)) return new DosageResponseWithDate(dosageResponse, tamaIVRContext.callStartTime().toLocalDate());
        }
        return null;
    }

}
