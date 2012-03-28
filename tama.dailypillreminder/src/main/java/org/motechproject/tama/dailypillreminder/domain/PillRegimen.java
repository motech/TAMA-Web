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

    public Dose getPreviousDoseAt(DateTime specifiedDateTime) {
        HashMap<DosageType, Dose> proximateDosesAt = getProximateDosesAt(specifiedDateTime);
        return proximateDosesAt.isEmpty() ? null : proximateDosesAt.get(DosageType.Previous);
    }

    public Dose getDoseAt(DateTime specifiedDateTime) {
        HashMap<DosageType, Dose> proximateDosesAt = getProximateDosesAt(specifiedDateTime);
        return proximateDosesAt.isEmpty() ? null : proximateDosesAt.get(DosageType.Current);
    }

    public boolean isWithinPillWindow(DateTime specifiedDateTime) {
        Dose currentDose = getDoseAt(specifiedDateTime);
        return currentDose.isOnTime(specifiedDateTime, getReminderRepeatWindowInHours() * 60);
    }

    public Dose getNextDoseAt(DateTime specifiedDateTime) {
        HashMap<DosageType, Dose> proximateDosesAt = getProximateDosesAt(specifiedDateTime);
        return proximateDosesAt.isEmpty() ? veryFirstDose() : proximateDosesAt.get(DosageType.Next);
    }

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
            DateTime startDate = DateUtil.newDateTime(dosageResponse.getStartDate());
            if (DateUtil.isOnOrBefore(startDate, DateUtil.newDateTime(givenDate.minusDays(2))))
                allProbableDoses.add(getDoseOn(dosageResponse, givenDate.minusDays(2)));
            if (DateUtil.isOnOrBefore(startDate, DateUtil.newDateTime(givenDate.minusDays(1))))
                allProbableDoses.add(getDoseOn(dosageResponse, givenDate.minusDays(1)));
            if (DateUtil.isOnOrBefore(startDate, DateUtil.newDateTime(givenDate)))
                allProbableDoses.add(getDoseOn(dosageResponse, givenDate));
            if (DateUtil.isOnOrBefore(startDate, DateUtil.newDateTime(givenDate.plusDays(1))))
                allProbableDoses.add(getDoseOn(dosageResponse, givenDate.plusDays(1)));
            if (DateUtil.isOnOrBefore(startDate, DateUtil.newDateTime(givenDate.plusDays(2))))
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