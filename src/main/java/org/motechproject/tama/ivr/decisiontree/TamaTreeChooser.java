package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.ivr.kookoo.action.TreeChooser;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest.CallDirection;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.util.TamaSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TamaTreeChooser implements TreeChooser {

    @Autowired
    @Qualifier("currentDosageTakenTree")
    private CurrentDosageTakenTree currentDosageTakenTree;

    @Autowired
    @Qualifier(value = "currentDosageReminderTree")
    private CurrentDosageReminderTree currentDosageReminderTree;

    @Autowired
    private CurrentDosageConfirmTree currentDosageConfirmTree;

    @Autowired
    private FourDayRecallTree fourDayRecallTree;

    public Tree getTree(IVRContext ivrContext) {
        TamaDecisionTree chosenTree;
        if (isIncomingCall(ivrContext)) {
            PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
            if (pillRegimenSnapshot.isCurrentDosageTaken()) {
                chosenTree = currentDosageTakenTree;
            } else {
                chosenTree = currentDosageConfirmTree;
            }
            ivrContext.ivrSession().set(TamaSessionUtil.TamaSessionAttribute.CALL_DIRECTION, CallDirection.Inbound);
        } else {
            if (TamaSessionUtil.patientOnFourDayRecall(ivrContext.ivrSession())) chosenTree = fourDayRecallTree;
            else chosenTree = currentDosageReminderTree;
        }
        return chosenTree.getTree(ivrContext);
    }

    private boolean isIncomingCall(IVRContext ivrContext) {
        return ivrContext.ivrRequest().getCallDirection() == CallDirection.Inbound;
    }
}
