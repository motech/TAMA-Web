package org.motechproject.tama.common.expectation;

import lombok.Data;

@Data
public class Pair<Expected, Actual> {

    private Expected expected;
    private Actual actual;

    public Pair(Expected expected, Actual actual) {
        this.expected = expected;
        this.actual = actual;
    }
}
