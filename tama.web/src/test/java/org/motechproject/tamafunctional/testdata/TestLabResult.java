package org.motechproject.tamafunctional.testdata;

import java.util.Arrays;
import java.util.List;

public class TestLabResult extends TestEntity {

    private List<String> testDates;
    private List<String> results;

    @Override
    public String resourceName() {
        return "labresults";
    }

    public static TestLabResult withMandatory() {
        TestLabResult labResult = new TestLabResult();
        labResult.testDates(Arrays.asList("12/11/1998", "12/11/1998"));
        labResult.results(Arrays.asList("20", "100"));
        return labResult;
    }

    public TestLabResult results(List<String> results) {
        this.results = results;
        return this;
    }

    public TestLabResult testDates(List<String> testDates) {
        this.testDates = testDates;
        return this;
    }

    public List<String> testDates() {
        return testDates;
    }

    public List<String> results() {
        return results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestLabResult labResult = (TestLabResult) o;

        if (results != null ? !results.equals(labResult.results) : labResult.results != null) return false;
        if (testDates != null ? !testDates.equals(labResult.testDates) : labResult.testDates != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = testDates != null ? testDates.hashCode() : 0;
        result = 31 * result + (results != null ? results.hashCode() : 0);
        return result;
    }
}
