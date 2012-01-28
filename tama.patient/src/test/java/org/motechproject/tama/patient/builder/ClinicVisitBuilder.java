package org.motechproject.tama.patient.builder;

import org.joda.time.DateTime;
import org.motechproject.tama.patient.domain.ClinicVisit;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ClinicVisitBuilder {

    private ClinicVisit clinicVisit = new ClinicVisit();

    public ClinicVisit build() {
        return this.clinicVisit;
    }

    public static ClinicVisitBuilder startRecording() {
        return new ClinicVisitBuilder();
    }

    public ClinicVisitBuilder withDefaults() {
        return this.withId("clinicVisitId").withPatientId("patientId").withVisitDate(DateUtil.now()).withTreatmentAdviceId("treatmentAdviceId").
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

    public ClinicVisitBuilder withPatientId(String patientId) {
        clinicVisit.setPatientId(patientId);
        return this;
    }

    public ClinicVisitBuilder withId(String clinicVisitId) {
        clinicVisit.setId(clinicVisitId);
        return this;
    }
}
