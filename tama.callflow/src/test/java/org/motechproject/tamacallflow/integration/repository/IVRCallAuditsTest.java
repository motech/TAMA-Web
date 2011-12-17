package org.motechproject.tamacallflow.integration.repository;

import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tamacallflow.domain.IVRCallAudit;
import org.motechproject.tamacallflow.repository.AllIVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationContext-TAMACallFlow.xml", inheritLocations = false)
public class IVRCallAuditsTest extends SpringIntegrationTest {

    @Autowired
    private AllIVRCallAudits audits;

    @Test
    public void shouldPersistAndFetchAudit() {
        IVRCallAudit audit = new IVRCallAudit("cid", "sid", "patientId", IVRCallAudit.State.USER_AUTHORISED);
        audits.add(audit);
        markForDeletion(audit);

        IVRCallAudit dbAudit = audits.get(audit.getId());
        assertEquals("cid", dbAudit.getCid());
        assertEquals("sid", dbAudit.getSid());
        assertEquals("patientId", dbAudit.getPatientId());
    }
}

