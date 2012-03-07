package org.motechproject.tama.outbox.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'OutboxMessageLog'")
public class OutboxMessageLog extends CouchEntity {

    @JsonProperty
    @Getter String patientDocId;
    @JsonProperty
    @Getter String outboxMessageId;
    @JsonProperty
    DateTime createdOn;
    @JsonProperty
    @Getter String typeName;
    @JsonProperty
    @Getter List<PlayedLog> playedLogs = new ArrayList<PlayedLog>();

    public OutboxMessageLog() {
    }

    public OutboxMessageLog(String patientDocId, String outboxMessageId, DateTime date, String typeName) {
        this.patientDocId = patientDocId;
        this.outboxMessageId = outboxMessageId;
        this.createdOn = date;
        this.typeName = typeName;
    }

    public DateTime getCreatedOn() {
        return DateUtil.setTimeZone(this.createdOn);
    }

    public OutboxMessageLog playedOn(DateTime date, List<String> files) {
        playedLogs.add(new PlayedLog(date, files));
        return this;
    }

    public static class PlayedLog {

        @JsonProperty
        @Getter DateTime date;
        @JsonProperty
        @Getter private List<String> files;

        public PlayedLog() {
        }

        public PlayedLog(DateTime date, List<String> files) {
            this.date = date;
            this.files = files;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PlayedLog that = (PlayedLog) o;

            if (date != that.date) return false;
            if (files != null ? !files.equals(that.files) : that.files != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = date != null ? date.hashCode() : 0;
            result = 31 * result + (files != null ? files.hashCode() : 0);
            return result;
        }
    }
}
