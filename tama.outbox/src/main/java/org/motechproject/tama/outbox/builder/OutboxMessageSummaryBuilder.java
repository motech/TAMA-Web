package org.motechproject.tama.outbox.builder;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@Component
public class OutboxMessageSummaryBuilder {

    private Properties outboxWaveFileToTextMapping;

    private Logger log = Logger.getLogger(OutboxMessageSummaryBuilder.class.getName());

    @Autowired
    public OutboxMessageSummaryBuilder(@Qualifier("outboxWaveFileToTextMapping") Properties outboxWaveFileToTextMapping) {
        this.outboxWaveFileToTextMapping = outboxWaveFileToTextMapping;
    }

    public List<OutboxMessageSummary> build(OutboxMessageLog log) {
        List<OutboxMessageLog.PlayedLog> playedLogs = log.getPlayedLogs();
        if (playedLogs.size() == 0) {
            return emptySummary(log);
        } else {
            return summaryPerPlayedLog(log, playedLogs);
        }
    }

    private List<OutboxMessageSummary> summaryPerPlayedLog(OutboxMessageLog log, List<OutboxMessageLog.PlayedLog> playedLogs) {
        List<OutboxMessageSummary> outboxMessageSummaries = new ArrayList<>();
        for (OutboxMessageLog.PlayedLog playedLog : playedLogs) {
            OutboxMessageSummary summary = summary(log);
            summary.playedOn(playedLog.getDate(), getPlayedFilesAsString(playedLog));
            outboxMessageSummaries.add(summary);
        }
        return outboxMessageSummaries;
    }

    private List<OutboxMessageSummary> emptySummary(OutboxMessageLog log) {
        List<OutboxMessageSummary> outboxMessageSummaries = new ArrayList<>();
        OutboxMessageSummary summary = summary(log);
        outboxMessageSummaries.add(summary);
        return outboxMessageSummaries;
    }

    private OutboxMessageSummary summary(OutboxMessageLog log) {
        OutboxMessageSummary summary = new OutboxMessageSummary(log.getCreatedOn(), log.getTypeName());
        summary.setPatientDocId(log.getPatientDocId());
        return summary;
    }

    private String getPlayedFilesAsString(OutboxMessageLog.PlayedLog playedLogs) {
        List<String> messages = new ArrayList<String>();
        boolean lastWordWasNumeric = false;
        for (String file : playedLogs.getFiles()) {
            String text = outboxWaveFileToTextMapping.getProperty(file.toLowerCase());
            if (text != null) {
                boolean currentWordIsNumeric = StringUtils.isNumeric(text);
                messages.add(lastWordWasNumeric && currentWordIsNumeric ? text : " " + text);
                lastWordWasNumeric = currentWordIsNumeric;
            } else
                log.warning("No outbox wave file mapping for " + file + " in outboxWaveFileToTextMapping.properties");
        }
        return StringUtils.join(messages.toArray());
    }
}
