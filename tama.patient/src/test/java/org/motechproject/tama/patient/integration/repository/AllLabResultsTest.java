package org.motechproject.tama.patient.integration.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.common.repository.AllAuditRecords;
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

    public static final String USER_NAME = "userName";
    @Autowired
    private AllLabResults allLabResults;

    @Qualifier("tamaDbConnector")
    @Autowired
    private CouchDbConnector couchDbConnector;

    @Autowired
    private AllLabTests allLabTests;

    @Autowired
    private AllAuditRecords allAuditRecords;

    LabTest cd4LabTest;

    @Before
    public void setUp() {
        cd4LabTest = LabTestBuilder.startRecording().withDefaults().withId("someLabTestId").build();
        allLabTests.add(cd4LabTest);

        allLabResults = new AllLabResults(couchDbConnector, allLabTests, allAuditRecords);
    }

    @After
    public void tearDown() {
        markForDeletion(allLabResults.getAll().toArray());
        markForDeletion(allLabTests.getAll().toArray());
    }

    @Test
    public void testGetShouldLoadLabTest() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTest(cd4LabTest).build();
        allLabResults.add(labResult, USER_NAME);
        markForDeletion(labResult);

        LabResult result = allLabResults.get(labResult.getId());
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

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTest(labTest1).withPatientId(patientId).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTest(labTest2).withPatientId(patientId).build();
        allLabResults.add(labResult1, USER_NAME);
        markForDeletion(labResult1);
        allLabResults.add(labResult2, USER_NAME);
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

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withResult("1").withTestDate(DateUtil.today().minusDays(1)).withLabTest(labTest1).withPatientId(patientId).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withResult("2").withTestDate(DateUtil.today()).withLabTest(labTest1).withPatientId(patientId).build();
        allLabResults.add(labResult1, USER_NAME);
        markForDeletion(labResult1);
        allLabResults.add(labResult2, USER_NAME);
        markForDeletion(labResult2);

        LabResults results = allLabResults.findLatestLabResultsByPatientId(patientId);

        assertEquals(1, results.size());

        assertEquals("2", results.get(0).getResult());
        assertEquals(DateUtil.today(), results.get(0).getTestDate());
    }

    @Test
    public void shouldNotSaveLabResultsWhenResultIsEmptyForANewRecord() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withResult(null).build();
        assertEquals(allLabResults.upsert(labResult, USER_NAME), null);
    }

    @Test
    public void shouldAddLabResultsWhenResultIsNotEmptyForANewRecord() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTest(cd4LabTest).withResult("1").build();
        allLabResults.upsert(labResult, USER_NAME);
        final LabResult savedLabResult = allLabResults.get(labResult.getId());
        assertEquals("1", savedLabResult.getResult());
    }

    @Test
    public void shouldUpdateLabResultsWhenResultIsNotEmptyForAnExistingRecord() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTest(cd4LabTest).withResult("1").build();
        allLabResults.add(labResult, USER_NAME);
        labResult.setResult("100");
        allLabResults.upsert(labResult, USER_NAME);
        final LabResult savedLabResult = allLabResults.get(labResult.getId());
        assertEquals("100", savedLabResult.getResult());
    }

    @Test(expected = DocumentNotFoundException.class)
    public void shouldRemoveLabResultsWhenResultIsEmptyEmptyForAnExistingRecord() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTest(cd4LabTest).withResult("1").build();
        allLabResults.add(labResult, USER_NAME);
        labResult.setResult(null);
        allLabResults.upsert(labResult, USER_NAME);
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

        LabResult labResultFromUI = LabResultBuilder.startRecording().withDefaults().withLabTest(labTest).withTestDate(DateUtil.today()).withResult("2").withPatientId(patientId).build();

        allLabResults.upsert(labResultFromUI, USER_NAME);

        labResultsForPatient = allLabResults.findLatestLabResultsByPatientId(patientId);

        assertEquals(1, labResultsForPatient.size());
        assertLabResult("2", labResultsForPatient.get(0));
    }

    @Test
    public void shouldGetListOfCD4LabResultsForPatient() throws Exception {
        String patientId = "patientId";
        String resultId = allLabResults.upsert(LabResultBuilder.startRecording().withDefaults().withPatientId(patientId).withLabTest(cd4LabTest).build(), USER_NAME);
        markForDeletion(allLabResults.get(resultId));
        int rangeInMonths = 3;

        List<LabResult> labResults = allLabResults.findCD4LabResultsFor(patientId, rangeInMonths);
        assertCD4LabResult(labResults.get(0));
    }

    @Test
    public void shouldNotReturnLabResultsOlderThanSpecifiedPeriod() throws Exception {
        String patientId = "patientId";
        final LocalDate today = DateUtil.today();
        String resultId = allLabResults.upsert(LabResultBuilder.defaultCD4Result().withPatientId(patientId).withLabTest(cd4LabTest).
                withTestDate(today.minusMonths(4)).build(), USER_NAME);
        String resultId2 = allLabResults.upsert(LabResultBuilder.defaultCD4Result().withPatientId(patientId).withLabTest(cd4LabTest).
                withTestDate(today.minusMonths(1)).build(), USER_NAME);
        markForDeletion(allLabResults.get(resultId));
        markForDeletion(allLabResults.get(resultId2));
        int rangeInMonths = 3;
        List<LabResult> labResults = allLabResults.findCD4LabResultsFor(patientId, rangeInMonths);
        assertEquals(1, labResults.size());
        assertEquals(resultId2, labResults.get(0).getId());
    }

    @Test
    public void shouldGetListOfPVLLabResultsForPatient() throws Exception {
        String patientId = "patientId";
        LabTest pvlLabTest = LabTestBuilder.startRecording().withDefaults().withType(TAMAConstants.LabTestType.PVL).withId("pvlTestId").build();
        allLabTests.add(pvlLabTest);
        String resultId = allLabResults.upsert(LabResultBuilder.startRecording().withDefaults().withPatientId(patientId).withLabTest(pvlLabTest).build(), USER_NAME);
        markForDeletion(allLabResults.get(resultId));
        int rangeInMonths = 3;

        List<LabResult> labResults = allLabResults.findPVLLabResultsFor(patientId, rangeInMonths);

        assertEquals(1, labResults.size());
        assertEquals(TAMAConstants.LabTestType.PVL.getName(), labResults.get(0).getLabTest().getName());
    }

    private void assertCD4LabResult(LabResult labResult) {
        assertEquals(TAMAConstants.LabTestType.CD4.getName(), labResult.getLabTest().getName());
    }

    private void assertLabResult(String result, LabResult labResult) {
        assertEquals(result, labResult.getResult());
        assertNotNull(labResult.getRevision());
        assertNotNull(labResult.getLabTest());
    }
}
