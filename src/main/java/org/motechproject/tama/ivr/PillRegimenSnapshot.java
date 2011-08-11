package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.util.DateUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PillRegimenSnapshot {
    private IVRContext ivrContext;
    private PillRegimenResponse pillRegimen;

    public PillRegimenSnapshot(IVRContext ivrContext) {
        this.ivrContext = ivrContext;
        this.pillRegimen = ivrContext.ivrSession().getPillRegimen();
    }

    public List<String> medicinesForCurrentDosage() {
        return medicinesFor(getCurrentDosage());
    }

    public List<String> medicinesForPreviousDosage() {
        return medicinesFor(getPreviousDosage());
    }

    public boolean isPreviousDosageCaptured() {
        return isVeryFirstDosageCall() || wasPreviousDosageCapturedYesterday();
    }

    private boolean isVeryFirstDosageCall() {
        DosageResponse currentDosage = getCurrentDosage();
        boolean noResponseCapturedForCurrentDosage = currentDosage.getResponseLastCapturedDate() == null;
        boolean dosageStartedToday = currentDosage.getStartDate().equals(DateUtil.today());
        boolean firstDosageInTheDay = getSortedDosages().get(0).equals(currentDosage);
        return noResponseCapturedForCurrentDosage && dosageStartedToday && firstDosageInTheDay;
    }

    private boolean wasPreviousDosageCapturedYesterday() {
        DosageResponse previousDosage = getPreviousDosage();
        if (previousDosage.getResponseLastCapturedDate() == null) return false;
        return !DateUtil.today().minusDays(1).isAfter(previousDosage.getResponseLastCapturedDate());
    }

    public DosageResponse getPreviousDosage() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        int currentDosageIndex = allDosages.indexOf(getCurrentDosage());
        return currentDosageIndex == 0 ? allDosages.get(allDosages.size() - 1) : allDosages.get(currentDosageIndex - 1);
    }

    public DosageResponse getNextDosage() {
        List<DosageResponse> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        int currentDosageIndex = allDosages.indexOf(getCurrentDosage());
        return currentDosageIndex == allDosages.size() - 1 ? allDosages.get(0) : allDosages.get(currentDosageIndex + 1);
    }

    public DateTime getPreviousDosageTime() {
        DosageResponse previousDosage = getPreviousDosage();
        if (previousDosage == null) return null;
        if (DateUtil.now().getHourOfDay() - pillRegimen.getReminderRepeatWindowInHours() < previousDosage.getDosageHour())
            return new Time(previousDosage.getDosageHour(), previousDosage.getDosageMinute()).getDateTime(DateUtil.now().minusDays(1));
        else
            return new Time(previousDosage.getDosageHour(), previousDosage.getDosageMinute()).getDateTime(DateUtil.now());
    }

    public DateTime getNextDosageTime() {
        DosageResponse nextDosage = getNextDosage();
        if (nextDosage == null) return null;
        if (DateUtil.now().getHourOfDay() + pillRegimen.getReminderRepeatWindowInHours() > nextDosage.getDosageHour())
            return new Time(nextDosage.getDosageHour(), nextDosage.getDosageMinute()).getDateTime(DateUtil.now().plusDays(1));
        else
            return new Time(nextDosage.getDosageHour(), nextDosage.getDosageMinute()).getDateTime(DateUtil.now());
    }

    private List<DosageResponse> getSortedDosages() {
        if (CollectionUtils.isEmpty(pillRegimen.getDosages())) return null;
        List<DosageResponse> sortedDosages = new ArrayList<DosageResponse>(pillRegimen.getDosages());
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

    private DosageResponse getCurrentDosage() {
        return getDosage((String) ivrContext.ivrRequest().getTamaParams().get(PillReminderCall.DOSAGE_ID));
    }

    private DosageResponse getDosage(String dosageId) {
        if (pillRegimen == null) return null;
        for (DosageResponse dosageResponse : pillRegimen.getDosages()) {
            if (dosageResponse.getDosageId().equals(dosageId)) return dosageResponse;
        }
        return null;
    }

    public int getScheduledDosagesTotalCount() {
        int totalCount = 0;
        for (DosageResponse dosageResponse : pillRegimen.getDosages()) {
            DateTime toDate = DateUtil.now();
            DateTime fromDate = DateUtil.newDateTime(dosageResponse.getStartDate(), dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);
            if (toDate.isBefore(fromDate)) continue;

            Days days = Days.daysBetween(fromDate, toDate);
            int dayCount = days.getDays() + 1;
            totalCount += Math.min(dayCount, TAMAConstants.DAYS_IN_FOUR_WEEKS);
        }
        return totalCount;
    }
}
