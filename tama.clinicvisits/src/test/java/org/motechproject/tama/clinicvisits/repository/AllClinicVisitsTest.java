package org.motechproject.tama.clinicvisits.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.dao.AllAppointments;
import org.motechproject.appointments.api.dao.AllVisits;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllClinicVisitsTest {

    @Mock
    private AllVisits allVisits;
    @Mock
    private AllAppointments allAppointments;

    private AllClinicVisits allClinicVisits;

    @Before
    public void setUp() {
        initMocks(this);
        allClinicVisits = new AllClinicVisits(allVisits, allAppointments);
    }

    @Test
    public void shouldFindByPatientId() {
        final String patientId = "patientId";
        final String appointmentId = "appointmentId";
        final Visit visitForPatient = new Visit() {{ setExternalId(patientId); setAppointmentId(appointmentId);}};
        final Appointment appointment = new Appointment();

        when(allVisits.findByExternalId(patientId)).thenReturn(new ArrayList<Visit>() {{
            add(visitForPatient);
        }});
        when(allAppointments.getAppointment(appointmentId)).thenReturn(appointment);

        ClinicVisits clinicVisits = allClinicVisits.findByPatientId(patientId);
        assertEquals(1, clinicVisits.size());
    }

    @Test
    public void shouldGetBaselineVisit() {
        final String patientId = "patientId";
        final String appointmentId = "appointmentId";
        final Visit visitForPatient = new Visit() {{ setExternalId(patientId); addData(ClinicVisit.TYPE_OF_VISIT, ClinicVisit.TypeOfVisit.Baseline); }};
        final Appointment appointment = new Appointment();

        when(allVisits.findByExternalId(patientId)).thenReturn(new ArrayList<Visit>() {{
            add(visitForPatient);
        }});
        when(allAppointments.getAppointment(appointmentId)).thenReturn(appointment);

        ClinicVisit clinicVisit = allClinicVisits.getBaselineVisit(patientId);
        assertEquals(ClinicVisit.TypeOfVisit.Baseline, clinicVisit.getTypeOfVisit());
    }

    @Test
    public void shouldAddClinicVisit() {
        Appointment appointment = new Appointment();
        final String appointmentId = "appointmentId";

        Visit visit = new Visit() {{setAppointmentId(appointmentId); }};
        ClinicVisit clinicVisit = new ClinicVisit(visit, appointment);

        when(allVisits.getVisit("visitId")).thenReturn(visit);
        when(allAppointments.getAppointment(appointmentId)).thenReturn(appointment);

        allClinicVisits.add(clinicVisit);

        verify(allVisits).addVisit(visit);
        verify(allAppointments).addAppointment(appointment);
    }

    @Test
    public void shouldUpdateClinicVisit() {
        Appointment appointment = new Appointment();
        final String appointmentId = "appointmentId";

        Visit visit = new Visit() {{setAppointmentId(appointmentId); }};
        ClinicVisit clinicVisit = new ClinicVisit(visit, appointment);

        when(allVisits.getVisit("visitId")).thenReturn(visit);
        when(allAppointments.getAppointment(appointmentId)).thenReturn(appointment);

        allClinicVisits.update(clinicVisit);

        verify(allVisits).updateVisit(visit);
        verify(allAppointments).updateAppointment(appointment);
    }
}
