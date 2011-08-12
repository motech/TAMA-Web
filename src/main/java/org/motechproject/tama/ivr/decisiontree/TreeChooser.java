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

    public Tree getTree(IVRContext ivrContext) {
        if (ivrContext.ivrRequest().hasNoTamaData()) {
            PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
            if (pillRegimenSnapshot.isCurrentDosageTaken()) {
                return currentDosageTakenTree.getTree();
            } else {
                return currentDosageConfirmTree.getTree();
            }
        } else {
            return currentDosageReminderTree.getTree();
        }
    }
}
