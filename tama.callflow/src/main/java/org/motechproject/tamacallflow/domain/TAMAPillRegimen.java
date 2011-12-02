package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.util.DateUtil;

import java.util.List;

public class TAMAPillRegimen {

    private PillRegimenResponse pillRegimenResponse;

    public TAMAPillRegimen(PillRegimenResponse pillRegimenResponse) {
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
}