package org.motechproject.tamadomain.domain;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.builder.LabResultBuilder;
import org.motechproject.tamadomain.builder.LabTestBuilder;
import org.motechproject.util.DateUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collections;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class LabResultTest {

    private Validator validator;

    @Before
    public void setup() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void testNotNullConstraintFieldsOnLabResult() {
        LabResult nullLabResult = new LabResult();

        Set<ConstraintViolation<LabResult>> constraintViolations = validator.validate(nullLabResult);
        assertConstraintViolation(constraintViolations, "patientId", "may not be null");
        assertConstraintViolation(constraintViolations, "result", "may not be null");
        assertConstraintViolation(constraintViolations, "testDateAsDate", "Test date must not be empty");
        assertConstraintViolation(constraintViolations, "labTest_id", "may not be null");
    }

    @Test
    public void testTestDateToBeAPastDate() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withTestDate(DateUtil.today().plusYears(1)).build();
        Set<ConstraintViolation<LabResult>> constraintViolations = validator.validate(labResult);
        assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "testDateAsDate", "Test date must be in the past.");
    }

    @Test
    public void shouldSortLabResultsInDescendingOrderBasedOnTestDate() {
        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withResult("60").withTestDate(DateUtil.newDate(2011, 8, 10)).build();
        labResult1.setLabTest(labTest);
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withResult("50").withTestDate(DateUtil.newDate(2011, 10, 10)).build();
        labResult2.setLabTest(labTest);
        LabResult labResult3 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withResult("70").withTestDate(DateUtil.newDate(2011, 9, 10)).build();
        labResult3.setLabTest(labTest);

        LabResults labResults = new LabResults();
        labResults.add(labResult1);
        labResults.add(labResult2);
        labResults.add(labResult3);

        Collections.sort(labResults, new LabResult.LabResultComparator());
        assertEquals(labResult2, labResults.get(0));
        assertEquals(labResult3, labResults.get(1));
        assertEquals(labResult1, labResults.get(2));
    }

    @Test
    public void shouldReturnTrueForCD4LabResult() {
        String labTestId = "labTestId1";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).withType(TAMAConstants.LabTestType.CD4).build();

        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTest_id("labTestId1").withResult("60").withTestDate(DateUtil.newDate(2011, 8, 10)).build();
        labResult.setLabTest(labTest);

        assertTrue(labResult.isCD4());
    }

    @Test
    public void shouldReturnFalseForNonCD4LabResult() {
        String labTestId = "labTestId1";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).withType(TAMAConstants.LabTestType.PVL).build();

        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTest_id("labTestId1").withResult("60").withTestDate(DateUtil.newDate(2011, 8, 10)).build();
        labResult.setLabTest(labTest);

        assertFalse(labResult.isCD4());
    }

    @Test
    public void shouldReturnFalseForCD4LabResultWhenNoLabTestSet() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withLabTest_id("labTestId1").withResult("60").withTestDate(DateUtil.newDate(2011, 8, 10)).build();

        assertFalse(labResult.isCD4());
    }

    private void assertConstraintViolation(Set<ConstraintViolation<LabResult>> constraintViolations, String property, String message) {

        for (ConstraintViolation<LabResult> labResultViolation : constraintViolations) {
            if (labResultViolation.getPropertyPath().toString().equals(property) && labResultViolation.getMessage().equals(message)) {
                return;
            }
        }
        Assert.fail("could not find expected violation for property " + property);
    }
}
