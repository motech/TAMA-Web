package org.motechproject.tama.builder;

import org.joda.time.LocalDate;
import org.motechproject.tama.domain.LabResult;

public class LabResultBuilder {

    private String labTestId;
    private String patientId;
    private String result;
    private String id;
    private String revision;
    private LocalDate testDate;

    public static LabResultBuilder startRecording() {
        return new LabResultBuilder();
    }

    public LabResultBuilder withDefaults() {
        withLabTest_id("");
        withPatientId("");
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
        return labResult;
    }

    public LabResultBuilder withLabTest_id(String labTestId) {
        this.labTestId = labTestId;
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
