package org.motechproject.tamafunctional.testdata;

public class TestClinician extends TestEntity {
    private String contactNumber = "1234567890";
    private String alternateContactNumber = "1234567890";
    private String name;
    private String userName;
    private String password = "test";

    private TestClinic clinic;

    private TestClinician() {
    }

    public static TestClinician withMandatory() {
        TestClinician testClinician = new TestClinician();
        String unique = unique("");
        testClinician.name("testName" + unique);
        testClinician.userName("test" + unique);

        String validContactNumber = "1234567890";
        String validAlternateContactNumber = "1234567890";
        String password = "test";

        testClinician.alternateContactNumber(validAlternateContactNumber).
                contactNumber(validContactNumber).
                password(password).
                clinic(TestClinic.withMandatory());
        return testClinician;
    }

    public TestClinician clinic(TestClinic testClinic) {
        this.clinic = testClinic;
        return this;
    }

    public String userName() {
        return userName;
    }

    public String password() {
        return password;
    }

    public TestClinician name(String name) {
        this.name = name;
        return this;
    }

    public TestClinician userName(String userName) {
        this.userName = userName;
        return this;
    }

    public TestClinician password(String password) {
        this.password = password;
        return this;
    }

    public String name() {
        return name;
    }

    public String contactNumber() {
        return contactNumber;
    }

    public String alternateContactNumber() {
        return alternateContactNumber;
    }

    public TestClinician alternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
        return this;
    }

    public TestClinic clinic() {
        return clinic;
    }

    public TestClinician contactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
        return this;
    }

    @Override
    public String resourceName() {
        return "clinicians";
    }
}
