package org.motechproject.tama.outbox.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.domain.OutboxSummary;
import org.motechproject.tama.outbox.integration.repository.AllOutboxLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@Component
public class OutboxMessageReportService {

    private AllOutboxLogs allOutboxLogs;
    private Properties outboxWaveFileToTextMapping;
    private Logger log = Logger.getLogger(OutboxMessageReportService.class.getName());

    @Autowired
    public OutboxMessageReportService(AllOutboxLogs allOutboxLogs, @Qualifier("outboxWaveFileToTextMapping") Properties outboxWaveFileToTextMapping) {
        this.allOutboxLogs = allOutboxLogs;
        this.outboxWaveFileToTextMapping = outboxWaveFileToTextMapping;
    }

    public JSONObject JSONReport(String patientDocId, LocalDate start, LocalDate end) throws JSONException {
        List<JSONObject> reportLogs = new ArrayList<JSONObject>();
        List<OutboxSummary> outboxSummaries = create(patientDocId, start, end);
        for (OutboxSummary outboxSummary : outboxSummaries) {
            reportLogs.add(new JSONObject(outboxSummary));
        }
        return new JSONObject().put("logs", reportLogs);
    }

    public List<OutboxSummary> create(String patientDocId, LocalDate startDate, LocalDate endDate) {
        final List<OutboxMessageLog> outboxMessageLogs = allOutboxLogs.list(patientDocId, DateUtil.newDateTime(startDate), DateUtil.newDateTime(endDate));
        List<OutboxSummary> outboxSummaries = new ArrayList<OutboxSummary>();
        for (OutboxMessageLog outboxMessageLog : outboxMessageLogs) {
            final List<OutboxMessageLog.PlayedLog> playedLogs = outboxMessageLog.getPlayedLogs();
            if (playedLogs.size() == 0) {
                outboxSummaries.add(outboxSummary(outboxMessageLog));
                continue;
            }
            for (OutboxMessageLog.PlayedLog playedLog : playedLogs) {
                final OutboxSummary summary = outboxSummary(outboxMessageLog);
                summary.setTypeName(outboxMessageLog.getTypeName());
                summary.setPlayedOn(formatDateTime(playedLog.getDate()));
                summary.setPlayedFiles(getPlayedFilesAsString(playedLog));
                outboxSummaries.add(summary);
            }
        }
        return outboxSummaries;
    }

    private OutboxSummary outboxSummary(OutboxMessageLog outboxMessageLog) {
        final OutboxSummary summary = new OutboxSummary();
        summary.setMessageId(outboxMessageLog.getOutboxMessageId());
        summary.setCreatedOn(formatDate(outboxMessageLog.getCreatedOn()));
        return summary;
    }

    String formatDate(DateTime date) {
        return date == null ? "" : date.toLocalDate().toString();
    }

    String formatDateTime(DateTime date) {
        return date == null ? "" : date.toString("yyyy-MM-dd hh:mm");
    }

    public String getPlayedFilesAsString(OutboxMessageLog.PlayedLog playedLogs) {
        List<String> messages = new ArrayList<String>();
        boolean lastWordWasNumeric = false;
        for (String file : playedLogs.getFiles()) {
            String text = outboxWaveFileToTextMapping.getProperty(file.toLowerCase());
            if (text != null) {
                boolean currentWordIsNumeric = StringUtils.isNumeric(text);
                messages.add(lastWordWasNumeric && currentWordIsNumeric ? text : "\n" + text);
                lastWordWasNumeric = currentWordIsNumeric;
            } else
                log.warning("No outbox wave file mapping for " + file + " in outboxWaveFileToTextMapping.properties");
        }
        return StringUtils.join(messages.toArray());
    }
}
