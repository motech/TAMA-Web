package org.motechproject.tama.dailypillreminder.domain;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.filter;

public class PillRegimen {

    private PillRegimenResponse pillRegimenResponse;

    public PillRegimen(PillRegimenResponse pillRegimenResponse) {
        this.pillRegimenResponse = pillRegimenResponse;
    }

    public String getId() {
        return pillRegimenResponse.getPillRegimenId();
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

    public Dose getDoseAt(DateTime specifiedDateTime) {
        List<DosageResponse> dosageResponses = getDosageResponses();
        List<Dose> allProbableDoses = new ArrayList<Dose>();
        for (DosageResponse dosageResponse : dosageResponses) {
            LocalDate givenDate = specifiedDateTime.toLocalDate();
            allProbableDoses.add(getDoseOn(givenDate, dosageResponse));
            allProbableDoses.add(getDoseOn(givenDate.minusDays(1), dosageResponse));
            allProbableDoses.add(getDoseOn(givenDate.plusDays(1), dosageResponse));
        }

        allProbableDoses = filterOnDosageStartDate(specifiedDateTime, allProbableDoses);
        allProbableDoses = sortDoses(allProbableDoses);

        Dose matchingDose = null;
        for (Dose dose : allProbableDoses) {
            if (specifiedDateTime.isAfter(pillWindowStartTime(dose))) {
                matchingDose = dose;
            }
        }
        return matchingDose;
    }

    private DateTime pillWindowStartTime(Dose dose) {
        return dose.getDoseTime().minusHours(pillRegimenResponse.getReminderRepeatWindowInHours());
    }

    private List<Dose> filterOnDosageStartDate(final DateTime givenDateTime, List<Dose> doses) {
        return filter(new TypeSafeMatcher<Dose>() {
            @Override
            public boolean matchesSafely(Dose dose) {
                Dose firstDose = new Dose(dose.getDosage(), dose.getDosage().getStartDate());
                return !pillWindowStartTime(firstDose).isAfter(givenDateTime);
            }

            @Override
            public void describeTo(Description description) {
            }
        }, doses);
    }

    private List<Dose> sortDoses(List<Dose> doses) {
        Collections.sort(doses, new Comparator<Dose>() {
            @Override
            public int compare(Dose d1, Dose d2) {
                return d1.getDoseTime().compareTo(d2.getDoseTime());
            }
        });
        return doses;
    }

    private Dose getDoseOn(LocalDate givenDate, DosageResponse candidateDosageResponse) {
        return new Dose(candidateDosageResponse, givenDate);
    }

    public Dose firstDose() {
        int marker = 0;
        List<DosageResponse> dosageResponses = getDosageResponses();
        DateTime earliestDoseTime = new Dosage(dosageResponses.get(0)).firstDose();
        for (DosageResponse dosageResponse : dosageResponses) {
            DateTime firstDoseDateTime = new Dosage(dosageResponse).firstDose();
            if (firstDoseDateTime.isBefore(earliestDoseTime)) {
                earliestDoseTime = firstDoseDateTime;
                marker = dosageResponses.indexOf(dosageResponse);
            }
        }
        DosageResponse firstDosageResponse = dosageResponses.get(marker);
        return new Dose(firstDosageResponse, firstDosageResponse.getStartDate());
    }
}