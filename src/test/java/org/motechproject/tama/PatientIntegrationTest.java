package org.motechproject.tama;

import junit.framework.Assert;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@RooIntegrationTest(entity = Patient.class)
public class PatientIntegrationTest {

    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setup() {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void testNotNullConstraintFieldsOnPatient() {
        PatientStub nullPatient = new PatientStub();
        Set<ConstraintViolation<PatientStub>> constraintViolations = localValidatorFactory.validate(nullPatient);
        Assert.assertEquals(3, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "patientId", "may not be null");
        assertConstraintViolation(constraintViolations, "dateOfBirth", "may not be null");
        assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "may not be null");
    }

    @Test
    public void testMobilePhoneNumberToMatchASpecificCriteria() {
        PatientStub patient = new PatientStub();
        patient.setPatientId("1234");
        patient.setDateOfBirth(new Date());
        String invalidPhoneNumber = "+001111111111";
        patient.setMobilePhoneNumber(invalidPhoneNumber);

        Set<ConstraintViolation<PatientStub>> constraintViolations = localValidatorFactory.validate(patient);
        Assert.assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "Mobile Phone Number should start with +91 and should be 10 digits long.");
    }

    @Test
    public void testDateOfBirthToBeAPastDate() {
        PatientStub patient = new PatientStub();
        patient.setPatientId("1234");
        patient.setMobilePhoneNumber("+911234567890");
        Calendar futureDate = Calendar.getInstance();
        futureDate.set(2020, 1, 20);
        patient.setDateOfBirth(futureDate.getTime());

        Set<ConstraintViolation<PatientStub>> constraintViolations = localValidatorFactory.validate(patient);
        Assert.assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "dateOfBirth", "Date Of Birth must be in the past.");
    }

    @Test
    public void testPatientWithNoConstraintViolation() {
        PatientStub patient = new PatientStub();
        patient.setPatientId("1234");
        patient.setMobilePhoneNumber("+911234567890");
        patient.setDateOfBirth(new Date());

        Set<ConstraintViolation<PatientStub>> constraintViolations = localValidatorFactory.validate(patient);
        Assert.assertTrue(constraintViolations.isEmpty());
    }

    private void assertConstraintViolation(Set<ConstraintViolation<PatientStub>> constraintViolations, String property, String message) {
        Object[] violations = constraintViolations.toArray();
        for (int i=0; i< violations.length; i ++) {
            ConstraintViolation<PatientStub> patientViolation = (ConstraintViolation<PatientStub>) violations[i];
            if (patientViolation.getPropertyPath().toString().equals(property) && patientViolation.getMessage().equals(message)) {
                return;
            }
        }
        Assert.fail("could not find expected violation for property " + property);
    }

    class PatientStub extends Patient{
        public void setPatientId(String patientId) {
            super.patientId = patientId;
        }

        public void setMobilePhoneNumber(String mobilePhoneNumber) {
            super.mobilePhoneNumber = mobilePhoneNumber;
        }

        public void setDateOfBirth(Date dateOfBirth) {
            super.dateOfBirth = dateOfBirth;
        }
    }
}
