package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.pillreminder.IvrAction;
import org.motechproject.tama.ivr.decisiontree.TreeChooser;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DialEventAction extends BaseIncomingAction {
    private TreeChooser treeChooser;
    private ThreadLocalTargetSource threadLocalTargetSource;

    @Autowired
    public DialEventAction(TreeChooser treeChooser, ThreadLocalTargetSource threadLocalTargetSource) {
        this.treeChooser = treeChooser;
        this.threadLocalTargetSource = threadLocalTargetSource;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        if (ivrRequest.getStatus().equals("answered")) {
            ivrRequest.setData("answered");
            return new IvrAction(treeChooser, messages, threadLocalTargetSource).handle(ivrRequest, ivrSession);
        }
        else {
            ivrRequest.setData("not_answered");
            return new IvrAction(treeChooser, messages, threadLocalTargetSource).handle(ivrRequest, ivrSession);
        }
    }
}
