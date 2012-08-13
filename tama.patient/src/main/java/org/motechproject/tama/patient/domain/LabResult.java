package org.motechproject.tama.patient.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.refdata.domain.LabTest;
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

    public static final int INVALID_CD4_COUNT = -1;
    //TODO: This should not be a field
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
        if (labTest != null) {
            this.labTest_id = labTest.getId();
            this.labTest = labTest;
        }
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
        if (getLabTest() == null) return false;
        return TAMAConstants.LabTestType.isCD4(getLabTest().getName());
    }

    public static class LabResultComparator implements Comparator<LabResult> {

        private boolean earliestToLatest;

        public LabResultComparator(boolean earliestToLatest) {
            this.earliestToLatest = earliestToLatest;
        }

        @Override
        public int compare(LabResult o1, LabResult o2) {
            if (earliestToLatest) {
                return o1.getTestDate().compareTo(o2.getTestDate());
            } else {
                return o2.getTestDate().compareTo(o1.getTestDate());
            }
        }
    }
}
