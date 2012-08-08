package org.motechproject.tama.patient.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.domain.LabTest;

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
    public void shouldReturnFirstResultAsBaselineWhenOnlyOneResultIsCaptured() {
        testThatCd4CountIs(60);
        testThatCd4CountIs(80);
    }

    @Test
    public void shouldReturnTheLatestCD4CountWhenMoreThanOneResultIsCaptured() {
        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTestId(labTestId).withTestDate(new LocalDate(2011, 10, 20)).withResult("50").build();
        labResult1.setLabTest(labTest);
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTestId(labTestId).withTestDate(new LocalDate(2011, 9, 20)).withResult("70").build();
        labResult2.setLabTest(labTest);

        labResults.add(labResult1);
        labResults.add(labResult2);

        assertEquals(70, labResults.getBaseLineCD4Count());
    }

    @Test
    public void shouldReturn_INVALID_CD4_COUNT_WhenNoLabResults() {
        assertEquals(-1, labResults.latestCD4Count());
    }

    private void testThatCd4CountIs(int cd4Count) {
        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();

        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTestId(labTestId).withTestDate(new LocalDate(2011, 6, 20)).withResult(cd4Count + "").build();
        labResult.setLabTest(labTest);

        LabResults results = new LabResults();
        results.add(labResult);

        assertEquals(cd4Count, results.getBaseLineCD4Count());
    }
}
