package org.motechproject.tama.patient.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

import javax.validation.constraints.NotNull;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'ClinicVisit'")
public class ClinicVisit extends CouchEntity {

    @NotNull
    private String patientId;
    @NotNull
    private String treatmentAdviceId;
    private List<String> labResultIds;
    private String vitalStatisticsId;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getTreatmentAdviceId() {
        return treatmentAdviceId;
    }

    public void setTreatmentAdviceId(String treatmentAdviceId) {
        this.treatmentAdviceId = treatmentAdviceId;
    }

    public List<String> getLabResultIds() {
        return labResultIds;
    }

    public void setLabResultIds(List<String> labResultIds) {
        this.labResultIds = labResultIds;
    }

    public String getVitalStatisticsId() {
        return vitalStatisticsId;
    }

    public void setVitalStatisticsId(String vitalStatisticsId) {
        this.vitalStatisticsId = vitalStatisticsId;
    }
}
