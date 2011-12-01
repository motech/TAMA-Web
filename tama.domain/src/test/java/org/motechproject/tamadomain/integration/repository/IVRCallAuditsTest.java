package org.motechproject.tamadomain.integration.repository;

import org.junit.Test;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamadomain.domain.IVRCallAudit;
import org.motechproject.tamadomain.repository.AllIVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class IVRCallAuditsTest extends SpringIntegrationTest {

    @Autowired
    private AllIVRCallAudits audits;

    @Test
    public void shouldPersistAndFetchAudit() {
        IVRCallAudit audit = new IVRCallAudit("cid", "sid", "patientId", IVRCallAudit.State.USER_AUTHORISED);
        audits.add(audit);
        markForDeletion(audit);

        IVRCallAudit dbAudit = audits.get(audit.getId());
        assertEquals("cid",dbAudit.getCid());
        assertEquals("sid",dbAudit.getSid());
        assertEquals("patientId",dbAudit.getPatientId());
    }
}

