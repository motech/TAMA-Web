package org.motechproject.tama.web.model;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.patient.domain.ReportedOpportunisticInfections;
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

    public boolean getHasInfectionsReported() {
        for(OIStatus oiStatus: infections) {
            if(oiStatus.getReported()) return true;
        }
        return false;
    }

    private void populateDefaultInfectionList(List<OpportunisticInfection> opportunisticInfectionList) {
        for(OpportunisticInfection opportunisticInfection: opportunisticInfectionList) {
            OIStatus oiStatus = new OIStatus();
            oiStatus.setOpportunisticInfection(opportunisticInfection.getName());
            oiStatus.setReported(false);
            infections.add(oiStatus);
        }
    }
    
    private void populateInfectionList(ReportedOpportunisticInfections reportedOpportunisticInfections, List<OpportunisticInfection> opportunisticInfectionList) {
        for(OpportunisticInfection opportunisticInfection: opportunisticInfectionList) {
            OIStatus oiStatus = new OIStatus();
            oiStatus.setOpportunisticInfection(opportunisticInfection.getName());
            oiStatus.setReported(infectionIsReported(opportunisticInfection, reportedOpportunisticInfections));
            infections.add(oiStatus);
        }
    }

    private boolean infectionIsReported(OpportunisticInfection opportunisticInfection, ReportedOpportunisticInfections reportedOpportunisticInfections) {
        return reportedOpportunisticInfections.getOpportunisticInfectionIds().contains(opportunisticInfection.getId());
    }
    
    public static OpportunisticInfectionsUIModel newDefault(ClinicVisit clinicVisit, List<OpportunisticInfection> opportunisticInfectionList) {
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
        opportunisticInfectionsUIModel.setClinicVisitId(clinicVisit.getId());
        opportunisticInfectionsUIModel.setPatientId(clinicVisit.getPatientId());
        opportunisticInfectionsUIModel.populateDefaultInfectionList(opportunisticInfectionList);
        return opportunisticInfectionsUIModel;
    }

    public static OpportunisticInfectionsUIModel create(ClinicVisit clinicVisit, ReportedOpportunisticInfections reportedOpportunisticInfections, List<OpportunisticInfection> opportunisticInfectionList) {
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
        opportunisticInfectionsUIModel.setClinicVisitId(clinicVisit.getId());
        opportunisticInfectionsUIModel.setPatientId(clinicVisit.getPatientId());
        opportunisticInfectionsUIModel.populateInfectionList(reportedOpportunisticInfections, opportunisticInfectionList);
        opportunisticInfectionsUIModel.setOtherDetails(reportedOpportunisticInfections.getOtherOpportunisticInfectionDetails());
        return opportunisticInfectionsUIModel;
    }

}
