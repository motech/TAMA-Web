package org.motechproject.tama.ivr;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;

public class StandardIVRResponse {
    public static KookooIVRResponseBuilder signatureTuneAndCollectDTMF(String callId) {
        return signatureTune(callId).collectDtmfLength(4);
    }

    private static KookooIVRResponseBuilder signatureTune(String callId) {
        return new KookooIVRResponseBuilder().withSid(callId).withPlayAudios(TamaIVRMessage.SIGNATURE_MUSIC);
    }

    public static KookooIVRResponseBuilder signatureTuneAndHangup(String callId) {
        return signatureTune(callId).withHangUp();
    }
}
