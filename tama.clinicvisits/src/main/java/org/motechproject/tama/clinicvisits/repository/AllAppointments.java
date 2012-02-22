package org.motechproject.tama.clinicvisits.repository;

import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllAppointments {
    
    private AppointmentService appointmentService;

    @Autowired
    public AllAppointments(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    
    public Appointment get(String appointmentId) {
        return appointmentService.getAppointment(appointmentId);
    }
}
