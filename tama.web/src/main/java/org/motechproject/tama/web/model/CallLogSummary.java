package org.motechproject.tama.web.model;

public class CallLogSummary {

    private String patientId;
    private String sourcePhoneNumber;
    private String destinationPhoneNumber;
    private String startDateTime;
    private String endDateTime;
    private String clinicName;
    private String language;
    private String patientDistanceFromClinic;
    private String flows;

    public CallLogSummary() {
    }

    public CallLogSummary(String patientId, String sourcePhoneNumber, String destinationPhoneNumber, String startDateTime, String endDateTime, String clinicName, String language, String patientDistanceFromClinic, String flows) {
        this.patientId = patientId;
        this.sourcePhoneNumber = sourcePhoneNumber;
        this.destinationPhoneNumber = destinationPhoneNumber;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.clinicName = clinicName;
        this.language = language;
        this.patientDistanceFromClinic = patientDistanceFromClinic;
        this.flows = flows;
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

    public void setFlows(String flows) {
        this.flows = flows;
    }

    public String getFlows() {
        return flows;
    }
}
