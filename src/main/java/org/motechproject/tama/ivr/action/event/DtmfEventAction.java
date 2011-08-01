package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.AuthenticateAction;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.pillreminder.TamaIVRAction;
import org.motechproject.tama.ivr.decisiontree.CurrentDosageReminderTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DtmfEventAction extends BaseIncomingAction {
    private AuthenticateAction authenticateAction;
    private CurrentDosageReminderTree currentDosageReminderTree;

    @Autowired
    public DtmfEventAction(AuthenticateAction authenticateAction,
                           CurrentDosageReminderTree currentDosageReminderTree) {
        this.authenticateAction = authenticateAction;
        this.currentDosageReminderTree = currentDosageReminderTree;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        if (ivrSession.isAuthentication()) {
            return authenticateAction.handle(ivrRequest, request, response);
        } else {
            return new TamaIVRAction(currentDosageReminderTree, messages).handle(ivrRequest, ivrSession);
        }
    }
}
