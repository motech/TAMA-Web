package org.motechproject.tama.web.command;

import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPillTakenDuringIncomingCall extends BaseTreeCommand {


    @Autowired
    private TamaIVRMessage ivrMessage;

    public MessageOnPillTakenDuringIncomingCall(){}

    protected MessageOnPillTakenDuringIncomingCall(TamaIVRMessage ivrMessage){
        this.ivrMessage = ivrMessage;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        int dosageInterval = Integer.valueOf(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL));

        ArrayList<String> messages = new ArrayList<String>();
        if(pillRegimenSnapshot.isEarlyToTakeDosage(dosageInterval))
            messages.add(TamaIVRMessage.TOOK_DOSE_BEFORE_TIME);
        else if(pillRegimenSnapshot.isLateToTakeDosage())
            messages.add(TamaIVRMessage.TOOK_DOSE_LATE);
        else if(pillRegimenSnapshot.hasTakenDosageOnTime(dosageInterval))
            messages.add(TamaIVRMessage.DOSE_TAKEN_ON_TIME);

        messages.add(TamaIVRMessage.DOSE_RECORDED);
        return messages.toArray(new String[]{});
    }
}
