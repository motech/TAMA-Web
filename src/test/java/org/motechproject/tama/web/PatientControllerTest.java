package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.AssertTrue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Patient.class)
public class PatientControllerTest {

    private PatientController controller;

    @Before
    public void setUp() {
        controller = new PatientController();
    }

    @Test
    public void shouldActivatePatientsByPost() {
        PowerMockito.spy(Patient.class);
        Patients patients = mock(Patients.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        Patient dbPatient = mock(Patient.class);
        String id = "1234";
        when(Patient.patients()).thenReturn(patients);
        when(patients.get(id)).thenReturn(dbPatient);
        when(dbPatient.activate()).thenReturn(dbPatient);

        String nextPage = controller.activate(id, request);

        verify(dbPatient).activate();
        verify(dbPatient).merge();
        assertTrue(nextPage.contains("redirect:/patients/"));
        assertTrue(nextPage.contains("1234"));
    }


    @Test
    public void shouldActivatePatientsByGet() {
        PowerMockito.spy(Patient.class);
        Patients patients = mock(Patients.class);
        Patient dbPatient = mock(Patient.class);
        String id = "1234";
        when(Patient.patients()).thenReturn(patients);
        when(patients.get(id)).thenReturn(dbPatient);
        when(dbPatient.activate()).thenReturn(dbPatient);

        String nextPage = controller.activate(id);

        verify(dbPatient).activate();
        verify(dbPatient).merge();
        assertEquals("redirect:/patients", nextPage);
    }
}
