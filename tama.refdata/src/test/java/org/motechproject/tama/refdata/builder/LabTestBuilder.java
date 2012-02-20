package org.motechproject.tama.refdata.builder;

import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.refdata.domain.LabTest;

public class LabTestBuilder {

    private String labTestId;
    private String name;

    public static LabTestBuilder startRecording() {
        return new LabTestBuilder();
    }

    public LabTestBuilder withDefaults() {
        withId("id");
        withType(TAMAConstants.LabTestType.CD4);
        return this;
    }

    public LabTestBuilder withId(String labTestId) {
        this.labTestId = labTestId;
        return this;
    }

    public LabTestBuilder withType(TAMAConstants.LabTestType labTestType) {
        this.name = labTestType.getName();
        return this;
    }

    public LabTest build() {
        LabTest labTest = new LabTest();
        labTest.setId(labTestId);
        labTest.setName(name);
        return labTest;
    }

    public static LabTestBuilder defaultCD4() {
        return startRecording().withType(TAMAConstants.LabTestType.CD4).withId("cd4LabTest");
    }
}
