package org.motechproject.tama.domain;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.LabResultBuilder;
import org.motechproject.util.DateUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

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

    private void assertConstraintViolation(Set<ConstraintViolation<LabResult>> constraintViolations, String property, String message) {

        for (ConstraintViolation<LabResult> labResultViolation : constraintViolations) {
            if (labResultViolation.getPropertyPath().toString().equals(property) && labResultViolation.getMessage().equals(message)) {
                return;
            }
        }
        Assert.fail("could not find expected violation for property " + property);
    }
}
