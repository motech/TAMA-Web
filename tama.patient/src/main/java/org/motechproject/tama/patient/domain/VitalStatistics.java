package org.motechproject.tama.patient.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.common.util.MathUtil;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'VitalStatistics'")
public class VitalStatistics extends CouchEntity implements Comparable<VitalStatistics> {

    private Double weightInKg;

    private Double heightInCm;

    private Integer systolicBp;

    private Integer diastolicBp;

    private Double temperatureInFahrenheit;

    private Integer pulse;

    @NotNull
    private String patientId;

    private LocalDate captureDate;

    public VitalStatistics() {
    }

    public VitalStatistics(VitalStatistics newVitalStatistics) {
        this(newVitalStatistics.getWeightInKg(), newVitalStatistics.getHeightInCm(), newVitalStatistics.getSystolicBp(),
                newVitalStatistics.getDiastolicBp(), newVitalStatistics.getTemperatureInFahrenheit(), newVitalStatistics.getPulse(),
                newVitalStatistics.getPatientId());
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

    public LocalDate getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(LocalDate captureDate) {
        this.captureDate = captureDate;
    }

    @JsonIgnore
    public double getBMI() {
        double heightInMetres = heightInCm / 100;
        return MathUtil.roundOffTo(weightInKg / Math.pow(heightInMetres, 2), 2);
    }

    @Override
    public int compareTo(VitalStatistics vitalStatistics) {
        return vitalStatistics.getCaptureDate().compareTo(getCaptureDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VitalStatistics that = (VitalStatistics) o;

        if (captureDate != null ? !captureDate.equals(that.captureDate) : that.captureDate != null) return false;
        if (diastolicBp != null ? !diastolicBp.equals(that.diastolicBp) : that.diastolicBp != null) return false;
        if (heightInCm != null ? !heightInCm.equals(that.heightInCm) : that.heightInCm != null) return false;
        if (patientId != null ? !patientId.equals(that.patientId) : that.patientId != null) return false;
        if (pulse != null ? !pulse.equals(that.pulse) : that.pulse != null) return false;
        if (systolicBp != null ? !systolicBp.equals(that.systolicBp) : that.systolicBp != null) return false;
        if (temperatureInFahrenheit != null ? !temperatureInFahrenheit.equals(that.temperatureInFahrenheit) : that.temperatureInFahrenheit != null)
            return false;
        if (weightInKg != null ? !weightInKg.equals(that.weightInKg) : that.weightInKg != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (weightInKg != null ? weightInKg.hashCode() : 0);
        result = 31 * result + (heightInCm != null ? heightInCm.hashCode() : 0);
        result = 31 * result + (systolicBp != null ? systolicBp.hashCode() : 0);
        result = 31 * result + (diastolicBp != null ? diastolicBp.hashCode() : 0);
        result = 31 * result + (temperatureInFahrenheit != null ? temperatureInFahrenheit.hashCode() : 0);
        result = 31 * result + (pulse != null ? pulse.hashCode() : 0);
        result = 31 * result + (patientId != null ? patientId.hashCode() : 0);
        result = 31 * result + (captureDate != null ? captureDate.hashCode() : 0);
        return result;
    }
}