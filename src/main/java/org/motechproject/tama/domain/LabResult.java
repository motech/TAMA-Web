package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAMessages;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@TypeDiscriminator("doc.documentType == 'LabResult'")
public class LabResult extends CouchEntity {

    private LabTest labTest;

    @NotNull
    private String patientId;

    private String result;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @Past(message = TAMAMessages.DATE_OF_BIRTH_MUST_BE_IN_PAST)
    @NotNull
    private Date testDateAsDate;

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

    public LocalDate getTestDate() {
        return DateUtil.newDate(testDateAsDate);
    }

    public void setTestDate(LocalDate testDate) {
        this.testDateAsDate = toDate(testDate);
    }

    @JsonIgnore
    public Date getTestDateAsDate() {
        return testDateAsDate;
    }

    public void setTestDateAsDate(Date testDateAsDate) {
        this.testDateAsDate = testDateAsDate;
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
