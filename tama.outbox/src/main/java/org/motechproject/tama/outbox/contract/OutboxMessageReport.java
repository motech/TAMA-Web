package org.motechproject.tama.outbox.contract;

import lombok.Data;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.tama.patient.domain.PatientReports;

import java.util.List;

@Data
public class OutboxMessageReport {

    private PatientReports patientReports;
    private List<OutboxMessageSummary> outboxMessageSummaries;

    public OutboxMessageReport(PatientReports patientReports, List<OutboxMessageSummary> outboxMessageSummaries) {
        this.patientReports = patientReports;
        this.outboxMessageSummaries = outboxMessageSummaries;
    }
}
