package org.motechproject.tama.ivr.reporting;


public enum SMSType {

    OTC("O"),
    Clinician("C"),
    AdditionalSMS("A");

    private String code;

    SMSType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
