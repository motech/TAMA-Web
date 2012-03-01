package org.motechproject.tama.web.model;

import org.motechproject.tama.refdata.domain.OpportunisticInfection;

import java.util.ArrayList;
import java.util.List;

public class OpportunisticInfectionsUIModel {

    private String clinicVisitId;

    private String patientId;

    private String otherDetails;

    private List<OIStatus> infections = new ArrayList<OIStatus>();

    public OpportunisticInfectionsUIModel() {
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getClinicVisitId() {
        return clinicVisitId;
    }

    public void setClinicVisitId(String clinicVisitId) {
        this.clinicVisitId = clinicVisitId;
    }

    public String getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(String otherDetails) {
        this.otherDetails = otherDetails;
    }

    public List<OIStatus> getInfections() {
        return infections;
    }

    public void setInfections(List<OIStatus> opportunisticInfectionUIModels) {
        this.infections = opportunisticInfectionUIModels;
    }

    public void addNewInfection(OpportunisticInfection opportunisticInfection) {
        OIStatus oiStatus = new OIStatus();
        oiStatus.setOpportunisticInfection(opportunisticInfection.getName());
        oiStatus.setReported(false);
        infections.add(oiStatus);
    }

    public boolean infectionsReported() {
        for(OIStatus oiStatus: infections) {
            if(oiStatus.getReported()) return true;
        }
        return false;
    }

}
