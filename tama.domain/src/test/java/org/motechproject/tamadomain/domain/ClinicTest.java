package org.motechproject.tamadomain.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamadomain.testutil.ValidationUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ClinicTest {

    private Validator validator;

    @Before
    public void setUp(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void validClinicCannotHaveEmptyClinicianContacts() {
        Clinic clinic = new Clinic();
        Set<ConstraintViolation<Clinic>> constraintViolations = validator.validate(clinic);
        ValidationUtil.assertConstraintViolation(constraintViolations, "clinicianContacts", "Please enter the contact details for at least one clinician");
    }

    @Test
    public void clinicianContactWithoutANameIsInvalid() {
        Clinic.ClinicianContact contact = new Clinic.ClinicianContact();
        Set<ConstraintViolation<Clinic.ClinicianContact>> constraintViolations = validator.validate(contact);
        ValidationUtil.assertConstraintViolation(constraintViolations, "name", "Clinician name is mandatory");
    }

    @Test
    public void clinicianContactWithoutANumberIsInvalid() {
        Clinic.ClinicianContact contact = new Clinic.ClinicianContact();
        Set<ConstraintViolation<Clinic.ClinicianContact>> constraintViolations = validator.validate(contact);
        ValidationUtil.assertConstraintViolation(constraintViolations, "phoneNumber", "Phone number is mandatory");
    }

    @Test
    public void clinicianContactWithAnInvalidNumberIsInvalid() {
        Clinic.ClinicianContact contact = new Clinic.ClinicianContact();
        contact.setPhoneNumber("abcde");
        Set<ConstraintViolation<Clinic.ClinicianContact>> constraintViolations = validator.validate(contact);
        ValidationUtil.assertConstraintViolation(constraintViolations, "phoneNumber", "Mobile Phone Number should be numeric and 10 digits long.");
    }
}
