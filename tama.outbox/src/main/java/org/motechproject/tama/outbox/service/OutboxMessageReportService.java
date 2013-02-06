package org.motechproject.tama.outbox.service;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.outbox.contract.OutboxMessageReport;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.tama.outbox.integration.repository.AllOutboxMessageSummaries;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OutboxMessageReportService {

    private AllOutboxMessageSummaries allOutboxMessageSummaries;
    private PatientService patientService;

    @Autowired
    public OutboxMessageReportService(AllOutboxMessageSummaries allOutboxMessageSummaries, PatientService patientService) {
        this.allOutboxMessageSummaries = allOutboxMessageSummaries;
        this.patientService = patientService;
    }

    public JSONObject JSONReport(String patientDocId, LocalDate start, LocalDate end) throws JSONException {
        List<JSONObject> reportLogs = new ArrayList<JSONObject>();
        List<OutboxMessageSummary> outboxMessageSummaries = allOutboxMessageSummaries.find(patientDocId, start, end);
        for (OutboxMessageSummary outboxMessageSummary : outboxMessageSummaries) {
            reportLogs.add(new JSONObject(outboxMessageSummary));
        }
        return new JSONObject().put("logs", reportLogs);
    }


    public OutboxMessageReport reports(String patientId, LocalDate start, LocalDate end) {
        PatientReports patientReports = patientService.getPatientReports(patientId);
        List<OutboxMessageSummary> outboxMessageSummaries = allOutboxMessageSummaries.findAll(patientReports.getPatientDocIds(), start, end);
        return new OutboxMessageReport(patientReports,outboxMessageSummaries);
    }
}
