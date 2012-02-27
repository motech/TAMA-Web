package org.motechproject.tama.clinicvisits.builder;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ClinicVisitBuilder {

    private ClinicVisit clinicVisit;

    public ClinicVisitBuilder() {
        Visit visit = new Visit();
        visit.addData(ClinicVisit.TYPE_OF_VISIT, TypeOfVisit.Baseline);
        clinicVisit = new ClinicVisit(PatientBuilder.startRecording().withDefaults().withId("patientId").build(), visit);
    }

    public ClinicVisit build() {
        return this.clinicVisit;
    }

    public static ClinicVisitBuilder startRecording() {
        return new ClinicVisitBuilder();
    }

    public ClinicVisitBuilder withDefaults() {
        return this.withId("baseline").withVisitDate(DateUtil.now()).withTreatmentAdviceId("treatmentAdviceId").
                withLabResultIds(new ArrayList<String>() {{ add("labResultId"); }}).withVitalStatisticsId("vitalStatisticsId");
    }

    public ClinicVisitBuilder withVisitDate(DateTime visitDate) {
        clinicVisit.setVisitDate(visitDate);
        return this;
    }

    public ClinicVisitBuilder withVitalStatisticsId(String vitalStatisticsId) {
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        return this;
    }

    public ClinicVisitBuilder withLabResultIds(List<String> labResultIds) {
        clinicVisit.setLabResultIds(labResultIds);
        return this;
    }

    public ClinicVisitBuilder withTreatmentAdviceId(String treatmentAdviceId) {
        clinicVisit.setTreatmentAdviceId(treatmentAdviceId);
        return this;
    }

    public ClinicVisitBuilder withId(String id) {
        clinicVisit.setId(id);
        return this;
    }
}
