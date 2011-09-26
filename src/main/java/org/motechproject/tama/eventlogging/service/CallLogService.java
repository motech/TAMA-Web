package org.motechproject.tama.eventlogging.service;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.eventlogging.domain.CallLog;

public interface CallLogService {

    CallLog create(String callType, Patient patient);

}