package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class TreeChooser {

    @Autowired
    private CurrentDosageTakenTree currentDosageTakenTree;

    @Autowired
    @Qualifier(value = "currentDosageReminderTree")
    private CurrentDosageReminderTree currentDosageReminderTree;

    @Autowired
    private CurrentDosageConfirmTree currentDosageConfirmTree;

    @Autowired
	private Regimen6PartialTree regimen6PartialTree;

    public Tree getTree(IVRContext ivrContext) {
		if (isIncomingCall(ivrContext)) {
			if (isSymptomsReportingCall(ivrContext)) {
				return regimen6PartialTree.getTree();
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

	private boolean isSymptomsReportingCall(IVRContext ivrContext) {
		return ivrContext.ivrSession().isSymptomsReportingCall();
	}

	private boolean isIncomingCall(IVRContext ivrContext) {
		return ivrContext.ivrRequest().hasNoTamaData();
	}
}
