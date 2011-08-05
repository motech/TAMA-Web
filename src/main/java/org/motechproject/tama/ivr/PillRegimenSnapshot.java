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
        DosageResponse previousDosage = getPreviousDosage();
        return isFirstDosage(previousDosage) || wasPreviousDosageCapturedYesterday(previousDosage);
    }

    private boolean isFirstDosage(DosageResponse previousDosage) {
        return previousDosage == null || previousDosage.getLastTakenDate() == null;
    }

    private boolean wasPreviousDosageCapturedYesterday(DosageResponse previousDosage) {
        return !DateUtil.today().minusDays(1).isAfter(previousDosage.getLastTakenDate());
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

    public DateTime getNextDosageTime() {
        DosageResponse nextDosage = getNextDosage();
        return nextDosage == null ? null : new Time(nextDosage.getDosageHour(), nextDosage.getDosageMinute()).getDateTime(DateUtil.now());
    }

    private List<DosageResponse> getSortedDosages() {
        if (CollectionUtils.isEmpty(pillRegimen.getDosages())) return null;
        List<DosageResponse> sortedDosages = new ArrayList<DosageResponse>(pillRegimen.getDosages());
        Collections.sort(sortedDosages, new Comparator<DosageResponse>() {
            @Override
            public int compare(DosageResponse d1, DosageResponse d2) {
                return d1.getDosageHour() - d2.getDosageHour();
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

    public int getScheduledDosagesTotalCount(LocalDate toDate) {
        int totalCount = 0;
        for (DosageResponse dosageResponse : pillRegimen.getDosages()) {
            Days days = Days.daysBetween(dosageResponse.getStartDate(), toDate);
            int dayCount = days.getDays() + 1;
            totalCount += Math.min(dayCount, TAMAConstants.DAYS_IN_FOUR_WEEKS);
        }
        return totalCount;
    }
}
