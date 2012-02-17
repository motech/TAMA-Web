package org.motechproject.tama.clinicvisits.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.ReminderService;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ListOfWeeks;
import org.motechproject.tama.clinicvisits.factory.AppointmentsFactory;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class ClinicVisitService {

    public static final String APPOINTMENT_SCHEDULE = "appointment-schedule";
    public static final String REMIND_FROM = "remindFrom";
    public static final String REMIND_TILL = "remindTill";

    private AllClinicVisits allClinicVisits;
    private ReminderService reminderService;
    private Properties appointmentsTemplate;

    @Autowired
    public ClinicVisitService(AllClinicVisits allClinicVisits, ReminderService reminderService, @Qualifier("appointmentsTemplate") Properties appointmentsTemplate) {
        this.allClinicVisits = allClinicVisits;
        this.reminderService = reminderService;
        this.appointmentsTemplate = appointmentsTemplate;
    }

    public void scheduleVisits(String patientId) {
        List<Integer> appointmentWeeks = ListOfWeeks.weeks(appointmentsTemplate.getProperty(APPOINTMENT_SCHEDULE));
        DateTime now = DateUtil.now();
        createExpectedVisit(patientId, now, 0);
        for (Integer week : appointmentWeeks) {
            createExpectedVisit(patientId, now.plusWeeks(week), week);
        }
    }

    void createExpectedVisit(String patientId, DateTime expectedVisitTime, int weeks) {
        Appointment appointment = AppointmentsFactory.createAppointment(patientId, expectedVisitTime);
        int remindFrom = Integer.parseInt(appointmentsTemplate.getProperty(REMIND_FROM));
        int remindTill = Integer.parseInt(appointmentsTemplate.getProperty(REMIND_TILL));
        Reminder reminder = AppointmentsFactory.createReminder(appointment, patientId, remindFrom, remindTill);
        ClinicVisit clinicVisit = AppointmentsFactory.createClinicVisit(appointment, patientId, expectedVisitTime, weeks);
        allClinicVisits.add(clinicVisit);
        reminderService.addReminder(reminder);
    }

    public String updateVisit(String visitId, DateTime visitDate, String patientId, String treatmentAdviceId, List<String> labResultIds, String vitalStatisticsId) {
        ClinicVisit clinicVisit = allClinicVisits.get(visitId);
        clinicVisit.setPatientId(patientId);
        clinicVisit.setTreatmentAdviceId(treatmentAdviceId);
        clinicVisit.setLabResultIds(labResultIds);
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        clinicVisit.setVisitDate(visitDate);
        allClinicVisits.update(clinicVisit);
        return clinicVisit.getId();
    }

    public ClinicVisit baselineVisit(String patientDocumentId) {
        return allClinicVisits.getBaselineVisit(patientDocumentId);
    }

    public void changeRegimen(String clinicVisitId, String newTreatmentAdviceId) {
        final ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setTreatmentAdviceId(newTreatmentAdviceId);
        allClinicVisits.update(clinicVisit);
    }

    public void updateLabResults(String clinicVisitId, List<String> labResultIds) {
        final ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setLabResultIds(labResultIds);
        allClinicVisits.update(clinicVisit);
    }

    public void updateVitalStatistics(String clinicVisitId, String vitalStatisticsId) {
        final ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        allClinicVisits.update(clinicVisit);
    }

    public List<ClinicVisit> getClinicVisits(String patientId) {
        return allClinicVisits.findByPatientId(patientId);
    }

    public void confirmVisitDate(String clinicVisitId, DateTime confirmedVisitDate) {
        ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setConfirmedVisitDate(confirmedVisitDate);
        allClinicVisits.update(clinicVisit);
    }

    public void adjustDueDate(String clinicVisitId, LocalDate adjustedDueDate) {
        ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setAdjustedDueDate(adjustedDueDate);
        allClinicVisits.update(clinicVisit);
    }

    public void markAsMissed(String clinicVisitId) {
        ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setMissed(true);
        allClinicVisits.update(clinicVisit);
    }

    public void setVisitDate(String clinicVisitId, DateTime visitDate) {
        ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setVisitDate(visitDate);
        allClinicVisits.update(clinicVisit);
    }
}