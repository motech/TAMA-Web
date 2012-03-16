package org.motechproject.tama.outbox.integration.repository;

import org.joda.time.LocalDate;
import org.motechproject.tama.outbox.builder.OutboxMessageSummaryBuilder;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AllOutboxMessageSummaries {

    private AllOutboxLogs allOutboxLogs;
    private OutboxMessageSummaryBuilder outboxMessageSummaryBuilder;

    @Autowired
    public AllOutboxMessageSummaries(AllOutboxLogs allOutboxLogs, OutboxMessageSummaryBuilder outboxMessageSummaryBuilder) {
        this.allOutboxLogs = allOutboxLogs;
        this.outboxMessageSummaryBuilder = outboxMessageSummaryBuilder;
    }

    public List<OutboxMessageSummary> find(String patientDocId, LocalDate startDate, LocalDate endDate) {
        List<OutboxMessageSummary> outboxMessageSummaries = new ArrayList<OutboxMessageSummary>();
        List<OutboxMessageLog> outboxMessageLogs = allOutboxLogs.list(patientDocId, DateUtil.newDateTime(startDate), DateUtil.newDateTime(endDate));
        for (OutboxMessageLog outboxMessageLog : outboxMessageLogs) {
            outboxMessageSummaries.addAll(outboxMessageSummaryBuilder.build(outboxMessageLog));
        }
        return outboxMessageSummaries;
    }
}
