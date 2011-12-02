package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.util.DateUtil;

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

    public DosageTimeLine getDosageTimeLine(){
        return new DosageTimeLine(pillRegimenResponse.getDosages(), DateUtil.now());
    }

    public DosageTimeLine getDosageTimeLine(DateTime from, DateTime to){
        return new DosageTimeLine(pillRegimenResponse.getDosages(), from, to);
    }

    public List<DosageResponse> getDosageResponses(){
        return pillRegimenResponse.getDosages();
    }

    public int getNumberOfDosagesBetween(DateTime from, DateTime till) {
        int count = 0;
        for (Dosage dosage : getDosages())
            count += dosage.getNumberOfDosagesBetween(from, till);
        return count;
    }

    private List<Dosage> getDosages() {
        List<Dosage> dosages = new ArrayList<Dosage>();
        for (DosageResponse dosageResponse : pillRegimenResponse.getDosages())
            dosages.add(new Dosage(dosageResponse));
        return dosages;
    }

    public int getNumberOfDosagesAsOf(DateTime till) {
        DateTime startOfTime = new DateTime(0);
        return getNumberOfDosagesBetween(startOfTime, till);
    }
}