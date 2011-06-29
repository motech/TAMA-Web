package org.motechproject.tama.integration.domain.patient;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith( PowerMockRunner.class )
@PrepareForTest( Patient.class )
public class PatientPersistenceTest{

    @Test
    public void testFindAllPatients(){
        Patients mockPatients = mock(Patients.class);

        when(mockPatients.getAll()).thenReturn(new ArrayList<Patient>());
        PowerMockito.spy(Patient.class);
        when(Patient.patients()).thenReturn(mockPatients);
        List<Patient> patientsList = Patient.findAllPatients();

        Assert.assertNotNull("Total patient list should not be null", patientsList);
        Assert.assertEquals(0, patientsList.size());

    }

    @Test
    public void testCountPatients(){
        Patients mockPatients = mock(Patients.class);

        when(mockPatients.getAll()).thenReturn(new ArrayList<Patient>());
        PowerMockito.spy(Patient.class);
        when(Patient.patients()).thenReturn(mockPatients);
        long totalPatients = Patient.countPatients();

        Assert.assertEquals(0, totalPatients);

    }

    @Test
    public void testFindPatientWithValidId(){
        Patients mockPatients = mock(Patients.class);
        String dummyPatientId = "Dummy";
        String mobileNumber = "+919876324678";
        Patient testPatient = PatientBuilder.startRecording().
                withPatientId(dummyPatientId).
                withMobileNumber(mobileNumber).build();

        when(mockPatients.get(dummyPatientId)).thenReturn(testPatient);
        PowerMockito.spy(Patient.class);
        when(Patient.patients()).thenReturn(mockPatients);

        Patient patient = Patient.findPatient(dummyPatientId);
        Assert.assertNotNull("Patient should not be null", patient);
        Assert.assertEquals(patient.getMobilePhoneNumber(), mobileNumber);

    }

    @Test
    public void testFindPatientWithNullId(){
        Patient patient = Patient.findPatient(null);
        Assert.assertNull("Patient should be null", patient);
    }

    @Test
    public void testFindPatientEntries(){
        Patients mockPatients = mock(Patients.class);

        when(mockPatients.getAll()).thenReturn(new ArrayList<Patient>());
        PowerMockito.spy(Patient.class);
        when(Patient.patients()).thenReturn(mockPatients);

         List<Patient> patientsList  = Patient.findPatientEntries(10, 10);
        Assert.assertNotNull("Total patient list should not be null", patientsList);
        Assert.assertEquals(0, patientsList.size());
    }

    @Test
    public void testSavePatient(){
        Patients mockPatients = mock(Patients.class);
        Patient patient = PatientBuilder.startRecording().build();
        patient.setPatients(mockPatients);

        patient.persist();
        verify(mockPatients).add(patient);
    }

    @Test
    public void testSavedPatientShouldHaveRegistrationDate(){
        Patients mockPatients = mock(Patients.class);
        Patient patient = PatientBuilder.startRecording().build();
        patient.setPatients(mockPatients);

        patient.persist();
        verify(mockPatients).add(patient);
        assertNotNull(patient.getRegistrationDate());
    }

    @Test
    public void testDeletePatient(){
        Patients mockPatients = mock(Patients.class);
        Patient patient = PatientBuilder.startRecording().build();
        patient.setPatients(mockPatients);

        patient.persist();
        verify(mockPatients).add(patient);

        patient.remove();
        verify(mockPatients).remove(patient);
    }


    @Test
    public void testUpdatePatient(){
        Patients mockPatients = mock(Patients.class);
        String revision = "rev";
        String id = "Dummy";
        Patient patient = PatientBuilder.startRecording().
                withId(id).
                withRevision(revision).
                build();
        patient.setPatients(mockPatients);
        when(mockPatients.get(id)).thenReturn(patient);

        patient.persist();
        verify(mockPatients).add(patient);

        patient.merge();
        verify(mockPatients).update(patient);
    }

}
