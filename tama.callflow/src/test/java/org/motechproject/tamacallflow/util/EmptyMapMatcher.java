package org.motechproject.tamacallflow.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Map;

public class EmptyMapMatcher extends BaseMatcher<Map<String, String>> {
    @Override
    public boolean matches(Object o) {
        Map<String, String> map = (Map<String, String>) o;
        return map.isEmpty();
    }

    @Override
    public void describeTo(Description description) {
    }
}