package org.motechproject.tama.clinicvisits.repository;

import org.motechproject.appointments.api.dao.AllAppointments;
import org.motechproject.appointments.api.dao.AllVisits;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllClinicVisits {

    private AllVisits allVisits;
    private AllAppointments allAppointments;

    @Autowired
    public AllClinicVisits(AllVisits allVisits, AllAppointments allAppointments) {
        this.allVisits = allVisits;
        this.allAppointments = allAppointments;
    }

    public ClinicVisit get(String visitId) {
        return get(allVisits.getVisit(visitId));
    }

    public void add(ClinicVisit clinicVisit) {
        allAppointments.addAppointment(clinicVisit.getAppointment());
        allVisits.addVisit(clinicVisit.getVisit());
    }

    public void update(ClinicVisit clinicVisit) {
        allAppointments.updateAppointment(clinicVisit.getAppointment());
        allVisits.updateVisit(clinicVisit.getVisit());
    }

    public ClinicVisits findByPatientId(String patientId) {
        ClinicVisits clinicVisits = new ClinicVisits();
        for (Visit visit : allVisits.findByExternalId(patientId)) {
            clinicVisits.add(get(visit));
        }
        return clinicVisits;
    }

    public ClinicVisit getBaselineVisit(String patientId) {
        return findByPatientId(patientId).getBaselineVisit();
    }

    private ClinicVisit get(Visit visit) {
        Appointment appointment = allAppointments.getAppointment(visit.getAppointmentId());
        return new ClinicVisit(visit, appointment);
    }
}
