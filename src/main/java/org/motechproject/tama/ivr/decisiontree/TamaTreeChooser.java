package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.ivr.action.TreeChooser;
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
    private Regimen6Tree regimen6Tree;

    public Tree getTree(IVRContext ivrContext) {
        if (isIncomingCall(ivrContext)) {
            if (TamaSessionUtil.isSymptomsReportingCall(ivrContext)) {
                return regimen6Tree.getTree();
            } else {
                PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
                if (pillRegimenSnapshot.isCurrentDosageTaken()) {
                    return currentDosageTakenTree.getTree();
                } else {
                    return currentDosageConfirmTree.getTree();
                }
            }
        } else {
            return currentDosageReminderTree.getTree();
        }
    }

    private boolean isIncomingCall(IVRContext ivrContext) {
        return ivrContext.ivrRequest().getCallDirection() == CallDirection.Inbound;
    }
}
