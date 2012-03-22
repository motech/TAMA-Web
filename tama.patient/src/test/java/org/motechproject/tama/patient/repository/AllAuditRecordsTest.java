package org.motechproject.tama.patient.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.domain.AuditRecord;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class AllAuditRecordsTest extends SpringIntegrationTest {

    @Autowired
    AllAuditRecords allAuditRecords;

    @Before
    public void setUp() {
    }

    @Test
    public void shouldAddTreatmentAdviceAuditRecord() {
        TreatmentAdvice before = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        TreatmentAdvice after = TreatmentAdviceBuilder.startRecording().withDefaults().withPatientId("newID").build();
        AuditRecord auditRecord = new AuditRecord(DateUtil.now(), "userName", before, after);
        allAuditRecords.add(auditRecord);

        AuditRecord savedAuditRecord = allAuditRecords.get(auditRecord.getId());
        assertNotNull(savedAuditRecord);
        Object savedAuditRecordBefore = savedAuditRecord.getBefore();
        System.out.println(savedAuditRecordBefore.getClass());
    }

    @After
    public void tearDown() {
        //markForDeletion(allAuditRecords.getAll().toArray());
    }

}
