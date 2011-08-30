package org.motechproject.tamafunctional.testdata;

public class TestPatientPreferences {
    private String passcode;
    private String dayOfWeeklyCall;
    private String bestCallTime;
    private String timeMeridiem;

    private TestPatientPreferences() {
    }

    public static TestPatientPreferences withMandatory() {
        TestPatientPreferences testPatientPreferences = new TestPatientPreferences();
        return testPatientPreferences.passcode("1234").dayOfWeeklyCall("Tuesday").bestCallTime("09:05").timeMeridiem("AM");
    }

    public String passcode() {
        return passcode;
    }

    public TestPatientPreferences passcode(String passcode) {
        this.passcode = passcode;
        return this;
    }

    public String dayOfWeeklyCall() {
        return dayOfWeeklyCall;
    }

    public TestPatientPreferences dayOfWeeklyCall(String dayOfWeeklyCall) {
        this.dayOfWeeklyCall = dayOfWeeklyCall;
        return this;
    }

    public String bestCallTime() {
        return bestCallTime;
    }

    public TestPatientPreferences bestCallTime(String bestCallTime) {
        this.bestCallTime = bestCallTime;
        return this;
    }

    public String timeMeridiem() {
        return timeMeridiem;
    }

    public TestPatientPreferences timeMeridiem(String timeMeridiem) {
        this.timeMeridiem = timeMeridiem;
        return this;
    }
}
