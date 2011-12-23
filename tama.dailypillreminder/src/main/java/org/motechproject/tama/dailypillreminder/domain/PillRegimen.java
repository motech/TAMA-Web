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

    public Dose getDoseAt(DateTime givenDateTime) {
        List<DosageResponse> dosageResponses = getDosageResponses();
        List<Dose> allProbableDoses = new ArrayList<Dose>();
        for (DosageResponse dosageResponse : dosageResponses) {
            LocalDate givenDate = givenDateTime.toLocalDate();
            allProbableDoses.add(getDoseOn(givenDate, dosageResponse));
            allProbableDoses.add(getDoseOn(givenDate.minusDays(1), dosageResponse));
            allProbableDoses.add(getDoseOn(givenDate.plusDays(1), dosageResponse));
        }

        allProbableDoses = removeDosesWhichStartAfter(givenDateTime, allProbableDoses);
        allProbableDoses = sortDoses(allProbableDoses);

        Dose matchingDose = null;
        for (Dose dose : allProbableDoses) {
            if (givenDateTime.isAfter(applicableStartTime(dose))) {
                matchingDose = dose;
            }
        }
        return matchingDose;
    }

    private DateTime applicableStartTime(Dose dose) {
        return dose.getDoseTime().minusHours(pillRegimenResponse.getReminderRepeatWindowInHours());
    }

    private List<Dose> removeDosesWhichStartAfter(final DateTime givenDateTime, List<Dose> doses) {
        return filter(new TypeSafeMatcher<Dose>() {
            @Override
            public boolean matchesSafely(Dose dose) {
                final Dose doseOnDoseStartDate = new Dose(dose.getDosage(), dose.getDosage().getStartDate());
                DateTime doseStartDateTime = applicableStartTime(doseOnDoseStartDate);
                return !doseStartDateTime.isAfter(givenDateTime);
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
}