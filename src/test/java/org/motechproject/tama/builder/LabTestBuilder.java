package org.motechproject.tama.builder;

import org.motechproject.tama.domain.LabTest;

public class LabTestBuilder {

    private String labTestId;

    public static LabTestBuilder startRecording() {
        return new LabTestBuilder();
    }

    public LabTestBuilder withDefaults() {
        withId("");
        return this;
    }

    public LabTestBuilder withId(String labTestId) {
        this.labTestId = labTestId;
        return this;
    }

    public LabTest build() {
        LabTest labTest = new LabTest();
        labTest.setId(labTestId);
        return labTest;
    }
}
