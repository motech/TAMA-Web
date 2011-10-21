package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAMessages;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Comparator;
import java.util.Date;

@TypeDiscriminator("doc.documentType == 'LabResult'")
public class LabResult extends CouchEntity {

    private LabTest labTest;

    @NotNull
    private String patientId;

    @NotNull
    private String result;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @Past(message = TAMAMessages.TEST_DATE_MUST_BE_IN_PAST)
    @NotNull(message = TAMAMessages.TEST_DATE_NOT_EMPTY)
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

    @JsonIgnore
    public boolean isCD4() {
        LabTest labTest = getLabTest();
        return labTest != null && labTest.getName().toLowerCase().equals("cd4");
    }

    public static class LabResultComparator implements Comparator<LabResult> {
        @Override
        public int compare(LabResult o1, LabResult o2) {
            Period period = new Period(o1.getTestDate(), o2.getTestDate(), PeriodType.minutes());
            return period.getMinutes() > 0 ? 1 : -1;
        }
    }
}
