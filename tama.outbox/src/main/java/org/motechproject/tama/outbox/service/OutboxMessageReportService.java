package org.motechproject.tama.outbox.service;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.tama.outbox.integration.repository.AllOutboxMessageSummaries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OutboxMessageReportService {

    private AllOutboxMessageSummaries allOutboxMessageSummaries;

    @Autowired
    public OutboxMessageReportService(AllOutboxMessageSummaries allOutboxMessageSummaries) {
        this.allOutboxMessageSummaries = allOutboxMessageSummaries;
    }

    public JSONObject JSONReport(String patientDocId, LocalDate start, LocalDate end) throws JSONException {
        List<JSONObject> reportLogs = new ArrayList<JSONObject>();
        List<OutboxMessageSummary> outboxMessageSummaries = allOutboxMessageSummaries.find(patientDocId, start, end);
        for (OutboxMessageSummary outboxMessageSummary : outboxMessageSummaries) {
            reportLogs.add(new JSONObject(outboxMessageSummary));
        }
        return new JSONObject().put("logs", reportLogs);
    }

}
