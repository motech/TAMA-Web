package org.motechproject.tama.eventlogging.service;

import org.joda.time.DateTime;
import org.motechproject.ivr.IVRCallEvent;

import java.util.Map;

public interface CallLogService {
    void create(IVRCallEvent ivrCallEvent);
}