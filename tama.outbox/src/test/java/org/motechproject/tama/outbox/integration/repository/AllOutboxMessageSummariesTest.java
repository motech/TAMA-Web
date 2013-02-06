package org.motechproject.tama.outbox.integration.repository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.common.expectation.Pair;
import org.motechproject.tama.outbox.builder.OutboxMessageSummaryBuilder;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.today;

public class AllOutboxMessageSummariesTest extends BaseUnitTest {

    public static final String PATIENT_DOC_ID_1 = "patientDocId1";
    public static final String PATIENT_DOC_ID_2 = "patientDocId2";

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
        OutboxMessageLog log1 = new OutboxMessageLog(PATIENT_DOC_ID_1, "id1", null, null);
        OutboxMessageLog log2 = new OutboxMessageLog(PATIENT_DOC_ID_1, "id2", null, null);
        List<OutboxMessageLog> outboxMessageLogs = Arrays.asList(log1, log2);
        when(allOutboxLogs.list(eq(PATIENT_DOC_ID_1), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(outboxMessageLogs);

        OutboxMessageSummary summary1 = new OutboxMessageSummary();
        OutboxMessageSummary summary2 = new OutboxMessageSummary();
        when(outboxMessageSummaryBuilder.build(log1)).thenReturn(Arrays.asList(summary1));
        when(outboxMessageSummaryBuilder.build(log2)).thenReturn(Arrays.asList(summary2));

        List<OutboxMessageSummary> outboxMessageSummaries = allOutboxMessageSummaries.find(PATIENT_DOC_ID_1, today().minusWeeks(1), today());

        assertEquals(2, outboxMessageSummaries.size());
        assertEquals(summary2, outboxMessageSummaries.get(0));
        assertEquals(summary2, outboxMessageSummaries.get(1));
    }

    @Test
    public void shouldBuildOutboxMessageSummariesOutOfOutboxMessageLogs() {
        LocalDate today = today();
        LocalDate fromDate = today.minusWeeks(1);
        List<Pair<OutboxMessageSummary, OutboxMessageLog>> results = asList(
                new Pair<>(new OutboxMessageSummary(), new OutboxMessageLog()),
                new Pair<>(new OutboxMessageSummary(), new OutboxMessageLog())
        );
        setupBuilder(results);
        when(allOutboxLogs.list(PATIENT_DOC_ID_1, newDateTime(fromDate), newDateTime(today))).thenReturn(
                asList(results.get(0).getActual())
        );
        when(allOutboxLogs.list(PATIENT_DOC_ID_2, newDateTime(fromDate), newDateTime(today))).thenReturn(
                asList(results.get(1).getActual())
        );
        List<OutboxMessageSummary> outboxMessageSummaries = allOutboxMessageSummaries.findAll(asList(PATIENT_DOC_ID_1, PATIENT_DOC_ID_2), today.minusWeeks(1), today);
        assertEquals(asList(results.get(0).getExpected(), results.get(1).getExpected()), outboxMessageSummaries);
    }

    private void setupBuilder(List<Pair<OutboxMessageSummary, OutboxMessageLog>> results) {
        for (Pair<OutboxMessageSummary, OutboxMessageLog> result : results) {
            when(outboxMessageSummaryBuilder.build(result.getActual())).thenReturn(asList(result.getExpected()));
        }
    }
}
