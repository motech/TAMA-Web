package org.motechproject.tama.clinicvisits.builder;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ClinicVisitBuilder {

    private ClinicVisit clinicVisit = new ClinicVisit(null, new Visit());

    public ClinicVisit build() {
        return this.clinicVisit;
    }

    public static ClinicVisitBuilder startRecording() {
        return new ClinicVisitBuilder();
    }

    public ClinicVisitBuilder withDefaults() {
        return this.withPatientId("patientId").withVisitDate(DateUtil.now()).withTreatmentAdviceId("treatmentAdviceId").
                withLabResultIds(new ArrayList<String>() {{ add("labResultId"); }}).withVitalStatisticsId("vitalStatisticsId");
    }

    public ClinicVisitBuilder withTypeOfVisit(ClinicVisit.TypeOfVisit typeOfVisit) {
        clinicVisit.setTypeOfVisit(typeOfVisit);
        return this;
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

    public ClinicVisitBuilder withPatientId(String patientId) {
        clinicVisit.setPatientId(patientId);
        return this;
    }
}
