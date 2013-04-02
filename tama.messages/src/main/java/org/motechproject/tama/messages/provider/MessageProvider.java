package org.motechproject.tama.messages.provider;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

public interface MessageProvider {

    public boolean hasMessage(TAMAIVRContext context);

    public KookooIVRResponseBuilder nextMessage(TAMAIVRContext context);
}
