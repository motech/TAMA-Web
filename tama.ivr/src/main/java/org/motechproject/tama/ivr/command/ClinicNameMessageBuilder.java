package org.motechproject.tama.ivr.command;


import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public  class ClinicNameMessageBuilder {

    private CMSLiteService cmsLiteService;

    @Autowired
    public ClinicNameMessageBuilder(CMSLiteService cmsLiteService){
        this.cmsLiteService = cmsLiteService;
    }

    public String getOutboundMessage(Clinic clinic, IVRLanguage preferredLanguage){
        if (!cmsLiteService.isStreamContentAvailable(preferredLanguage.getCode(), String.format("%s%s", clinic.getName(), TamaIVRMessage.WAV))) {
            return TamaIVRMessage.DEFAULT_OUTBOUND_CLINIC_MESSAGE;
        }
        return clinic.getName();
    }

    public String getInboundMessage(Clinic clinic, IVRLanguage preferredLanguage) {
        if (!cmsLiteService.isStreamContentAvailable(preferredLanguage.getCode(), String.format("%s%s", clinic.getName(), TamaIVRMessage.WAV))) {
            return TamaIVRMessage.DEFAULT_INBOUND_CLINIC_MESSAGE;
        }
        return String.format("welcome_to_%s", clinic.getName());
    }
}
