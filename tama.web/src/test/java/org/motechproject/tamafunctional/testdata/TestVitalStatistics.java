package org.motechproject.tamafunctional.testdata;

public class TestVitalStatistics {

    private Double weightInKg;
    private Double heightInCm;
    private Integer pulse;
    private Integer systolicBp;
    private Integer diastolicBp;
    private Double temperatureInFahrenheit;

    public TestVitalStatistics() {
    }

    public static TestVitalStatistics withMandatory() {
        TestVitalStatistics testVitalStatistics = new TestVitalStatistics();
        return testVitalStatistics.weightInKg(new Double(60.2)).heightInCm(new Double(140)).diastolicBp(new Integer(100)).systolicBp(new Integer(50)).pulse(new Integer(72)).temperatureInFahrenheit(new Double(98.4));
    }

    public Double weightInKg() {
        return weightInKg;
    }

    public TestVitalStatistics weightInKg(Double weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public Double heightInCm() {
        return heightInCm;
    }

    public TestVitalStatistics heightInCm(Double heightInCm) {
        this.heightInCm = heightInCm;
        return this;
    }

    public Integer pulse() {
        return pulse;
    }

    public TestVitalStatistics pulse(Integer pulse) {
        this.pulse = pulse;
        return this;
    }

    public Integer systolicBp() {
        return systolicBp;
    }

    public TestVitalStatistics systolicBp(Integer systolicBp) {
        this.systolicBp = systolicBp;
        return this;
    }

    public Integer diastolicBp() {
        return diastolicBp;
    }

    public TestVitalStatistics diastolicBp(Integer diastolicBp) {
        this.diastolicBp = diastolicBp;
        return this;
    }

    public Double temperatureInFahrenheit() {
        return temperatureInFahrenheit;
    }

    public TestVitalStatistics temperatureInFahrenheit(Double temperatureInFahrenheit) {
        this.temperatureInFahrenheit = temperatureInFahrenheit;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestVitalStatistics that = (TestVitalStatistics) o;

        if (diastolicBp != null ? !diastolicBp.equals(that.diastolicBp) : that.diastolicBp != null) return false;
        if (heightInCm != null ? !heightInCm.equals(that.heightInCm) : that.heightInCm != null) return false;
        if (pulse != null ? !pulse.equals(that.pulse) : that.pulse != null) return false;
        if (systolicBp != null ? !systolicBp.equals(that.systolicBp) : that.systolicBp != null) return false;
        if (temperatureInFahrenheit != null ? !temperatureInFahrenheit.equals(that.temperatureInFahrenheit) : that.temperatureInFahrenheit != null)
            return false;
        if (weightInKg != null ? !weightInKg.equals(that.weightInKg) : that.weightInKg != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = weightInKg != null ? weightInKg.hashCode() : 0;
        result = 31 * result + (heightInCm != null ? heightInCm.hashCode() : 0);
        result = 31 * result + (pulse != null ? pulse.hashCode() : 0);
        result = 31 * result + (systolicBp != null ? systolicBp.hashCode() : 0);
        result = 31 * result + (diastolicBp != null ? diastolicBp.hashCode() : 0);
        result = 31 * result + (temperatureInFahrenheit != null ? temperatureInFahrenheit.hashCode() : 0);
        return result;
    }
}
