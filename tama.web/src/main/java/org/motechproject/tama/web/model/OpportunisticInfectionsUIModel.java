package org.motechproject.tama.web.model;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.patient.domain.OpportunisticInfections;

import java.util.ArrayList;
import java.util.List;

public class OpportunisticInfectionsUIModel {

    public static class Summary {

        private List<String> infections;

        public Summary() {
            infections = new ArrayList<String>();
        }

        public void addAll(OpportunisticInfections opportunisticInfections) {
            infections = opportunisticInfections.getChosenInfections();
        }

        public List<String> getInfections() {
            return infections;
        }

        public boolean contains(String infection) {
            return infections.contains(infection);
        }
    }

    private String clinicVisitId;

    private OpportunisticInfections opportunisticInfections = new OpportunisticInfections();

    private Summary summary;

    public OpportunisticInfectionsUIModel() {
        summary = new Summary();
    }

    public OpportunisticInfectionsUIModel(String patientId) {
        this();
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

    public Summary getSummary() {
        summary.addAll(opportunisticInfections);
        return summary;
    }
}
