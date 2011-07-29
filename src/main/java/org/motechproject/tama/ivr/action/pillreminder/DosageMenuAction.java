package org.motechproject.tama.ivr.action.pillreminder;

import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DosageMenuAction extends BaseIncomingAction {

    private CurrentDosageMenuAction currentDosageMenuAction;
    private PreviousDosageMenuAction previousDosageMenuAction;

    @Autowired
    public DosageMenuAction(CurrentDosageMenuAction currentDosageMenuAction, PreviousDosageMenuAction previousDosageMenuAction) {
        this.currentDosageMenuAction = currentDosageMenuAction;
        this.previousDosageMenuAction = previousDosageMenuAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);

        if (ivrSession.isPreviousDoseResponse())
            return previousDosageMenuAction.handle(ivrRequest, request, response);
        return currentDosageMenuAction.handle(ivrRequest, request, response);
    }
}
