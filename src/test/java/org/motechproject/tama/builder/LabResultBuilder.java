package org.motechproject.tama.builder;

import org.motechproject.tama.domain.LabResult;

public class LabResultBuilder {

    private String labTestId;
    private String patientId;

    public static LabResultBuilder startRecording() {
        return new LabResultBuilder();
    }

    public LabResultBuilder withDefaults() {
        withLabTest_id("");
        withPatientId("");
        return this;
    }

    public LabResult build() {
        LabResult labResult = new LabResult();
        labResult.setLabTest_id(labTestId);
        labResult.setPatientId(patientId);
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
}
