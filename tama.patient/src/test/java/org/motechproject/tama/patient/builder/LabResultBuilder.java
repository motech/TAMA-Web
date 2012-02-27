package org.motechproject.tama.patient.builder;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.util.DateUtil;

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
        withLabTest(LabTestBuilder.defaultCD4().build());
        withPatientId("patientId");
        withResult("100");
        withTestDate(DateUtil.today());
        return this;
    }

    public static LabResultBuilder defaultCD4Result() {
        return startRecording().
                withDefaults().
                withLabTest(LabTestBuilder.defaultCD4().build()).
                withResult("100");
    }

    public static LabResultBuilder defaultPVLResult() {
        return startRecording().
                withDefaults().
                withLabTest(LabTestBuilder.defaultPVL().build()).
                withResult("0");
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

    public LabResultBuilder withId(String id) {
        this.id = id;
        return this;
    }
}
