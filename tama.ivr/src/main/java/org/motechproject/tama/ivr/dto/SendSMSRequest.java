package org.motechproject.tama.ivr.dto;

import lombok.Data;

@Data
public class SendSMSRequest {

    private String recipientNumber;
    private String externalId;

    public SendSMSRequest(String recipientNumber, String externalId) {
        this.recipientNumber = recipientNumber;
        this.externalId = externalId;
    }

    public SendSMSRequest(String recipientNumber) {
        this(recipientNumber, "");
    }
}
