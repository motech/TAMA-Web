package org.motechproject.tama.patient.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.domain.LabTest;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class LabResultsTest {

    LabResults labResults;

    @Before
    public void setUp() {
        labResults = new LabResults();
    }

    @Test
    public void shouldSortLabResultsBasedOnDateAndReturnLatestCD4Count() {
        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTestId(labTestId).withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        labResult1.setLabTest(labTest);
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTestId(labTestId).withTestDate(new LocalDate(2011, 10, 20)).withResult("50").build();
        labResult2.setLabTest(labTest);
        LabResult labResult3 = LabResultBuilder.startRecording().withDefaults().withLabTestId(labTestId).withTestDate(new LocalDate(2011, 9, 20)).withResult("70").build();
        labResult3.setLabTest(labTest);

        labResults.add(labResult1);
        labResults.add(labResult2);
        labResults.add(labResult3);

        assertEquals(50, labResults.latestCD4Count());
    }

    @Test
    public void shouldReturnCD4CountOfTheOnlyResultAsBaselineCD4Count() {
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("labTestId").build();
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTestId("labTestId").withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        labResult.setLabTest(labTest);

        labResults.add(labResult);
        assertEquals(60, labResults.baselineCD4Count());
    }

    @Test
    public void shouldReturnUpdatedCD4OfLabResultAsBaselineCD4Count() {
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("labTestId").build();
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTestId("labTestId").withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        labResult.setLabTest(labTest);
        labResult.setResult("90");

        labResults.add(labResult);
        assertEquals(90, labResults.baselineCD4Count());
    }

    @Test
    public void shouldReturnInvalidCD4CountWhenOnlyResultIsNotCD4Result() {
        LabTest labTest = LabTestBuilder.defaultPVL().withId("labTestId").build();
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTestId("labTestId").withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        labResult.setLabTest(labTest);

        labResults.add(labResult);
        assertEquals(LabResult.INVALID_CD4_COUNT, labResults.baselineCD4Count());
    }

    @Test
    public void shouldReturnResultOfCD4TestIgnoringOtherTestResults() {
        LabTest pvlTest = LabTestBuilder.defaultPVL().withId("labTestId").build();
        LabResult pvlResult = LabResultBuilder.startRecording().withDefaults().withLabTestId("labTestId").withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        pvlResult.setLabTest(pvlTest);

        LabTest cd4Test = LabTestBuilder.defaultCD4().withId("labTestId1").build();
        LabResult cd4Result = LabResultBuilder.startRecording().withDefaults().withLabTestId("labTestId1").withTestDate(new LocalDate(2011, 6, 20)).withResult("70").build();
        cd4Result.setLabTest(cd4Test);

        labResults.addAll(asList(pvlResult, cd4Result));
        assertEquals(70, labResults.baselineCD4Count());
    }

    @Test
    public void shouldReturnEarlierCD4ResultAsBaselineCD4Count() {
        LabTest earlierTest = LabTestBuilder.defaultCD4().withId("labTestId").build();
        LabResult earlierResult = LabResultBuilder.startRecording().withDefaults().withLabTestId("labTestId").withTestDate(new LocalDate(2011, 6, 19)).withResult("60").build();
        earlierResult.setLabTest(earlierTest);

        LabTest laterTest = LabTestBuilder.defaultCD4().withId("labTestId1").build();
        LabResult laterResult = LabResultBuilder.startRecording().withDefaults().withLabTestId("labTestId1").withTestDate(new LocalDate(2011, 6, 20)).withResult("70").build();
        laterResult.setLabTest(laterTest);

        labResults.addAll(asList(laterResult, earlierResult));
        assertEquals(60, labResults.baselineCD4Count());
    }

    @Test
    public void shouldReturn_INVALID_CD4_COUNT_WhenNoLabResults() {
        assertEquals(-1, labResults.latestCD4Count());
    }
}
