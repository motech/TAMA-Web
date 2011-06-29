package org.motechproject.tama.integration.domain.doctor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.Doctor;
import org.motechproject.tama.repository.Doctors;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Doctor.class)

public class DoctorPersistenceTest {
    @Test
    public void testFindAllDoctors() {
        Doctors mockDoctors = mock(Doctors.class);

        when(mockDoctors.getAll()).thenReturn(new ArrayList<Doctor>());
        PowerMockito.spy(Doctor.class);
        when(Doctor.doctors()).thenReturn(mockDoctors);
        List<Doctor> doctorsList = Doctor.findAllDoctors();

        Assert.assertNotNull("Total doctor list should not be null", doctorsList);
        Assert.assertEquals(0, doctorsList.size());

    }

    @Test
    public void testCountDoctors() {
        Doctors mockDoctors = mock(Doctors.class);

        when(mockDoctors.getAll()).thenReturn(new ArrayList<Doctor>());
        PowerMockito.spy(Doctor.class);
        when(Doctor.doctors()).thenReturn(mockDoctors);
        long totalDoctors = Doctor.countDoctors();

        Assert.assertEquals(0, totalDoctors);

    }

    @Test
    public void testFindDoctorWithValidId() {
        Doctors mockDoctors = mock(Doctors.class);
        String dummyDoctorId = "Dummy";
        String name = "name";
        Doctor testDoctor = new Doctor();
        testDoctor.setFirstName(name);

        when(mockDoctors.get(dummyDoctorId)).thenReturn(testDoctor);
        PowerMockito.spy(Doctor.class);
        when(Doctor.doctors()).thenReturn(mockDoctors);
        Doctor doctor = Doctor.findDoctor(dummyDoctorId);
        Assert.assertNotNull("Doctor should not be null", doctor);
        Assert.assertEquals(doctor.getFirstName(), name);

    }

    @Test
    public void testFindDoctorWithNullId() {
        Doctor doctor = Doctor.findDoctor(null);
        Assert.assertNull("Doctor should be null", doctor);
    }

    @Test
    public void testFindDoctorEntries() {
        Doctors mockDoctors = mock(Doctors.class);

        when(mockDoctors.getAll()).thenReturn(new ArrayList<Doctor>());
        PowerMockito.spy(Doctor.class);
        when(Doctor.doctors()).thenReturn(mockDoctors);

        List<Doctor> doctorList = Doctor.findDoctorEntries(10, 10);
        Assert.assertNotNull("Total Doctors list should not be null", doctorList);
        Assert.assertEquals(0, doctorList.size());
    }

    @Test
    public void testSaveDoctor() {
        Doctors mockDoctors = mock(Doctors.class);
        Doctor doctor = new Doctor();
        doctor.setDoctors(mockDoctors);

        doctor.persist();
        verify(mockDoctors).add(doctor);
    }

    @Test
    public void testDeleteDoctor() {
        Doctors mockDoctors = mock(Doctors.class);
        Doctor doctor = new Doctor();
        doctor.setDoctors(mockDoctors);

        doctor.persist();
        verify(mockDoctors).add(doctor);

        doctor.remove();
        verify(mockDoctors).remove(doctor);
    }


    @Test
    public void testUpdateDoctor() {
        Doctors mockDoctors = mock(Doctors.class);
        String id = "Dummy";
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setRevision("rev");
        doctor.setDoctors(mockDoctors);
        when(mockDoctors.get(id)).thenReturn(doctor);

        doctor.persist();
        verify(mockDoctors).add(doctor);

        doctor.merge();
        verify(mockDoctors).update(doctor);
    }
}
