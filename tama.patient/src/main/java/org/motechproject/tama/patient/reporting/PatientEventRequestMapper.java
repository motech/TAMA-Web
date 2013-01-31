package org.motechproject.tama.patient.reporting;

import org.joda.time.DateTime;
import org.motechproject.tama.patient.domain.PatientEvent;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.tama.reports.contract.PatientEventRequest;

import java.util.Date;

public class PatientEventRequestMapper {

    private PatientEventLog entity;

    public PatientEventRequestMapper(PatientEventLog entity) {
        this.entity = entity;
    }

    public PatientEventRequest map(String user) {
        PatientEventRequest request = new PatientEventRequest();
        request.setDateTime(getEntityDateString());
        request.setEventName(getDisplayName());
        request.setPatientDocumentId(entity.getPatientId());
        request.setPerformedBy(user);
        return request;
    }

    private String getDisplayName() {
        PatientEvent event = entity.getEvent();
        return (null == event) ? "" : event.getDisplayName();
    }

    private Date getEntityDateString() {
        DateTime date = entity.getDate();
        return (null == date) ? null : date.toDate();
    }
}
