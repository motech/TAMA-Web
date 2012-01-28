package org.motechproject.tama.patient.builder;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.refdata.domain.LabTest;

public class LabResultBuilder {

    private String labTestId;
    private String patientId;
    private String result;
    private String id;
    private String revision;
    private LocalDate testDate;
    private LabTest labTest;

    public static LabResultBuilder startRecording() {
        return new LabResultBuilder();
    }

    public LabResultBuilder withDefaults() {
        withLabTestId("");
        withPatientId("patientId");
        withResult("");
        withTestDate(null);
        return this;
    }

    public LabResult build() {
        LabResult labResult = new LabResult();
        labResult.setLabTest_id(labTestId);
        labResult.setPatientId(patientId);
        labResult.setResult(result);
        labResult.setTestDate(testDate);
        labResult.setLabTest(labTest);
        return labResult;
    }

    public LabResultBuilder withLabTestId(String labTestId) {
        this.labTestId = labTestId;
        return this;
    }

    public LabResultBuilder withLabTest(LabTest labTest) {
        this.labTest = labTest;
        return this;
    }

    public LabResultBuilder withPatientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public LabResultBuilder withResult(String result) {
        this.result = result;
        return this;
    }


    public LabResultBuilder withTestDate(LocalDate testDate) {
        this.testDate = testDate;
        return this;
    }
}
