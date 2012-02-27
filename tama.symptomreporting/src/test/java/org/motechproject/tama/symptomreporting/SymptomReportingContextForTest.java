package org.motechproject.tama.symptomreporting;

import org.motechproject.tama.symptomreporting.context.SymptomsReportingContext;

public class SymptomReportingContextForTest extends SymptomsReportingContext {

    private int numberOfCliniciansCalled;
    private String patientDocumentId;
    private String callId;
    private boolean isAnswered = false;
    private boolean endCall = false;
    private String preferredLanguage;

    public SymptomReportingContextForTest() {
    }

    @Override
    public String patientDocumentId() {
        return patientDocumentId;
    }

    @Override
    public String callId() {
        return callId;
    }

    @Override
    public boolean isAnswered() {
        return isAnswered;
    }

    @Override
    public int numberOfCliniciansCalled() {
        return numberOfCliniciansCalled;
    }

    @Override
    public void endCall() {
        endCall = true;
    }

    @Override
    public int anotherClinicianCalled() {
        return numberOfCliniciansCalled += 1;
    }

    @Override
    public String preferredLanguage() {
        return preferredLanguage;
    }

    public boolean getEndCall() {
        return endCall;
    }

    public SymptomReportingContextForTest patientDocumentId(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
        return this;
    }

    public SymptomReportingContextForTest numberOfCliniciansCalled(int numberOfCliniciansCalled) {
        this.numberOfCliniciansCalled = numberOfCliniciansCalled;
        return this;
    }

    public SymptomReportingContextForTest isAnswered(boolean answered) {
        this.isAnswered = true;
        return this;
    }

    public SymptomReportingContextForTest callId(String callId) {
        this.callId = callId;
        return this;
    }

    public SymptomReportingContextForTest preferredLanguage(String language) {
        this.preferredLanguage = language;
        return this;
    }
}
