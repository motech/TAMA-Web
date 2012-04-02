package org.motechproject.tama.symptomreporting.domain;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'SymptomReport'")
public class SymptomReport extends CouchEntity {
    @JsonProperty
    private List<String> symptomIds = new ArrayList<String>();
    @JsonProperty
    private String patientDocId;
    @JsonProperty
    private String callId;
    @JsonProperty
    private String adviceGiven;
    @JsonProperty
    private TAMAConstants.ReportedType doctorContacted = TAMAConstants.ReportedType.NA;
    @JsonProperty
    private DateTime reportedAt;

    protected SymptomReport() {
    }

    public SymptomReport(String patientDocId, String callId, DateTime reportedAt) {
        this.patientDocId = patientDocId;
        this.callId = callId;
        this.reportedAt = reportedAt;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public List<String> getSymptomIds() {
        return symptomIds;
    }

    public void addSymptomId(String symptomId) {
        symptomIds.add(symptomId);
    }

    public String getPatientDocId() {
        return patientDocId;
    }

    public void setPatientDocId(String patientDocId) {
        this.patientDocId = patientDocId;
    }

    public DateTime getReportedAt() {
        return DateUtil.setTimeZone(reportedAt);
    }

    public void setReportedAt(DateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public TAMAConstants.ReportedType getDoctorContacted() {
        return doctorContacted;
    }

    public void setDoctorContacted(TAMAConstants.ReportedType doctorContacted) {
        this.doctorContacted = doctorContacted;
    }

    public String getAdviceGiven() {
        return adviceGiven;
    }

    public void setAdviceGiven(String adviceGiven) {
        this.adviceGiven = adviceGiven;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SymptomReport that = (SymptomReport) o;

        if (patientDocId != null ? !patientDocId.equals(that.patientDocId) : that.patientDocId != null) return false;
        if (symptomIds != null ? !CollectionUtils.isEqualCollection(symptomIds, that.symptomIds) : that.symptomIds != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (symptomIds != null ? symptomIds.hashCode() : 0);
        result = 31 * result + (patientDocId != null ? patientDocId.hashCode() : 0);
        return result;
    }
}
