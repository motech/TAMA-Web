package org.motechproject.tama.ivr;

public class SymptomsReportingContextForTest extends SymptomsReportingContextWrapper {

    private boolean dialState;

    public void switchToDialState(boolean dialState) {
        this.dialState = dialState;
    }

    @Override
    public boolean isDialState() {
        return dialState;
    }
}
