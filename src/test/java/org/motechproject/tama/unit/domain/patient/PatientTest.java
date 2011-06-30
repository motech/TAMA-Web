package org.motechproject.tama.unit.domain.patient;

import java.util.Calendar;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;

public class PatientTest {

	private Validator validator;

    @Before
    public void setup() {
    	ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    	validator = validatorFactory.getValidator();
    }

    @Test
    public void testNotNullConstraintFieldsOnPatient() {
        Patient nullPatient = PatientBuilder.startRecording().build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(nullPatient);
        Assert.assertEquals(4, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "patientId", "may not be null");
        assertConstraintViolation(constraintViolations, "dateOfBirth", "may not be null");
        assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "may not be null");
        assertConstraintViolation(constraintViolations, "passcode", "may not be null");
    }

    @Test
    public void testMobilePhoneNumberToMatchASpecificCriteria() {
        String invalidPhoneNumber = "2222";
        Patient patient = PatientBuilder.startRecording().withDefaults().withMobileNumber(invalidPhoneNumber).build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        Assert.assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "Mobile Phone Number should be numeric and 10 digits long.");
    }

    @Test
    public void testPasscodeToMatchASpecificCriteria() {
        String invalidPasscode = "111";
        Patient patient = PatientBuilder.startRecording().withDefaults().withPasscode(invalidPasscode).build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        System.out.println("****** " + constraintViolations + " *******");
        Assert.assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "passcode", "Passcode should be numeric and 4-10 digits long.");
    }

    @Test
    public void testDateOfBirthToBeAPastDate() {
        Calendar futureDate = Calendar.getInstance();
        futureDate.set(2020, 1, 20);
        Patient patient = PatientBuilder.startRecording().withDefaults().withDateOfBirth(futureDate.getTime()).build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        Assert.assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "dateOfBirth", "Date Of Birth must be in the past.");
    }

    @Test
    public void testPatientWithNoConstraintViolation() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        Assert.assertTrue(constraintViolations == null || constraintViolations.isEmpty());
    }

    private void assertConstraintViolation(Set<ConstraintViolation<Patient>> constraintViolations, String property, String message) {
        Object[] violations = constraintViolations.toArray();
        for (int i = 0; i < violations.length; i++) {
            ConstraintViolation<Patient> patientViolation = (ConstraintViolation<Patient>) violations[i];
            if (patientViolation.getPropertyPath().toString().equals(property) && patientViolation.getMessage().equals(message)) {
                return;
            }
        }
        Assert.fail("could not find expected violation for property " + property);
    }

}
