package org.motechproject.tama.patient.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'PatientEventLog'")
public class PatientEventLog extends CouchEntity {

    @NotNull
    protected String patientId;

    @NotNull
    private PatientEvent event;

    @NotNull
    private DateTime date;

    public PatientEventLog() {
    }

    public PatientEventLog(String patientId, PatientEvent event, DateTime date) {
        this.patientId = patientId;
        this.event = event;
        this.date = date;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public PatientEvent getEvent() {
        return event;
    }

    public void setEvent(PatientEvent event) {
        this.event = event;
    }

    public DateTime getDate() {
        return date == null ? null : DateUtil.setTimeZone(date);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }
}