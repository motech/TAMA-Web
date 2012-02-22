package org.motechproject.tama.clinicvisits.repository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.ListOfWeeks;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Properties;

@Repository
public class AllClinicVisits {

    public static final String REMIND_FROM = "remindFrom";
    public static final String APPOINTMENT_SCHEDULE = "appointment-schedule";

    private AllPatients allPatients;
    private AppointmentService appointmentService;
    private Properties appointmentsTemplate;

    @Autowired
    public AllClinicVisits(AllPatients allPatients, AppointmentService appointmentService, @Qualifier("appointments") Properties appointmentsTemplate) {
        this.allPatients = allPatients;
        this.appointmentService = appointmentService;
        this.appointmentsTemplate = appointmentsTemplate;
    }

    public ClinicVisit get(String patientDocId, String visitId) {
        Patient patient = allPatients.get(patientDocId);
        AppointmentCalendar appointmentCalendar = appointmentService.getAppointmentCalendar(patientDocId);
        return new ClinicVisit(patient, appointmentCalendar.getVisit(visitId));
    }

    public void addAppointmentCalendar(String patientDocId) {
        List<Integer> appointmentWeeks = ListOfWeeks.weeks(appointmentsTemplate.getProperty(APPOINTMENT_SCHEDULE));
        ReminderConfiguration reminderConfiguration = getReminderConfiguration();
        AppointmentCalendarRequest appointmentCalendarRequest = new AppointmentCalendarRequest().setExternalId(patientDocId).setWeekOffsets(appointmentWeeks).setReminderConfiguration(reminderConfiguration);
        appointmentService.removeCalendar(patientDocId);
        appointmentService.addCalendar(appointmentCalendarRequest);
    }

    public ClinicVisits clinicVisits(String patientDocId) {
        AppointmentCalendar appointmentCalendar = appointmentService.getAppointmentCalendar(patientDocId);
        if (appointmentCalendar == null) return new ClinicVisits();
        Patient patient = allPatients.get(patientDocId);
        ClinicVisits clinicVisits = new ClinicVisits();
        for (Visit visit : appointmentCalendar.visits()) {
            clinicVisits.add(new ClinicVisit(patient, visit));
        }
        return clinicVisits;
    }

    public ClinicVisit getBaselineVisit(String patientDocId) {
        AppointmentCalendar appointmentCalendar = appointmentService.getAppointmentCalendar(patientDocId);
        Patient patient = allPatients.get(patientDocId);
        return new ClinicVisit(patient, appointmentCalendar.baselineVisit());
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

    public String createAppointment(String patientDocId, DateTime appointmentDueDate, TypeOfVisit typeOfVisit) {
        ReminderConfiguration reminderConfiguration = getReminderConfiguration();
        return appointmentService.addVisit(patientDocId, appointmentDueDate, reminderConfiguration, typeOfVisit);
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

    private ReminderConfiguration getReminderConfiguration() {
        int remindFrom = Integer.parseInt(appointmentsTemplate.getProperty(REMIND_FROM));
        return new ReminderConfiguration().setRemindFrom(remindFrom).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.DAYS).setRepeatCount(remindFrom);
    }

    private void updateVisit(ClinicVisit clinicVisit) {
        appointmentService.updateVisit(clinicVisit.getVisit(), clinicVisit.getPatientId());
    }
}
