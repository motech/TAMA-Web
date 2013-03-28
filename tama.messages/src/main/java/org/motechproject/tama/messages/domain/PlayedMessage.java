package org.motechproject.tama.messages.domain;

import lombok.EqualsAndHashCode;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.outbox.context.OutboxContext;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@EqualsAndHashCode
public class PlayedMessage {

    public static enum Types {
        HEALTH_TIPS,
        OUTBOX
    }

    private KooKooIVRContext ivrContext;

    public PlayedMessage(KooKooIVRContext ivrContext) {
        this.ivrContext = ivrContext;
    }

    public boolean exists() {
        return (isNotBlank(new TAMAIVRContextFactory().create(ivrContext).getLastPlayedHealthTip())
                || isNotBlank(new OutboxContext(ivrContext).lastPlayedMessageId()));
    }

    public void reset() {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(ivrContext);
        OutboxContext outboxContext = new OutboxContext(ivrContext);
        if (isNotBlank(tamaivrContext.getLastPlayedHealthTip())) {
            tamaivrContext.setLastPlayedHealthTip(null);
        } else {
            outboxContext.lastPlayedMessageId(null);
        }
        tamaivrContext.currentDecisionTreePath("");
        tamaivrContext.setMessagesCategory("");
    }

    public String id() {
        if (Types.HEALTH_TIPS.equals(type())) {
            return new TAMAIVRContextFactory().create(ivrContext).getLastPlayedHealthTip();
        } else {
            return new OutboxContext(ivrContext).lastPlayedMessageId();
        }
    }

    public Types type() {
        if (isNotBlank(new TAMAIVRContextFactory().create(ivrContext).getLastPlayedHealthTip())) {
            return Types.HEALTH_TIPS;
        } else {
            return Types.OUTBOX;
        }
    }
}
