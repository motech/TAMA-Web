package org.motechproject.tama.patient.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'CallTimeSlot'")
public class CallTimeSlot extends CouchEntity {

    @JsonProperty
    private DateTime callTime;

    @NotNull
    private String patientDocumentId;

    @JsonIgnore
    public LocalTime getCallTime() {
        if (callTime == null) return null;
        return DateUtil.setTimeZone(callTime).toLocalTime();
    }

    @JsonIgnore
    public void setCallTime(LocalTime callTime) {
        this.callTime = new DateTime(0).withTime(callTime.getHourOfDay(), callTime.getMinuteOfHour(), callTime.getSecondOfMinute(), 0);
    }

    public String getPatientDocumentId() {
        return patientDocumentId;
    }

    public void setPatientDocumentId(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
    }

}
