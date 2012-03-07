package org.motechproject.tama.outbox.integration.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.outbox.builder.OutboxMessageSummaryBuilder;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllOutboxMessageSummariesTest extends BaseUnitTest {

    public static final String PATIENT_DOC_ID = "patientDocId";

    @Mock
    private AllOutboxLogs allOutboxLogs;
    @Mock
    private OutboxMessageSummaryBuilder outboxMessageSummaryBuilder;

    private AllOutboxMessageSummaries allOutboxMessageSummaries;

    @Before
    public void setup() {
        initMocks(this);
        allOutboxMessageSummaries = new AllOutboxMessageSummaries(allOutboxLogs, outboxMessageSummaryBuilder);
    }

    @Test
    public void shouldReturnOutboxSummaryWhenOutboxMessageWasPlayed() {
        final OutboxMessageLog log1 = new OutboxMessageLog(PATIENT_DOC_ID, "id1", null, null);
        final OutboxMessageLog log2 = new OutboxMessageLog(PATIENT_DOC_ID, "id2", null, null);
        List<OutboxMessageLog> outboxMessageLogs = Arrays.asList(log1, log2);
        when(allOutboxLogs.list(eq(PATIENT_DOC_ID), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(outboxMessageLogs);

        final OutboxMessageSummary summary1 = new OutboxMessageSummary();
        final OutboxMessageSummary summary2 = new OutboxMessageSummary();
        when(outboxMessageSummaryBuilder.build(log1)).thenReturn(Arrays.asList(summary1));
        when(outboxMessageSummaryBuilder.build(log2)).thenReturn(Arrays.asList(summary2));

        List<OutboxMessageSummary> outboxMessageSummaries = allOutboxMessageSummaries.find(PATIENT_DOC_ID, DateUtil.today().minusWeeks(1), DateUtil.today());
        assertEquals(2, outboxMessageSummaries.size());
        assertEquals(summary2, outboxMessageSummaries.get(0));
        assertEquals(summary2, outboxMessageSummaries.get(1));
    }

}
