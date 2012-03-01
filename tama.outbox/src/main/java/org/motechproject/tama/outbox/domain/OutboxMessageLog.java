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
    @Getter
    String outboxMessageId;
    @Getter
    DateTime date;
    @JsonProperty
    @Getter
    OutboxEventType event;
    @JsonProperty
    private List<String> files;

    public OutboxMessageLog() {
    }

    public OutboxMessageLog(String outboxMessageId, DateTime date, OutboxEventType event, List<String> files) {
        this(outboxMessageId, date, event);
        this.files = new ArrayList<String>(files);
    }

    public OutboxMessageLog(String outboxMessageId, DateTime date, OutboxEventType event) {
        this.outboxMessageId = outboxMessageId;
        setDate(date);
        this.event = event;
    }

    public void setDate(DateTime date) {
        this.date = DateUtil.setTimeZone(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OutboxMessageLog that = (OutboxMessageLog) o;

        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (event != that.event) return false;
        if (outboxMessageId != null ? !outboxMessageId.equals(that.outboxMessageId) : that.outboxMessageId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (outboxMessageId != null ? outboxMessageId.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (event != null ? event.hashCode() : 0);
        return result;
    }

}
