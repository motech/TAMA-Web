package org.motechproject.tamafunctionalframework.testdata;

import org.apache.commons.collections.list.AbstractLinkedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestLabResult extends TestEntity {

    private String[] testDates = new String[2];
    private String[] results = new String[2];

    @Override
    public String resourceName() {
        return "labresults";
    }

    public static TestLabResult withMandatory() {
        TestLabResult labResult = new TestLabResult();
        labResult.setCd4Count("12/11/1998", "20");
        labResult.setPvlCount("12/11/1998", "100");
        return labResult;
    }

    public TestLabResult results(List<String> results) {
        this.results = results.toArray(new String[0]);
        return this;
    }

    public TestLabResult testDates(List<String> testDates) {
        this.testDates = testDates.toArray(new String[0]);
        return this;
    }

    public List<String> testDates() {
        return Arrays.asList(testDates);
    }

    public TestLabResult setCd4Count(String date, String count) {
        this.results[0] = count;
        this.testDates[0] = date;
        return this;
    }

    public TestLabResult setPvlCount(String date, String count) {
        this.results[1] = count;
        this.testDates[1] = date;
        return this;
    }

    public List<String> results() {
        return Arrays.asList(results);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestLabResult labResult = (TestLabResult) o;

        if (results != null ? !Arrays.equals(results, labResult.results) : labResult.results != null) return false;
        if (testDates != null ? !Arrays.equals(testDates, labResult.testDates) : labResult.testDates != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = testDates != null ? Arrays.hashCode(testDates) : 0;
        result = 31 * result + (results != null ? Arrays.hashCode(results) : 0);
        return result;
    }
}
