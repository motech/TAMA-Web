package org.motechproject.tama.web.model;

public class CallLogSummary {

    private String patientId;
    private String sourcePhoneNumber;
    private String destinationPhoneNumber;
    private String initiatedDateTime;
    private String startDateTime;
    private String endDateTime;
    private String clinicName;
    private String language;
    private String patientDistanceFromClinic;
    private String flowDurations;
    private String flows;
    private String age;

    public CallLogSummary(String patientId, String sourcePhoneNumber, String destinationPhoneNumber, String initiatedDateTime,
                          String startDateTime, String endDateTime, String clinicName, String language, String patientDistanceFromClinic, String flows, String flowDurations, String age) {
        this.patientId = patientId;
        this.sourcePhoneNumber = sourcePhoneNumber;
        this.destinationPhoneNumber = destinationPhoneNumber;
        this.initiatedDateTime = initiatedDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.clinicName = clinicName;
        this.language = language;
        this.patientDistanceFromClinic = patientDistanceFromClinic;
        this.flows = flows;
        this.flowDurations = flowDurations;
        this.age = age;
    }


    public String getPatientId() {
        return patientId;
    }

    public String getSourcePhoneNumber() {
        return sourcePhoneNumber;
    }

    public String getDestinationPhoneNumber() {
        return destinationPhoneNumber;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getLanguage() {
        return language;
    }

    public String getPatientDistanceFromClinic() {
        return patientDistanceFromClinic;
    }

    public String getFlows() {
        return flows;
    }

    public String getInitiatedDateTime() {
        return initiatedDateTime;
    }

    public String getFlowDurations() {
        return flowDurations;
    }

    public String getAge() {
        return age;
    }
}
