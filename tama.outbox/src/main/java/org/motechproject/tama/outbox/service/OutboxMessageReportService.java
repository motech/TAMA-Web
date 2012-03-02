package org.motechproject.tama.outbox.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.integration.repository.AllOutboxLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class OutboxMessageReportService {

    private AllOutboxLogs allOutboxLogs;
    @Autowired
    Properties outboxWaveFileToTextMapping;

    @Autowired
    public OutboxMessageReportService(AllOutboxLogs allOutboxLogs, @Qualifier("outboxWaveFileToTextMapping") Properties outboxWaveFileToTextMapping) {
        this.allOutboxLogs = allOutboxLogs;
        this.outboxWaveFileToTextMapping = outboxWaveFileToTextMapping;
    }

    public JSONObject JSONReport(String patientDocId, LocalDate start, LocalDate end) throws JSONException {
        final List<OutboxMessageLog> outboxMessageLogs = allOutboxLogs.list(patientDocId, DateUtil.newDateTime(start, 0, 0, 0), DateUtil.newDateTime(end, 0, 0, 0));

        JSONObject result = new JSONObject();

        List<JSONObject> reportLogs = new ArrayList<JSONObject>();
        for (OutboxMessageLog outboxMessageLog : outboxMessageLogs) {
            final List<OutboxMessageLog.PlayedLog> playedLogs = outboxMessageLog.getPlayedLogs();
            if (playedLogs.size() == 0) //when outbox message was never played
                reportLogs.add(newLogJsonObject(outboxMessageLog));
            for (OutboxMessageLog.PlayedLog playedLog : playedLogs) {
                final JSONObject log = newLogJsonObject(outboxMessageLog);
                log.put("playedOn", formatDate(playedLog.getDate()));
                log.put("playedFiles", getPlayedFilesAsString(playedLog));
                reportLogs.add(log);
            }
        }
        result.put("logs", reportLogs);
        return result;
    }

    private JSONObject newLogJsonObject(OutboxMessageLog outboxMessageLog) throws JSONException {
        final JSONObject log = new JSONObject();
        log.put("messageId", outboxMessageLog.getOutboxMessageId());
        log.put("createdOn", formatDate(outboxMessageLog.getCreatedOn()));
        return log;
    }

    private String formatDate(DateTime date) {
        return date == null ? "" : date.toLocalDate().toString();
    }

    private String getPlayedFilesAsString(OutboxMessageLog.PlayedLog playedLogs) {
        List<String> messages = new ArrayList<String>();
        for (String file : playedLogs.getFiles()) {
            messages.add(outboxWaveFileToTextMapping.getProperty(file));
        }
        return StringUtils.join(messages.toArray(), ",\n");
    }
}
