package org.motechproject.tama.web.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.ivr.log.CallFlowDetails;

import java.util.Map;
import java.util.Set;

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
    private Set<String> messageCategories;
    private String gender;
    private String callMadeBy;

    public CallLogSummary(String patientId, String callMadeBy, String destinationPhoneNumber, String initiatedDateTime, String startDateTime, String endDateTime, String clinicName, String language, String patientDistanceFromClinic, String flows, Map<String, CallFlowDetails> flowDetailsMap, String gender, String age, Set<String> messageCategories, String sourcePhoneNumber) {
        this.patientId = patientId;
        this.callMadeBy = callMadeBy;
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
        this.messageCategories = messageCategories;
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

    public String getMessageCategories() {
        return CollectionUtils.isEmpty(messageCategories) ? "-" : StringUtils.join(messageCategories, ", ");
    }

    public String getCallMadeBy() {
        return callMadeBy;
    }
}
