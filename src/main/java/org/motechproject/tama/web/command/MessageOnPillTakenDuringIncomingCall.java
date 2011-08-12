package org.motechproject.tama.web.command;

import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageOnPillTakenDuringIncomingCall extends BaseTreeCommand {


    @Autowired
    private IVRMessage ivrMessage;

    public MessageOnPillTakenDuringIncomingCall(){}

    protected MessageOnPillTakenDuringIncomingCall(IVRMessage ivrMessage){
        this.ivrMessage = ivrMessage;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        int dosageInterval = Integer.valueOf(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL));

        if(pillRegimenSnapshot.isEarlyToTakeDosage(dosageInterval))
            return new String[]{IVRMessage.TOOK_DOSE_BEFORE_TIME};
        else if(pillRegimenSnapshot.isLateToTakeDosage())
            return new String[]{IVRMessage.TOOK_DOSE_LATE};
        else
            return new String[0];
    }
}
