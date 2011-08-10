package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PillRegimenSnapshot {
    private IVRContext ivrContext;
    private PillRegimenResponse pillRegimen;
    private final DateTime now;
    private LocalDate today;

    public PillRegimenSnapshot(IVRContext ivrContext, DateTime now) {
        this.ivrContext = ivrContext;
        this.pillRegimen = ivrContext.ivrSession().getPillRegimen();
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
        return !today.minusDays(1).isAfter(previousDosage.getResponseLastCapturedDate());
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
        int currentDosageIndex = allDosages.indexOf(getCurrentDosage().getDosage());
        return currentDosageIndex == allDosages.size() - 1 ? new MyDosageResponse(allDosages.get(0),today.plusDays(1)) :
                                                             new MyDosageResponse(allDosages.get(currentDosageIndex + 1), today);
    }

    public DateTime getNextDosageTime() {
        DosageResponse nextDosage = getNextDosage();
        if (nextDosage == null) return null;
        if (now.plusHours(pillRegimen.getReminderRepeatWindowInHours()).isAfter(now.withHourOfDay(nextDosage.getDosageHour())))
            return new Time(nextDosage.getDosageHour(), nextDosage.getDosageMinute()).getDateTime(now.plusDays(1));
        else
            return new Time(nextDosage.getDosageHour(), nextDosage.getDosageMinute()).getDateTime(now);
    }

    public MyDosageResponse getCurrentDosage() {
        if (ivrContext.ivrRequest().hasNoTamaData()) {
            List<DosageResponse> dosageResponses = getSortedDosages();
            DosageResponse currentDosage = null;
            for (DosageResponse dosageResponse : dosageResponses) {
                if (isCandidate(dosageResponse))
                    currentDosage = dosageResponse;
            }
            return currentDosage == null ? getLastDosage(dosageResponses) : new MyDosageResponse(currentDosage, today);
        } else {
            return getDosage((String) ivrContext.ivrRequest().getTamaParams().get(PillReminderCall.DOSAGE_ID));
        }
    }

    public int getScheduledDosagesTotalCount() {
        int totalCount = 0;
        for (DosageResponse dosageResponse : pillRegimen.getDosages()) {
            DateTime toDate = now;
            DateTime fromDate = DateUtil.newDateTime(dosageResponse.getStartDate(), dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);
            if (toDate.isBefore(fromDate)) continue;

            Days days = Days.daysBetween(fromDate, toDate);
            int dayCount = days.getDays() + 1;
            totalCount += Math.min(dayCount, TAMAConstants.DAYS_IN_FOUR_WEEKS);
        }
        return totalCount;
    }

    public boolean isCurrentDosageTaken() {
        return getCurrentDosage().getResponseLastCapturedDate().equals(today);
    }

    public boolean isTodaysDosage(MyDosageResponse dosage) {
        return today.equals(dosage.getDosageDate());
    }

    public List<String> medicinesForCurrentDosage() {
        return medicinesFor(getCurrentDosage());
    }

    public List<String> medicinesForPreviousDosage() {
        return medicinesFor(getPreviousDosage());
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
            medicines.add(medicine.getName());
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
