package org.motechproject.tama.web.model;

import org.motechproject.tama.patient.domain.ClinicVisit;
import org.motechproject.tama.patient.domain.VitalStatistics;

public class VitalStatisticsUIModel {

    private String clinicVisitId;

    private VitalStatistics vitalStatistics = new VitalStatistics();

    public String getClinicVisitId() {
        return clinicVisitId;
    }

    public void setClinicVisitId(String clinicVisitId) {
        this.clinicVisitId = clinicVisitId;
    }

    public VitalStatistics getVitalStatistics() {
        return vitalStatistics;
    }

    public void setVitalStatistics(VitalStatistics vitalStatistics) {
        this.vitalStatistics = vitalStatistics;
    }

    public static VitalStatisticsUIModel newDefault(ClinicVisit clinicVisit) {
        final VitalStatisticsUIModel vitalStatisticsUIModel = new VitalStatisticsUIModel();
        vitalStatisticsUIModel.setClinicVisitId(clinicVisit.getId());
        final VitalStatistics vitalStatistics = new VitalStatistics();
        vitalStatistics.setPatientId(clinicVisit.getPatientId());
        vitalStatisticsUIModel.setVitalStatistics(vitalStatistics);
        return vitalStatisticsUIModel;
    }

    public static VitalStatisticsUIModel get(ClinicVisit clinicVisit, VitalStatistics vitalStatistics) {
        final VitalStatisticsUIModel vitalStatisticsUIModel = new VitalStatisticsUIModel();
        vitalStatisticsUIModel.setClinicVisitId(clinicVisit.getId());
        vitalStatisticsUIModel.setVitalStatistics(vitalStatistics);
        return vitalStatisticsUIModel;
    }
}
