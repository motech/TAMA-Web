package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.MyContext;
import org.motechproject.tama.ivr.action.AuthenticateAction;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.pillreminder.IVRAction;
import org.motechproject.tama.ivr.decisiontree.CurrentDosageReminderTree;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DtmfEventAction extends BaseIncomingAction {
    private AuthenticateAction authenticateAction;
    private CurrentDosageReminderTree currentDosageReminderTree;
    private ThreadLocalTargetSource threadLocalTargetSource;

    @Autowired
    public DtmfEventAction(AuthenticateAction authenticateAction,
                           CurrentDosageReminderTree currentDosageReminderTree, ThreadLocalTargetSource threadLocalTargetSource) {
        this.authenticateAction = authenticateAction;
        this.currentDosageReminderTree = currentDosageReminderTree;
        this.threadLocalTargetSource = threadLocalTargetSource;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        if (ivrSession.isAuthentication()) {
            return authenticateAction.handle(ivrRequest, request, response);
        } else {
            MyContext myContext = (MyContext) threadLocalTargetSource.getTarget();
            myContext.setIvrContext(new IVRContext(ivrRequest,  ivrSession));
            return new IVRAction(currentDosageReminderTree, messages).handle(ivrRequest, ivrSession);
        }
    }
}
