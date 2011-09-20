package org.motechproject.tama.ivr.audit;

import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.repository.AllIVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    protected AllIVRCallAudits audits;

    @Autowired
    public AuditServiceImpl(AllIVRCallAudits audits) {
        this.audits = audits;
    }

    @Override
    public void audit(IVRRequest ivrRequest, String patientId, IVRCallAudit.State state) {
        audits.add(new IVRCallAudit(ivrRequest.getCallerId(), ivrRequest.getSessionId(), patientId, state));
    }
}
