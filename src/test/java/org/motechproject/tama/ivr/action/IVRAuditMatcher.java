package org.motechproject.tama.ivr.action;

import org.mockito.ArgumentMatcher;
import org.motechproject.tama.domain.IVRCallAudit;

public class IVRAuditMatcher extends ArgumentMatcher<IVRCallAudit> {
    private String sid;
    private String cid;
    private String patientId;
    private IVRCallAudit.State state;

    public IVRAuditMatcher(String sid, String cid, String patientId, IVRCallAudit.State state) {
        this.sid = sid;
        this.cid = cid;
        this.patientId = patientId;
        this.state = state;
    }

    @Override
    public boolean matches(Object o) {
        IVRCallAudit audit = (IVRCallAudit) o;
        return audit.getSid().equals(sid)
                && audit.getCid().equals(cid)
                && audit.getPatientId().equals(patientId)
                && audit.getState().equals(state);
    }
}
