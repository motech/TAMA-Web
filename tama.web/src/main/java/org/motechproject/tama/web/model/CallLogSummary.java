package org.motechproject.tama.web.model;

import java.util.Map;

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
    private Map<String, CallFlowDetails> flowDetailsMap;
    private String flows;
    private String age;
    private String gender;

    public CallLogSummary(String patientId, String sourcePhoneNumber, String destinationPhoneNumber, String initiatedDateTime,
                          String startDateTime, String endDateTime, String clinicName, String language, String patientDistanceFromClinic,
                          String flows, Map<String, CallFlowDetails> flowDetailsMap, String gender, String age) {
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
        this.flowDetailsMap = flowDetailsMap;
        this.gender = gender;
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

    public Map<String, CallFlowDetails> getFlowDetailsMap() {
        return flowDetailsMap;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }
}
