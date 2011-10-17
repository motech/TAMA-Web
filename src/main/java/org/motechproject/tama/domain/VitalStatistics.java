package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'VitalStatistics'")
public class VitalStatistics extends CouchEntity {

    @NotNull
    private Double weightInKg;

    @NotNull
    private Double heightInCm;

    @NotNull
    private Integer systolicBp;

    @NotNull
    private Integer diastolicBp;

    @NotNull
    private Double temperatureInFahrenheit;

    @NotNull
    private Integer pulse;

    @NotNull
    private String patientId;

    public VitalStatistics() {
    }

    public VitalStatistics(String patientId) {
        this.patientId = patientId;
    }

    public VitalStatistics(Double weightInKg, Double heightInCm, Integer systolicBp, Integer diastolicBp, Double temperatureInFahrenheit, Integer pulse, String patientId) {
        this.weightInKg = weightInKg;
        this.heightInCm = heightInCm;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.temperatureInFahrenheit = temperatureInFahrenheit;
        this.pulse = pulse;
        this.patientId = patientId;
    }

    public Double getWeightInKg() {
        return weightInKg;
    }

    public void setWeightInKg(Double weightInKg) {
        this.weightInKg = weightInKg;
    }

    public Double getHeightInCm() {
        return heightInCm;
    }

    public void setHeightInCm(Double heightInCm) {
        this.heightInCm = heightInCm;
    }

    public Integer getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(Integer systolicBp) {
        this.systolicBp = systolicBp;
    }

    public Integer getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(Integer diastolicBp) {
        this.diastolicBp = diastolicBp;
    }

    public Double getTemperatureInFahrenheit() {
        return temperatureInFahrenheit;
    }

    public void setTemperatureInFahrenheit(Double temperatureInFahrenheit) {
        this.temperatureInFahrenheit = temperatureInFahrenheit;
    }

    public Integer getPulse() {
        return pulse;
    }

    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}