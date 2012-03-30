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

    private String newValue;

    public PatientEventLog() {
    }

    public PatientEventLog(String patientDocId, PatientEvent event) {
        this.patientId = patientDocId;
        this.event = event;
        this.date = DateUtil.now();
    }

    public PatientEventLog(String patientDocId, PatientEvent event, String newValue) {
        this(patientDocId, event);
        this.newValue = newValue;
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
        return DateUtil.setTimeZone(date);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}