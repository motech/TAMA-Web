package org.motechproject.tama.domain;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.motechproject.tama.testutil.ValidationUtil.assertConstraintViolation;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
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
    public void shouldTestDeactivationOfPatient() {
        Patient patient = PatientBuilder.startRecording().withStatus(Patient.Status.Active).build();
        patient.deactivate();
        assertTrue(patient.getStatus().equals(Patient.Status.Inactive));
    }

    @Test
    public void shouldGetIVRMobilePhoneNumber() {
        Patient patient = PatientBuilder.startRecording().withMobileNumber("9876543210").build();
        assertEquals("09876543210", patient.getIVRMobilePhoneNumber());
    }

    @Test
    public void shouldReturnUniqueId() {
        Clinic clinic = new Clinic("C1");
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("P1").withClinic(clinic).withMobileNumber("1234567890").withPasscode("1234").build();
        List<String> uniqueFields = patient.uniqueFields();

        assertEquals(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT + "C1/P1", uniqueFields.get(0));
        assertEquals(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT + "1234567890/1234", uniqueFields.get(1));
    }

    @Test
    public void shouldReturnCorrectAgeOfPatient() {
        PowerMockito.mockStatic(DateUtil.class);
        LocalDate dateOfBirth = new LocalDate(2000, 10, 1);
        Patient patient = PatientBuilder.startRecording().withDateOfBirth(dateOfBirth).build();

        when(DateUtil.today()).thenReturn(new LocalDate(2011, 10, 19));
        when(DateUtil.newDate(any(Date.class))).thenReturn(dateOfBirth);
        assertEquals(11, patient.getAge());

        when(DateUtil.today()).thenReturn(new LocalDate(2011, 10, 1));
        when(DateUtil.newDate(any(Date.class))).thenReturn(dateOfBirth);
        assertEquals(11, patient.getAge());

        when(DateUtil.today()).thenReturn(new LocalDate(2011, 9, 30));
        when(DateUtil.newDate(any(Date.class))).thenReturn(dateOfBirth);
        assertEquals(10, patient.getAge());
    }
}
