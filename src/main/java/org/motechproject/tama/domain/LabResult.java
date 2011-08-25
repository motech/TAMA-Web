package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'LabResult'")
public class LabResult extends CouchEntity {

    private LabTest labTest;

    @NotNull
    private String patientId;

    private String result;

    private LocalDate clinicVisitDate;

    @NotNull
    private String labTest_id;

    @JsonIgnore
    public LabTest getLabTest() {
        return labTest;
    }

    public void setLabTest(LabTest labTest) {
        this.labTest = labTest;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDate getClinicVisitDate() {
        return clinicVisitDate;
    }

    public void setClinicVisitDate(LocalDate clinicVisitDate) {
        this.clinicVisitDate = clinicVisitDate;
    }


    public String getLabTest_id() {
        return labTest_id;
    }

    public void setLabTest_id(String labTest_id) {
        this.labTest_id = labTest_id;
    }

    public static LabResult newDefault() {
        return new LabResult();
    }
}
