package org.motechproject.tama.ivr.command;


import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.tama.common.util.FileUtil;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClinicNameMessageBuilder {

    private CMSLiteService cmsLiteService;

    @Autowired
    public ClinicNameMessageBuilder(CMSLiteService cmsLiteService) {
        this.cmsLiteService = cmsLiteService;
    }

    public String getOutboundMessage(Clinic clinic, IVRLanguage preferredLanguage) {
        if (!cmsLiteService.isStreamContentAvailable(preferredLanguage.getCode(), getClinicWavFileName(clinic.getName()))) {
            return TamaIVRMessage.DEFAULT_OUTBOUND_CLINIC_MESSAGE;
        }
        return clinic.getName();
    }

    public String getInboundMessage(Clinic clinic, IVRLanguage preferredLanguage) {
        String clinicMessage = String.format("welcome_to_%s", clinic.getName());
        if (!cmsLiteService.isStreamContentAvailable(preferredLanguage.getCode(), getClinicWavFileName(clinicMessage))) {
            return TamaIVRMessage.DEFAULT_INBOUND_CLINIC_MESSAGE;
        }
        return clinicMessage;
    }

    private String getClinicWavFileName(String clinicMessage) {
        return FileUtil.sanitizeFilename(String.format("%s%s", clinicMessage, TamaIVRMessage.WAV));
    }
}
