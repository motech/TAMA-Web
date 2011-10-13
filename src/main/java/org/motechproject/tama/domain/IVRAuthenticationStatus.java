package org.motechproject.tama.domain;

public class IVRAuthenticationStatus {
    private boolean found;
    private boolean allowRetry;
    private boolean authenticated;
    private boolean active;
    private String patientId;
    private int loginAttemptNumber;

    private IVRAuthenticationStatus() {
    }

    public static IVRAuthenticationStatus notFound() {
        return new IVRAuthenticationStatus().found(false);
    }

    public static IVRAuthenticationStatus notAuthenticated() {
        return new IVRAuthenticationStatus().found(true).authenticated(false);
    }

    public static IVRAuthenticationStatus authenticated(String patientId) {
        return new IVRAuthenticationStatus().found(true).authenticated(true).patientId(patientId);
    }

    private IVRAuthenticationStatus patientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public IVRAuthenticationStatus found(boolean found) {
        this.found = found;
        return this;
    }

    public IVRAuthenticationStatus authenticated(boolean authenticated) {
        this.authenticated = authenticated;
        return this;
    }

    public IVRAuthenticationStatus active(boolean active) {
        this.active = active;
        return this;
    }

    public IVRAuthenticationStatus allowRetry(boolean allowRetry) {
        this.allowRetry = allowRetry;
        return this;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public boolean isFound() {
        return found;
    }

    public boolean isActive() {
        return active;
    }

    public boolean doAllowRetry() {
        return allowRetry;
    }

    public String patientId() {
        return patientId;
    }

    public IVRAuthenticationStatus loginAttemptNumber(int loginAttemptNumber) {
        this.loginAttemptNumber = loginAttemptNumber;
        return this;
    }

    public static IVRAuthenticationStatus allowRetry(int loginAttemptNumber) {
        return new IVRAuthenticationStatus().found(true).authenticated(false).allowRetry(true).loginAttemptNumber(loginAttemptNumber);
    }

    public int loginAttemptNumber() {
        return loginAttemptNumber;
    }

    @Override
    public String toString() {
        return String.format("{Found=%s, AllowRetry=%s, Authenticated=%s, Active=%s, PatientId='%s\', LoginAttemptNumber=%d}",
                found, allowRetry, authenticated, active, patientId, loginAttemptNumber);
    }
}
