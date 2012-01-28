package org.motechproject.tama.patient.builder;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.util.DateUtil;

public class VitalStatisticsBuilder {

    private VitalStatistics vitalStatistics = new VitalStatistics();

    public VitalStatistics build() {
        return this.vitalStatistics;
    }

    public static VitalStatisticsBuilder startRecording() {
        return new VitalStatisticsBuilder();
    }

    public VitalStatisticsBuilder withDefaults() {
        return withPatientId("patientId").withWeight(100).withHeight(100.0).withCaptureDate(DateUtil.today()).withDiastolicBp(80).withSystolicBp(100).withTemperature(100.0).withPulse(100);

    }

    public VitalStatisticsBuilder withHeight(Double heightInCm) {
        vitalStatistics.setHeightInCm(heightInCm);
        return this;
    }

    public VitalStatisticsBuilder withSystolicBp(Integer systolicBp) {
        vitalStatistics.setSystolicBp(systolicBp);
        return this;
    }

    public VitalStatisticsBuilder withDiastolicBp(Integer diastolicBp) {
        vitalStatistics.setDiastolicBp(diastolicBp);
        return this;
    }

    public VitalStatisticsBuilder withPulse(Integer pulse) {
        vitalStatistics.setPulse(pulse);
        return this;
    }

    public VitalStatisticsBuilder withTemperature(Double temperature) {
        vitalStatistics.setTemperatureInFahrenheit(temperature);
        return this;
    }

    public VitalStatisticsBuilder withCaptureDate(LocalDate date) {
        vitalStatistics.setCaptureDate(date);
        return this;
    }


    public VitalStatisticsBuilder withPatientId(String patientId) {
        vitalStatistics.setPatientId(patientId);
        return this;
    }

    public VitalStatisticsBuilder withWeight(double weightInKg) {
        vitalStatistics.setWeightInKg(weightInKg);
        return this;
    }

    public VitalStatisticsBuilder withId(String id) {
        vitalStatistics.setId(id);
        return this;
    }
}
