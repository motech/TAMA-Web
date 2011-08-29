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

    private void results(List<String> results) {
        this.results = results;
    }

    private void testDates(List<String> testDates) {
        this.testDates = testDates;
    }

    public List<String> testDates() {
        return testDates;
    }

    public List<String> results() {
        return results;
    }
}
