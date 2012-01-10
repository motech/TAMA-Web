package org.motechproject.tama.patient.domain;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.util.ValidationUtil;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PatientTest extends BaseUnitTest {

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
        ValidationUtil.assertConstraintViolation(constraintViolations, "dateOfBirthAsDate", "may not be null");
        ValidationUtil.assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "may not be null");
        ValidationUtil.assertConstraintViolation(constraintViolations, "patientPreferences.passcode", "may not be null");
    }

    @Test
    public void testMobilePhoneNumberToMatchASpecificCriteria() {
        String invalidPhoneNumber = "2222";
        Patient patient = PatientBuilder.startRecording().withDefaults().withMobileNumber(invalidPhoneNumber).build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        assertEquals(1, constraintViolations.size());
        ValidationUtil.assertConstraintViolation(constraintViolations, "mobilePhoneNumber", "Mobile Phone Number should be numeric and 10 digits long.");
    }

    @Test
    public void testPasscodeToMatchASpecificCriteria() {
        String invalidPasscode = "11111";
        Patient patient = PatientBuilder.startRecording().withDefaults().withPasscode(invalidPasscode).build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        assertEquals(1, constraintViolations.size());
        ValidationUtil.assertConstraintViolation(constraintViolations, "patientPreferences.passcode", "Passcode should be numeric and 4 digits long.");
    }

    @Test
    public void testDateOfBirthToBeAPastDate() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withDateOfBirth(DateUtil.newDate(2020, 1, 20)).build();
        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        assertEquals(1, constraintViolations.size());
        ValidationUtil.assertConstraintViolation(constraintViolations, "dateOfBirthAsDate", "Date Of Birth must be in the past.");
    }

    @Test
    public void testPatientWithNoConstraintViolation() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        Set<ConstraintViolation<Patient>> constraintViolations = validator.validate(patient);
        Assert.assertTrue(constraintViolations == null || constraintViolations.isEmpty());
    }

    @Test
    public void shouldTestIfOutboxCallsAreAllowedForPatient() {
        Patient activePatient = PatientBuilder.startRecording().withStatus(Status.Active).build();
        Assert.assertTrue(activePatient.allowOutboxCalls());

        Patient suspendedPatient = PatientBuilder.startRecording().withStatus(Status.Suspended).build();
        Assert.assertTrue(suspendedPatient.allowOutboxCalls());
    }

    @Test
    public void shouldTestIfIncomingCallsAreAllowedForPatient() {
        Patient activePatient = PatientBuilder.startRecording().withStatus(Status.Active).build();
        Assert.assertTrue(activePatient.allowIncomingCalls());

        Patient suspendedPatient = PatientBuilder.startRecording().withStatus(Status.Suspended).build();
        Assert.assertTrue(suspendedPatient.allowIncomingCalls());
    }

    @Test
    public void shouldTestIfAdherenceCallsAreAllowedForPatient() {
        Patient activePatient = PatientBuilder.startRecording().withStatus(Status.Active).build();
        Assert.assertTrue(activePatient.allowAdherenceCalls());

        Patient suspendedPatient = PatientBuilder.startRecording().withStatus(Status.Suspended).build();
        Assert.assertFalse(suspendedPatient.allowAdherenceCalls());
    }

    @Test
    public void shouldTestTheFirstDeactivationStatusOfPatient() {
        assertEquals(Status.Temporary_Deactivation, Status.deactivationStatuses().get(0));
    }

    @Test
    public void shouldActivatePatient() {
        DateTime now = DateUtil.newDateTime(new LocalDate(2011, 10, 19), 9, 0, 0);
        mockCurrentDate(now);
        Patient patient = PatientBuilder.startRecording().withStatus(Status.Inactive).build();
        patient.activate();
        assertTrue(patient.getStatus().equals(Status.Active));
        assertTrue(patient.getActivationDate().equals(now));
    }

    @Test
    public void shouldNotResetActivationDateForSubsequentActivation() {
        DateTime activationDate = DateUtil.newDateTime(new LocalDate(2011, 10, 19), 9, 0, 0);
        Patient patient = PatientBuilder.startRecording().withStatus(Status.Inactive).withActivationDate(activationDate).build();
        patient.activate();
        assertTrue(patient.getStatus().equals(Status.Active));
        assertTrue(patient.getActivationDate().equals(activationDate));
    }

    @Test
    public void shouldDeactivatePatient() {
        Patient patient = PatientBuilder.startRecording().withStatus(Status.Active).build();
        patient.deactivate(Status.Inactive);
        assertTrue(patient.getStatus().equals(Status.Inactive));
        assertEquals(DateUtil.today(), patient.getLastDeactivationDate().toLocalDate());
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
    public void canTransitionToWeeklyAfterFourWeeksAfterActivation() {
        final DateTime now = DateTime.now();
        Patient patient = PatientBuilder.startRecording().withDefaults().withActivationDate(now.minusDays(1)).withCallPreference(CallPreference.DailyPillReminder).build();
        assertFalse(patient.canTransitionToWeekly(28));

        patient = PatientBuilder.startRecording().withDefaults().withActivationDate(now.minusDays(1)).withCallPreference(CallPreference.FourDayRecall).build();
        assertFalse(patient.canTransitionToWeekly(28));

        patient = PatientBuilder.startRecording().withDefaults().withActivationDate(now.minusDays(28)).withCallPreference(CallPreference.FourDayRecall).build();
        assertFalse(patient.canTransitionToWeekly(28));

        patient = PatientBuilder.startRecording().withDefaults().withActivationDate(now.minusDays(28)).withCallPreference(CallPreference.DailyPillReminder).build();
        assertTrue(patient.canTransitionToWeekly(28));
    }

    @Test
    public void cannotTransitionToWeeklyIfPatientIsNotActivated() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withActivationDate(null).withCallPreference(CallPreference.DailyPillReminder).build();
        assertFalse(patient.canTransitionToWeekly(28));
    }

    @Test
    public void shouldReturnCorrectAgeOfPatient() {
        LocalDate dateOfBirth = new LocalDate(2000, 10, 1);
        Patient patient = PatientBuilder.startRecording().withDateOfBirth(dateOfBirth).build();

        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 10, 19), 9, 0, 0));
        assertEquals(11, patient.getAge());

        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 10, 1), 9, 0, 0));
        assertEquals(11, patient.getAge());

        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 9, 30), 9, 0, 0));
        assertEquals(10, patient.getAge());
    }
}
