package org.motechproject.tama.builder;

import org.motechproject.tama.domain.LabTest;

public class LabTestBuilder {

    private String labTestId;
    private String name;

    public static LabTestBuilder startRecording() {
        return new LabTestBuilder();
    }

    public LabTestBuilder withDefaults() {
        withId("id");
        withName("name");
        return this;
    }

    public LabTestBuilder withId(String labTestId) {
        this.labTestId = labTestId;
        return this;
    }

    public LabTestBuilder withName(String name){
        this.name = name;
        return this;
    }

    public LabTest build() {
        LabTest labTest = new LabTest();
        labTest.setId(labTestId);
        labTest.setName(name);
        return labTest;
    }
}
