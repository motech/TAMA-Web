package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.repository.Clinics;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicianControllerTest {
    private ClinicianController controller;
    @Mock
    private Clinicians clinicians;
    @Mock
    private Clinics clinics;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private Model uiModel;
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new ClinicianController(clinicians, clinics);
    }

    @Test
    public void shouldNotCreateAClinicianIfUserNameIsNotUnique() {
        Clinician clinician = new Clinician();
        when(uiModel.asMap()).thenThrow(new UpdateConflictException());

        String page = controller.create(clinician, bindingResult, uiModel, request);

        verify(bindingResult).addError(new FieldError("Clinician", "username", clinician.getUsername(), false,
                    new String[]{"clinician_username_not_unique"}, new Object[]{}, ClinicianController.USERNAME_ALREADY_IN_USE));
        verify(uiModel).addAttribute("clinician", clinician);
        assertEquals("clinicians/create", page);
    }
}
