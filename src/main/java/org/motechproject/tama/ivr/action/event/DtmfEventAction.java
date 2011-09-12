package org.motechproject.tama.ivr.action.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.tama.eventlogging.EventLogConstants;
import org.motechproject.tama.ivr.IVREvent;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.AuthenticateAction;
import org.motechproject.tama.ivr.action.pillreminder.IvrAction;
import org.motechproject.tama.ivr.decisiontree.TreeChooser;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DtmfEventAction extends BaseEventAction {
    private AuthenticateAction authenticateAction;
    private TreeChooser treeChooser;
    private ThreadLocalTargetSource threadLocalTargetSource;
 
    @Autowired
    public DtmfEventAction(AuthenticateAction authenticateAction,
                           TreeChooser treeChooser, ThreadLocalTargetSource threadLocalTargetSource) {
        this.authenticateAction = authenticateAction;
        this.treeChooser = treeChooser;
        this.threadLocalTargetSource = threadLocalTargetSource;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        addEventLogData(EventLogConstants.DTMF_DATA, ivrRequest.getData());
        if (ivrSession.isAuthentication()) {
            return authenticateAction.handle(ivrRequest, request, response);
        } else {
            return new IvrAction(treeChooser, messages, threadLocalTargetSource).handle(ivrRequest, ivrSession);
        }
    } 
}
