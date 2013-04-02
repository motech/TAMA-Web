package org.motechproject.tama.messages.domain;

import lombok.EqualsAndHashCode;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@EqualsAndHashCode
public class PlayedMessage {

    private KooKooIVRContext ivrContext;

    public PlayedMessage(KooKooIVRContext ivrContext) {
        this.ivrContext = ivrContext;
    }

    public boolean exists() {
        TAMAIVRContext context = new TAMAIVRContextFactory().create(ivrContext);
        return (isNotBlank(context.getLastPlayedHealthTip()) || isNotBlank(context.lastPlayedMessageId()));
    }

    public void reset() {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(ivrContext);
        if (isNotBlank(tamaivrContext.getLastPlayedHealthTip())) {
            tamaivrContext.setLastPlayedHealthTip(null);
        } else {
            tamaivrContext.lastPlayedMessageId(null);
        }
        tamaivrContext.callState(CallState.AUTHENTICATED);
        tamaivrContext.currentDecisionTreePath("");
        tamaivrContext.setMessagesCategory("");
        tamaivrContext.setTAMAMessageType("");
    }

    public String id() {
        TAMAIVRContext context = new TAMAIVRContextFactory().create(ivrContext);
        if (Types.HEALTH_TIPS.equals(type())) {
            return context.getLastPlayedHealthTip();
        } else {
            return context.lastPlayedMessageId();
        }
    }

    public Types type() {
        if (isNotBlank(new TAMAIVRContextFactory().create(ivrContext).getLastPlayedHealthTip())) {
            return Types.HEALTH_TIPS;
        } else {
            return Types.MESSAGES;
        }
    }

    public static enum Types {
        HEALTH_TIPS,
        MESSAGES
    }
}
