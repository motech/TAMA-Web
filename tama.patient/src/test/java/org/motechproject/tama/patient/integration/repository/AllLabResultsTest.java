package org.motechproject.tama.patient.integration.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.repository.AllLabTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class AllLabResultsTest extends SpringIntegrationTest {

    @Autowired
    private AllLabResults allLabResults;

    @Qualifier("tamaDbConnector")
    @Autowired
    private CouchDbConnector couchDbConnector;

    @Autowired
    private AllLabTests allLabTests;

    @Before
    public void setUp() {
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId").build();
        allLabTests.add(labTest);
        allLabResults = new AllLabResults(couchDbConnector, allLabTests);
        markForDeletion(labTest);
    }

    @Test
    public void testGetShouldLoadLabTest() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTest_id("someLabTestId").build();
        allLabResults.add(labResult);
        String labResultId = labResult.getId();

        LabResult result = allLabResults.get(labResultId);

        assertEquals("someLabTestId", result.getLabTest().getId());
        markForDeletion(labResult);
    }

    @Test
    public void shouldReturnLabResultsForPatientGivenPatientId() {
        String patientId = "somePatientId_byPatient";

        LabTest labTest1 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId1_byPatient").build();
        LabTest labTest2 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId2_byPatient").build();
        allLabTests.add(labTest1);
        allLabTests.add(labTest2);

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest1.getId()).withPatientId(patientId).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest2.getId()).withPatientId(patientId).build();
        allLabResults.add(labResult1);
        allLabResults.add(labResult2);

        LabResults results = allLabResults.findByPatientId(patientId);

        assertEquals(2, results.size());

        List<String> expectedIds = Arrays.asList(labTest1.getId(), labTest2.getId());

        assertTrue(expectedIds.contains(results.get(0).getLabTest_id()));
        assertTrue(expectedIds.contains(results.get(1).getLabTest_id()));

        markForDeletion(labResult2);
        markForDeletion(labResult1);

        markForDeletion(labTest2);
        markForDeletion(labTest1);
    }

    @Test
    public void shouldMergeLabResultsWithLabResultsFromTheDatabase() {

        String patientId = "patientId_merge";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId_merge").build();
        allLabTests.add(labTest);

        LabResult labResultAlreadyPresentInDB = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest.getId()).withResult("1").withPatientId(patientId).build();
        allLabResults.add(labResultAlreadyPresentInDB);

        LabResults labResultsForPatient = allLabResults.findByPatientId(patientId);


        assertEquals("1", labResultsForPatient.get(0).getResult());

        LabResult labResultFromUI = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest.getId()).withResult("2").withPatientId(patientId).build();
        labResultFromUI.setId(labResultAlreadyPresentInDB.getId());

        allLabResults.merge(new LabResults(Arrays.asList(labResultFromUI)));

        labResultsForPatient = allLabResults.findByPatientId(patientId);

        assertEquals("2", labResultsForPatient.get(0).getResult());
        assertNotNull(labResultsForPatient.get(0).getRevision());

        markForDeletion(allLabResults.get(labResultAlreadyPresentInDB.getId()));
        markForDeletion(labTest);
    }
}
