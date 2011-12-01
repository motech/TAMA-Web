package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tamadomain.domain.Clinic;
import org.motechproject.tamadomain.domain.Clinician;
import org.motechproject.tamadomain.repository.AllClinicians;
import org.motechproject.tamadomain.repository.AllClinics;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicianControllerTest {
    private ClinicianController controller;
    @Mock
    private AllClinicians clinicians;
    @Mock
    private AllClinics clinics;
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

    @Test
    public void shouldSortClinicsInAlphabeticalOrderCaseInsensitive() {
        List<Clinic> clinicList = new ArrayList<Clinic>();
        Clinic clinic1 = new Clinic("1");
        clinic1.setName("foo");
        Clinic clinic2 = new Clinic("2");
        clinic2.setName("hello world");
        Clinic clinic3 = new Clinic("3");
        clinic3.setName("bar");
        Clinic clinic4 = new Clinic("4");
        clinic4.setName("FooBar");
        clinicList.add(clinic1);
        clinicList.add(clinic2);
        clinicList.add(clinic3);
        clinicList.add(clinic4);

        Mockito.when(clinics.getAll()).thenReturn(clinicList);
        controller = new ClinicianController(clinicians, clinics);

        Collection<Clinic> sortedClinics = controller.populateClinics();
        Assert.assertEquals(4, sortedClinics.size());
        Clinic[] sortedClinicArray = sortedClinics.toArray(new Clinic[0]);
        Assert.assertEquals("bar", sortedClinicArray[0].getName());
        Assert.assertEquals("foo", sortedClinicArray[1].getName());
        Assert.assertEquals("FooBar", sortedClinicArray[2].getName());
        Assert.assertEquals("hello world", sortedClinicArray[3].getName());
    }
}
