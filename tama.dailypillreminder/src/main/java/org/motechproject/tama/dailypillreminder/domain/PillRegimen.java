package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PillRegimen {

    private enum DosageType {Previous, Current, Next}

    private PillRegimenResponse pillRegimenResponse;

    public PillRegimen(PillRegimenResponse pillRegimenResponse) {
        this.pillRegimenResponse = pillRegimenResponse;
    }

    public String getId() {
        return pillRegimenResponse.getPillRegimenId();
    }

    public int getReminderRepeatWindowInHours() {
        return pillRegimenResponse.getReminderRepeatWindowInHours();
    }

    public List<DosageResponse> getDosageResponses() {
        return pillRegimenResponse.getDosages();
    }

    public int getNumberOfDosesAsOf(DateTime till) {
        final LocalDate startOfTime = new LocalDate(0);
        return getDosesBetween(startOfTime, till);
    }

    public int getDosesBetween(LocalDate fromDate, DateTime toDate) {
        int count = 0;
        for (DosageResponse dosageResponse : getDosageResponses()) {
            count += new Dosage(dosageResponse).getDosesBetween(fromDate, toDate);
        }
        return count;
    }
/* ---------------------- Previous Dose Operations Start ----------------------------- */
    public Dose getPreviousDoseAt(DateTime specifiedDateTime) {
        HashMap<DosageType, Dose> proximateDosesAt = getProximateDosesAt(specifiedDateTime);
        return proximateDosesAt.isEmpty() ? null : proximateDosesAt.get(DosageType.Previous);
    }

    public DateTime getPreviousDoseTime(DateTime specifiedDateTime) {
        Dose previousDose = getPreviousDoseAt(specifiedDateTime);
        return previousDose == null ? null : previousDose.getDoseTime();
    }

    public boolean isPreviousDosageTaken(DateTime specifiedDateTime) {
        Dose currentDose = getDoseAt(specifiedDateTime);
        Dose previousDose = getPreviousDoseAt(specifiedDateTime);
        boolean isVeryFirstDosageCall = currentDose.isVeryFirstDosageCall(veryFirstDose());
        boolean wasPreviousDosageCapturedYesterday = previousDose == null || previousDose.wasDosageResponseCapturedYesterday();
        return isVeryFirstDosageCall || wasPreviousDosageCapturedYesterday;
    }

    public List<String> medicinesForPreviousDose(DateTime specifiedDateTime) {
        Dose previousDose = getPreviousDoseAt(specifiedDateTime);
        return previousDose == null ? new ArrayList<String>() : previousDose.medicineNames();
    }
/* ---------------------- Previous Dose Operations End ----------------------------- */

/* ---------------------- Current Dose Operations Start ----------------------------- */
    public Dose getDoseAt(DateTime specifiedDateTime) {
        HashMap<DosageType, Dose> proximateDosesAt = getProximateDosesAt(specifiedDateTime);
        return proximateDosesAt.isEmpty() ? null : proximateDosesAt.get(DosageType.Current);
    }

    public boolean isNowWithinCurrentDosePillWindow(DateTime specifiedDateTime) {
        return isNowWithinCurrentDosageInterval(specifiedDateTime, getReminderRepeatWindowInHours() * 60);
    }

    public boolean isNowWithinCurrentDosageInterval(DateTime specifiedDateTime, int dosageIntervalInMinutes) {
        Dose currentDose = getDoseAt(specifiedDateTime);
        if (currentDose == null) return false;
        return currentDose.isWithinSpecifiedInterval(specifiedDateTime, dosageIntervalInMinutes);
    }

    public boolean isCurrentDoseTaken(DateTime specifiedDateTime) {
        Dose currentDose = getDoseAt(specifiedDateTime);
        return currentDose == null || currentDose.wasDosageResponseCaptured();
    }

    public boolean isEarlyToTakeDose(DateTime specifiedDateTime, int dosageIntervalInMinutes) {
        Dose currentDose = getDoseAt(specifiedDateTime);
        if (currentDose == null) return true;
        return currentDose.isEarlyToTake(specifiedDateTime, getReminderRepeatWindowInHours(), dosageIntervalInMinutes);
    }

    public boolean isLateToTakeDose(DateTime specifiedDateTime, int dosageIntervalInMinutes) {
        Dose currentDose = getDoseAt(specifiedDateTime);
        return currentDose.isLateToTake(specifiedDateTime, dosageIntervalInMinutes);
    }

    public List<String> medicinesForCurrentDose(DateTime specifiedDateTime) {
        Dose currentDose = getDoseAt(specifiedDateTime);
        return currentDose == null ? new ArrayList<String>() : currentDose.medicineNames();
    }
/* ---------------------- Current Dose Operations End ----------------------------- */

/* ---------------------- Next Dose Operations Start ----------------------------- */
    public Dose getNextDoseAt(DateTime specifiedDateTime) {
        HashMap<DosageType, Dose> proximateDosesAt = getProximateDosesAt(specifiedDateTime);
        return proximateDosesAt.isEmpty() ? veryFirstDose() : proximateDosesAt.get(DosageType.Next);
    }

    public DateTime getNextDoseTime(DateTime specifiedDateTime) {
        Dose nextDose = getNextDoseAt(specifiedDateTime);
        return nextDose == null ? null : nextDose.getDoseTime();
    }
/* ---------------------- Next Dose Operations End ----------------------------- */

    Dose veryFirstDose() {
        List<DosageResponse> dosageResponses = getDosageResponses();
        DosageResponse earliestDose = dosageResponses.get(0);
        for (DosageResponse dosageResponse : dosageResponses) {
            DateTime firstDoseDateTime = new Dosage(dosageResponse).firstDose();
            if (firstDoseDateTime.isBefore(new Dosage(earliestDose).firstDose())) {
                earliestDose = dosageResponse;
            }
        }
        return getDoseOn(earliestDose, earliestDose.getStartDate());
    }

    private HashMap<DosageType, Dose> getProximateDosesAt(DateTime specifiedDateTime) {
        List<Dose> allProbableDoses = getAllProbableDoses(specifiedDateTime);
        HashMap<DosageType, Dose> proximateDosages = new HashMap<DosageType, Dose>();
        for (int i = 0; i < allProbableDoses.size(); i++) {
            if (specifiedDateTime.isAfter(pillWindowStartTime(allProbableDoses.get(i)))) {
                if (i - 1 >= 0) {
                    proximateDosages.put(DosageType.Previous, allProbableDoses.get(i - 1));
                }
                proximateDosages.put(DosageType.Current, allProbableDoses.get(i));
                if (i + 1 < allProbableDoses.size()) {
                    proximateDosages.put(DosageType.Next, allProbableDoses.get(i + 1));
                }
            }
        }
        return proximateDosages;
    }

    private List<Dose> getAllProbableDoses(DateTime specifiedDateTime) {
        List<Dose> allProbableDoses = new ArrayList<Dose>();
        LocalDate givenDate = specifiedDateTime.toLocalDate();
        for (DosageResponse dosageResponse : getDosageResponses()) {
            if (DateUtil.isOnOrBefore(dosageResponse.getStartDate(), givenDate.minusDays(2)))
                allProbableDoses.add(getDoseOn(dosageResponse, givenDate.minusDays(2)));
            if (DateUtil.isOnOrBefore(dosageResponse.getStartDate(), givenDate.minusDays(1)))
                allProbableDoses.add(getDoseOn(dosageResponse, givenDate.minusDays(1)));
            if (DateUtil.isOnOrBefore(dosageResponse.getStartDate(), givenDate))
                allProbableDoses.add(getDoseOn(dosageResponse, givenDate));
            if (DateUtil.isOnOrBefore(dosageResponse.getStartDate(), givenDate.plusDays(1)))
                allProbableDoses.add(getDoseOn(dosageResponse, givenDate.plusDays(1)));
            if (DateUtil.isOnOrBefore(dosageResponse.getStartDate(), givenDate.plusDays(2)))
                allProbableDoses.add(getDoseOn(dosageResponse, givenDate.plusDays(2)));
        }
        Collections.sort(allProbableDoses);
        return allProbableDoses;
    }

    private DateTime pillWindowStartTime(Dose dose) {
        return dose.getDoseTime().minusHours(getReminderRepeatWindowInHours());
    }

    private Dose getDoseOn(DosageResponse candidateDosageResponse, LocalDate givenDate) {
        return new Dose(candidateDosageResponse, givenDate);
    }
}