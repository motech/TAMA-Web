package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

import java.util.ArrayList;
import java.util.List;

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
        return  getDosesBetween(startOfTime, till);
    }

    public int getDosesBetween(LocalDate fromDate, DateTime toDate) {
        int count = 0;
        for (Dosage dosage : getDosages())
            count += dosage.getDosesBetween(fromDate, toDate);
        return count;
    }

    private List<Dosage> getDosages() {
        List<Dosage> dosages = new ArrayList<Dosage>();
        for (DosageResponse dosageResponse : pillRegimenResponse.getDosages())
            dosages.add(new Dosage(dosageResponse));
        return dosages;
    }
}