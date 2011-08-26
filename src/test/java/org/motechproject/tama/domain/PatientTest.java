package org.motechproject.tama.domain;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.util.DateUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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
        assertConstraintViolation(constraintViolations, "dateOfBirthAsDate", "may not be null");
        assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "may not be null");
        assertConstraintViolation(constraintViolations, "patientPreferences.passcode", "may not be null");
    }

    @Test
    public void testMobilePhoneNumberToMatchASpecificCriteria() {
        String invalidPhoneNumber = "2222";
        Patient patient = PatientBuilder.startRecording().withDefaults().withMobileNumber(invalidPhoneNumber).build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "Mobile Phone Number should be numeric and 10 digits long.");
    }

    @Test
    public void testPasscodeToMatchASpecificCriteria() {
        String invalidPasscode = "111";
        Patient patient = PatientBuilder.startRecording().withDefaults().withPasscode(invalidPasscode).build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "patientPreferences.passcode", "Passcode should be numeric and 4-10 digits long.");
    }

    @Test
    public void testDateOfBirthToBeAPastDate() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withDateOfBirth(DateUtil.newDate(2020, 1, 20)).build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        assertEquals(1, constraintViolations.size());
        assertConstraintViolation(constraintViolations, "dateOfBirthAsDate", "Date Of Birth must be in the past.");
    }

    @Test
    public void testPatientWithNoConstraintViolation() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        Assert.assertTrue(constraintViolations == null || constraintViolations.isEmpty());
    }

    @Test
    public void shouldTestIfPatientIsActive() {
        Patient patient = PatientBuilder.startRecording().withStatus(Patient.Status.Active).build();
        Assert.assertTrue(patient.isActive());
    }

    @Test
    public void shouldTestActivationOfPatient() {
        Patient patient = PatientBuilder.startRecording().withStatus(Patient.Status.Inactive).build();
        patient.activate();
        assertTrue(patient.getStatus().equals(Patient.Status.Active));
    }

    @Test
    public void shouldGetIVRMobilePhoneNumber(){
        Patient patient = PatientBuilder.startRecording().withMobileNumber("9876543210").build();
        assertEquals("09876543210", patient.getIVRMobilePhoneNumber());
    }

    @Test
    public void shouldReturnUniqueId(){
        Clinic clinic = new Clinic("C1");
        Patient patient = PatientBuilder.startRecording().withClinic(clinic).withPatientId("P1").build();
        assertEquals("C1_P1",patient.uniqueId());
    }

    private void assertConstraintViolation(Set<ConstraintViolation<Patient>> constraintViolations, String property, String message) {

        for (ConstraintViolation<Patient> patientViolation : constraintViolations) {
            if (patientViolation.getPropertyPath().toString().equals(property) && patientViolation.getMessage().equals(message)) {
                return;
            }
        }
        Assert.fail("could not find expected violation for property " + property);
    }
}
