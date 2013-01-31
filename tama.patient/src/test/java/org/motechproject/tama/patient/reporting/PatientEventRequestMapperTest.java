package org.motechproject.tama.patient.reporting;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.patient.domain.PatientEvent;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.tama.reports.contract.PatientEventRequest;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;

public class PatientEventRequestMapperTest {

    private PatientEventRequestMapper patientRequestMapper;
    private PatientEventLog eventLog;
    private DateTime now;

    @Before
    public void setup() {
        now = DateUtil.now();

        eventLog = new PatientEventLog();
        eventLog.setDate(now);
        eventLog.setEvent(PatientEvent.Activation);
        eventLog.setNewValue("Value");
        eventLog.setPatientId("patientDocumentId");

        patientRequestMapper = new PatientEventRequestMapper(eventLog);
    }

    @Test
    public void shouldMapPatientDocumentId() {
        PatientEventRequest request = new PatientEventRequest();
        request.setDateTime(now.toDate());
        request.setEventName(PatientEvent.Activation.getDisplayName());
        request.setPatientDocumentId("patientDocumentId");
        request.setPerformedBy("user");

        assertEquals(request, patientRequestMapper.map("user"));
    }
}
