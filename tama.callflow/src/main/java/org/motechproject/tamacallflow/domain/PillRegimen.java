package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.Dosage;
import org.motechproject.util.DateUtil;

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

    public int getDosageCount(DateTime from, DateTime till) {
        int count = 0;
        DosageTimeLine dosageTimeLine = new DosageTimeLine(pillRegimenResponse.getDosages(), from, till);
        while(dosageTimeLine.hasNext()){
            count++;
            dosageTimeLine.next();
        }
        return count;
    }

    public int getDosageCount(DateTime now) {
        DateTime startOfTime = new DateTime(0);
        return getDosageCount(startOfTime, now);
    }
}