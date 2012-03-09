package org.motechproject.tamafunctionalframework.testdata;

public class TestOpportunisticInfections {

    private boolean anemia;
    private boolean hypertension;
    private boolean malaria;
    private boolean other;
    private String otherDetails;

    public TestOpportunisticInfections() {
    }

    public static TestOpportunisticInfections withMandatory() {
        TestOpportunisticInfections testOpportunisticInfections = new TestOpportunisticInfections();
        return testOpportunisticInfections.setAnemia(true).setMalaria(false).setHypertension(true).setOther(true).setOtherDetails("fever");
    }

    public boolean isAnemia() {
        return anemia;
    }

    public TestOpportunisticInfections setAnemia(boolean anemia) {
        this.anemia = anemia;
        return this;
    }

    public boolean isHypertension() {
        return hypertension;
    }

    public TestOpportunisticInfections setHypertension(boolean hypertension) {
        this.hypertension = hypertension;
        return this;
    }

    public boolean isMalaria() {
        return malaria;
    }

    public TestOpportunisticInfections setMalaria(boolean malaria) {
        this.malaria = malaria;
        return this;
    }

    public boolean isOther() {
        return other;
    }

    public TestOpportunisticInfections setOther(boolean other) {
        this.other = other;
        return this;
    }

    public String getOtherDetails() {
        return otherDetails;
    }

    public TestOpportunisticInfections setOtherDetails(String otherDetails) {
        this.otherDetails = otherDetails;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestOpportunisticInfections that = (TestOpportunisticInfections) o;

        if (anemia != that.anemia) return false;
        if (hypertension != that.hypertension) return false;
        if (malaria != that.malaria) return false;
        if (other != that.other) return false;
        if (otherDetails != null ? !otherDetails.equals(that.otherDetails) : that.otherDetails != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (anemia ? 1 : 0);
        result = 31 * result + (hypertension ? 1 : 0);
        result = 31 * result + (malaria ? 1 : 0);
        result = 31 * result + (other ? 1 : 0);
        result = 31 * result + (otherDetails != null ? otherDetails.hashCode() : 0);
        return result;
    }
}
