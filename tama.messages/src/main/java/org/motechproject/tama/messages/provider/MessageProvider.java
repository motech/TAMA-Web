package org.motechproject.tama.messages.provider;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.Method;

public interface MessageProvider {

    public boolean hasMessage(Method method, TAMAIVRContext context, TAMAMessageType type);

    public KookooIVRResponseBuilder nextMessage(TAMAIVRContext context);
}
