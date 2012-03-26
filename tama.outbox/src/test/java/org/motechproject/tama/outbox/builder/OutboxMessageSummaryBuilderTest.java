package org.motechproject.tama.outbox.builder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class OutboxMessageSummaryBuilderTest {

    private Properties outboxWaveFileToTextMapping;

    private OutboxMessageSummaryBuilder outboxMessageSummaryBuilder;

    @Before
    public void setUp() {
        outboxWaveFileToTextMapping = new Properties();
        outboxWaveFileToTextMapping.put("file1", "FILE_ONE");
        outboxWaveFileToTextMapping.put("file2", "FILE_TWO");
        outboxWaveFileToTextMapping.put("100", "100");

        outboxMessageSummaryBuilder = new OutboxMessageSummaryBuilder(outboxWaveFileToTextMapping);
    }

    @Test
    public void shouldBuildSummaryWhenOutboxMessageIsNeverPlayed() {
        LocalDate createdDate = DateUtil.newDate(2011, 10, 10);
        DateTime createdOn = DateUtil.newDateTime(createdDate, 10, 0, 0);
        OutboxMessageLog log = new OutboxMessageLog("patientDocId", "id", createdOn, "type");
        List<OutboxMessageSummary> summaries = outboxMessageSummaryBuilder.build(log);
        assertEquals(1, summaries.size());
        assertSummary(summaries.get(0), "2011-10-10", "type", null, null);
    }

    @Test
    public void shouldBuildSummaryWhenOutboxMessageIsPlayed() {
        LocalDate createdDate = DateUtil.newDate(2011, 10, 10);
        DateTime createdOn = DateUtil.newDateTime(createdDate, 10, 0, 0);
        OutboxMessageLog log = new OutboxMessageLog("patientDocId", "id", createdOn, "type");
        log.playedOn(createdOn.plusDays(1), Arrays.asList("file1", "file2"));
        log.playedOn(createdOn.plusDays(2), Arrays.asList("file2", "100"));
        List<OutboxMessageSummary> summaries = outboxMessageSummaryBuilder.build(log);
        assertEquals(2, summaries.size());
        assertSummary(summaries.get(0), "2011-10-10", "type", "2011-10-11 10:00:00", " FILE_ONE FILE_TWO");
        assertSummary(summaries.get(1), "2011-10-10", "type", "2011-10-12 10:00:00", " FILE_TWO 100");
    }

    @Test
    public void shouldJoinPhoneNumber() throws Exception {
        outboxWaveFileToTextMapping.put("a", "a");
        outboxWaveFileToTextMapping.put("num_0", "0");
        outboxWaveFileToTextMapping.put("num_1", "1");
        outboxWaveFileToTextMapping.put("num_2", "2");
        outboxWaveFileToTextMapping.put("num_3", "3");
        outboxWaveFileToTextMapping.put("b", "b");

        LocalDate createdDate = DateUtil.newDate(2011, 10, 10);
        DateTime createdOn = DateUtil.newDateTime(createdDate, 10, 0, 0);
        OutboxMessageLog log = new OutboxMessageLog("patientDocId", "id", createdOn, "type");
        log.playedOn(createdOn.plusDays(1), Arrays.asList("a", "num_1", "num_2", "num_3", "b"));

        List<OutboxMessageSummary> summaries = outboxMessageSummaryBuilder.build(log);
        assertEquals(1, summaries.size());
        assertSummary(summaries.get(0), "2011-10-10", "type", "2011-10-11 10:00:00", " a 123 b");
    }

    private void assertSummary(OutboxMessageSummary summary, String createdOn, String type, String playedOn, String playedFiles) {
        assertEquals(createdOn, summary.getCreatedOn());
        assertEquals(type, summary.getTypeName());
        assertEquals(playedOn, summary.getPlayedOn());
        assertEquals(playedFiles, summary.getPlayedFiles());
    }

}
