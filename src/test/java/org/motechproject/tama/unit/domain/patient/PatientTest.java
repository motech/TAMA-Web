package org.motechproject.tama.unit.domain.patient;

import junit.framework.Assert;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.Patient;
import org.motechproject.tama.builders.PatientBuilder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Calendar;
import java.util.Set;

public class PatientTest {

    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setup() {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void testNotNullConstraintFieldsOnPatient() {
        Patient nullPatient = PatientBuilder.startRecording().build();
        Set<ConstraintViolation<org.motechproject.tama.Patient>> constraintViolations = localValidatorFactory.validate(nullPatient);
        Assert.assertEquals(4, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "patientId", "may not be null");
        assertConstraintViolation(constraintViolations, "dateOfBirth", "may not be null");
        assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "may not be null");
        assertConstraintViolation(constraintViolations, "passcode", "may not be null");
    }

    @Test
    public void testMobilePhoneNumberToMatchASpecificCriteria() {
        String invalidPhoneNumber = "+001111111111";
        Patient patient = PatientBuilder.startRecording().withDefaults().withMobileNumber(invalidPhoneNumber).build();
        Set<ConstraintViolation<Patient>> constraintViolations = localValidatorFactory.validate(patient);
        Assert.assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "Mobile Phone Number should start with +91 and should be 10 digits long.");
    }

    @Test
    public void testPasscodeToMatchASpecificCriteria() {
        String invalidPasscode = "111";
        Patient patient = PatientBuilder.startRecording().withDefaults().withPasscode(invalidPasscode).build();
        Set<ConstraintViolation<Patient>> constraintViolations = localValidatorFactory.validate(patient);
        Assert.assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "passcode", "Passcode should be numeric and 4-10 digits long.");
    }

    @Test
    public void testDateOfBirthToBeAPastDate() {
        Calendar futureDate = Calendar.getInstance();
        futureDate.set(2020, 1, 20);
        Patient patient = PatientBuilder.startRecording().withDefaults().withDateOfBirth(futureDate.getTime()).build();
        Set<ConstraintViolation<Patient>> constraintViolations = localValidatorFactory.validate(patient);
        Assert.assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "dateOfBirth", "Date Of Birth must be in the past.");
    }

    @Test
    public void testPatientWithNoConstraintViolation() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        Set<ConstraintViolation<Patient>> constraintViolations = localValidatorFactory.validate(patient);
        Assert.assertTrue(constraintViolations.isEmpty());
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
