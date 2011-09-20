package org.motechproject.tama.ivr.audit;

import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.domain.IVRCallAudit;

public interface AuditService {
    public void audit(IVRRequest ivrRequest, String patientId, IVRCallAudit.State state);
}
