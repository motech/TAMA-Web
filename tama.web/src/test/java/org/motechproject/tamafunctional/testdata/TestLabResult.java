package org.motechproject.tamafunctional.testdata;

import java.util.Arrays;
import java.util.List;

public class TestLabResult extends TestEntity {

    private List<String> testDates;
    private List<String> results;

    private TestLabResult() {
    }

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
}
