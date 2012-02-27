package org.motechproject.tama.web.model;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.patient.domain.OpportunisticInfections;

public class OpportunisticInfectionsUIModel {

    private String clinicVisitId;

    private OpportunisticInfections opportunisticInfections = new OpportunisticInfections();

    public OpportunisticInfectionsUIModel() {
    }

    public OpportunisticInfectionsUIModel(String patientId) {
        opportunisticInfections.setPatientId(patientId);
    }

    public String getClinicVisitId() {
        return clinicVisitId;
    }

    public void setClinicVisitId(String clinicVisitId) {
        this.clinicVisitId = clinicVisitId;
    }

    public String getId() {
        return opportunisticInfections.getId();
    }

    public String getPatientId() {
        return opportunisticInfections.getPatientId();
    }

    public void setId(String id) {
        if (StringUtils.isNotEmpty(id)) {
            opportunisticInfections.setId(id);
        }
    }

    public static OpportunisticInfectionsUIModel newDefault(ClinicVisit clinicVisit) {
        final OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
        opportunisticInfectionsUIModel.setClinicVisitId(clinicVisit.getId());
        final OpportunisticInfections opportunisticInfections = new OpportunisticInfections();
        opportunisticInfections.setPatientId(clinicVisit.getPatientId());
        opportunisticInfectionsUIModel.setOpportunisticInfections(opportunisticInfections);
        return opportunisticInfectionsUIModel;
    }

    public static OpportunisticInfectionsUIModel get(ClinicVisit clinicVisit, OpportunisticInfections opportunisticInfections) {
        final OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
        opportunisticInfectionsUIModel.setClinicVisitId(clinicVisit.getId());
        opportunisticInfectionsUIModel.setOpportunisticInfections(opportunisticInfections);
        return opportunisticInfectionsUIModel;
    }

    public OpportunisticInfections getOpportunisticInfections() {
        return opportunisticInfections;
    }

    public void setOpportunisticInfections(OpportunisticInfections opportunisticInfections) {
        this.opportunisticInfections = opportunisticInfections;
    }
}
