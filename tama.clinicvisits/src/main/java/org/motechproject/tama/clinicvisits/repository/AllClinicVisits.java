package org.motechproject.tama.clinicvisits.repository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.ListOfWeeks;
import org.motechproject.tama.clinicvisits.factory.AppointmentsFactory;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Properties;

@Repository
public class AllClinicVisits {

    public static final String REMIND_FROM = "remindFrom";
    public static final String REMIND_TILL = "remindTill";
    public static final String APPOINTMENT_SCHEDULE = "appointment-schedule";

    private AppointmentService appointmentService;
    private Properties appointmentsTemplate;

    @Autowired
    public AllClinicVisits(AppointmentService appointmentService, @Qualifier("appointmentsTemplate") Properties appointmentsTemplate) {
        this.appointmentService = appointmentService;
        this.appointmentsTemplate = appointmentsTemplate;
    }

    public ClinicVisit get(String patientDocId, String visitId) {
        AppointmentCalendar appointmentCalendar = appointmentService.getAppointmentCalendar(patientDocId);
        return new ClinicVisit(patientDocId, appointmentCalendar.getVisit(visitId));
    }

    public void scheduleVisits(String patientDocId) {
        List<Integer> appointmentWeeks = ListOfWeeks.weeks(appointmentsTemplate.getProperty(APPOINTMENT_SCHEDULE));
        DateTime now = DateUtil.now();
        addVisit(patientDocId, now, 0);
        for (Integer week : appointmentWeeks) {
            addVisit(patientDocId, now.plusWeeks(week), week);
        }
    }

    public ClinicVisits clinicVisits(String patientDocId) {
        AppointmentCalendar appointmentCalendar = appointmentService.getAppointmentCalendar(patientDocId);
        ClinicVisits clinicVisits = new ClinicVisits();
        for (Visit visit : appointmentCalendar.visits()) {
            clinicVisits.add(new ClinicVisit(patientDocId, visit));
        }
        return clinicVisits;
    }

    public ClinicVisit getBaselineVisit(String patientDocId) {
        return clinicVisits(patientDocId).getBaselineVisit();
    }

    void addVisit(String patientDocId, DateTime expectedVisitTime, int weeks) {
        int remindFrom = Integer.parseInt(appointmentsTemplate.getProperty(REMIND_FROM));
        int remindTill = Integer.parseInt(appointmentsTemplate.getProperty(REMIND_TILL));
        Reminder reminder = weeks == 0 ? null : AppointmentsFactory.createReminder(expectedVisitTime, remindFrom, remindTill);
        Appointment appointment = AppointmentsFactory.createAppointment(expectedVisitTime, reminder);
        Visit visit = AppointmentsFactory.createVisit(appointment, weeks);
        appointmentService.addVisit(visit, patientDocId);
    }

    public String updateVisit(String visitId, DateTime visitDate, String patientDocId, String treatmentAdviceId, List<String> labResultIds, String vitalStatisticsId) {
        ClinicVisit clinicVisit = get(patientDocId, visitId);
        clinicVisit.setTreatmentAdviceId(treatmentAdviceId);
        clinicVisit.setLabResultIds(labResultIds);
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        clinicVisit.setVisitDate(visitDate);
        updateVisit(clinicVisit);
        return clinicVisit.getId();
    }

    public void changeRegimen(String patientDocId, String clinicVisitId, String newTreatmentAdviceId) {
        final ClinicVisit clinicVisit = get(patientDocId, clinicVisitId);
        clinicVisit.setTreatmentAdviceId(newTreatmentAdviceId);
        updateVisit(clinicVisit);
    }

    public void updateLabResults(String patientDocId, String clinicVisitId, List<String> labResultIds) {
        final ClinicVisit clinicVisit = get(patientDocId, clinicVisitId);
        clinicVisit.setLabResultIds(labResultIds);
        updateVisit(clinicVisit);
    }

    public void updateVitalStatistics(String patientDocId, String clinicVisitId, String vitalStatisticsId) {
        final ClinicVisit clinicVisit = get(patientDocId, clinicVisitId);
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        updateVisit(clinicVisit);
    }

    public void confirmVisitDate(String patientDocId, String clinicVisitId, DateTime confirmedVisitDate) {
        ClinicVisit clinicVisit = get(patientDocId, clinicVisitId);
        clinicVisit.setConfirmedVisitDate(confirmedVisitDate);
        updateVisit(clinicVisit);
    }

    public void adjustDueDate(String patientDocId, String clinicVisitId, LocalDate adjustedDueDate) {
        ClinicVisit clinicVisit = get(patientDocId, clinicVisitId);
        clinicVisit.setAdjustedDueDate(adjustedDueDate);
        updateVisit(clinicVisit);
    }

    public void markAsMissed(String patientDocId, String clinicVisitId) {
        ClinicVisit clinicVisit = get(patientDocId, clinicVisitId);
        clinicVisit.setMissed(true);
        updateVisit(clinicVisit);
    }

    public void setVisitDate(String patientDocId, String clinicVisitId, DateTime visitDate) {
        ClinicVisit clinicVisit = get(patientDocId, clinicVisitId);
        clinicVisit.setVisitDate(visitDate);
        updateVisit(clinicVisit);
    }

    private void updateVisit(ClinicVisit clinicVisit) {
        appointmentService.updateVisit(clinicVisit.getVisit(), clinicVisit.getPatientId());
    }
}
