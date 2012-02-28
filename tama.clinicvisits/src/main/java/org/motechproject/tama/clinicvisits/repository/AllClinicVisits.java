package org.motechproject.tama.clinicvisits.repository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.tama.clinicvisits.mapper.AppointmentCalendarRequestBuilder;
import org.motechproject.tama.clinicvisits.mapper.VisitRequestBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Properties;

@Repository
public class AllClinicVisits {

    public static final String REMIND_FOR_VISIT_FROM = "remindForVisitFrom";

    private AllPatients allPatients;
    private AppointmentService appointmentService;
    private AppointmentCalendarRequestBuilder appointmentCalendarRequestBuilder;
    private VisitRequestBuilder visitRequestBuilder;
    private Properties appointmentsProperties;

    @Autowired
    public AllClinicVisits(AllPatients allPatients, AppointmentService appointmentService, AppointmentCalendarRequestBuilder appointmentCalendarRequestBuilder, VisitRequestBuilder visitRequestBuilder, @Qualifier("appointments") Properties appointmentsProperties) {
        this.allPatients = allPatients;
        this.appointmentService = appointmentService;
        this.appointmentCalendarRequestBuilder = appointmentCalendarRequestBuilder;
        this.visitRequestBuilder = visitRequestBuilder;
        this.appointmentsProperties = appointmentsProperties;
    }

    public ClinicVisit get(String patientDocId, String visitId) {
        Patient patient = allPatients.get(patientDocId);
        AppointmentCalendar appointmentCalendar = appointmentService.getAppointmentCalendar(patientDocId);
        return new ClinicVisit(patient, appointmentCalendar.getVisit(visitId));
    }

    public void addAppointmentCalendar(String patientDocId) {
        AppointmentCalendarRequest appointmentCalendarRequest = appointmentCalendarRequestBuilder.calendarForPatient(patientDocId);
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
        return new ClinicVisit(patient, appointmentCalendar.getVisit(ClinicVisit.BASELINE));
    }

    public String updateVisit(String visitId, DateTime visitDate, String patientDocId, String treatmentAdviceId, List<String> labResultIds, String vitalStatisticsId, String opportunisticInfectionsId) {
        ClinicVisit clinicVisit = get(patientDocId, visitId);
        clinicVisit.setTreatmentAdviceId(treatmentAdviceId);
        clinicVisit.setLabResultIds(labResultIds);
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        clinicVisit.setOpportunisticInfectionsId(opportunisticInfectionsId);
        clinicVisit.setVisitDate(visitDate);
        updateVisit(clinicVisit);
        return clinicVisit.getId();
    }

    public String createAppointment(String patientDocId, DateTime appointmentDueDate, TypeOfVisit typeOfVisit) {
        String visitName = "visitFor-" + appointmentDueDate.getMillis();
        VisitRequest visitRequest = visitRequestBuilder.visitWithoutReminder(appointmentDueDate, typeOfVisit);
        return appointmentService.addVisit(patientDocId, visitName, visitRequest);
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
        ReminderConfiguration visitReminderConfiguration = getVisitReminderConfiguration();
        appointmentService.confirmVisit(patientDocId, clinicVisitId, confirmedVisitDate, visitReminderConfiguration);
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

    public void closeVisit(String patientDocId, String clinicVisitId, DateTime visitDate) {
        appointmentService.setVisitDate(patientDocId, clinicVisitId, visitDate);
    }

    private ReminderConfiguration getVisitReminderConfiguration() {
        int remindFrom = Integer.parseInt(appointmentsProperties.getProperty(REMIND_FOR_VISIT_FROM));
        return new ReminderConfiguration().setRemindFrom(remindFrom).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.DAYS).setRepeatCount(remindFrom);
    }

    private void updateVisit(ClinicVisit clinicVisit) {
        appointmentService.updateVisit(clinicVisit.getVisit(), clinicVisit.getPatientId());
    }
}
