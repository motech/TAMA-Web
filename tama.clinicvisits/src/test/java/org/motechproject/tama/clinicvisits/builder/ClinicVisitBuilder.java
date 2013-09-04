package org.motechproject.tama.clinicvisits.builder;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ClinicVisitBuilder {

    private ClinicVisit clinicVisit;
    private VisitResponse visitResponse;

    public ClinicVisitBuilder() {
        visitResponse = new VisitResponse();
        clinicVisit = new ClinicVisit(PatientBuilder.startRecording().withDefaults().withId("patientId").build(), visitResponse);
    }

    public ClinicVisitBuilder(String typeOfVisit) {
        visitResponse = new VisitResponse().setTypeOfVisit(typeOfVisit);
        clinicVisit = new ClinicVisit(PatientBuilder.startRecording().withDefaults().withId("patientId").build(), visitResponse);
    }

    public ClinicVisit build() {
        return this.clinicVisit;
    }

    public static ClinicVisitBuilder startRecording() {
        return new ClinicVisitBuilder();
    }

    public static ClinicVisitBuilder startRecording(String typeOfVisit) {
        return new ClinicVisitBuilder(typeOfVisit);
    }

    public ClinicVisitBuilder withDefaults() {
        return this.withId("baseline").withVisitDate(DateUtil.now()).withTreatmentAdviceId("treatmentAdviceId").
                withLabResultIds(new ArrayList<String>() {{
                    add("labResultId");
                }}).withVitalStatisticsId("vitalStatisticsId");
    }

    public ClinicVisitBuilder withVisitDate(DateTime visitDate) {
        visitResponse.setVisitDate(visitDate);
        return this;
    }

    public ClinicVisitBuilder withAppointmentDueDate(DateTime appointmentDueDate) {
        visitResponse.setOriginalAppointmentDueDate(appointmentDueDate);
        return this;
    }

    public ClinicVisitBuilder withAppointmentAdjustedDate(DateTime appointmentAdjustedDate) {
        visitResponse.setAppointmentDueDate(appointmentAdjustedDate);
        return this;
    }

    public ClinicVisitBuilder withAppointmentConfirmedDate(DateTime appointmentConfirmedDate) {
        visitResponse.setAppointmentConfirmDate(appointmentConfirmedDate);
        return this;
    }

    public ClinicVisitBuilder withVitalStatisticsId(String vitalStatisticsId) {
        visitResponse.addVisitData(ClinicVisit.VITAL_STATISTICS, vitalStatisticsId);
        return this;
    }

    public ClinicVisitBuilder withLabResultIds(List<String> labResultIds) {
        visitResponse.addVisitData(ClinicVisit.LAB_RESULTS, labResultIds);
        return this;
    }

    public ClinicVisitBuilder withTreatmentAdviceId(String treatmentAdviceId) {
        visitResponse.addVisitData(ClinicVisit.TREATMENT_ADVICE, treatmentAdviceId);
        return this;
    }

    public ClinicVisitBuilder withReportedOpportunisticInfection(String opportunisticInfection) {
        visitResponse.addVisitData(ClinicVisit.REPORTED_OPPORTUNISTIC_INFECTIONS, opportunisticInfection);
        return this;
    }

    public ClinicVisitBuilder withId(String id) {
        visitResponse.setName(id);
        return this;
    }
}
