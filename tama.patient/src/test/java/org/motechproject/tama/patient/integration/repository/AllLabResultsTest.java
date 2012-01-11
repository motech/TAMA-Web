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
import org.motechproject.util.DateUtil;
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
        markForDeletion(labResult);
        String labResultId = labResult.getId();

        LabResult result = allLabResults.get(labResultId);

        assertEquals("someLabTestId", result.getLabTest().getId());
    }

    @Test
    public void shouldReturnLabResultsForPatientGivenPatientId() {
        String patientId = "somePatientId_byPatient";

        LabTest labTest1 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId1_byPatient").build();
        LabTest labTest2 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId2_byPatient").build();
        allLabTests.add(labTest1);
        markForDeletion(labTest1);
        allLabTests.add(labTest2);
        markForDeletion(labTest2);

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest1.getId()).withPatientId(patientId).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest2.getId()).withPatientId(patientId).build();
        allLabResults.add(labResult1);
        markForDeletion(labResult1);
        allLabResults.add(labResult2);
        markForDeletion(labResult2);

        LabResults results = allLabResults.findLatestLabResultsByPatientId(patientId);

        assertEquals(2, results.size());

        List<String> expectedIds = Arrays.asList(labTest1.getId(), labTest2.getId());

        assertTrue(expectedIds.contains(results.get(0).getLabTest_id()));
        assertTrue(expectedIds.contains(results.get(1).getLabTest_id()));
    }

    @Test
    public void shouldReturnALabResultForPatientGivenPatientIdAndLabTestIdAndTestDate() {
        String patientId = "somePatientId_byPatient";

        LabTest labTest1 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId1_byPatient").build();
        LabTest labTest2 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId2_byPatient").build();
        allLabTests.add(labTest1);
        markForDeletion(labTest1);
        allLabTests.add(labTest2);
        markForDeletion(labTest2);

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withResult("1").withTestDate(DateUtil.today()).withLabTest_id(labTest1.getId()).withPatientId(patientId).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withResult("1").withTestDate(DateUtil.today()).withLabTest_id(labTest2.getId()).withPatientId(patientId).build();
        LabResult labResult3 = LabResultBuilder.startRecording().withDefaults().withResult("2").withTestDate(DateUtil.today()).withLabTest_id(labTest2.getId()).withPatientId(patientId).build();
        allLabResults.add(labResult1);
        markForDeletion(labResult1);
        allLabResults.add(labResult2);
        markForDeletion(labResult2);
        allLabResults.add(labResult3);
        markForDeletion(labResult3);

        LabResult labResult = allLabResults.findByPatientIdLabTestIdAndTestDate(patientId, labTest2, DateUtil.today());

        assertNotNull(labResult);
        assertLabResult("1", labResult);
    }

    @Test
    public void shouldReturnLatestLabResultsForPatientGivenPatientId() {
        String patientId = "somePatientId_byPatient";

        LabTest labTest1 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId3_byPatient").build();
        allLabTests.add(labTest1);
        markForDeletion(labTest1);

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withResult("1").withTestDate(DateUtil.today().minusDays(1)).withLabTest_id(labTest1.getId()).withPatientId(patientId).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withResult("2").withTestDate(DateUtil.today()).withLabTest_id(labTest1.getId()).withPatientId(patientId).build();
        allLabResults.add(labResult1);
        markForDeletion(labResult1);
        allLabResults.add(labResult2);
        markForDeletion(labResult2);

        LabResults results = allLabResults.findLatestLabResultsByPatientId(patientId);

        assertEquals(1, results.size());

        assertEquals("2", results.get(0).getResult());
        assertEquals(DateUtil.today(), results.get(0).getTestDate());
    }

    @Test
    public void shouldMergeLabResultsWithLabResultsFromTheDatabase_WhenTestDateIsSame() {

        String patientId = "patientId_merge";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId_merge1").build();
        allLabTests.add(labTest);
        markForDeletion(labTest);

        LabResult labResultAlreadyPresentInDB = LabResultBuilder.startRecording().withDefaults().withTestDate(DateUtil.today()).withLabTest(labTest).withLabTest_id(labTest.getId()).withResult("1").withPatientId(patientId).build();
        allLabResults.add(labResultAlreadyPresentInDB);
        markForDeletion(labResultAlreadyPresentInDB);

        LabResults labResultsForPatient = allLabResults.findByPatientId(patientId);

        assertEquals(1, labResultsForPatient.size());
        assertLabResult("1", labResultsForPatient.get(0));

        LabResult labResultFromUI = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest.getId()).withLabTest(labTest).withTestDate(DateUtil.today()).withResult("2").withPatientId(patientId).build();
        labResultFromUI.setId(labResultAlreadyPresentInDB.getId());

        allLabResults.upsert(labResultFromUI);

        markLabResultsForDeletion(patientId);

        labResultsForPatient = allLabResults.findByPatientId(patientId);

        assertEquals(1, labResultsForPatient.size());
        assertLabResult("2", labResultsForPatient.get(0));
    }

    @Test
    public void upsertShouldCreateNewLabResults_WhenTestDateIsNotSame() {

        String patientId = "patientId_merge";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId_merge2").build();
        allLabTests.add(labTest);
        markForDeletion(labTest);

        LabResult labResultAlreadyPresentInDB = LabResultBuilder.startRecording().withDefaults().withLabTest(labTest).withTestDate(DateUtil.today().minusDays(1)).withLabTest_id(labTest.getId()).withResult("1").withPatientId(patientId).build();
        allLabResults.add(labResultAlreadyPresentInDB);

        LabResults labResultsForPatient = allLabResults.findByPatientId(patientId);

        assertEquals(1, labResultsForPatient.size());
        assertLabResult("1", labResultsForPatient.get(0));

        LabResult labResultFromUI = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest.getId()).withLabTest(labTest).withTestDate(DateUtil.today()).withResult("2").withPatientId(patientId).build();
        labResultFromUI.setId(labResultAlreadyPresentInDB.getId());

        allLabResults.upsert(labResultFromUI);

        markLabResultsForDeletion(patientId);

        labResultsForPatient = allLabResults.findByPatientId(patientId);

        assertEquals(2, labResultsForPatient.size());
        assertLabResult("2", labResultsForPatient.get(1));
    }

    @Test
    public void upsertShouldCreateNewLabResults_WhenItDoesNotExistInDb() {

        String patientId = "patientId_merge";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId_merge3").build();
        allLabTests.add(labTest);
        markForDeletion(labTest);

        LabResults labResultsForPatient = allLabResults.findLatestLabResultsByPatientId(patientId);

        assertEquals(0, labResultsForPatient.size());

        LabResult labResultFromUI = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTest.getId()).withLabTest(labTest).withTestDate(DateUtil.today()).withResult("2").withPatientId(patientId).build();

        allLabResults.upsert(labResultFromUI);

        markLabResultsForDeletion(patientId);

        labResultsForPatient = allLabResults.findLatestLabResultsByPatientId(patientId);

        assertEquals(1, labResultsForPatient.size());
        assertLabResult("2", labResultsForPatient.get(0));
    }

    private void assertLabResult(String result, LabResult labResult) {
        assertEquals(result, labResult.getResult());
        assertNotNull(labResult.getRevision());
        assertNotNull(labResult.getLabTest());
    }

    private void markLabResultsForDeletion(String patientId) {
        LabResults labResults = allLabResults.findByPatientId(patientId);
        for(LabResult labResult: labResults)
            markForDeletion(labResult);
    }
}
