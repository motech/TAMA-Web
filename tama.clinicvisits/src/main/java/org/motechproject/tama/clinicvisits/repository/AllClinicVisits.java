package org.motechproject.tama.clinicvisits.repository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.contract.*;
import org.motechproject.tama.clinicvisits.builder.servicecontract.AppointmentCalendarRequestBuilder;
import org.motechproject.tama.clinicvisits.builder.servicecontract.ConfirmAppointmentRequestBuilder;
import org.motechproject.tama.clinicvisits.builder.servicecontract.CreateVisitRequestBuilder;
import org.motechproject.tama.clinicvisits.builder.servicecontract.RescheduleAppointmentRequestBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.tama.common.repository.AllAuditEvents;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class AllClinicVisits {

    private AllPatients allPatients;
    private AppointmentService appointmentService;
    private AppointmentCalendarRequestBuilder appointmentCalendarRequestBuilder;
    private CreateVisitRequestBuilder createVisitRequestBuilder;
    private ConfirmAppointmentRequestBuilder confirmAppointmentRequestBuilder;
    private RescheduleAppointmentRequestBuilder rescheduleAppointmentRequestBuilder;
    private AllAuditEvents allAuditEvents;

    @Autowired
    public AllClinicVisits(AllPatients allPatients, AppointmentService appointmentService, AppointmentCalendarRequestBuilder appointmentCalendarRequestBuilder,
                           CreateVisitRequestBuilder createVisitRequestBuilder, ConfirmAppointmentRequestBuilder confirmAppointmentRequestBuilder, RescheduleAppointmentRequestBuilder rescheduleAppointmentRequestBuilder, AllAuditEvents allAuditEvents) {
        this.allPatients = allPatients;
        this.appointmentService = appointmentService;
        this.appointmentCalendarRequestBuilder = appointmentCalendarRequestBuilder;
        this.createVisitRequestBuilder = createVisitRequestBuilder;
        this.confirmAppointmentRequestBuilder = confirmAppointmentRequestBuilder;
        this.rescheduleAppointmentRequestBuilder = rescheduleAppointmentRequestBuilder;
        this.allAuditEvents = allAuditEvents;
    }

    public void addAppointmentCalendar(String patientDocId) {
        AppointmentCalendarRequest appointmentCalendarRequest = appointmentCalendarRequestBuilder.calendarForPatient(patientDocId);
        appointmentService.removeCalendar(patientDocId);
        appointmentService.addCalendar(appointmentCalendarRequest);
    }

    public ClinicVisit get(String patientDocId, String visitId) {
        Patient patient = allPatients.get(patientDocId);
        VisitResponse visitResponse = appointmentService.findVisit(patientDocId, visitId);
        return new ClinicVisit(patient, visitResponse);
    }

    public ClinicVisit getBaselineVisit(String patientDocId) {
        return get(patientDocId, TypeOfVisit.Baseline.toString());
    }

    public ClinicVisits clinicVisits(String patientDocId) {
        ClinicVisits clinicVisits = new ClinicVisits();
        List<VisitResponse> allVisits = appointmentService.getAllVisits(patientDocId);
        Patient patient = allPatients.get(patientDocId);
        for (VisitResponse visitResponse : allVisits) {
            clinicVisits.add(new ClinicVisit(patient, visitResponse));
        }
        return clinicVisits;
    }

    public void createUnScheduledAppointment(String patientDocId, DateTime dueDate, TypeOfVisit typeOfVisit, String userName) {
        String visitName = "visitFor-" + dueDate.getMillis();
        CreateVisitRequest createVisitRequest = createVisitRequestBuilder.adHocVisitRequest(visitName, typeOfVisit, dueDate);
        allAuditEvents.recordAppointmentEvent(userName, "Added unscheduled appointment for : " + patientDocId + " with dueDate set as " + dueDate);
        appointmentService.addVisit(patientDocId, createVisitRequest);
    }

    public String createUnscheduledVisit(String patientDocId, DateTime dueDate, TypeOfVisit typeOfVisit, String userName) {
        String visitName = "visitFor-" + dueDate.getMillis();
        CreateVisitRequest createVisitRequest = createVisitRequestBuilder.adHocVisitRequestForToday(visitName, typeOfVisit, dueDate);
        allAuditEvents.recordAppointmentEvent(userName, "Added ad-hoc visit for : " + patientDocId);
        return appointmentService.addVisit(patientDocId, createVisitRequest).getName();
    }

    public String updateVisitDetails(String visitId, DateTime visitDate, String patientDocId, String treatmentAdviceId, List<String> labResultIds, String vitalStatisticsId, String opportunisticInfectionsId, String userName) {
        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(ClinicVisit.LAB_RESULTS, labResultIds);
        dataMap.put(ClinicVisit.REPORTED_OPPORTUNISTIC_INFECTIONS, opportunisticInfectionsId);
        dataMap.put(ClinicVisit.TREATMENT_ADVICE, treatmentAdviceId);
        dataMap.put(ClinicVisit.VITAL_STATISTICS, vitalStatisticsId);
        appointmentService.addCustomDataToVisit(patientDocId, visitId, dataMap);
        closeVisit(patientDocId, visitId, visitDate, userName);
        return visitId;
    }

    public void changeRegimen(String patientDocId, String clinicVisitId, String newTreatmentAdviceId) {
        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(ClinicVisit.TREATMENT_ADVICE, newTreatmentAdviceId);
        appointmentService.addCustomDataToVisit(patientDocId, clinicVisitId, dataMap);
    }

    public void updateLabResults(String patientDocId, String clinicVisitId, List<String> labResultIds) {
        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(ClinicVisit.LAB_RESULTS, labResultIds);
        appointmentService.addCustomDataToVisit(patientDocId, clinicVisitId, dataMap);
    }

    public void updateVitalStatistics(String patientDocId, String clinicVisitId, String vitalStatisticsId) {
        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(ClinicVisit.VITAL_STATISTICS, vitalStatisticsId);
        appointmentService.addCustomDataToVisit(patientDocId, clinicVisitId, dataMap);
    }

    public void updateOpportunisticInfections(String patientDocId, String clinicVisitId, String reportedOpportunisticInfectionId) {
        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(ClinicVisit.REPORTED_OPPORTUNISTIC_INFECTIONS, reportedOpportunisticInfectionId);
        appointmentService.addCustomDataToVisit(patientDocId, clinicVisitId, dataMap);
    }

    public void adjustDueDate(String patientDocId, String clinicVisitId, LocalDate adjustedDueDate, String userName) {
        RescheduleAppointmentRequest rescheduleAppointmentRequest = rescheduleAppointmentRequestBuilder.create(patientDocId, clinicVisitId, adjustedDueDate);
        allAuditEvents.recordAppointmentEvent(userName, "Adjusting due date for patient : " + patientDocId + " for visit " + clinicVisitId + " to " + adjustedDueDate);
        appointmentService.rescheduleAppointment(rescheduleAppointmentRequest);
    }

    public void confirmAppointmentDate(String patientDocId, String clinicVisitId, DateTime confirmedAppointmentDate, String userName) {
        ConfirmAppointmentRequest request = confirmAppointmentRequestBuilder.confirmAppointmentRequest(patientDocId, clinicVisitId, confirmedAppointmentDate);
        allAuditEvents.recordAppointmentEvent(userName, "Confirming visit date for patient : " + patientDocId + " for visit " + clinicVisitId + " to " + confirmedAppointmentDate);
        appointmentService.confirmAppointment(request);
    }

    public void markAsMissed(String patientDocId, String clinicVisitId, String userName) {
        allAuditEvents.recordAppointmentEvent(userName, "Marking visit  : " + clinicVisitId + " for patient : " + patientDocId + " as missed ");
        appointmentService.markVisitAsMissed(patientDocId, clinicVisitId);
    }

    public void closeVisit(String patientDocId, String clinicVisitId, DateTime visitDate, String userName) {
        allAuditEvents.recordAppointmentEvent(userName, "Closing visit " + clinicVisitId + " for : " + patientDocId + " on " + visitDate);
        appointmentService.visited(patientDocId, clinicVisitId, visitDate);
    }
}
