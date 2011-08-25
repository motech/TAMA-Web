package org.motechproject.tama.integration.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.LabResultBuilder;
import org.motechproject.tama.builder.LabTestBuilder;
import org.motechproject.tama.domain.LabResult;
import org.motechproject.tama.domain.LabTest;
import org.motechproject.tama.repository.LabResults;
import org.motechproject.tama.repository.LabTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class LabResultsTest extends SpringIntegrationTest {

    @Autowired
    private LabResults labResults;

    @Qualifier("tamaDbConnector")
    @Autowired
    private CouchDbConnector couchDbConnector;

    @Autowired
    private LabTests labTests;

    @Before
    public void setUp() {
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId").build();

        labTests.add(labTest);

        labResults = new LabResults(couchDbConnector, labTests);

        markForDeletion(labTest);
    }

    @Test
    public void testGetShouldLoadLabTest() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTest_id("someLabTestId").build();
        labResults.add(labResult);
        String labResultId = labResult.getId();

        LabResult result = labResults.get(labResultId);

        assertEquals("someLabTestId", result.getLabTest().getId());
        markForDeletion(labResult);
    }

    @Test
    public void shouldReturnLabResultsForPatientGivenPatientId() {
        String patientId = "somePatientId";

        LabTest labTest1 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId1").build();
        LabTest labTest2 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId2").build();
        labTests.add(labTest1);
        labTests.add(labTest2);

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest1.getId()).withPatientId(patientId).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest2.getId()).withPatientId(patientId).build();
        labResults.add(labResult1);
        labResults.add(labResult2);

        List<LabResult> results = labResults.findByPatientId(patientId);

        assertEquals(2, results.size());

        assertEquals(patientId, results.get(0).getPatientId());
        assertEquals(patientId, results.get(1).getPatientId());

        assertEquals(labTest1.getId(), results.get(0).getLabTest_id());
        assertEquals(labTest2.getId(), results.get(1).getLabTest_id());

        markForDeletion(labResult2);
        markForDeletion(labResult1);

        markForDeletion(labTest2);
        markForDeletion(labTest1);
    }

}
