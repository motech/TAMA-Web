package org.motechproject.tama.messages.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;

public interface PatientMessagesController {

    public boolean markAsRead(KooKooIVRContext kooKooIVRContext);

    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext);
}
