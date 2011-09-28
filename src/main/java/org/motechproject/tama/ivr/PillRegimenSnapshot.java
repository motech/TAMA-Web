package org.motechproject.tama.ivr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRRequest.CallDirection;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.util.DosageUtil;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.util.DateUtil;
import org.springframework.util.CollectionUtils;

public class PillRegimenSnapshot {
    private IVRContext ivrContext;
    private PillRegimenResponse pillRegimen;
    private final DateTime now;
    private LocalDate today;

    public PillRegimenSnapshot(IVRContext ivrContext, DateTime now) {
        this.ivrContext = ivrContext;
        this.pillRegimen = TamaSessionUtil.getPillRegimen(ivrContext);
        this.now = now;
        this.today = new LocalDate(now);
    }

    public PillRegimenSnapshot(IVRContext ivrContext) {
        this(ivrContext, ivrContext.ivrSession().getCallTime());
    }

    public MyDosageResponse getPreviousDosage() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        int currentDosageIndex = allDosages.indexOf(getCurrentDosage().getDosage());
        return currentDosageIndex == 0 ? getLastDosage(allDosages) : new MyDosageResponse(allDosages.get(currentDosageIndex - 1), today);
    }

    public boolean isPreviousDosageCaptured() {
        return isVeryFirstDosageCall() || wasPreviousDosageCapturedYesterday();
    }

    public DateTime getPreviousDosageTime() {
        MyDosageResponse previousDosage = getPreviousDosage();
        if (previousDosage == null) return null;
        if (isTodaysDosage(previousDosage))
            return new Time(previousDosage.getDosageHour(), previousDosage.getDosageMinute()).getDateTime(now);
        else
            return new Time(previousDosage.getDosageHour(), previousDosage.getDosageMinute()).getDateTime(now.minusDays(1));
    }

    public MyDosageResponse getNextDosage() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        MyDosageResponse currentDosage = getCurrentDosage();
        int currentDosageIndex = allDosages.indexOf(currentDosage.getDosage());
        return currentDosageIndex == allDosages.size() - 1 ? new MyDosageResponse(allDosages.get(0), currentDosage.getDosageDate().plusDays(1)) :
                new MyDosageResponse(allDosages.get(currentDosageIndex + 1), currentDosage.getDosageDate());
    }

    public DateTime getNextDosageTime() {
        MyDosageResponse nextDosage = getNextDosage();
        if (nextDosage == null) return null;
        if (isTodaysDosage(nextDosage))
            return new Time(nextDosage.getDosageHour(), nextDosage.getDosageMinute()).getDateTime(now);
        else
            return new Time(nextDosage.getDosageHour(), nextDosage.getDosageMinute()).getDateTime(now.plusDays(1));

    }

    public MyDosageResponse getCurrentDosage() {
        IVRRequest ivrRequest = ivrContext.ivrRequest();
        if (ivrRequest.getCallDirection() == CallDirection.Inbound) {
            List<DosageResponse> dosageResponses = getSortedDosages();
            DosageResponse currentDosage = null;
            for (DosageResponse dosageResponse : dosageResponses) {
                if (isCandidate(dosageResponse))
                    currentDosage = dosageResponse;
            }
            return getCurrentDosageWithDosageDate(dosageResponses, currentDosage);
        } else {
            return getDosage(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID));
        }
    }

    private MyDosageResponse getCurrentDosageWithDosageDate(List<DosageResponse> dosageResponses, DosageResponse currentDosage) {
        if (currentDosage == null) {
            return getLastDosage(dosageResponses);
        }

        int pillWindowStartHour = now.withHourOfDay(currentDosage.getDosageHour()).minusHours(pillRegimen.getReminderRepeatWindowInHours()).getHourOfDay();
        boolean isTomorrowsDosage = pillWindowStartHour > currentDosage.getDosageHour();
        if (isTomorrowsDosage) {
            return new MyDosageResponse(currentDosage, today.plusDays(1));
        }

        return new MyDosageResponse(currentDosage, today);
    }
    
    public int getScheduledDosagesTotalCountForLastFourWeeks() {
    	return getScheduledDosagesTotalCountForLastFourWeeks(now);
    }

    public int getScheduledDosagesTotalCountForLastFourWeeks(DateTime endDate) {
    	DateTime startTime = endDate.minusWeeks(4);
    	return DosageUtil.getScheduledDosagesTotalCount(startTime, endDate, pillRegimen);
    }

    public boolean isCurrentDosageTaken() {
        MyDosageResponse currentDosage = getCurrentDosage();
        boolean currentDosageIsUndefined = currentDosage.getDosageDate().isBefore(currentDosage.getStartDate());
        return currentDosageIsUndefined || (currentDosage.getResponseLastCapturedDate() != null && currentDosage.getResponseLastCapturedDate().equals(today));
    }

    public boolean isTodaysDosage(MyDosageResponse dosage) {
        return today.equals(dosage.getDosageDate());
    }


    public boolean isTimeToTakeCurrentPill() {
        int pillWindowInMinutes = pillRegimen.getReminderRepeatWindowInHours() * 60;

        return nowIsWithin(pillWindowInMinutes);
    }

    public boolean isEarlyToTakeDosage(int dosageIntervalInMinutes) {
        MyDosageResponse currentDosage = getCurrentDosage();
        DateTime dosageTime = DateUtil.newDateTime(currentDosage.getDosageDate(), currentDosage.getDosageHour(), currentDosage.getDosageMinute(), 0);
        DateTime dosageWindowStart = dosageTime.minusMinutes(dosageIntervalInMinutes);
        DateTime pillWindowStart = dosageTime.minusHours(pillRegimen.getReminderRepeatWindowInHours());
        return now.isAfter(pillWindowStart) && now.isBefore(dosageWindowStart);
    }

    public boolean isLateToTakeDosage() {
        MyDosageResponse currentDosage = getCurrentDosage();
        DateTime dosageTime = DateUtil.newDateTime(currentDosage.getDosageDate(), currentDosage.getDosageHour(), currentDosage.getDosageMinute(), 0);
        DateTime pillWindowEnd = dosageTime.plusHours(pillRegimen.getReminderRepeatWindowInHours());
        return now.isAfter(pillWindowEnd);
    }

    public boolean hasTakenDosageOnTime(int dosageIntervalInMinutes) {
        return nowIsWithin(dosageIntervalInMinutes);
    }

    private boolean nowIsWithin(int dosageIntervalInMinutes) {
        MyDosageResponse dosage = getCurrentDosage();
        int dosageHour = dosage.getDosageHour();
        int dosageMinute = dosage.getDosageMinute();

        DateTime dosageTime = now.withHourOfDay(dosageHour).withMinuteOfHour(dosageMinute);

        boolean nowAfterPillWindowStart = now.isAfter(dosageTime.minusMinutes(dosageIntervalInMinutes));
        boolean nowBeforePillWindowEnd = now.isBefore(dosageTime.plusMinutes(dosageIntervalInMinutes));

        return nowAfterPillWindowStart && nowBeforePillWindowEnd;
    }

    public List<String> medicinesForCurrentDosage() {
        return medicinesFor(getCurrentDosage());
    }

    public List<String> medicinesForPreviousDosage() {
        return medicinesFor(getPreviousDosage());
    }

    private boolean isVeryFirstDosageCall() {
        MyDosageResponse currentDosage = getCurrentDosage();
        boolean noResponseCapturedForCurrentDosage = currentDosage.getResponseLastCapturedDate() == null;
        boolean dosageStartedToday = currentDosage.getStartDate().equals(today);
        boolean firstDosageInTheDay = getSortedDosages().get(0).equals(currentDosage.getDosage());
        return noResponseCapturedForCurrentDosage && dosageStartedToday && firstDosageInTheDay;
    }

    private boolean wasPreviousDosageCapturedYesterday() {
        DosageResponse previousDosage = getPreviousDosage();
        if (previousDosage.getResponseLastCapturedDate() == null) return false;
        return !dosageWasCapturedSomeDayBeforeYesterday(previousDosage);
    }

    private boolean dosageWasCapturedSomeDayBeforeYesterday(DosageResponse previousDosage) {
        return today.minusDays(1).isAfter(previousDosage.getResponseLastCapturedDate());
    }

    private List<DosageResponse> getSortedDosages() {
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
        return sortedDosages;
    }

    private List<String> medicinesFor(DosageResponse dosage) {
        if (dosage == null) return new ArrayList<String>();
        List<String> medicines = new ArrayList<String>();
        for (MedicineResponse medicine : dosage.getMedicines())
            medicines.add(String.format("pill%s", medicine.getName()));
        return medicines;
    }

    private boolean isCandidate(DosageResponse dosageResponse) {
        int hourToCaptureDosage = now.withHourOfDay(dosageResponse.getDosageHour()).minusHours(pillRegimen.getReminderRepeatWindowInHours()).getHourOfDay();
        int minuteToCaptureDosage = dosageResponse.getDosageMinute();

        return now.isAfter(now.withHourOfDay(hourToCaptureDosage).withMinuteOfHour(minuteToCaptureDosage));
    }

    private MyDosageResponse getLastDosage(List<DosageResponse> dosageResponses) {
        return new MyDosageResponse(dosageResponses.get(dosageResponses.size() - 1), today.minusDays(1));
    }

    private MyDosageResponse getDosage(String dosageId) {
        if (pillRegimen == null) return null;
        for (DosageResponse dosageResponse : pillRegimen.getDosages()) {
            if (dosageResponse.getDosageId().equals(dosageId)) return new MyDosageResponse(dosageResponse, today);
        }
        return null;
    }

}
