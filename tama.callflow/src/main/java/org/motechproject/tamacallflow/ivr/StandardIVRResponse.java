package org.motechproject.tamacallflow.ivr;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;

public class StandardIVRResponse {
    public static KookooIVRResponseBuilder signatureTuneAndCollectDTMF(String callId) {
        return signatureTune(callId).collectDtmfLength(10);
    }

    public static KookooIVRResponseBuilder endOfCallTuneAndHangup(String callId) {
        return endOfCallTune(callId).withHangUp();
    }

    public static KookooIVRResponseBuilder hangup() {
        return new KookooIVRResponseBuilder().withHangUp();
    }

    private static KookooIVRResponseBuilder signatureTune(String callId) {
        return new KookooIVRResponseBuilder().withSid(callId).withPlayAudios(TamaIVRMessage.SIGNATURE_MUSIC);
    }

    private static KookooIVRResponseBuilder endOfCallTune(String callId) {
        return new KookooIVRResponseBuilder().withSid(callId).withPlayAudios(TamaIVRMessage.END_OF_CALL);
    }
}
