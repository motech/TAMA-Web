package org.motechproject.tama.patient.integration.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
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
        markForDeletion(allLabResults.getAll().toArray());
        markForDeletion(allLabTests.getAll().toArray());
    }

    @Test
    public void testGetShouldLoadLabTest() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTestId("someLabTestId").build();
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

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTestId(labTest1.getId()).withPatientId(patientId).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTestId(labTest2.getId()).withPatientId(patientId).build();
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
    public void shouldReturnLatestLabResultsForPatientGivenPatientId() {
        String patientId = "somePatientId_byPatient";

        LabTest labTest1 = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId3_byPatient").build();
        allLabTests.add(labTest1);
        markForDeletion(labTest1);

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withResult("1").withTestDate(DateUtil.today().minusDays(1)).withLabTestId(labTest1.getId()).withPatientId(patientId).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withResult("2").withTestDate(DateUtil.today()).withLabTestId(labTest1.getId()).withPatientId(patientId).build();
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
    public void shouldNotSaveLabResultsWhenResultIsEmptyForANewRecord() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withResult(null).build();
        assertEquals(allLabResults.upsert(labResult), null);
    }

    @Test
    public void shouldAddLabResultsWhenResultIsNotEmptyForANewRecord() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTestId(allLabTests.getAll().get(0).getId()).withResult("1").build();
        allLabResults.upsert(labResult);
        final LabResult savedLabResult = allLabResults.get(labResult.getId());
        assertEquals("1", savedLabResult.getResult());
    }

    @Test
    public void shouldUpdateLabResultsWhenResultIsNotEmptyForAnExistingRecord() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTestId(allLabTests.getAll().get(0).getId()).withResult("1").build();
        allLabResults.add(labResult);
        labResult.setResult("100");
        allLabResults.upsert(labResult);
        final LabResult savedLabResult = allLabResults.get(labResult.getId());
        assertEquals("100", savedLabResult.getResult());
    }

    @Test(expected = DocumentNotFoundException.class)
    public void shouldRemoveLabResultsWhenResultIsEmptyEmptyForAnExistingRecord() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTestId(allLabTests.getAll().get(0).getId()).withResult("1").build();
        allLabResults.add(labResult);
        labResult.setResult(null);
        allLabResults.upsert(labResult);
        allLabResults.get(labResult.getId());
    }

    @Test
    public void upsertShouldCreateNewLabResults_WhenItDoesNotExistInDb() {
        String patientId = "patientId_merge";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId_merge3").build();
        allLabTests.add(labTest);
        markForDeletion(labTest);

        LabResults labResultsForPatient = allLabResults.findLatestLabResultsByPatientId(patientId);

        assertEquals(0, labResultsForPatient.size());

        LabResult labResultFromUI = LabResultBuilder.startRecording().withDefaults().withLabTestId(labTest.getId()).withTestDate(DateUtil.today()).withResult("2").withPatientId(patientId).build();

        allLabResults.upsert(labResultFromUI);

        labResultsForPatient = allLabResults.findLatestLabResultsByPatientId(patientId);

        assertEquals(1, labResultsForPatient.size());
        assertLabResult("2", labResultsForPatient.get(0));
    }

    private void assertLabResult(String result, LabResult labResult) {
        assertEquals(result, labResult.getResult());
        assertNotNull(labResult.getRevision());
        assertNotNull(labResult.getLabTest());
    }
}
